package ru.practicum.shareit.booking.api;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_Id(long bookerId);

    List<Booking> findAllByBooker_IdAndStatusEquals(long bookerId, BookingStatus status);

    List<Booking> findAllByBooker_IdAndStatusEqualsAndEndIsBefore(long bookerId, BookingStatus status, LocalDateTime now, Sort sort);

    List<Booking> findAllByBooker_IdAndStatusEqualsAndStartIsAfter(
            long bookerId, BookingStatus status, LocalDateTime localDateTime, Sort sort);

    @Query("select b from Booking b where b.booker.id = :bookerId AND b.status = 'APPROVED' " +
            "AND b.start <= :now AND b.end >= :now")
    List<Booking> findCurrentByBooker(@Param("bookerId") long ownerId, @Param("now") LocalDateTime now, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = :ownerId")
    List<Booking> findAllByItemOwnerId(@Param("ownerId") long ownerId);

    @Query("select b from Booking b where b.item.owner.id = :ownerId AND b.status = :status")
    List<Booking> findByOwnerAndStatus(@Param("ownerId") long ownerId, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.status = 'APPROVED' AND b.end < :now ORDER BY b.end DESC")
    List<Booking> findPastByItemOwner(
            @Param("ownerId") long ownerId, @Param("now") LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id = :ownerId AND b.status = 'APPROVED' " +
            "AND b.start <= :now AND b.end >= :now")
    List<Booking> findCurrentByItemOwner(@Param("ownerId") long ownerId, @Param("now") LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = 'APPROVED'" +
            " AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findFutureByItemOwner(
            long ownerId, LocalDateTime now);

    List<Booking> findAllByItem_IdIn(Set<Long> itemId);

    Optional<Booking> findByBooker_IdAndItem_Id(long bookerId, long itemId);
}
