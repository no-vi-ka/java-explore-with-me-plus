package ru.practicum.event.service;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ResponseStatDto;
import ru.practicum.StatDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.client.StatsClient;
import ru.practicum.errors.exceptions.ConditionsNotMetException;
import ru.practicum.errors.exceptions.ForbiddenException;
import ru.practicum.errors.exceptions.NotFoundException;
import ru.practicum.errors.exceptions.ValidationException;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;

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
        List<Event> events = eventRepository.findAllByInitiator_Id(userId, pageable);
        return eventMapper.toEventShortDtoList(events);
    }

    @Override
    public EventFullDto getByIdPrivate(long eventId, long userId) {
        Event event = findByIdAndInitiator(eventId, userId);
        return eventMapper.toFullDto(event);
    }

    @Override
    public EventFullDto updatePrivate(long userId, long eventId, EventUserUpdateDto eventUpdate) {
        Event event = findByIdAndInitiator(eventId, userId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConditionsNotMetException("Нельзя обновить опубликованное событие");
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
                case PUBLISH_EVENT -> handlePublishEvent(event, eventUpdate);
                case REJECT_EVENT -> handleRejectEvent(event);
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
        List<Long> users = params.getUsers();
        BooleanExpression byUsers = (users != null && !users.isEmpty())
                ? QEvent.event.initiator.id.in(users) : null;

        List<EventState> states = params.getStates();
        BooleanExpression byStates = (states != null && !states.isEmpty())
                ? QEvent.event.state.in(states) : null;

        List<Long> categories = params.getCategories();
        BooleanExpression byCategories = (categories != null && !categories.isEmpty())
                ? QEvent.event.category.id.in(params.getCategories()) : null;

        BooleanExpression byEventDate = (params.getRangeStart() != null && params.getRangeEnd() != null)
                ? QEvent.event.eventDate.between(params.getRangeStart(), params.getRangeEnd()) : null;

        Predicate predicate = ExpressionUtils.allOf(byUsers, byStates, byCategories, byEventDate);

        List<Event> events = (predicate != null)
                ? eventRepository.findAll(predicate, params.getPageable()).toList()
                : eventRepository.findAll(params.getPageable()).toList();

        return eventMapper.toEventFullDtoList(events);
    }

    @Override
    public List<EventShortDto> getAllPublic(EventPublicParam params) {
        if (params.getRangeEnd() != null && params.getRangeStart() != null &&
                params.getRangeEnd().isBefore(params.getRangeStart())) {
            throw new ValidationException("Параметр rangeEnd должен быть позже rangeStart");
        }
        BooleanExpression byState = QEvent.event.state.eq(EventState.PUBLISHED);

        String text = params.getText();
        BooleanExpression byText = (text != null && !text.isEmpty()) ?
                QEvent.event.description.containsIgnoreCase(text)
                        .or(QEvent.event.annotation.containsIgnoreCase(text)) : null;

        BooleanExpression byEventDate = (params.getRangeStart() != null && params.getRangeEnd() != null)
                ? QEvent.event.eventDate.between(params.getRangeStart(), params.getRangeEnd())
                : QEvent.event.eventDate.after(LocalDateTime.now());

        List<Long> categories = params.getCategories();
        BooleanExpression byCategories = (categories != null && !categories.isEmpty())
                ? QEvent.event.category.id.in(params.getCategories()) : null;

        Boolean paid = params.getPaid();
        BooleanExpression byPaid = (paid != null) ? QEvent.event.paid.eq(paid) : null;

        /*Boolean onlyAvailable = params.getOnlyAvailable();
        BooleanExpression byParticipantLimit = (onlyAvailable != null && onlyAvailable == true) ?
                QEvent.event.participantLimit.gt(confirmedRequests) : null*/

        Predicate predicate = ExpressionUtils.allOf(byState, byText, byPaid, byCategories, byEventDate);

        Pageable pageable = toPageable(params.getSort(), params.getFrom(), params.getSize());

        List<Event> events = (predicate != null)
                ? eventRepository.findAll(predicate, pageable).toList()
                : eventRepository.findAll(pageable).toList();

        List<EventShortDto> eventShorts = eventMapper.toEventShortDtoList(events);

        return applyViewsToEvents(eventShorts);
    }

    @Override
    public EventFullDto getByIdPublic(long eventId, StatDto statDto) {
        Event event = findById(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие с id: " + eventId + " не найдено");
        }

        EventFullDto eventFullDto = eventMapper.toFullDto(event);
        List<ResponseStatDto> stats = statsClient.getStats(LocalDateTime.now().minusMonths(1), LocalDateTime.now(),
                List.of(statDto.getUri()), true);

        if (!stats.isEmpty()) {
            eventFullDto.setViews(stats.get(0).getHits());
        }

        statsClient.hit(statDto);

        return eventFullDto;
    }

    private Event findById(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id: " + eventId + " не существует"));
    }

    private Event findByIdAndInitiator(long eventId, long initiatorId) {
        return eventRepository.findByIdAndInitiator_Id(eventId, initiatorId).orElseThrow(() ->
                new NotFoundException("Событие с id: " + eventId + " не существует"));
    }

    private Pageable toPageable(EventPublicParam.EventSort eventSort, int from, int size) {
        if (eventSort == null) {
            eventSort = EventPublicParam.EventSort.EVENT_DATE;
        }
        Sort sort = switch (eventSort) {
            case EVENT_DATE -> Sort.by(Sort.Direction.DESC, "eventDate");
            case VIEWS -> Sort.by(Sort.Direction.DESC, "views");
        };
        return PageRequest.of(from, size, sort);
    }

    private List<EventShortDto> applyViewsToEvents(List<EventShortDto> events) {
        Map<String, EventShortDto> uriToEventMap = events.stream()
                .collect(Collectors.toMap(
                        event -> UriComponentsBuilder.fromUriString("/events")
                                .pathSegment(String.valueOf(event.getId()))
                                .toUriString(),
                        Function.identity()
                ));

        List<ResponseStatDto> stats = statsClient.getStats(
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now(),
                new ArrayList<>(uriToEventMap.keySet()),
                true
        );

        for (ResponseStatDto stat : stats) {
            EventShortDto dto = uriToEventMap.get(stat.getUri());
            if (dto != null) {
                dto.setViews(stat.getHits());
            }
        }

        return new ArrayList<>(uriToEventMap.values());
    }

    private void handlePublishEvent(Event event, EventAdminUpdateDto eventUpdate) {
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ConditionsNotMetException("Нельзя опубликовать событие, не находящееся в состоянии ожидания");
        }
        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        LocalDateTime eventDate = eventUpdate.getEventDate();
        if (eventDate != null) {
            if (eventDate.isBefore(event.getPublishedOn().plusHours(1))) {
                throw new ConditionsNotMetException(
                        "Начало события должно быть не ранее, чем через час от даты публикации: " + eventDate);
            }
            event.setEventDate(eventDate);
        }
    }

    private void handleRejectEvent(Event event) {
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ConditionsNotMetException("Нельзя отклонить опубликованное событие");
        }
        event.setState(EventState.CANCELED);
    }

}