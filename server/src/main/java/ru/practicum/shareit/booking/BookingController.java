package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.api.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody BookingCreateDto bookingCreateDto,
                                    @RequestHeader("X-Sharer-User-Id") long bookerId) {
        log.info("Пришел Post запрос /bookings с телом: {} и заголовком X-Sharer-User-Id: {}",
                bookingCreateDto, bookerId);
        BookingDto bookingDto = bookingService.create(bookingCreateDto, bookerId);
        log.info("Отправлен ответ Post /bookings с телом: {}", bookingDto);
        return bookingDto;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto changeStatus(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long ownerId,
                                   @RequestParam(name = "approved") boolean isApproved) {
        log.info("Пришел Patch запрос /bookings с id: {}, заголовком X-Sharer-User-Id: {} и параметром isApproved: {}",
                id, ownerId, isApproved);
        BookingDto bookingDto = bookingService.changeStatus(id, ownerId, isApproved);
        log.info("Отправлен ответ Patch /bookings с телом: {}", bookingDto);
        return bookingDto;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getById(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Пришел Get запрос /bookings/{id} с id: {}, userId: {}", id, userId);
        BookingDto bookingDto = bookingService.getById(id, userId);
        log.info("Отправлен ответ Get /bookings/{id} с телом: {}", bookingDto);
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> getAllByBooker(@RequestParam(required = false, defaultValue = "all") State state,
                                           @RequestHeader("X-Sharer-User-Id") long bookerId) {
        log.info("Пришел Get запрос /bookings userId: {}, state {} на получение всех бронирований арендатора",
                bookerId, state);
        List<BookingDto> bookings = bookingService.getAllByRole(bookerId, state, UserRole.BOOKER);
        log.info("Отправлен ответ Get /bookings с телом: {}", bookings);
        return bookings;
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestParam(required = false) State state, @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Пришел Get запрос /bookings/owner ownerId: {}, state {} на получение всех бронирований владельца",
                ownerId, state);
        List<BookingDto> bookings = bookingService.getAllByRole(ownerId, state, UserRole.OWNER);
        log.info("Отправлен ответ Get /bookings/owner с телом: {}", bookings);
        return bookings;
    }
}
