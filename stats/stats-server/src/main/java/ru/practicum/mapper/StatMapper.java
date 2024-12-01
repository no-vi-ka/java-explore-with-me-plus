package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.StatDto;
import ru.practicum.model.Stat;

@Component
@Mapper(componentModel = "spring")
public interface StatMapper {
    StatDto toDto(Stat stat);

    Stat toEntity(StatDto statDto);
}