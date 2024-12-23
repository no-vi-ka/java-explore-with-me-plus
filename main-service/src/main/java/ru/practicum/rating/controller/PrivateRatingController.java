package ru.practicum.rating.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.rating.dto.*;
import ru.practicum.rating.service.RatingService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/ratings")
public class PrivateRatingController {
    private final RatingService ratingService;

    //просмотреть все лайки пользователя
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RatingDto> getAllMarksByUserId(@PathVariable("userId") long userId,
                                      @RequestParam(value = "from", defaultValue = "0") int from,
                                      @RequestParam(value = "size", defaultValue = "10") int size) {
        RatingParam ratingParam = new RatingParam();
        ratingParam.setFrom(from);
        ratingParam.setSize(size);
        log.info("Get all marks by user with id = {} and params: {}", userId, ratingParam);
        return ratingService.getAllMarksByUserId(userId, ratingParam);
    }

    //добавить лайк
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RatingDto addRatingMark(@PathVariable("userId") long userId,
                         @RequestBody @Valid NewRatingDto newRatingDto) {
        log.info("Add new like/dislike: {} from user with id = {}.", newRatingDto, userId);
        return ratingService.addRatingMark(userId, newRatingDto);
    }

    //изменить лайк
    @PatchMapping("/{ratingId}")
    @ResponseStatus(HttpStatus.OK)
    public RatingDto updateRatingMark(@PathVariable("userId") long userId, @PathVariable("ratingId") long ratingId,
                                      @RequestBody @Valid UpdateRatingDto updateRatingDto) {
        log.info("Update like/dislike: {} from user with id = {}.", updateRatingDto, userId);
        UpdateRatingParam updateRatingParam = new UpdateRatingParam();
        updateRatingParam.setRatingId(ratingId);
        updateRatingParam.setUpdateRatingDto(updateRatingDto);
        return ratingService.updateRatingMark(userId, updateRatingParam);
    }

    //удалить лайк
    @DeleteMapping("/{ratingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable("userId") long userId, @PathVariable("ratingId") long ratingId) {
        log.info("Delete like/dislike with id = {} from user with id = {}.", ratingId, userId);
        ratingService.removeRatingMark(userId, ratingId);
    }
}
