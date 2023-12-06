package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @PersistenceContext
    EntityManager entityManager = null;

    Optional<Booking> findByIdAndItemOwnerId(long bookingId, long OwnerId);

    @Query(value = "SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.id = ?1 AND (b.item.owner.id = ?2 OR b.booker.id = ?2)")
    Optional<Booking> findByIdAndOwnerIdOrBookerId(long bookingId, long bookerId);

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    @Query(value = "SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.end < NOW() AND b.booker.id = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllPastBookingByBookerIdOrderByStartDesc(long bookerId);

    @Query(value = "SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE (NOW() BETWEEN b.start AND b.end) AND b.booker.id = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllCurrentBookingByBookerIdOrderByStartDesc(long bookerId);

    @Query(value = "SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.start > NOW() AND b.booker.id = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllFutureBookingByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    @Query(value = "SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.end < NOW() AND b.item.owner.id = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllPastBookingByItemOwnerIdOrderByStartDesc(long ownerId);

    @Query(value = "SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE (NOW() BETWEEN b.start AND b.end) AND b.item.owner.id = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllCurrentBookingByItemOwnerIdOrderByStartDesc(long ownerId);

    @Query(value = "SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.start > NOW() AND b.item.owner.id = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllFutureBookingByItemOwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, BookingStatus status);

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
            "ORDER BY b.start_date_time ASC", nativeQuery = true)
    List<Booking> findAllNextForDateTime(Collection<Long> itemIdList, LocalDateTime time);

    @Query(value = "SELECT COUNT(b.booking_id) " +
            "FROM bookings AS b " +
            "JOIN items AS i ON b.item_id = i.item_id " +
            "JOIN users AS u ON b.booker_id = u.user_id " +
            "WHERE (b.end_date_time < :time) AND (b.item_id = :itemId) AND (b.booker_id = :bookerId) AND (b.status = 'APPROVED') ", nativeQuery = true)
    long countAllPastForItemByTime(long itemId, long bookerId, LocalDateTime time);

}
