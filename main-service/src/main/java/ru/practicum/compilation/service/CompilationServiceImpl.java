package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationParam;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.errors.exceptions.NotFoundException;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;

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
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> eventList = new ArrayList<>();
        if (newCompilationDto.getEvents() != null) {
            eventList = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        }
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);
        compilation.setEvents(eventList);
        compilationRepository.save(compilation);
        return mapToDto(compilation, compilation.getEvents());
    }

    @Override
    @Transactional
    public void deleteCompilation(long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id = " + compId + " not found.");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilationFromTable = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation with id = " + compId + " not found."));
        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            compilationFromTable.setEvents(eventRepository.findAllByIdIn(updateCompilationRequest.getEvents()));
        }
        if (updateCompilationRequest.getPinned() != null)
            compilationFromTable.setPinned(updateCompilationRequest.getPinned());
        if (updateCompilationRequest.getTitle() != null)
            compilationFromTable.setTitle(updateCompilationRequest.getTitle());
        compilationRepository.save(compilationFromTable);
        return mapToDto(compilationFromTable, compilationFromTable.getEvents());
    }

    public List<CompilationDto> getAllCompilations(CompilationParam param) {
        Boolean isPinned = param.getIsPinned();
        int from = param.getFrom();
        int size = param.getSize();
        List<Compilation> compilations = compilationRepository.findAllByPinned(isPinned, PageRequest.of(from / size, size));

        List<CompilationDto> compilationDtoList = new ArrayList<>();
        for (Compilation compilation : compilations) {
            List<Event> eventList = eventRepository.findAllByIdIn(compilation.getEvents().stream().map(Event::getId)
                    .collect(Collectors.toList()));
            CompilationDto compilationDto = mapToDto(compilation, eventList);
            compilationDtoList.add(compilationDto);
        }
        return compilationDtoList;
    }

    @Override
    public CompilationDto getCompilationById(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation with id = " + compId + " not found."));
        List<Event> eventList = eventRepository.findAllByIdIn(compilation.getEvents().stream().map(Event::getId)
                .collect(Collectors.toList()));

        return mapToDto(compilation, eventList);
    }

    private CompilationDto mapToDto(Compilation compilation, List<Event> eventList) {
        List<EventShortDto> eventsToSet = eventMapper.toEventShortDtoList(eventList);
        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilation);
        compilationDto.setEvents(eventsToSet);
        return compilationDto;
    }
}
