package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByEvent_Id(long eventId);

    List<Request> findAllByRequester_Id(long userId);


    @Query("""
    SELECT r
    FROM Request r
    JOIN Event e ON r.event.id = e.id
    WHERE e.initiator.id = ?1 AND e.id = ?2
    """)
    List<Request> findAllByInitiatorIdAndEventId(long userId, long eventId);

    @Query("select count(r) from Request r where r.event.id = :eventId and r.status = 'CONFIRMED'")
    int findCountOfConfirmedRequestsByEventId(long eventId);

    @Query("select r from Request r where r.event.id IN :eventIds and status = :status")
    List<Request> findAllByEvent_IdInAndStatusEquals(List<Long> eventIds, RequestStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE Request r SET r.status = :status WHERE r.id IN :ids")
    void updateStatus(RequestStatus status, List<Long> ids);
}
