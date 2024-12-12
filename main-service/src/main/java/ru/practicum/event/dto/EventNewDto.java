package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
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
public class EventNewDto {
    @NotBlank(message = "Аннотация не должна быть пустой")
    @Size(max = 2000, min = 20, message = "Длина аннотации должна быть от 20 до 2000 символов")
    String annotation;
    @NotNull(message = "Событие должно быть привязано к категории")
    Long category;
    @NotBlank(message = "Описание не должно быть пустым")
    @Size(max = 7000, min = 20, message = "Длина описания должна быть от 20 до 7000 символов")
    String description;
    @NotNull(message = "Нужно указать дату")
    @Future(message = "Дата события должна быть позже настоящего момента")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    @NotNull(message = "Нужно указать место проведения")
    Location location;
    boolean paid;
    @PositiveOrZero(message = "Число участников должно быть больше или равно нулю")
    int participantLimit;
    boolean requestModeration = true;
    @Size(max = 120, min = 3, message = "Длина заголовка должна быть от 3 до 120 символов")
    @NotBlank(message = "Нужно указать название")
    String title;
}