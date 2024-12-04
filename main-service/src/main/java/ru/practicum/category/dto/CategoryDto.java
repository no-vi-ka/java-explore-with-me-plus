package ru.practicum.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDto {
    @NotNull(message = "Поле id должно быть указан.")
    @NotBlank(message = "Поле id не должно быть пустым.")
    Long id;
    @NotNull(message = "Поле name должно быть указано.")
    @NotBlank(message = "Поле name не должно быть пустым.")
    String name;
}
