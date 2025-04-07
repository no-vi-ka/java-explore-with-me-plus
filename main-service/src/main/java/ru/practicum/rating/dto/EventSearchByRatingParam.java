package ru.practicum.rating.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class EventSearchByRatingParam {
    int limit;
}
