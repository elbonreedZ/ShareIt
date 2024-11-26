package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.api.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.api.ItemService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.api.UserService;
import ru.practicum.shareit.user.dto.UserCreateDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class ItemServiceIntegrationTest {
    @Autowired
    UserService userService;

    @Autowired
    ItemService itemService;

    @Autowired
    BookingService bookingService;


    @Test
    public void testGetAllByOwner() {
        ItemCreateDto itemRequestDto = new ItemCreateDto("ItemName1", "description", true, null);
        ItemCreateDto itemRequestDto2 = new ItemCreateDto("ItemName2", "description", true, null);
        UserCreateDto ownerRequestDto = new UserCreateDto("owner", "emailOwner");
        UserCreateDto anotherUserRequestDto = new UserCreateDto("another", "anotherEmail");
        long ownerId = userService.create(ownerRequestDto).getId();
        long anotherId = userService.create(anotherUserRequestDto).getId();
        long item1Id = itemService.create(itemRequestDto, ownerId).getId();
        long item2Id = itemService.create(itemRequestDto2, ownerId).getId();
        List<ItemWIthCommentsDto> itemsOwner = itemService.getByOwner(ownerId);
        List<ItemWIthCommentsDto> itemsAnother = itemService.getByOwner(anotherId);
        assertEquals(2, itemsOwner.size());
        assertEquals(0, itemsAnother.size());
        assertEquals(item1Id, itemsOwner.get(0).getId());
        assertEquals(item2Id, itemsOwner.get(1).getId());
    }

    @Test
    public void testAddComment() {
        UserCreateDto ownerRequestDto = new UserCreateDto("owner", "emailOwner");
        UserCreateDto bookerRequestDto = new UserCreateDto("booker", "emailBooker");
        ItemCreateDto itemRequestDto1 = new ItemCreateDto("ItemName1", "description", true, null);
        ItemCreateDto itemRequestDto2 = new ItemCreateDto("ItemName2", "description", true, null);
        CreateCommentDto commentDto = new CreateCommentDto("likeIt");
        long ownerId = userService.create(ownerRequestDto).getId();
        long bookerId = userService.create(bookerRequestDto).getId();
        long item1Id = itemService.create(itemRequestDto1, ownerId).getId();
        long item2Id = itemService.create(itemRequestDto2, ownerId).getId();
        BookingCreateDto bookingRequestPast = new BookingCreateDto(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item1Id);
        BookingCreateDto bookingRequestCurrent = new BookingCreateDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                item2Id);
        assertThrows(ForbiddenException.class,() -> itemService.addComment(commentDto, item1Id, bookerId));
        bookingService.create(bookingRequestPast, bookerId);
        CommentDto comment = itemService.addComment(commentDto, item1Id, bookerId);
        assertNotNull(comment);
        assertEquals(commentDto.getText(), comment.getText());
        bookingService.create(bookingRequestPast, bookerId);
        bookingService.create(bookingRequestCurrent, bookerId);
        assertThrows(BadRequestException.class, () -> itemService.addComment(commentDto, item2Id, bookerId));
    }

    @Test
    public void testUpdate() {
        ItemCreateDto itemRequestDto = new ItemCreateDto("ItemName1", "description", false, null);
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("newName", "newDescription", true);
        UserCreateDto ownerRequestDto = new UserCreateDto("owner", "emailOwner");
        UserCreateDto anotherUserRequestDto = new UserCreateDto("another", "anotherEmail");
        long ownerId = userService.create(ownerRequestDto).getId();
        long anotherId = userService.create(anotherUserRequestDto).getId();
        long itemId = itemService.create(itemRequestDto, ownerId).getId();
        ItemDto itemUpdated = itemService.update(itemUpdateDto, ownerId, itemId);
        assertEquals(itemUpdateDto.getName(), itemUpdated.getName());
        assertEquals(itemUpdateDto.getDescription(), itemUpdated.getDescription());
        assertEquals(itemUpdateDto.getAvailable(), itemUpdated.isAvailable());
        assertThrows(ForbiddenException.class, () -> itemService.update(itemUpdateDto, anotherId, itemId));
    }

    @Test
    public void testGetById() {
        UserCreateDto ownerRequestDto = new UserCreateDto("owner", "emailOwner");
        ItemCreateDto itemRequestDto = new ItemCreateDto("ItemName", "description", false, null);
        long ownerId = userService.create(ownerRequestDto).getId();
        long itemId = itemService.create(itemRequestDto, ownerId).getId();
        assertThrows(NotFoundException.class, () -> itemService.getById(0));
        ItemWIthCommentsDto item = itemService.getById(itemId);
        assertEquals(item.getId(), itemId);
    }

    @Test
    public void testSearch() {
        UserCreateDto ownerRequestDto = new UserCreateDto("owner", "emailOwner");
        ItemCreateDto itemRequestDto1 = new ItemCreateDto("search", "description", true, null);
        ItemCreateDto itemRequestDto2 = new ItemCreateDto("name", "text", true, null);
        long ownerId = userService.create(ownerRequestDto).getId();
        long itemNameEqualsSearchId = itemService.create(itemRequestDto1, ownerId).getId();
        long itemDescriptionEqualsTextId = itemService.create(itemRequestDto2, ownerId).getId();
        List<ItemDto> searchResultBySearch = itemService.search("SeArch");
        List<ItemDto> searchResultByText = itemService.search("TeXt");
        List<ItemDto> emptyResult = itemService.search("");
        assertEquals(itemNameEqualsSearchId, searchResultBySearch.get(0).getId());
        assertEquals(itemDescriptionEqualsTextId, searchResultByText.get(0).getId());
        assertTrue(emptyResult.isEmpty());
    }
}

