package ru.practicum.request.service;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.errors.exceptions.ConditionsNotMetException;
import ru.practicum.errors.exceptions.NotFoundException;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    UserRepository userRepository;
    EventRepository eventRepository;
    RequestRepository requestRepository;
    RequestMapper requestMapper;

    @Override
    public ParticipationRequestDto createParticipationRequest(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Событие с id=%d не найдено", eventId))
        );

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConditionsNotMetException("Нельзя участвовать в неопубликованном событии");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConditionsNotMetException("Инициатор события не может добавить запрос на участие в своём событии");
        }

        List<Request> eventRequests = requestRepository.findAllByEventId(eventId);
        if (eventRequests.size() >= event.getParticipantLimit()) {
            throw new ConditionsNotMetException("У события заполнен лимит участников");
        }

        User requester = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId)));
        Request request = Request.builder()
                .event(event)
                .requester(requester)
                .status(event.isRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED)
                .created(LocalDateTime.now())
                .build();

        try {
            request = requestRepository.save(request);
        } catch (DataIntegrityViolationException e) {
            throw new ConditionsNotMetException("Нельзя добавить повторный запрос на участие в событии");
        }

        return requestMapper.toDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getAllByParticipantId(long userId) {
        List<Request> foundRequests = requestRepository.findAllByRequesterId(userId);
        return requestMapper.toDtoList(foundRequests);
    }

    @Override
    public List<ParticipationRequestDto> getAllByInitiatorIdAndEventId(long userId, long eventId) {
        List<Request> foundRequests = requestRepository.findAllByInitiatorIdAndEventId(userId, eventId);
        return requestMapper.toDtoList(foundRequests);
    }

    @Override
    public EventRequestStatusUpdateResult changeEventRequestsStatusByInitiator(EventRequestStatusUpdateRequest updateRequest, long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Событие с id=%d не найдено", eventId))
        );

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
        }

        // TODO event.getState().equals(EventState.)

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConditionsNotMetException("Инициатор события не соответствует id инициатора в запросе");
        }

        List<Long> requestIds = updateRequest.getRequestIds();
        List<Request> foundRequests = requestRepository.findAllById(requestIds);

        // TODO смотреть api swagger
        return null;
    }

    @Override
    public ParticipationRequestDto cancelParticipantRequest(long userId, long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Запрос на участие в событии с id запроса=%d не найден", requestId))
        );

        Long requesterId = request.getRequester().getId();
        if (!requesterId.equals(userId)) {
            throw new ConditionsNotMetException("Пользователь не является участником в запросе на участие в событии");
        }

        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            request.setStatus(RequestStatus.PENDING);
            requestRepository.save(request);
        }

        return requestMapper.toDto(request);
    }
}