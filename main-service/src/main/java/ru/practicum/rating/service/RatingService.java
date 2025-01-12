package ru.practicum.rating.service;

import ru.practicum.rating.dto.NewRatingDto;
import ru.practicum.rating.dto.RatingDto;
import ru.practicum.rating.dto.UpdateRatingDto;

public interface RatingService {
    RatingDto create(long userId, long eventId, NewRatingDto newRatingDto);

    RatingDto update(long userId, long eventId, long ratingId, UpdateRatingDto updateRatingDto);

    void delete(long userId, long eventId, long ratingId);
}
