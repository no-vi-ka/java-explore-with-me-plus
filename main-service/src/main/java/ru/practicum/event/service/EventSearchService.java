package ru.practicum.event.service;

import ru.practicum.event.dto.EventShortDto;
import ru.practicum.rating.dto.EventSearchByRatingParam;

import java.util.List;

public interface EventSearchService {
    List<EventShortDto> searchMostLikedEvents(EventSearchByRatingParam eventSearchByRatingParam);
}
