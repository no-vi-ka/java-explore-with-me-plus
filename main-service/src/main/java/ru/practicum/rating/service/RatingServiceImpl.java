package ru.practicum.rating.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.errors.exceptions.ConditionsNotMetException;
import ru.practicum.errors.exceptions.NotFoundException;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.rating.dto.NewRatingDto;
import ru.practicum.rating.dto.RatingDto;
import ru.practicum.rating.dto.RatingParam;
import ru.practicum.rating.dto.UpdateRatingDto;
import ru.practicum.rating.mapper.RatingMapper;
import ru.practicum.rating.model.Rating;
import ru.practicum.rating.repository.RatingRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<RatingDto> getAllMarksByUserId(long userId, RatingParam ratingParam) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id = " + userId + " not found."));
        int from = ratingParam.getFrom();
        int size = ratingParam.getSize();
        List<Rating> ratingList = ratingRepository.findAllByUser(user, PageRequest.of(from, size));
        return ratingMapper.toRatingDtoList(ratingList);
    }

    @Override
    public RatingDto addRatingMark(long userId, NewRatingDto newRatingDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id = " + userId + " not found."));
        Event event = eventRepository.findById(newRatingDto.getEventId()).orElseThrow(() ->
                new NotFoundException("Event with id = " + newRatingDto.getEventId() + " not found."));
        Rating newMark = ratingMapper.toRating(newRatingDto);
        newMark.setUser(user);
        newMark.setEvent(event);
        ratingRepository.save(newMark);
        log.info("Rating mark: {} created.", newMark);
        return ratingMapper.toRatingDto(newMark);
    }

    @Override
    public RatingDto updateRatingMark(long userId, long ratingId, UpdateRatingDto updateRatingDto) {
        Rating ratingMark = ratingRepository.findById(ratingId).orElseThrow(() ->
                new NotFoundException("Rating mark with id = " + ratingId + " not found."));
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found.");
        }
        if (userId != updateRatingDto.getUserId()) {
            throw new ConditionsNotMetException("User with id = " + userId + " is not author of mark.");
        }
        ratingMark.setMark(updateRatingDto.getStatus());
        log.info("Rating mark: {} updated.", ratingMark);
        return ratingMapper.toRatingDto(ratingMark);
    }

    @Override
    public void removeRatingMark(long userId, long ratingId) {
        Rating ratingMark = ratingRepository.findById(ratingId).orElseThrow(() ->
                new NotFoundException("Rating mark with id = " + ratingId + " not found."));
        if (userId != ratingMark.getUser().getId()) {
            throw new ConditionsNotMetException("User with id = " + userId + " is not author of mark.");
        }
        ratingRepository.deleteById(ratingId);
        log.info("Rating mark: {} deleted.", ratingMark);
    }
}
