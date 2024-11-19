package ru.practicum.shareit.booking.api;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.UserRole;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingCreateDto bookingCreateDto, long bookerId);

    BookingDto changeStatus(long id, long ownerId, boolean isApproved);

    BookingDto getById(long id, long userId);

    List<BookingDto> getAllByRole(long userId, State state, UserRole role);

    Booking findById(long id);
}
