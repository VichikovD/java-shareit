package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "item_requests")
public class ItemRequest {
    @Id
    @Column(name = "item_request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "requesting_user_id")
    User requestingUser;

    @Column(name = "description")
    String description;

    @Column(name = "created")
    LocalDateTime created;

    /*@OneToMany(mappedBy = "itemRequest")
    Set<Item> itemResponses;*/
}
