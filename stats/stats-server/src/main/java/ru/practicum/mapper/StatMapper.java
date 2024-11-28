package ru.practicum.mapper;

import ru.practicum.StatDto;
import ru.practicum.model.Stat;

public class StatMapper {

    public static StatDto toStatDto(Stat stat) {
        return StatDto.builder()
                .app(stat.getApp())
                .uri(stat.getUri())
                .ip(stat.getIp())
                .timestamp(stat.getTimestamp())
                .build();
    }

    public static Stat toStat(StatDto statDto) {
        return Stat.builder()
                .app(statDto.getApp())
                .uri(statDto.getUri())
                .ip(statDto.getIp())
                .timestamp(statDto.getTimestamp())
                .build();
    }
}
