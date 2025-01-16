package ru.practicum.rating.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.rating.dto.NewRatingDto;
import ru.practicum.rating.dto.RatingDto;
import ru.practicum.rating.dto.UpdateRatingDto;
import ru.practicum.rating.service.RatingService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/ratings")
public class PrivateRatingController {
    private final RatingService ratingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RatingDto add(@PathVariable("userId") long userId,
                         @PathVariable("eventId") long eventId,
                         @RequestBody @Valid NewRatingDto newRatingDto) {
        return ratingService.create(userId, eventId, newRatingDto);
    }

    @PatchMapping("/{ratingId}")
    @ResponseStatus(HttpStatus.OK)
    public RatingDto update(@PathVariable("userId") long userId,
                            @PathVariable("eventId") long eventId,
                            @PathVariable("ratingId") long ratingId,
                            @RequestBody @Valid UpdateRatingDto updateRatingDto) {
        return ratingService.update(userId, eventId, ratingId, updateRatingDto);
    }

    @DeleteMapping("/{ratingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable("userId") long userId,
                       @PathVariable("eventId") long eventId,
                       @PathVariable("ratingId") long ratingId) {
        ratingService.delete(userId, eventId, ratingId);
    }
}
