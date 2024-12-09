package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventAdminParam;
import ru.practicum.event.dto.EventAdminUpdateDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.EventState;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getAll(@RequestParam List<Long> users,
                                     @RequestParam List<EventState> states,
                                     @RequestParam List<Long> categories,
                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size) {
        EventAdminParam eventAdminParam = new EventAdminParam();
        eventAdminParam.setUsers(users);
        eventAdminParam.setStates(states);
        eventAdminParam.setCategories(categories);
        eventAdminParam.setRangeStart(rangeStart);
        eventAdminParam.setRangeEnd(rangeEnd);
        eventAdminParam.setPageable(PageRequest.of(from, size));
        return eventService.getAllByAdmin(eventAdminParam);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto update(@PathVariable long eventId, @RequestBody @Valid EventAdminUpdateDto eventUpdate) {
        log.info("Пришел Patch запрос /admin/events/{} с телом: {}",eventId, eventUpdate);
        EventFullDto event = eventService.updateAdmin(eventId, eventUpdate);
        log.info("Отправлен ответ Patch /admin/events/{} с телом: {}", eventId, event);
        return event;
    }
}
