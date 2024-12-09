package ru.practicum.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;

import java.util.List;

public interface EventService {
    EventFullDto add(EventNewDto newEvent, long userId);

    List<EventShortDto> getAllByUser(long userId, Pageable pageable);

    EventFullDto getByIdPrivate(long userId, long eventId);

    EventFullDto updatePrivate(long userId, long eventId, EventUserUpdateDto eventUpdate);

    EventFullDto updateAdmin(long eventId, EventAdminUpdateDto eventUpdate);

    List<EventFullDto> getAllByAdmin(EventAdminParam params);

    List<EventShortDto> getAllPublic(EventPublicParam params);

    EventFullDto getByIdPublic(long eventId);

    Event findById(long eventId);

}
