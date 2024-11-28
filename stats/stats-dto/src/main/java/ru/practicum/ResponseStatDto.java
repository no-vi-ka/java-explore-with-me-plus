package ru.practicum;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseStatDto {
    String app;
    String uri;
    Long hits;

//    public ResponseStatDto(String app, String uri, Long hits){
//        this.app = app;
//        this.uri = uri;
//        this.hits = String.valueOf(hits);
//    }
}
