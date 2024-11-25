package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.api.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateBooking() throws Exception {
        long bookerId = 1;
        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                2);
        ItemDto item = new ItemDto(2, "Item name", "description", true, null);
        UserDto booker = new UserDto(bookerId, "User name", "email");
        BookingDto bookingDto = new BookingDto(1,
                bookingCreateDto.getStart(),
                bookingCreateDto.getEnd(),
                item,
                booker,
                BookingStatus.WAITING);

        when(bookingService.create(bookingCreateDto, bookerId)).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookerId)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.item.id").value(item.getId()))
                .andExpect(jsonPath("$.item.name").value(item.getName()))
                .andExpect(jsonPath("$.booker.id").value(booker.getId()))
                .andExpect(jsonPath("$.booker.name").value(booker.getName()))
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.name()));
    }

    @Test
    void testChangeStatus() throws Exception {
        long bookingId = 1;
        long ownerId = 2;
        boolean isApproved = true;
        ItemDto item = new ItemDto(2, "Item name", "description", true, null);
        UserDto booker = new UserDto(ownerId, "User name", "email");
        BookingDto bookingDto = new BookingDto(bookingId,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                booker,
                BookingStatus.APPROVED);

        when(bookingService.changeStatus(bookingId, ownerId, isApproved)).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{id}", bookingId)
                        .param("approved", String.valueOf(isApproved))
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(BookingStatus.APPROVED.name()))
                .andExpect(jsonPath("$.item.name").value(item.getName()))
                .andExpect(jsonPath("$.booker.name").value(booker.getName()));
    }

    @Test
    void testGetById() throws Exception {
        long bookingId = 1;
        long userId = 1;
        ItemDto item = new ItemDto(2, "Item name", "description", true, null);
        UserDto booker = new UserDto(userId, "User name", "email");
        BookingDto bookingDto = new BookingDto(bookingId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                booker,
                BookingStatus.WAITING);

        when(bookingService.getById(bookingId, userId)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.item.id").value(item.getId()))
                .andExpect(jsonPath("$.booker.id").value(booker.getId()))
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.name()));
    }

    @Test
    void testGetAllByBooker() throws Exception {
        long bookerId = 1;
        ItemDto item1 = new ItemDto(2, "Item name", "description", true, null);
        ItemDto item2 = new ItemDto(2, "Item name2", "description", true, null);
        UserDto booker = new UserDto(bookerId, "User name", "email");

        List<BookingDto> bookings = List.of(
                new BookingDto(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item1, booker, BookingStatus.WAITING),
                new BookingDto(2, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), item2, booker, BookingStatus.APPROVED)
        );

        when(bookingService.getAllByRole(bookerId, State.ALL, UserRole.BOOKER)).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookings.size()))
                .andExpect(jsonPath("$[0].id").value(bookings.get(0).getId()))
                .andExpect(jsonPath("$[0].status").value(BookingStatus.WAITING.name()))
                .andExpect(jsonPath("$[0].item.id").value(item1.getId()))
                .andExpect(jsonPath("$[1].id").value(bookings.get(1).getId()))
                .andExpect(jsonPath("$[1].status").value(BookingStatus.APPROVED.name()))
                .andExpect(jsonPath("$[1].item.id").value(item2.getId()));
    }

    @Test
    void testGetAllByOwner() throws Exception {
        long ownerId = 2;
        ItemDto item1 = new ItemDto(2, "Item name", "description", true, null);
        ItemDto item2 = new ItemDto(2, "Item name2", "description", true, null);
        UserDto booker = new UserDto(1, "User name", "email");

        List<BookingDto> bookings = List.of(
                new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item1, booker, BookingStatus.WAITING),
                new BookingDto(2L, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), item2, booker, BookingStatus.APPROVED)
        );

        when(bookingService.getAllByRole(ownerId, State.ALL, UserRole.OWNER)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookings.size()))
                .andExpect(jsonPath("$[0].id").value(bookings.get(0).getId()))
                .andExpect(jsonPath("$[0].status").value(BookingStatus.WAITING.name()))
                .andExpect(jsonPath("$[0].item.id").value(item1.getId()))
                .andExpect(jsonPath("$[1].id").value(bookings.get(1).getId()))
                .andExpect(jsonPath("$[1].status").value(BookingStatus.APPROVED.name()))
                .andExpect(jsonPath("$[1].item.id").value(item2.getId()));
    }

    @Test
    void createBookingThrowBadRequestException() throws Exception {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1
        );

        when(bookingService.create(bookingCreateDto, 1L))
                .thenThrow(new BadRequestException("Вещь недоступна для бронирования"));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации"))
                .andExpect(jsonPath("$.description").value("Вещь недоступна для бронирования"));
    }
}
