package ru.practicum.request.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class PrivateRequestController {
    private final RequestService requestService;

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable(name = "userId") @Positive long userId,
                                          @RequestParam(name = "eventId") @Positive long eventId) {
        log.info("Пришел POST запрос /users/{}/requests?eventId={}", userId, eventId);
        var response = requestService.createParticipationRequest(userId, eventId);
        log.info("Отправлен ответ POST /users/{}/requests?eventId={} с содержимым: {}", userId, eventId, response);
        return response;
    }

    @GetMapping("/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getAllByParticipantId(@PathVariable(name = "userId") @Positive long userId) {
        log.info("Пришел GET запрос /users/{}/requests", userId);
        var response = requestService.getAllByParticipantId(userId);
        log.info("Отправлен ответ POST /users/{}/requests с содержимым: {}", userId, response);
        return response;
    }

    @GetMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getAllEventsOfInitiator(@PathVariable(name = "userId") @Positive long userId,
                                                                         @PathVariable(name = "eventId") @Positive long eventId) {
        log.info("Пришел GET запрос /users/{}/events/{}/requests", userId, eventId);
        var response = requestService.getAllByInitiatorIdAndEventId(userId, eventId);
        log.info("Отправлен ответ POST /users/{}/events/{}/requests с содержимым: {}", userId, eventId, response);
        return response;
    }

    @PatchMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult changeEventRequestsStatus (@PathVariable(name = "userId") @Positive long userId,
                                                                     @PathVariable(name = "eventId") @Positive long eventId,
                                                                     @RequestBody @Valid EventRequestStatusUpdateRequest updateRequest) {
        log.info("Пришел PATCH запрос /users/{}/events/{}/requests с телом запроса {}", userId, eventId, updateRequest);
        var response = requestService.changeEventRequestsStatusByInitiator(updateRequest, userId, eventId);
        log.info("Отправлен ответ PATCH /users/{}/events/{}/requests с содержимым: {}", userId, eventId, response);
        return response;
    }
}