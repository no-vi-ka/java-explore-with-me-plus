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
public class NewUserRequest {
    @NotNull(message = "name должно быть указано.")
    @NotBlank(message = "name не должен быть пустым.")
    String name;
    @NotNull(message = "email должно быть указано.")
    @NotBlank(message = "email не должен быть пустым.")
    @Email(message = "Поле email должно быть указано корректно.")
    String email;
}
