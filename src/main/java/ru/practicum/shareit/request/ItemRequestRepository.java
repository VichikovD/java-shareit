package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestingUserId(long requestingUser);

    @Query(value = "SELECT * " +
            "FROM item_requests AS ir " +
            "JOIN users AS u ON ir.requesting_user_id = u.user_id " +
            "WHERE u.user_id <> :userId " +
            "ORDER BY ir.created DESC " +
            "LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<ItemRequest> getAllWithOffsetAndLimit(long userId, long limit, long offset);
}
