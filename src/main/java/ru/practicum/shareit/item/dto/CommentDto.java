package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CommentDto {
    private long id;
    private String text;
    private long itemId;
    private String authorName;
    LocalDate created;
}
