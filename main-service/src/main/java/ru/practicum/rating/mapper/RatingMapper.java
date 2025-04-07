package ru.practicum.rating.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.rating.dto.RatingDto;
import ru.practicum.rating.model.Rating;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RatingMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "eventId", source = "event.id")
    RatingDto toRatingDto(Rating rating);
}
