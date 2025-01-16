package ru.practicum.rating.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.errors.exceptions.ConditionsNotMetException;
import ru.practicum.errors.exceptions.NotFoundException;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.rating.dto.NewRatingDto;
import ru.practicum.rating.dto.RatingDto;
import ru.practicum.rating.dto.UpdateRatingDto;
import ru.practicum.rating.mapper.RatingMapper;
import ru.practicum.rating.mark.Mark;
import ru.practicum.rating.model.Rating;
import ru.practicum.rating.repository.RatingRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingServiceImpl implements RatingService {
    RatingRepository ratingRepository;
    RatingMapper ratingMapper;
    UserRepository userRepository;
    EventRepository eventRepository;

    @Override
    @Transactional
    public RatingDto create(long userId, long eventId, NewRatingDto newRatingDto) {
        boolean exists = ratingRepository.existsByUserIdAndEventId(userId, eventId);
        if (exists) {
            throw new ConditionsNotMetException(
                    String.format("Пользователь с ID %d уже оценивал событие с ID %d", userId, eventId));
        }

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id = " + userId + " not found."));

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id = " + eventId + " not found."));

        Rating newMark = getRating(newRatingDto, user, event);
        newMark.setUser(user);
        newMark.setEvent(event);
        ratingRepository.save(newMark);
        log.info("Rating mark: {} created.", newMark);
        return ratingMapper.toRatingDto(newMark);
    }

    @Override
    @Transactional
    public RatingDto update(long userId, long eventId, long ratingId, UpdateRatingDto updateRatingDto) {
        Rating rating = ratingRepository.findByIdAndUserId(ratingId, userId).orElseThrow(() ->
                new NotFoundException(String.format(
                        "Rating with userId = %d and id = %d was not found", userId, ratingId)));
        Mark newMark = updateRatingDto.getMark();
        rating.setMark(newMark);

        Mark oldMark = rating.getMark();
        if (newMark != oldMark) {
            log.info("Rating updated: {}", rating);
        }

        return ratingMapper.toRatingDto(rating);
    }

    @Override
    @Transactional
    public void delete(long userId, long eventId, long ratingId) {
        Rating ratingMark = ratingRepository.findById(ratingId).orElseThrow(() ->
                new NotFoundException(String.format(
                        "Rating mark not found. id = '%d'", ratingId))
        );

        if (userId != ratingMark.getUser().getId()) {
            throw new ConditionsNotMetException("User with id = " + userId + " is not author of mark");
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Removing mark from non-existent event with id = " + eventId);
        }

        ratingRepository.deleteById(ratingId);
        log.info("Rating mark '{}' deleted", ratingId);
    }

    private static Rating getRating(NewRatingDto newRatingDto, User user, Event event) {
        return Rating.builder()
                .user(user)
                .event(event)
                .mark(newRatingDto.getMark())
                .build();
    }
}