package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;

    @Column(name = "start_date_time")
    private LocalDateTime start;

    @Column(name = "end_date_time")
    private LocalDateTime end;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private BookingStatus status;
}
