package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByEventId(long eventId);

    List<Request> findAllByRequesterId(long userId);


    @Query("""
    SELECT r
    FROM Request r
    JOIN Event e ON r.event.id = e.id
    WHERE e.initiator.id = ?1 AND e.id = ?2
    """)
    List<Request> findAllByInitiatorIdAndEventId(long userId, long eventId);
}
