package ru.practicum.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCategoryDto {
    @NotNull(message = "Поле name должно быть указано.")
    @NotBlank(message = "Поле name не должно быть пустым.")
    @Size(min = 1, max = 50, message = "Поле name должно быть до 50 символов")
    String name;
}
