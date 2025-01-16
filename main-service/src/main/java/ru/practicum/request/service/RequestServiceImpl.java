package ru.practicum.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
    @Transactional
    public ParticipationRequestDto createParticipationRequest(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Событие с id=%d не найдено", eventId))
        );

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConditionsNotMetException("Нельзя участвовать в неопубликованном событии");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConditionsNotMetException("Инициатор события не может добавить запрос на участие в своём событии");
        }

        checkParticipantLimit(event.getParticipantLimit(), getConfirmedRequests(eventId));

        User requester = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId)));

        RequestStatus status = RequestStatus.PENDING;
        if (event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        } else if (!event.isRequestModeration()) {
            status = RequestStatus.CONFIRMED;
        }

        Request request = Request.builder()
                .event(event)
                .requester(requester)
                .status(status)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
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
        List<Request> foundRequests = requestRepository.findAllByRequester_Id(userId);
        return requestMapper.toDtoList(foundRequests);
    }

    @Override
    public List<ParticipationRequestDto> getAllByInitiatorIdAndEventId(long userId, long eventId) {
        List<Request> foundRequests = requestRepository.findAllByInitiatorIdAndEventId(userId, eventId);
        return requestMapper.toDtoList(foundRequests);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeEventRequestsStatusByInitiator(EventRequestStatusUpdateRequest updateRequest, long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Событие с id=%d не найдено", eventId))
        );

        List<Long> requestIds = updateRequest.getRequestIds();
        List<Request> foundRequests = requestRepository.findAllById(requestIds);
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();

        for (Request request : foundRequests) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConditionsNotMetException("Заявка должна находиться в ожидании");
            }
        }

        switch (updateRequest.getStatus()) {
            case CONFIRMED -> handleConfirmedRequests(event, foundRequests, result, confirmed, rejected);
            case REJECTED -> handleRejectedRequests(foundRequests, result, rejected);
        }

        result.setConfirmedRequests(confirmed);
        result.setRejectedRequests(rejected);

        return result;
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelParticipantRequest(long userId, long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Запрос на участие в событии с id запроса=%d не найден", requestId))
        );

        Long requesterId = request.getRequester().getId();
        if (!requesterId.equals(userId)) {
            throw new ConditionsNotMetException("Пользователь не является участником в запросе на участие в событии");
        }

        request.setStatus(RequestStatus.CANCELED);
        requestRepository.save(request);

        return requestMapper.toDto(request);
    }

    private void checkParticipantLimit(int participantLimit, int confirmedRequests) {
        if (confirmedRequests >= participantLimit && participantLimit != 0) {
            throw new ConditionsNotMetException("У события заполнен лимит участников");
        }
    }

    private int getConfirmedRequests(long eventId) {
        return requestRepository.findCountOfConfirmedRequestsByEventId(eventId);
    }

    private void updateStatus(RequestStatus status, List<Long> ids) {
        requestRepository.updateStatus(status, ids);
    }

    private void handleConfirmedRequests(Event event, List<Request> foundRequests, EventRequestStatusUpdateResult result, List<ParticipationRequestDto> confirmed, List<ParticipationRequestDto> rejected) {
        int confirmedRequests = getConfirmedRequests(event.getId());
        int participantLimit = event.getParticipantLimit();
        if (participantLimit == 0 || !event.isRequestModeration()) {
            result.setConfirmedRequests(requestMapper.toDtoList(foundRequests));
            return;
        }
        checkParticipantLimit(participantLimit, confirmedRequests);
        for (Request request : foundRequests) {
            if (confirmedRequests >= participantLimit) {
                rejected.add(requestMapper.toDto(request));
                continue;
            }
            request.setStatus(RequestStatus.CONFIRMED);
            confirmed.add(requestMapper.toDto(request));
            ++confirmedRequests;
        }
        List<Long> confirmedRequestIds = confirmed.stream().map(ParticipationRequestDto::getId).toList();
        updateStatus(RequestStatus.CONFIRMED, confirmedRequestIds);
    }

    private void handleRejectedRequests(List<Request> foundRequests, EventRequestStatusUpdateResult result, List<ParticipationRequestDto> rejected) {
        for (Request request : foundRequests) {
            request.setStatus(RequestStatus.REJECTED);
            rejected.add(requestMapper.toDto(request));
        }
        List<Long> rejectedRequestIds = rejected.stream().map(ParticipationRequestDto::getId).toList();
        updateStatus(RequestStatus.REJECTED, rejectedRequestIds);
    }
}