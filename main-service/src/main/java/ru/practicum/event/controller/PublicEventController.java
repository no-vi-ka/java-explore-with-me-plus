package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatDto;
import ru.practicum.client.StatsClient;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventPublicParam;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
public class PublicEventController {

    private final EventService eventService;
    private final StatsClient statsClient;

    @GetMapping
    public List<EventShortDto> getAll(@RequestParam(required = false) String text,
                                      @RequestParam(required = false) List<Long> categories,
                                      @RequestParam(required = false) Boolean paid,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                      @RequestParam(required = false) Boolean onlyAvailable,
                                      @RequestParam(required = false) EventPublicParam.EventSort sort,
                                      @RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String requestUri = request.getRequestURI();
        EventPublicParam eventPublicParam = new EventPublicParam();
        eventPublicParam.setText(text);
        eventPublicParam.setCategories(categories);
        eventPublicParam.setPaid(paid);
        eventPublicParam.setRangeStart(rangeStart);
        eventPublicParam.setRangeEnd(rangeEnd);
        eventPublicParam.setOnlyAvailable(onlyAvailable);
        eventPublicParam.setSort(sort);
        eventPublicParam.setFrom(from);
        eventPublicParam.setSize(size);
        List<EventShortDto> events = eventService.getAllPublic(eventPublicParam);
        statsClient.hit(new StatDto("ewm-main-service", requestUri, clientIp, LocalDateTime.now()));
        return events;
    }

    @GetMapping("/{id}")
    public EventFullDto getById(@PathVariable long id, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String requestUri = request.getRequestURI();
        return eventService.getByIdPublic(id, new StatDto("ewm-main-service", requestUri, clientIp,
                LocalDateTime.now()));
    }
}
