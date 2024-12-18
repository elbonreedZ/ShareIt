package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
        bookingDto.setBooker(UserMapper.toUserDto(booking.getBooker()));
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static Booking toBooking(BookingCreateDto bookingCreateDto, User booker, BookingStatus status, Item item) {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setStatus(status);
        booking.setItem(item);
        booking.setStart(bookingCreateDto.getStart());
        booking.setEnd(bookingCreateDto.getEnd());
        return booking;
    }

    public static BookingDateDto toBookingDateDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookingDateDto bookingDateDto = new BookingDateDto();
        bookingDateDto.setStart(booking.getStart());
        bookingDateDto.setEnd(booking.getEnd());
        return bookingDateDto;
    }
}
