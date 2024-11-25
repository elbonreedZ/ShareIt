package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.request.api.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.api.UserService;
import ru.practicum.shareit.user.dto.UserCreateDto;

import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class RequestServiceIntegrationTest {
    @Autowired
    UserService userService;

    @Autowired
    ItemRequestService itemRequestService;

    @Test
    public void testGetByRequestor() {
        UserCreateDto requestorCreateDto1 = new UserCreateDto("requestor", "email1");
        UserCreateDto requestorCreateDto2 = new UserCreateDto("requestor", "email2");
        ItemRequestCreateDto requestCreateDto1 = new ItemRequestCreateDto("SomeItem");
        ItemRequestCreateDto requestCreateDto2 = new ItemRequestCreateDto("Something");
        long requestorId1 = userService.create(requestorCreateDto1).getId();
        long requestorId2 = userService.create(requestorCreateDto2).getId();
        itemRequestService.create(requestCreateDto1, requestorId1);
        itemRequestService.create(requestCreateDto2, requestorId2);
        List<ItemRequestWithItemsDto> requestsForUser1 = itemRequestService.getByRequestor(requestorId1);
        List<ItemRequestWithItemsDto> requestsForUser2 = itemRequestService.getByRequestor(requestorId2);
        Assertions.assertEquals(1, requestsForUser1.size());
        Assertions.assertEquals(1, requestsForUser2.size());
        Assertions.assertEquals(requestCreateDto1.getDescription(), requestsForUser1.get(0).getDescription());
        Assertions.assertEquals(requestCreateDto2.getDescription(), requestsForUser2.get(0).getDescription());
    }

    @Test
    public void testGetAll() {
        UserCreateDto requestorCreateDto1 = new UserCreateDto("requestor", "email1");
        UserCreateDto requestorCreateDto2 = new UserCreateDto("requestor", "email2");
        ItemRequestCreateDto requestCreateDto1 = new ItemRequestCreateDto("SomeItem");
        ItemRequestCreateDto requestCreateDto2 = new ItemRequestCreateDto("Something");
        long requestorId1 = userService.create(requestorCreateDto1).getId();
        long requestorId2 = userService.create(requestorCreateDto2).getId();
        long reqId1 = itemRequestService.create(requestCreateDto1, requestorId1).getId();
        long reqId2 = itemRequestService.create(requestCreateDto2, requestorId2).getId();
        List<ItemRequestDto> requestsForRequestor1 = itemRequestService.getAll(requestorId1);
        List<ItemRequestDto> requestsForRequestor2 = itemRequestService.getAll(requestorId2);
        Assertions.assertEquals(1, requestsForRequestor1.size());
        Assertions.assertEquals(1, requestsForRequestor2.size());
        Assertions.assertEquals(reqId2, requestsForRequestor1.get(0).getId());
        Assertions.assertEquals(reqId1, requestsForRequestor2.get(0).getId());
    }

    @Test
    public void testGetById() {
        UserCreateDto requestorCreateDto1 = new UserCreateDto("requestor", "email1");
        ItemRequestCreateDto requestCreateDto1 = new ItemRequestCreateDto("SomeItem");
        ItemRequestCreateDto requestCreateDto2 = new ItemRequestCreateDto("Something");
        long requestorId1 = userService.create(requestorCreateDto1).getId();
        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getById(1));
        long reqId1 = itemRequestService.create(requestCreateDto1, requestorId1).getId();
        itemRequestService.create(requestCreateDto2, requestorId1);
        ItemRequestWithItemsDto itemRequestWithItemsDto = itemRequestService.getById(reqId1);
        Assertions.assertEquals(reqId1, itemRequestWithItemsDto.getId());
    }
}
