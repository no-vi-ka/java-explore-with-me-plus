package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.errors.exceptions.NotFoundException;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;
    private final EventService eventService;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> eventList = new ArrayList<>();
        if (newCompilationDto.getEvents() != null) {
            eventList = eventService.findAllByIdIn(newCompilationDto.getEvents());
        }
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);
        compilation.setEvents(eventList);
        compilationRepository.save(compilation);
        CompilationDto toReturn = compilationMapper.toCompilationDto(compilation);
        List<EventShortDto> eventsToSet = eventMapper.toEventShortDtoList(eventList);
        toReturn.setEvents(eventsToSet);
        return toReturn;
    }

    @Override
    public void deleteCompilation(long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id = " + compId + " not found.");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilationFromTable = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation with id = " + compId + " not found."));
        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            compilationFromTable.setEvents(eventService.findAllByIdIn(updateCompilationRequest.getEvents()));
        }
        if (updateCompilationRequest.getPinned() != null)
            compilationFromTable.setPinned(updateCompilationRequest.getPinned());
        if (updateCompilationRequest.getTitle() != null)
            compilationFromTable.setTitle(updateCompilationRequest.getTitle());
        compilationRepository.save(compilationFromTable);
        CompilationDto toReturn = compilationMapper.toCompilationDto(compilationFromTable);
        List<EventShortDto> eventsToSet = eventMapper.toEventShortDtoList(compilationFromTable.getEvents());
        toReturn.setEvents(eventsToSet);
        return toReturn;
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean isPinned, Integer from, Integer size) {
        List<Compilation> compilations = compilationRepository.findAllByPinned(isPinned, PageRequest.of(from / size, size));

        List<CompilationDto> compilationDtoList = new ArrayList<>();
        for (Compilation compilation : compilations) {
            List<Event> eventList = eventService.findAllByIdIn(compilation.getEvents().stream().map(Event::getId)
                    .collect(Collectors.toList()));
            List<EventShortDto> eventsToSet = eventMapper.toEventShortDtoList(eventList);
            CompilationDto compilationDto = compilationMapper.toCompilationDto(compilation);
            compilationDto.setEvents(eventsToSet);
            compilationDtoList.add(compilationDto);
        }
        return compilationDtoList;
    }

    @Override
    public CompilationDto getCompilationById(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation with id = " + compId + " not found."));
        List<Event> eventList = eventService.findAllByIdIn(compilation.getEvents().stream().map(Event::getId)
                .collect(Collectors.toList()));
        List<EventShortDto> eventsToSet = eventMapper.toEventShortDtoList(eventList);
        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilation);
        compilationDto.setEvents(eventsToSet);
        return compilationDto;
    }
}
