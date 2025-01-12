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

    //добавить лайк
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RatingDto add(@PathVariable("userId") long userId,
                         @PathVariable("eventId") long eventId,
                         @RequestBody @Valid NewRatingDto newRatingDto) {
        log.info("Add new mark: '{}' from user '{}' to event '{}'", newRatingDto.getMark(), userId, eventId);
        return ratingService.create(userId, eventId, newRatingDto);
    }

    //изменить rating
    @PatchMapping("/{ratingId}")
    @ResponseStatus(HttpStatus.OK)
    public RatingDto update(@PathVariable("userId") long userId,
                            @PathVariable("eventId") long eventId,
                            @PathVariable("ratingId") long ratingId,
                            @RequestBody @Valid UpdateRatingDto updateRatingDto) {
        log.info("Update like/dislike: {} from user with id = {}.", updateRatingDto, userId);
        return ratingService.update(userId, eventId, ratingId, updateRatingDto);
    }

    //удалить лайк
    @DeleteMapping("/{ratingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable("userId") long userId,
                       @PathVariable("eventId") long eventId,
                       @PathVariable("ratingId") long ratingId) {
        log.info("Delete mark by user '{}' from event '{}'", userId, eventId);
        ratingService.delete(userId, eventId, ratingId);
    }
}
