package ru.practicum.request.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.request.model.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class ParticipationRequestDto {
    Long id;
    Long requester;
    Long event;
    RequestStatus status;
    LocalDateTime created;
}