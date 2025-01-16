package ru.practicum.event.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventSearchService;
import ru.practicum.rating.dto.EventSearchByRatingParam;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events/top-liked")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PublicEventSearchController {
    EventSearchService eventSearchService;

    @GetMapping
    public List<EventShortDto> searchByRating(
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        EventSearchByRatingParam param = new EventSearchByRatingParam();
        param.setLimit(limit);
        return eventSearchService.searchMostLikedEvents(param);
    }
}
