package ru.practicum.shareit.item.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface JpaItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwner_id(long ownerId);

    @Query("select i from Item i " +
            "where (lower(i.name) like lower(concat('%', ?1, '%')) " +
            " or lower(i.description) like lower(concat('%', ?1, '%'))) and i.available = true")
    List<Item> searchByNameAndDescription(String text);

    List<Item> findByRequest_IdIn(Set<Long> requestIds);

    List<Item> findByRequest_Id(long id);
}
