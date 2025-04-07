package ru.practicum.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.rating.model.Rating;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    boolean existsByUserIdAndEventId(long userId, long eventId);

    Optional<Rating> findByIdAndUserId(long ratingId, long userId);
}
