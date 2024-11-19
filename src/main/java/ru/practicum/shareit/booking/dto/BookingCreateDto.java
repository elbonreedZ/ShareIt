package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingCreateDto {
    @NotNull(message = "Нужно указать даты бронирования")
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull(message = "Нужно указать даты бронирования")
    @FutureOrPresent
    private LocalDateTime end;
    @NotNull
    private long itemId;
}
