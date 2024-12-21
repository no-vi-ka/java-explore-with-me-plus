package ru.practicum.rating.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.Event;
import ru.practicum.rating.mark.Mark;
import ru.practicum.user.model.User;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatingDto {
    Long id;
    User user;
    Event event;
    Mark mark;
}
