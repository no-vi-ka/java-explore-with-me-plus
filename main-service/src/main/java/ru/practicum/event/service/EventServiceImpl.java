package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.errors.exceptions.BadRequestException;
import ru.practicum.errors.exceptions.ForbiddenException;
import ru.practicum.errors.exceptions.NotFoundException;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventMapper eventMapper;

    @Override
    public EventFullDto add(EventNewDto newEvent, long userId) {
        LocalDateTime eventDate = newEvent.getEventDate();
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("Начало события ранее, чем через два часа: " + eventDate);
        }
        User user = userService.findById(userId);
        long categoryId = newEvent.getCategory();
        Category category = categoryService.findById(categoryId);
        Event event = eventMapper.toEvent(newEvent, category, user);
        return eventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getAllByUser(long userId, Pageable pageable) {
        return eventMapper.toEventShortDtoList(eventRepository.findAllByInitiator_Id(userId, pageable));
    }

    @Override
    public EventFullDto getByIdPrivate(long eventId, long userId) {
        return eventMapper.toFullDto(findByIdAndInitiator(eventId, userId));
    }

    @Override
    public EventFullDto updatePrivate(long userId, long eventId, EventUserUpdateDto eventUpdate) {
        Event event = findByIdAndInitiator(eventId, userId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Нельзя обновить опубликованное событие");
        }

        Long categoryId = eventUpdate.getCategory();
        if (categoryId != null) {
            Category category = categoryService.findById(categoryId);
            event.setCategory(category);
        }

        LocalDateTime eventDate = eventUpdate.getEventDate();
        if (eventDate != null) {
            if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ForbiddenException("Начало события должно быть не ранее, чем через два часа: " + eventDate);
            }
            event.setEventDate(eventDate);
        }

        EventUserUpdateDto.StateAction stateAction = eventUpdate.getStateAction();
        if (stateAction != null) {
            switch (stateAction) {
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                default -> throw new BadRequestException("Неизвестное действие: " + stateAction);
            }
        }

        Event updated = eventMapper.toEventFromEventUserUpdateDto(eventUpdate, event);

        return eventMapper.toFullDto(eventRepository.save(updated));
    }

    @Override
    public EventFullDto updateAdmin(long eventId, EventAdminUpdateDto eventUpdate) {
        Event event = findById(eventId);

        EventAdminUpdateDto.StateAction stateAction = eventUpdate.getStateAction();
        if (stateAction != null) {
            switch (stateAction) {
                case PUBLISH_EVENT -> {
                    if (!event.getState().equals(EventState.PENDING)) {
                        throw new ForbiddenException("Нельзя опубликовать событие, не находящееся в состоянии ожидания");
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    LocalDateTime eventDate = eventUpdate.getEventDate();
                    if (eventDate != null) {
                        if (eventDate.isBefore(event.getPublishedOn().plusHours(1))) {
                            throw new ForbiddenException(
                                    "Начало события должно быть не ранее, чем через час от даты публикации: " + eventDate);
                        }
                        event.setEventDate(eventDate);
                    }
                }
                case REJECT_EVENT -> {
                    if (event.getState().equals(EventState.PENDING)) {
                        event.setState(EventState.CANCELED);
                    } else {
                        throw new ForbiddenException("Нельзя отклонить опубликованное событие");
                    }
                }
                default -> throw new BadRequestException("Неизвестное действие: " + stateAction);
            }
        }

        Long categoryId = eventUpdate.getCategory();
        if (categoryId != null) {
            Category category = categoryService.findById(categoryId);
            event.setCategory(category);
        }

        Event updated = eventMapper.toEventFromEventAdminUpdateDto(eventUpdate, event);

        return eventMapper.toFullDto(eventRepository.save(updated));
    }

    @Override
    public List<EventFullDto> getAllByAdmin(EventAdminParam params) {
        return null;
    }

    @Override
    public List<EventShortDto> getAllPublic(EventPublicParam params) {
        return null;
    }

    @Override
    public EventFullDto getByIdPublic(long eventId) {
        return eventMapper.toFullDto(findById(eventId));
    }

    @Override
    public Event findById(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id: " + eventId + " не существует"));
    }

    private Event findByIdAndInitiator(long eventId, long initiatorId) {
        return eventRepository.findByIdAndInitiator_Id(eventId, initiatorId).orElseThrow(() ->
                new NotFoundException("Событие с id: " + eventId + " не существует"));
    }
}
