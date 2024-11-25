package ru.practicum.shareit.item.api;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItem_Id(long item);

    List<Comment> findAllByItem_IdIn(Set<Long> itemId);
}
