package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getAllByBooker(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Пришел Get запрос /bookings userId: {}, state {} на получение всех бронирований арендатора",
				userId, state);
		return bookingClient.getByBooker(userId, state);
	}

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Пришел Post запрос /bookings с телом: {} и заголовком X-Sharer-User-Id: {}",
				requestDto, userId);
		return bookingClient.createBooking(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable Long bookingId) {
		log.info("Пришел Get запрос /bookings/{id} с id: {}, userId: {}", bookingId, userId);
		return bookingClient.getBookingById(userId, bookingId);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Object> changeStatus(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long ownerId,
								   @RequestParam(name = "approved") boolean isApproved) {
		log.info("Пришел Patch запрос /bookings с id: {}, заголовком X-Sharer-User-Id: {} и параметром isApproved: {}",
				id, ownerId, isApproved);
		return bookingClient.changeStatus(ownerId, id, isApproved);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllByOwner(@RequestParam(name = "state", defaultValue = "all") String stateParam,
												@RequestHeader("X-Sharer-User-Id") long ownerId) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Пришел Get запрос /bookings/owner ownerId: {}, state {} на получение всех бронирований владельца",
				ownerId, state);
		return bookingClient.getByOwner(ownerId, state);
	}
}
