package ru.practicum.client;

import ru.practicum.ResponseStatDto;
import ru.practicum.StatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {
    List<ResponseStatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

    StatDto hit(StatDto statDto);
}
