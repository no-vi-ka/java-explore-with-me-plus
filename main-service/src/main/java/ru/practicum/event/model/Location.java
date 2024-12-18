package ru.practicum.event.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Location {
    float lat;
    float lon;
}
