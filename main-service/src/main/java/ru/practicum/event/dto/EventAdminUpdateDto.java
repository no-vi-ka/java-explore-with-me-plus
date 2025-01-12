package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.Location;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventAdminUpdateDto {
    @Size(max = 2000, min = 20, message = "Длина аннотации должна быть от 20 до 2000 символов")
    String annotation;
    Long category;
    @Size(max = 7000, min = 20, message = "Длина описания должна быть от 20 до 7000 символов")
    String description;
    @Future(message = "Датча события должна быть позже настоящего момента")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    Location location;
    Boolean paid;
    @PositiveOrZero(message = "Число участников должно быть больше или равно нулю")
    Integer participantLimit;
    Boolean requestModeration;
    StateAction stateAction;
    @Size(max = 120, min = 3, message = "Длина заголовка должна быть от 3 до 120 символов")
    String title;

    public enum StateAction {
        PUBLISH_EVENT,
        REJECT_EVENT
    }
}
