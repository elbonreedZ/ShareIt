package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDateDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemWIthCommentsDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private List<CommentDto> comments;
    private BookingDateDto lastBooking;
    private BookingDateDto nextBooking;
}
