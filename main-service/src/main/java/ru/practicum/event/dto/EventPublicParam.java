package ru.practicum.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventPublicParam {
    String text;
    List<Long> categories;
    boolean paid;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    boolean onlyAvailable;
    EventSort sort;
    Pageable pageable;

    public enum EventSort {
        EVENT_DATE,
        VIEWS
    }
}
