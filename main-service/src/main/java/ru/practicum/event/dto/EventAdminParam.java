package ru.practicum.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventAdminParam {
    List<Long> users;
    List<EventState> states;
    List<Long> categories;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    Pageable pageable;
}
