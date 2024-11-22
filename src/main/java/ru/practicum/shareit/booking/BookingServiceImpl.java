package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.api.BookingRepository;
import ru.practicum.shareit.booking.api.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.api.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.api.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingDto create(BookingCreateDto bookingCreateDto, long bookerId) {
        User booker = userService.findById(bookerId);
        Item item = itemService.findById(bookingCreateDto.getItemId());
        if (!item.isAvailable()) {
            log.error("Вещь с id = {} недоступна для бронирования", item.getId());
            throw new BadRequestException("Вещь недоступна для бронирования");
        }
        LocalDateTime start = bookingCreateDto.getStart();
        LocalDateTime end = bookingCreateDto.getEnd();
        if (start.isEqual(end)) {
            log.error("Совпадение даты начала {} и конца {} бронирования", start, end);
            throw new BadRequestException("Дата начала и окончания бронирования не могут совпадать");
        }
        if (bookingCreateDto.getStart().isAfter(bookingCreateDto.getEnd())) {
            log.error("Дата начала {} позже даты конца {} бронирования", start, end);
            throw new BadRequestException("Дата начала не может быть позже даты окончания бронирования");
        }
        Booking booking = BookingMapper.toBooking(bookingCreateDto, booker, BookingStatus.WAITING, item);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto changeStatus(long id, long ownerId, boolean isApproved) {
        Booking existed = findById(id);
        Item item = existed.getItem();
        if (item.getOwner().getId() != ownerId) {
            log.error("Доступ запрещён: Пользователь с id = {} не является владельцем вещи c id = {}",
                    ownerId, item.getId());
            throw new ForbiddenException(String.format("Пользователь с id = %d не является владельцем вещи c id = %d",
                    ownerId, item.getId()));
        }
        existed.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(existed));
    }

    @Override
    public BookingDto getById(long id, long userId) {
        Booking booking = findById(id);
        Item item = booking.getItem();
        if (userId != booking.getBooker().getId() && userId != item.getOwner().getId()) {
            log.error("Доступ запрещён: Пользователь с id = {} не является владельцем арендованной вещи c id = {}" +
                    " или её арендатором", userId, item.getId());
            throw new ForbiddenException(String.format(
                    "Пользователь с id = %d не является владельцем вещи c id = %d или её арендатором",
                    userId, item.getId()));
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByRole(long userId, State state, UserRole role) {

        userService.findById(userId);

        if (state == null) {
            state = State.ALL;
        }

        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL -> bookings = (role == UserRole.OWNER)
                    ? bookingRepository.findAllByItemOwnerId(userId)
                    : bookingRepository.findAllByBooker_Id(userId);
            case FUTURE -> bookings = (role == UserRole.OWNER)
                    ? bookingRepository.findFutureByItemOwner(userId, LocalDateTime.now())
                    : bookingRepository.findAllByBooker_IdAndStatusEqualsAndStartIsAfter(userId,
                    BookingStatus.APPROVED, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
            case CURRENT -> bookings = (role == UserRole.OWNER)
                    ? bookingRepository.findCurrentByItemOwner(userId, LocalDateTime.now(),
                    Sort.by(Sort.Direction.DESC, "start"))
                    : bookingRepository.findCurrentByBooker(userId, LocalDateTime.now(),
                    Sort.by(Sort.Direction.DESC, "start"));
            case PAST -> bookings = (role == UserRole.OWNER)
                    ? bookingRepository.findPastByItemOwner(userId, LocalDateTime.now())
                    : bookingRepository.findAllByBooker_IdAndStatusEqualsAndEndIsBefore(userId,
                    BookingStatus.APPROVED, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "end"));
            case WAITING -> bookings = (role == UserRole.OWNER)
                    ? bookingRepository.findByOwnerAndStatus(userId, BookingStatus.WAITING)
                    : bookingRepository.findAllByBooker_IdAndStatusEquals(userId, BookingStatus.WAITING);
            case REJECTED -> bookings = (role == UserRole.OWNER)
                    ? bookingRepository.findByOwnerAndStatus(userId, BookingStatus.REJECTED)
                    : bookingRepository.findAllByBooker_IdAndStatusEquals(userId, BookingStatus.REJECTED);
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }


    @Override
    public Booking findById(long id) {
        return bookingRepository.findById(id).orElseThrow(() -> {
            log.error("Бронирование с id {} не найдено", id);
            return new NotFoundException(String.format("Бронирование с id %d не найдено", id));
        });
    }
}
