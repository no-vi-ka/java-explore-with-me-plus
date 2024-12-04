package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
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
public class UserDto {
    @NotNull(message = "Поле id должно быть указано.")
    @NotBlank(message = "Поле id не должно быть пустым.")
    Long id;
    @NotNull(message = "Поле name должно быть указано.")
    @NotBlank(message = "Поле name не должно быть пустым.")
    String name;
    @NotNull(message = "Поле email должно быть указано.")
    @NotBlank(message = "Поле email не должно быть пустым.")
    @Email(message = "Поле email должно быть указано корректно.")
    String email;
}
