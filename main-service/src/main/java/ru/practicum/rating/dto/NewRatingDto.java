package ru.practicum.rating.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class NewRatingDto {
    @NotNull(message = "Поле user должно быть указано.")
    @NotBlank(message = "Поле user не должно быть пустым.")
    User user;
    @NotNull(message = "Поле event должно быть указано.")
    @NotBlank(message = "Поле event не должно быть пустым.")
    Event event;
    @NotNull(message = "Поле mark должно быть указано.")
    @NotBlank(message = "Поле mark не должно быть пустым.")
    Mark mark;
}
