package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.StatDto;
import ru.practicum.model.Stat;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StatMapper {
    StatDto toDto(Stat stat);

    Stat toEntity(StatDto statDto);
}