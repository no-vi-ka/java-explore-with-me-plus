package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ResponseStatDto;
import ru.practicum.StatDto;
import ru.practicum.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class StatsController {
    private final StatsClient statsClient;

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseStatDto> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                          @RequestParam(defaultValue = "") List<String> uris,
                                          @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Пришел GET/stats запрос");
        return statsClient.getStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatDto saveHitRequest(@RequestBody @Valid StatDto statDto) {
        log.info("Пришел POST/hit запрос с телом {}", statDto);
        return statsClient.hit(statDto);
    }
}