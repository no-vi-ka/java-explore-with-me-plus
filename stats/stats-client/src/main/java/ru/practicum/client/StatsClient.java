package ru.practicum.client;

import org.springframework.http.ResponseEntity;
import ru.practicum.ResponseStatDto;
import ru.practicum.StatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {
    List<ResponseStatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

    ResponseEntity<StatDto> hit(StatDto statDto);
}