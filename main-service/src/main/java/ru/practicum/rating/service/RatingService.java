package ru.practicum.rating.service;

import ru.practicum.rating.dto.*;

import java.util.List;

public interface RatingService {
    List<RatingDto> getAllMarksByUserId(long userId, RatingParam ratingParam);

    RatingDto addRatingMark(long userId, NewRatingDto newRatingDto);

    RatingDto updateRatingMark(long userId, UpdateRatingParam updateRatingParam);

    void removeRatingMark(long userId, long ratingId);
}
