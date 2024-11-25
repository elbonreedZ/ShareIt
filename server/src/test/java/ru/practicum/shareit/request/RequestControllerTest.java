package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.api.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetByRequestor() throws Exception {
        List<ItemRequestWithItemsDto> response = List.of(
                ItemRequestWithItemsDto.builder()
                        .id(1)
                        .description("Test request 1")
                        .created(LocalDateTime.now())
                        .items(new ArrayList<>())
                        .build(),
                ItemRequestWithItemsDto.builder()
                        .id(2)
                        .description("Test request 2")
                        .created(LocalDateTime.now())
                        .items(new ArrayList<>())
                        .build()
        );

        when(itemRequestService.getByRequestor(1L)).thenReturn(response);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(response.size()))
                .andExpect(jsonPath("$[0].id").value(response.get(0).getId()))
                .andExpect(jsonPath("$[1].description").value(response.get(1).getDescription()));
    }

    @Test
    void testGetAll() throws Exception {
        List<ItemRequestDto> response = List.of(
                ItemRequestDto.builder()
                        .id(1)
                        .description("Request 1")
                        .created(LocalDateTime.now())
                        .build(),
                ItemRequestDto.builder()
                        .id(2)
                        .description("Request 2")
                        .created(LocalDateTime.now())
                        .build()
        );

        when(itemRequestService.getAll(1)).thenReturn(response);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(response.size()))
                .andExpect(jsonPath("$[0].description").value(response.get(0).getDescription()))
                .andExpect(jsonPath("$[1].id").value(response.get(1).getId()));
    }

    @Test
    void testGetById() throws Exception {
        ItemRequestWithItemsDto response = ItemRequestWithItemsDto.builder()
                .id(1)
                .description("Request description")
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        when(itemRequestService.getById(1)).thenReturn(response);

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.description").value(response.getDescription()))
                .andExpect(jsonPath("$.items.length()").value(0));
    }

    @Test
    void testCreate() throws Exception {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Test description");
        ItemRequestDto responseDto = ItemRequestDto.builder()
                .id(1)
                .description("Test description")
                .created(LocalDateTime.now())
                .build();

        when(itemRequestService.create(any(ItemRequestCreateDto.class), eq(1)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated());
    }
}