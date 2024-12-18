package ru.practicum.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationRequest {
    Boolean pinned;
    @Size(min = 1, max = 50, message = "Length of title must be more than 0 and less than 51.")
    String title;
    List<Long> events;
}
