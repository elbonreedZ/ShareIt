package ru.practicum.shareit.request.api;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestor_Id(long requestorId, Sort sort);

    List<ItemRequest> findAllByRequestor_IdNot(long requestorId, Sort sort);
}
