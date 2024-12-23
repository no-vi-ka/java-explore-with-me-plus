package ru.practicum.rating.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.rating.dto.NewRatingDto;
import ru.practicum.rating.dto.RatingDto;
import ru.practicum.rating.model.Rating;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RatingMapper {
    Rating toRating(NewRatingDto newRatingDto);

    @Mapping(target = "userId", source = "rating.user.id")
    @Mapping(target = "eventId", source = "rating.event.id")
    List<RatingDto> toRatingDtoList(List<Rating> ratings);

    @Mapping(target = "userId", source = "rating.user.id")
    @Mapping(target = "eventId", source = "rating.event.id")
    RatingDto toRatingDto(Rating rating);
}
