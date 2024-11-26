package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.item.api.ItemService;
import ru.practicum.shareit.item.dto.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetById() throws Exception {
        long itemId = 1L;
        BookingDateDto bookingDateDto = new BookingDateDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        ItemWIthCommentsDto item = new ItemWIthCommentsDto(itemId, "name", "description", true,
                new ArrayList<>(), bookingDateDto, null);
        when(itemService.getById(itemId)).thenReturn(item);

        mockMvc.perform(get("/items/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.comments").isEmpty())
                .andExpect(jsonPath("$.lastBooking.start").isNotEmpty())
                .andExpect(jsonPath("$.lastBooking.end").isNotEmpty())
                .andExpect(jsonPath("$.nextBooking").isEmpty());
    }

    @Test
    public void testCreate() throws Exception {
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(1L, "name", "description", true, null);
        ItemCreateDto itemCreateDto = new ItemCreateDto("name", "description", true, null);
        when(itemService.create(itemCreateDto, ownerId)).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    public void testUpdate() throws Exception {
        long itemId = 1L;
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(1L, "name", "description", true, null);
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("name", "description", true);
        when(itemService.update(itemUpdateDto, ownerId, itemId)).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .content(objectMapper.writeValueAsString(itemUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    public void testGetByOwner() throws Exception {
        long ownerId = 1L;
        ItemWIthCommentsDto item = new ItemWIthCommentsDto(1L, "name", "description",
                true, Collections.emptyList(), null, null);
        List<ItemWIthCommentsDto> items = List.of(item);
        when(itemService.getByOwner(ownerId)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].description").value("description"));
    }

    @Test
    public void testSearch() throws Exception {
        String searchText = "name";
        ItemDto itemDto = new ItemDto(1L, "name", "description", true, null);
        List<ItemDto> items = List.of(itemDto);
        when(itemService.search(searchText)).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].description").value("description"));
    }

    @Test
    public void testAddComment() throws Exception {
        long itemId = 1L;
        long authorId = 1L;
        CommentDto commentDto = new CommentDto(1L, "likeIt", itemId, "author", LocalDate.now());
        CreateCommentDto createCommentDto = new CreateCommentDto("likeIt");
        when(itemService.addComment(createCommentDto, itemId, authorId))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(objectMapper.writeValueAsString(createCommentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", authorId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.itemId").value(commentDto.getItemId()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.created").isNotEmpty());
    }

    @Test
    public void testUpdateForbiddenEx() throws Exception {
        long id = 1;
        long ownerId = 1;

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("name", "description", true);
        when(itemService.update(itemUpdateDto, ownerId, id))
                .thenThrow(new ForbiddenException("Ошибка аутентификации"));

        mockMvc.perform(patch("/items/{id}", id)
                        .content(objectMapper.writeValueAsString(itemUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Доступ запрещён"))
                .andExpect(jsonPath("$.description").value("Ошибка аутентификации"));
    }
}