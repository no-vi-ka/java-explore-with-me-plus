package ru.practicum.rating.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.rating.mark.Mark;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRatingDto {
    @NotNull(message = "Поле mark должно быть указано.")
    Mark mark;
}
