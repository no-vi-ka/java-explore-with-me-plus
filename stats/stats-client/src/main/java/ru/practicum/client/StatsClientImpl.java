package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.ResponseStatDto;
import ru.practicum.StatDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class StatsClientImpl implements StatsClient {
    private final RestClient restClient;

    @Autowired
    public StatsClientImpl(@Value("${stats-server.url}") String statsUrl) {
        this.restClient = RestClient.builder().baseUrl(statsUrl).build();
    }

    @Override
    public List<ResponseStatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        var response = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/stats")
                        .queryParam("start", start.format(formatter))
                        .queryParam("end", end.format(formatter))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<ResponseStatDto>>() {
                });
        log.info("Получен ответ от сервера по запросу /stats с параметрами: start={}, end={}, uris={}, unique={}",
                start, end, uris, unique);
        return response;
    }

    @Override
    public ResponseEntity<StatDto> hit(StatDto statDto) {
        return restClient.post().uri("/hit").body(statDto).retrieve().toEntity(StatDto.class);
    }
}
