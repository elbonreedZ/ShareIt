package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.api.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.api.ItemService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.user.api.UserService;
import ru.practicum.shareit.user.dto.UserCreateDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class BookingServiceIntegrationTest {
    @Autowired
    BookingService bookingService;
    @Autowired
    UserService userService;

    @Autowired
    ItemService itemService;

    @Test
    public void testGetAllByRole() {
        ItemCreateDto itemRequestDtoForFuture = new ItemCreateDto("future", "description", true, null);
        ItemCreateDto itemRequestDtoForCurrent = new ItemCreateDto("current", "description", true, null);
        ItemCreateDto itemRequestDtoForPast = new ItemCreateDto("past", "description", true, null);
        ItemCreateDto itemRequestDtoForWaiting = new ItemCreateDto("waiting", "description", true, null);
        ItemCreateDto itemRequestDtoForRejected = new ItemCreateDto("rejected", "description", true, null);
        UserCreateDto ownerRequestDto = new UserCreateDto("owner", "emailOwner");
        UserCreateDto bookerRequestDto = new UserCreateDto("booker", "emailBooker");
        long ownerId = userService.create(ownerRequestDto).getId();
        long bookerId = userService.create(bookerRequestDto).getId();
        long itemIdFuture = itemService.create(itemRequestDtoForFuture, ownerId).getId();
        long itemIdCurrent = itemService.create(itemRequestDtoForCurrent, ownerId).getId();
        long itemIdPast = itemService.create(itemRequestDtoForPast, ownerId).getId();
        long itemIdWaiting = itemService.create(itemRequestDtoForWaiting, ownerId).getId();
        long itemIdRejected = itemService.create(itemRequestDtoForRejected, ownerId).getId();
        BookingCreateDto bookingRequestForFuture = new BookingCreateDto(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3),
                itemIdFuture);
        BookingCreateDto bookingRequestForCurrent = new BookingCreateDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                itemIdCurrent);
        BookingCreateDto bookingRequestForPast = new BookingCreateDto(LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2),
                itemIdPast);
        BookingCreateDto bookingRequestForWaiting = new BookingCreateDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                itemIdWaiting);
        BookingCreateDto bookingRequestForRejected = new BookingCreateDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                itemIdRejected);
        long bookingFutureId = bookingService.create(bookingRequestForFuture, bookerId).getId();
        long bookingCurrentId = bookingService.create(bookingRequestForCurrent, bookerId).getId();
        long bookingPastId = bookingService.create(bookingRequestForPast, bookerId).getId();
        long bookingWaitingId = bookingService.create(bookingRequestForWaiting, bookerId).getId();
        long bookingRejectedId = bookingService.create(bookingRequestForRejected, bookerId).getId();
        bookingService.changeStatus(bookingFutureId, ownerId, true);
        bookingService.changeStatus(bookingCurrentId, ownerId, true);
        bookingService.changeStatus(bookingPastId, ownerId, true);
        bookingService.changeStatus(bookingRejectedId, ownerId, false);
        Map<State, Long> bookingIds = new HashMap<>();
        bookingIds.put(State.FUTURE, bookingFutureId);
        bookingIds.put(State.CURRENT, bookingCurrentId);
        bookingIds.put(State.PAST, bookingPastId);
        bookingIds.put(State.WAITING, bookingWaitingId);
        bookingIds.put(State.REJECTED, bookingRejectedId);

        assertGetAllByRole(UserRole.BOOKER, bookerId, bookingIds);
        assertGetAllByRole(UserRole.OWNER, ownerId, bookingIds);
    }

    public void assertGetAllByRole(UserRole userRole, long userId, Map<State, Long> bookingIds) {
        List<BookingDto> bookingsAll = bookingService.getAllByRole(userId, State.ALL, userRole);
        assertEquals(5, bookingsAll.size());
        List<BookingDto> bookingsFuture = bookingService.getAllByRole(userId, State.FUTURE, userRole);
        assertEquals(1, bookingsFuture.size());
        assertEquals(bookingIds.get(State.FUTURE), bookingsFuture.get(0).getId());
        List<BookingDto> bookingsCurrent = bookingService.getAllByRole(userId, State.CURRENT, userRole);
        assertEquals(1, bookingsCurrent.size());
        assertEquals(bookingIds.get(State.CURRENT), bookingsCurrent.get(0).getId());
        List<BookingDto> bookingsPast = bookingService.getAllByRole(userId, State.PAST, userRole);
        assertEquals(1, bookingsPast.size());
        assertEquals(bookingIds.get(State.PAST), bookingsPast.get(0).getId());
        List<BookingDto> bookingsWaiting = bookingService.getAllByRole(userId, State.WAITING, userRole);
        assertEquals(1, bookingsWaiting.size());
        assertEquals(bookingIds.get(State.WAITING), bookingsWaiting.get(0).getId());
        List<BookingDto> bookingsRejected = bookingService.getAllByRole(userId, State.REJECTED, userRole);
        assertEquals(1, bookingsRejected.size());
        assertEquals(bookingIds.get(State.REJECTED), bookingsRejected.get(0).getId());
    }

    @Test
    public void testGetById() {
        ItemCreateDto itemRequestDto = new ItemCreateDto("item", "description", true, null);
        UserCreateDto ownerRequestDto = new UserCreateDto("owner", "emailOwner");
        UserCreateDto bookerRequestDto = new UserCreateDto("booker", "emailBooker");
        UserCreateDto anotherUserRequestDto = new UserCreateDto("another", "emailAnother");
        long ownerId = userService.create(ownerRequestDto).getId();
        long bookerId = userService.create(bookerRequestDto).getId();
        long anotherUserId = userService.create(anotherUserRequestDto).getId();
        long itemId = itemService.create(itemRequestDto, ownerId).getId();
        BookingCreateDto bookingRequest = new BookingCreateDto(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3),
                itemId);
        BookingDto bookingDto = bookingService.create(bookingRequest, bookerId);
        BookingDto findBookingByOwner = bookingService.getById(bookingDto.getId(), ownerId);
        BookingDto findBookingByBooker = bookingService.getById(bookingDto.getId(), bookerId);
        assertNotNull(findBookingByOwner);
        assertNotNull(findBookingByBooker);
        assertThrows(ForbiddenException.class, () -> bookingService.getById(bookingDto.getId(), anotherUserId));
        assertThrows(NotFoundException.class, () -> bookingService.getById(1000, anotherUserId));
    }

    @Test
    public void testCreateExceptions() {
        ItemCreateDto itemRequestDtoNotAvailable = new ItemCreateDto("item", "description", false, null);
        ItemCreateDto itemRequestDtoAvailable = new ItemCreateDto("item", "description", true, null);
        UserCreateDto ownerRequestDto = new UserCreateDto("owner", "emailOwner");
        UserCreateDto bookerRequestDto = new UserCreateDto("booker", "emailBooker");
        long ownerId = userService.create(ownerRequestDto).getId();
        long bookerId = userService.create(bookerRequestDto).getId();
        long itemIdNotAvailable = itemService.create(itemRequestDtoNotAvailable, ownerId).getId();
        long itemIdAvailable = itemService.create(itemRequestDtoAvailable, ownerId).getId();
        BookingCreateDto bookingRequestNotAvailableItem = new BookingCreateDto(LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3),
                itemIdNotAvailable);
        LocalDateTime startAndEnd = LocalDateTime.now();
        BookingCreateDto bookingRequestStartEqualEnd = new BookingCreateDto(startAndEnd, startAndEnd,
                itemIdAvailable);
        BookingCreateDto bookingRequestStartAfterEnd = new BookingCreateDto(LocalDateTime.now().plusDays(1), LocalDateTime.now(),
                itemIdAvailable);
        BadRequestException exceptionNotAvailable = assertThrows(BadRequestException.class,
                () -> bookingService.create(bookingRequestNotAvailableItem, bookerId));
        assertEquals("Вещь недоступна для бронирования", exceptionNotAvailable.getMessage());

        BadRequestException exceptionStartEqualsEnd = assertThrows(BadRequestException.class,
                () -> bookingService.create(bookingRequestStartEqualEnd, bookerId));
        assertEquals("Дата начала и окончания бронирования не могут совпадать", exceptionStartEqualsEnd.getMessage());

        BadRequestException exceptionStartAfterEnd = assertThrows(BadRequestException.class,
                () -> bookingService.create(bookingRequestStartAfterEnd, bookerId));
        assertEquals("Дата начала не может быть позже даты окончания бронирования", exceptionStartAfterEnd.getMessage());

    }
}
