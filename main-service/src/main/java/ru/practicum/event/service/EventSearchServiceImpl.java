package ru.practicum.event.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.rating.dto.EventSearchByRatingParam;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EventSearchServiceImpl implements EventSearchService {
    EventRepository eventRepository;
    EventMapper eventMapper;

    @Transactional
    @Override
    public List<EventShortDto> searchMostLikedEvents(EventSearchByRatingParam eventSearchByRatingParam) {
        int limit = eventSearchByRatingParam.getLimit();
        List<Event> events = eventRepository.findMostLikedEvents(limit);
        return eventMapper.toEventShortDtoList(events);
    }
}
