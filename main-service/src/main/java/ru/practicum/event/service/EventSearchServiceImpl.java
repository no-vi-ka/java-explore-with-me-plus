package ru.practicum.event.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
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

    @Override
    public List<EventShortDto> searchMostLikedEvents(EventSearchByRatingParam eventSearchByRatingParam) {
        PageRequest pageRequest = PageRequest.of(0, eventSearchByRatingParam.getLimit());
        Page<Event> eventsPage = eventRepository.findMostLikedEvents(pageRequest);
        List<Event> events = eventsPage.getContent();
        return eventMapper.toEventShortDtoList(events);
    }
}
