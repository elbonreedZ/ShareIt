package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.api.JpaItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.api.ItemRequestRepository;
import ru.practicum.shareit.request.api.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.api.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final JpaItemRepository itemRepository;

    @Override
    public ItemRequestDto create(ItemRequestCreateDto itemRequestCreateDto, long userId) {
        User user = userService.findById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestCreateDto, user);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestWithItemsDto> getByRequestor(long userId) {
        userService.findById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_Id(userId,
                Sort.by(Sort.Direction.DESC, "created"));
        Set<Long> itemRequestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toSet());
        Map<Long, List<Item>> items = itemRepository.findByRequest_IdIn(itemRequestIds)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
        return itemRequests.stream()
                .map(request -> {
                    List<Item> requestItems = items.getOrDefault(request.getId(), Collections.emptyList());
                    return ItemRequestMapper.toItemRequestWithItemsDto(request, requestItems);
                })
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAll(long userId) {
        return itemRequestRepository
                .findAllByRequestor_IdNot(userId, Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestWithItemsDto getById(long id) {
        ItemRequest itemRequest = findById(id);
        List<Item> items = itemRepository.findByRequest_Id(id);
        return ItemRequestMapper.toItemRequestWithItemsDto(itemRequest, items);
    }

    public ItemRequest findById(long id) {
        return itemRequestRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Запрос с id {} не найден", id);
                    return new NotFoundException(String.format("Запрос с id %d не найден", id));
                });
    }
}
