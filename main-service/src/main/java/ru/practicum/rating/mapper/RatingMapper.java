package ru.practicum.rating.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.rating.dto.NewRatingDto;
import ru.practicum.rating.dto.RatingDto;
import ru.practicum.rating.model.Rating;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RatingMapper {
    Rating toRating(NewRatingDto newRatingDto);

    List<RatingDto> toRatingDtoList(List<Rating> ratings);

    RatingDto toRatingDto(Rating rating);
}
