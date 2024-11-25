package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
	private long itemId;
	@NotNull(message = "Нужно указать даты бронирования")
	@FutureOrPresent
	private LocalDateTime start;
	@NotNull(message = "Нужно указать даты бронирования")
	@Future
	private LocalDateTime end;
}