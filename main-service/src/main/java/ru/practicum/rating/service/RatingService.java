package ru.practicum.rating.service;

import ru.practicum.rating.dto.NewRatingDto;
import ru.practicum.rating.dto.RatingDto;
import ru.practicum.rating.dto.RatingParam;
import ru.practicum.rating.dto.UpdateRatingDto;

import java.util.List;

public interface RatingService {
    List<RatingDto> getAllMarksByUserId(long userId, RatingParam ratingParam);

    RatingDto addRatingMark(long userId, NewRatingDto newRatingDto);

    RatingDto updateRatingMark(long userId, long ratingId, UpdateRatingDto updateRatingDto);

    void removeRatingMark(long userId, long ratingId);
}
