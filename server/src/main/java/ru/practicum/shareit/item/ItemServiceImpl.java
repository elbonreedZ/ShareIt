package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.api.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.api.CommentRepository;
import ru.practicum.shareit.item.api.ItemService;
import ru.practicum.shareit.item.api.JpaItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.api.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.api.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final JpaItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ItemRequestService itemRequestService;

    @Override
    public ItemWIthCommentsDto getById(long id) {
        Item item = findById(id);
        List<Comment> comments = commentRepository.findAllByItem_Id(id);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(item.getOwner().getId());
        return getItemWithBookingsAndComments(item, bookings, comments);
    }

    @Override
    public ItemDto create(ItemCreateDto itemCreateDto, long ownerId) {
        User user = userService.findById(ownerId);
        Long requestId = itemCreateDto.getRequestId();
        ItemRequest itemRequest = null;
        if (requestId != null) {
            itemRequest = itemRequestService.findById(requestId);
        }
        Item item = ItemMapper.toItem(itemCreateDto, itemRequest, user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(ItemUpdateDto itemUpdateDto, long ownerId, long id) {
        Item existed = findById(id);
        if (existed.getOwner().getId() != ownerId) {
            log.error("Ошибка аутентификации: Владелец вещи c id = {}: {}, запрос от : {}",
                    id, existed.getOwner(), ownerId);
            throw new ForbiddenException("Ошибка аутентификации");
        }
        if (itemUpdateDto.getName() != null) {
            existed.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null) {
            existed.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            existed.setAvailable(itemUpdateDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(existed));
    }

    @Override
    public List<ItemWIthCommentsDto> getByOwner(long ownerId) {
        userService.findById(ownerId);
        Map<Long, Item> items = itemRepository.findAllByOwner_id(ownerId)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        Map<Long, List<Booking>> bookings = bookingRepository.findAllByItem_IdIn(items.keySet())
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        Map<Long, List<Comment>> comments = commentRepository.findAllByItem_IdIn(items.keySet())
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return items.values().stream()
                .map(item -> {
                    List<Booking> itemBookings = bookings.getOrDefault(item.getId(), Collections.emptyList());
                    List<Comment> itemComments = comments.getOrDefault(item.getId(), Collections.emptyList());
                    return getItemWithBookingsAndComments(item, itemBookings, itemComments);
                })
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchByNameAndDescription(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Item findById(long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Вещь с id {} не найдена", id);
                    return new NotFoundException(String.format("Вещь с id %d не найдена", id));
                });
    }

    @Override
    public CommentDto addComment(CreateCommentDto createCommentDto, long itemId, long authorId) {
        Item item = findById(itemId);
        User author = userService.findById(authorId);
        Booking booking = bookingRepository.findByBooker_IdAndItem_Id(authorId, itemId)
                .orElseThrow(() -> {
                    log.error("Добавление комментария: " +
                            "Пользователь с id {} не бронировал вещь c id {}", authorId, itemId);
                    return new ForbiddenException(
                            String.format("Пользователь с id %d не бронировал вещь c id %d", authorId, itemId));
                });
        if (!booking.getEnd().isBefore(LocalDateTime.now())) {
            log.error("Добавление комментария: Бронирование вещи c id {} пользователем с id {} еще не закончилось",
                    itemId, authorId);
            throw new BadRequestException(String.format("Бронирование вещи c id %d " +
                            "пользователем с id %d еще не закончилось", authorId, itemId));
        }
        Comment comment = CommentMapper.toComment(createCommentDto, author, item);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private ItemWIthCommentsDto getItemWithBookingsAndComments(Item item, List<Booking> bookings, List<Comment> comments) {
        Booking lastBooking = bookings.stream()
                .filter(booking -> {
                    LocalDateTime now = LocalDateTime.now();
                    return (booking.getStart().isBefore(now) && booking.getEnd().isAfter(now));
                })
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);

        Booking nextBooking = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);

        return ItemMapper.toItemWIthCommentsDto(item, comments, lastBooking, nextBooking);
    }
}
