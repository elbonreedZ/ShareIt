package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
        if (item.getRequest() != null) {
            itemDto.setRequest(ItemRequestMapper.toItemRequestDto(item.getRequest()));
        }
        return itemDto;
    }

    public static Item toItem(ItemCreateDto itemDto, ItemRequest request, User owner) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .request(request)
                .build();
    }

    public static ItemWIthCommentsDto toItemWIthCommentsDto(
            Item item, List<Comment> comments, Booking bookingLast, Booking bookingNext) {
        ItemWIthCommentsDto itemWIthCommentsDto = new ItemWIthCommentsDto();
        itemWIthCommentsDto.setId(item.getId());
        itemWIthCommentsDto.setName(item.getName());
        itemWIthCommentsDto.setAvailable(item.isAvailable());
        itemWIthCommentsDto.setDescription(item.getDescription());
        itemWIthCommentsDto.setLastBooking(BookingMapper.toBookingDateDto(bookingLast));
        itemWIthCommentsDto.setNextBooking(BookingMapper.toBookingDateDto(bookingNext));
        itemWIthCommentsDto.setComments(CommentMapper.toCommentDtos(comments));
        return itemWIthCommentsDto;
    }

    public static ItemForRequestDto toItemForRequestDto(Item item) {
        return ItemForRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }
}
