package ru.practicum.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> createCompilation(@Valid @RequestBody
                                                            NewCompilationDto newCompilationDto) {
        log.info("Add new compilation with title = {}, pinned = {}, events = {}.", newCompilationDto.getTitle(),
                newCompilationDto.getPinned(), newCompilationDto.getEvents());
        return ResponseEntity.status(HttpStatus.CREATED).body(compilationService.createCompilation(newCompilationDto));
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable long compId) {
        log.info("Delete compilation by id: {}.", compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(
            @PathVariable long compId,
            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("Update category with id = {}, used pinned = {}, title = {}, events = {}.", compId,
                updateCompilationRequest.getPinned(), updateCompilationRequest.getTitle(),
                updateCompilationRequest.getEvents());
        return ResponseEntity.status(HttpStatus.OK)
                .body(compilationService.updateCompilation(compId, updateCompilationRequest));
    }
}
