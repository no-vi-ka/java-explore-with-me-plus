package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
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

    //private final StatsClient statsClient;
    @GetMapping
    public List<EventShortDto> getAll(@RequestParam String text,
                                      @RequestParam List<Long> categories,
                                      @RequestParam boolean paid,
                                      @RequestParam LocalDateTime rangeStart,
                                      @RequestParam LocalDateTime rangeEnd,
                                      @RequestParam boolean onlyAvailable,
                                      @RequestParam EventPublicParam.EventSort sort,
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
        eventPublicParam.setPageable(PageRequest.of(from, size));
        log.info("Пришел Get запрос /events на получение всех пользователей");
        List<EventShortDto> events = eventService.getAllPublic(eventPublicParam);
        log.info("Отправлен ответ Get /events с телом: {}", events);
        return events;
    }

    @GetMapping("/{id}")
    public EventFullDto getById(@PathVariable long id, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String requestUri = request.getRequestURI();
        log.info("Пришел Get запрос /events/{id} с id: {}", id);
        EventFullDto event = eventService.getByIdPublic(id);
        log.info("Отправлен ответ Get /events/{id} с телом: {}", event);
        //statsClient.hit(new StatDto("ewm-main-service", requestUri, clientIp, LocalDateTime.now()));
        return event;
    }
}
