package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .request(item.getRequest() != null ? item.getRequest() : null)
                .build();
    }

    public static Item toItem(ItemCreateDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static Item toItem(ItemUpdateDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemOwnerDto toItemOwnerDto(Item item, Booking bookingLast, Booking bookingNext, List<Comment> comments) {
        return ItemOwnerDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .lastBooking(BookingMapper.toBookingDateDto(bookingLast))
                .nextBooking(BookingMapper.toBookingDateDto(bookingNext))
                .comments(CommentMapper.toCommentDtos(comments))
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
}
