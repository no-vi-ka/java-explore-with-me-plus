package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    Optional<Event> findByIdAndInitiator_Id(long eventId, long initiatorId);

    List<Event> findAllByInitiator_Id(long initiatorId, Pageable pageable);

    boolean existsByCategory_Id(long categoryId);

    List<Event> findAllByIdIn(List<Long> ids);

    @Query(value = """
            SELECT e
            FROM Event e
            JOIN Rating r ON e.id = r.event.id
            WHERE r.mark = 'LIKE'
            GROUP BY e.id
            ORDER BY COUNT(r.id) DESC
            LIMIT ?1
            """)
    List<Event> findMostLikedEvents(int limit);
}
