package ru.practicum.rating.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.rating.mark.Mark;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatingDto {
    Long id;
    Long userId;
    Long eventId;
    Mark mark;
}
