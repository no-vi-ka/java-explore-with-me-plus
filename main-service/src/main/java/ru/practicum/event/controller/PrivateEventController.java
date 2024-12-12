package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUserUpdateDto;
import ru.practicum.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class PrivateEventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@RequestBody @Valid EventNewDto newEvent, @PathVariable long userId) {
        log.info("Пришел Post запрос /users/{}/events с телом: {}", userId, newEvent);
        EventFullDto event = eventService.add(newEvent, userId);
        log.info("Отправлен ответ Post /users/{}/events с телом: {}",userId, event);
        return event;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllByUser(@RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "10") int size, @PathVariable long userId) {
        log.info("Пришел Get запрос /users/{}/events?from={}&size={}", userId, from,  size);
        List<EventShortDto> events = eventService.getAllByUser(userId, PageRequest.of(from, size));
        log.info("Отправлен ответ Get /users/{}/events?from={}&size={} с телом: {}",userId, from, size, events);
        return events;
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getById(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Пришел Get запрос /users/{}/events/{}", userId, eventId);
        EventFullDto event = eventService.getByIdPrivate(eventId, userId);
        log.info("Отправлен ответ Get /users/{}/events/{} с телом: {}", userId, eventId, event);
        return event;
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto update(@PathVariable long userId, @PathVariable long eventId,
                               @RequestBody @Valid EventUserUpdateDto eventUpdate) {
        log.info("Пришел Patch запрос /users/{}/events/{} с телом: {}",
                userId, eventId, eventUpdate);
        EventFullDto event = eventService.updatePrivate(userId, eventId, eventUpdate);
        log.info("Отправлен ответ Patch /users/{}/events/{} с телом: {}", userId, eventId, event);
        return event;
    }
}
