package ru.practicum.rating.service;

import jakarta.validation.Valid;
import ru.practicum.rating.dto.NewRatingDto;
import ru.practicum.rating.dto.RatingDto;
import ru.practicum.rating.dto.RatingParam;
import ru.practicum.rating.dto.UpdateRatingDto;

import java.util.List;

public interface RatingService {
    List<RatingDto> getAllMarksByUserId(long userId, RatingParam ratingParam);

    RatingDto addRatingMark(long userId, @Valid NewRatingDto newRatingDto);

    RatingDto updateRatingMark(long userId, @Valid UpdateRatingDto updateRatingDto);

    void removeRatingMark(long userId, long ratingId);
}
