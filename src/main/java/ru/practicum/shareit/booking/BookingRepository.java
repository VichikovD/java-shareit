package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByIdAndItemOwnerId(long bookingId, long ownerId);

    @Query(value = "SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.id = ?1 AND (b.item.owner.id = ?2 OR b.booker.id = ?2)")
    Optional<Booking> findByIdAndOwnerIdOrBookerId(long bookingId, long bookerId);

    List<Booking> findAllByBookerId(long bookerId, Pageable pageRequest);

    @Query(value = "SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.end < NOW() AND b.booker.id = ?1 ")
    List<Booking> findAllPastBookingByBookerId(long bookerId, Pageable pageRequest);

    @Query(value = "SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE (NOW() BETWEEN b.start AND b.end) AND b.booker.id = ?1 ")
    List<Booking> findAllCurrentBookingByBookerId(long bookerId, Pageable pageRequest);

    @Query(value = "SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.start > NOW() AND b.booker.id = ?1 ")
    List<Booking> findAllFutureBookingByBookerId(long bookerId, Pageable pageRequest);

    List<Booking> findAllByBookerIdAndStatus(long bookerId, BookingStatus status, Pageable pageRequest);

    List<Booking> findAllByItemOwnerId(long ownerId, Pageable pageRequest);

    @Query(value = "SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.end < NOW() AND b.item.owner.id = ?1 ")
    List<Booking> findAllPastBookingByItemOwnerId(long ownerId, Pageable pageRequest);

    @Query(value = "SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE (NOW() BETWEEN b.start AND b.end) AND b.item.owner.id = ?1 ")
    List<Booking> findAllCurrentBookingByItemOwnerId(long ownerId, Pageable pageRequest);

    @Query(value = "SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.start > NOW() AND b.item.owner.id = ?1 ")
    List<Booking> findAllFutureBookingByItemOwnerId(long ownerId, Pageable pageRequest);

    List<Booking> findAllByItemOwnerIdAndStatus(long ownerId, BookingStatus status, Pageable pageRequest);

    @Query(value = "SELECT * " +
            "FROM bookings AS b " +
            "JOIN items AS i ON b.item_id = i.item_id " +
            "JOIN users AS u ON b.booker_id = u.user_id " +
            "WHERE b.start_date_time < ?2 AND b.item_id = ?1 AND b.status = 'APPROVED' " +
            "ORDER BY b.start_date_time DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<Booking> findLastForDateTime(long itemId, LocalDateTime time);

    @Query(value = "SELECT * " +
            "FROM bookings AS b " +
            "JOIN items AS i ON b.item_id = i.item_id " +
            "JOIN users AS u ON b.booker_id = u.user_id " +
            "WHERE b.start_date_time > ?2 AND b.item_id = ?1 AND b.status = 'APPROVED' " +
            "ORDER BY b.start_date_time ASC " +
            "LIMIT 1", nativeQuery = true)
    Optional<Booking> findNextForDateTime(long itemId, LocalDateTime time);

    @Query(value = "SELECT * " +
            "FROM bookings AS b " +
            "JOIN items AS i ON b.item_id = i.item_id " +
            "JOIN users AS u ON b.booker_id = u.user_id " +
            "WHERE (b.start_date_time < ?2) AND (b.item_id IN ?1) AND (b.status = 'APPROVED') " +
            "ORDER BY b.start_date_time DESC", nativeQuery = true)
    List<Booking> findAllLastForDateTime(Collection<Long> itemIdList, LocalDateTime time);

    @Query(value = "SELECT * " +
            "FROM bookings AS b " +
            "JOIN items AS i ON b.item_id = i.item_id " +
            "JOIN users AS u ON b.booker_id = u.user_id " +
            "WHERE (b.start_date_time > ?2) AND (b.item_id IN ?1) AND (b.status = 'APPROVED') " +
            "ORDER BY b.start_date_time DESC", nativeQuery = true)
    List<Booking> findAllNextForDateTime(Collection<Long> itemIdList, LocalDateTime time);

    @Query(value = "SELECT COUNT(b.booking_id) " +
            "FROM bookings AS b " +
            "JOIN items AS i ON b.item_id = i.item_id " +
            "JOIN users AS u ON b.booker_id = u.user_id " +
            "WHERE (b.end_date_time < :time) AND (b.item_id = :itemId) AND (b.booker_id = :bookerId) " +
            "AND (b.status = 'APPROVED') ", nativeQuery = true)
    long countAllPastForItemByTime(long itemId, long bookerId, LocalDateTime time);

    @Query(value = "SELECT COUNT(b.booking_id) " +
            "FROM bookings AS b " +
            "JOIN items AS i ON b.item_id = i.item_id " +
            "JOIN users AS u ON b.booker_id = u.user_id " +
            "WHERE (b.start_date_time < :end AND b.end_date_time > :start) " +
            "AND (b.item_id = :itemId) AND (b.status = 'APPROVED')", nativeQuery = true)
    int countIntersectionInTime(LocalDateTime start, LocalDateTime end, long itemId);
}
