package ru.practicum.rating.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.rating.model.Rating;
import ru.practicum.user.model.User;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findAllByUser(User user);
    List<Rating> findAllByUser(User user, PageRequest pageRequest);
}
