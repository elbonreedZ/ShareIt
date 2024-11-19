package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDateDto;

import java.util.List;

@Data
public class ItemWIthCommentsDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private List<CommentDto> comments;
    private BookingDateDto lastBooking;
    private BookingDateDto nextBooking;
}
