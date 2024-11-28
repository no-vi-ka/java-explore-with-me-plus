package ru.practicum.service;

import ru.practicum.ResponseStatDto;
import ru.practicum.StatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    StatDto saveRequest(StatDto statDto);

    List<ResponseStatDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
