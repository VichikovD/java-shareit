package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookingRepository bookingRepository;

    public static final LocalDateTime CURRENT = LocalDateTime.now();

    @BeforeEach
    public void beforeEach() {
        // owner id = 1L; booker id = 2L; distracter id = 3L;
        // item id = 1L
        // booking id = 1L, 2L, 3L; booking distracters id = 4L, 5L;

        User owner = User.builder()
                .id(null)
                .email("owner@email.com")
                .name("ownerName")
                .build();
        entityManager.persist(owner);

        Item item = Item.builder()
                .id(null)
                .owner(owner)
                .name("itemName")
                .description("itemDescription")
                .isAvailable(true)
                .itemRequest(null)
                .build();
        entityManager.persist(item);

        User booker = User.builder()
                .id(null)
                .email("booker@email.com")
                .name("bookerName")
                .build();
        entityManager.persist(booker);

        Booking bookingPast = Booking.builder()
                .id(null)
                .item(item)
                .booker(booker)
                .start(CURRENT.minusDays(3))
                .end(CURRENT.minusDays(2))
                .status(BookingStatus.APPROVED)
                .build();
        entityManager.persist(bookingPast);

        Booking bookingCurrent = Booking.builder()
                .id(null)
                .item(item)
                .booker(booker)
                .start(CURRENT.minusDays(1))
                .end(CURRENT.plusDays(1))
                .status(BookingStatus.APPROVED)
                .build();
        entityManager.persist(bookingCurrent);

        Booking bookingFuture = Booking.builder()
                .id(null)
                .item(item)
                .booker(booker)
                .start(CURRENT.plusDays(2))
                .end(CURRENT.plusDays(3))
                .status(BookingStatus.APPROVED)
                .build();
        entityManager.persist(bookingFuture);

        User bookerDistracter = User.builder()
                .id(null)
                .email("bookerDistracter@email.com")
                .name("bookerDistracterName")
                .build();
        entityManager.persist(bookerDistracter);

        Booking bookingPastDistracter = Booking.builder()
                .id(null)
                .item(item)
                .booker(bookerDistracter)
                .start(CURRENT.minusDays(5))
                .end(CURRENT.minusDays(4))
                .status(BookingStatus.APPROVED)
                .build();
        entityManager.persist(bookingPastDistracter);

        // current can be only 1

        Booking bookingFutureDistracter = Booking.builder()
                .id(null)
                .item(item)
                .booker(bookerDistracter)
                .start(CURRENT.plusDays(4))
                .end(CURRENT.plusDays(5))
                .status(BookingStatus.APPROVED)
                .build();
        entityManager.persist(bookingFutureDistracter);
    }

    @Test
    void findByIdAndOwnerIdOrBookerId() {
        Booking bookingByOwnerId = bookingRepository.findByIdAndOwnerIdOrBookerId(1L, 1L).get();
        Booking bookingByBookerId = bookingRepository.findByIdAndOwnerIdOrBookerId(1L, 2L).get();
        boolean isEmptyBookingByOtherUser = bookingRepository.findByIdAndOwnerIdOrBookerId(1L, 3L).isEmpty();

        assertThat(bookingByOwnerId.getId(), is(1L));
        assertThat(bookingByOwnerId.getItem().getOwner().getId(), is(1L));
        assertThat(bookingByBookerId.getId(), is(1L));
        assertThat(bookingByBookerId.getBooker().getId(), is(2L));
        assertThat(isEmptyBookingByOtherUser, is(true));
    }

    @Test
    void findAllPastBookingByBookerId() {
        /* это бы работало при native query + offset and limit а не offset/limit
        // to insure offset is working, let's create another past booking for same item by same booker
        User owner = User.builder()
                .id(1L)
                .email("owner@email.com")
                .name("ownerName")
                .build();
        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .name("itemName")
                .description("itemDescription")
                .isAvailable(true)
                .itemRequest(null)
                .build();
        User booker = User.builder()
                .id(2L)
                .email("booker@email.com")
                .name("bookerName")
                .build();
        Booking bookingPast = Booking.builder()
                .id(null)
                .item(item)
                .booker(booker)
                .start(CURRENT.minusDays(7))
                .end(CURRENT.minusDays(6))
                .status(BookingStatus.APPROVED)
                .build();
        entityManager.persist(bookingPast);
        int offset = 1;
        int limit = 2;*/
        int offset = 0;
        int limit = 2;
        PageRequest pageRequest = PageRequest.of((offset / limit), limit, Sort.Direction.DESC, "start");

        List<Booking> bookingList = bookingRepository.findAllPastBookingByBookerId(2L, pageRequest);
        Booking actualBooking = bookingList.get(0);

        assertThat(bookingList.size(), is(1));
        assertThat(actualBooking.getId(), is(1L));
    }

    @Test
    void findAllCurrentBookingByBookerId() {
        int offset = 0;
        int limit = 2;
        PageRequest pageRequest = PageRequest.of((offset / limit), limit, Sort.Direction.DESC, "start");

        List<Booking> bookingList = bookingRepository.findAllCurrentBookingByBookerId(2L, pageRequest);
        Booking actualBooking = bookingList.get(0);

        assertThat(bookingList.size(), is(1));
        assertThat(actualBooking.getId(), is(2L));
    }

    @Test
    void findAllFutureBookingByBookerId() {
        int offset = 0;
        int limit = 2;
        PageRequest pageRequest = PageRequest.of((offset / limit), limit, Sort.Direction.DESC, "start");

        List<Booking> bookingList = bookingRepository.findAllFutureBookingByBookerId(2L, pageRequest);
        Booking actualBooking = bookingList.get(0);

        assertThat(bookingList.size(), is(1));
        assertThat(actualBooking.getId(), is(3L));
    }

    @Test
    void findAllPastBookingByItemOwnerId() {
        int offset = 0;
        int limit = 4;
        PageRequest pageRequest = PageRequest.of((offset / limit), limit, Sort.Direction.DESC, "start");

        List<Booking> bookingList = bookingRepository.findAllPastBookingByItemOwnerId(1L, pageRequest);
        Booking actualBooking1 = bookingList.get(0);
        Booking actualBooking2 = bookingList.get(1);

        assertThat(bookingList.size(), is(2));
        assertThat(actualBooking1.getId(), is(1L));
        assertThat(actualBooking2.getId(), is(4L));
    }

    @Test
    void findAllCurrentBookingByItemOwnerId() {
        int offset = 0;
        int limit = 4;
        PageRequest pageRequest = PageRequest.of((offset / limit), limit, Sort.Direction.DESC, "start");

        List<Booking> bookingList = bookingRepository.findAllCurrentBookingByItemOwnerId(1L, pageRequest);
        Booking actualBooking1 = bookingList.get(0);

        assertThat(bookingList.size(), is(1));
        assertThat(actualBooking1.getId(), is(2L));
    }

    @Test
    void findAllFutureBookingByItemOwnerId() {
        int offset = 0;
        int limit = 4;
        PageRequest pageRequest = PageRequest.of((offset / limit), limit, Sort.Direction.DESC, "start");

        List<Booking> bookingList = bookingRepository.findAllFutureBookingByItemOwnerId(1L, pageRequest);
        Booking actualBooking1 = bookingList.get(0);
        Booking actualBooking2 = bookingList.get(1);

        assertThat(bookingList.size(), is(2));
        assertThat(actualBooking1.getId(), is(5L));
        assertThat(actualBooking2.getId(), is(3L));
    }

    @Test
    void findLastForDateTime() {
        // 5th booking is last for this LDT
        LocalDateTime ldt = CURRENT.plusDays(5);

        Booking booking = bookingRepository.findLastForDateTime(1L, ldt).get();

        assertThat(booking.getId(), is(5L));
    }

    @Test
    void findNextForDateTime() {
        // 2nd booking is next for this LDT
        LocalDateTime ldt = CURRENT.minusDays(2);

        Booking booking = bookingRepository.findNextForDateTime(1L, ldt).get();

        assertThat(booking.getId(), is(2L));
    }

    @Test
    void findAllLastForDateTime() {
        // 1,2,4 bookings are last for this LDT
        LocalDateTime ldt = CURRENT;
        Collection<Long> itemIdList = List.of(1L);

        List<Booking> bookingList = bookingRepository.findAllLastForDateTime(itemIdList, ldt);
        Booking actualBooking1 = bookingList.get(0);
        Booking actualBooking2 = bookingList.get(1);
        Booking actualBooking3 = bookingList.get(2);

        assertThat(bookingList.size(), is(3));
        assertThat(actualBooking1.getId(), is(2L));
        assertThat(actualBooking2.getId(), is(1L));
        assertThat(actualBooking3.getId(), is(4L));
    }

    @Test
    void findAllNextForDateTime() {
        // 3,5 bookings are last for this LDT
        LocalDateTime ldt = CURRENT;
        Collection<Long> itemIdList = List.of(1L);

        List<Booking> bookingList = bookingRepository.findAllNextForDateTime(itemIdList, ldt);
        Booking actualBooking1 = bookingList.get(0);
        Booking actualBooking2 = bookingList.get(1);

        assertThat(bookingList.size(), is(2));
        assertThat(actualBooking1.getId(), is(5L));
        assertThat(actualBooking2.getId(), is(3L));
    }

    @Test
    void countAllPastForItemByTime() {
        // Only 1 finished booking for this ldt for user 2L
        LocalDateTime ldt = CURRENT;

        long bookedQuantity = bookingRepository.countAllPastForItemByTime(1L, 2L, ldt);

        assertThat(bookedQuantity, is(1L));
    }

    @Test
    void countIntersectionInTime() {
        // Only 1 intersection
        LocalDateTime start = CURRENT.minusDays(1);
        LocalDateTime end = CURRENT.plusDays(1);

        long bookedQuantity = bookingRepository.countIntersectionInTime(start, end, 1L);

        assertThat(bookedQuantity, is(1L));
    }
}