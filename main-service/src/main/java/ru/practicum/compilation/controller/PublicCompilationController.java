package ru.practicum.compilation.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getAllCompilations(
            @RequestParam(required = false, defaultValue = "false") String pinned,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Get all compilations with params: from = {}, size = {}.", from, size);
        return ResponseEntity.status(HttpStatus.OK).body(compilationService.getAllCompilations(
                Boolean.valueOf(pinned), from, size));
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilationById(@PathVariable long compId) {
        log.info("Get compilation by id = {}.", compId);
        return ResponseEntity.status(HttpStatus.OK).body(compilationService.getCompilationById(compId));
    }
}
