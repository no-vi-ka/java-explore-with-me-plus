package ru.practicum.rating.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.rating.mark.Mark;
import ru.practicum.user.model.User;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRatingDto {
    @NotNull(message = "Поле id должно быть указано.")
    @NotBlank(message = "Поле id не должно быть пустым.")
    Long id;
    @NotNull(message = "Поле user должно быть указано.")
    @NotBlank(message = "Поле user не должно быть пустым.")
    User user;
    @NotNull(message = "Поле status должно быть указано.")
    @NotBlank(message = "Поле status не должно быть пустым.")
    Mark status;
}
