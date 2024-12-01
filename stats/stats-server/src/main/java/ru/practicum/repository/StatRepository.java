package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ResponseStatDto;
import ru.practicum.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Stat, Long> {
    @Query("""
        SELECT new ru.practicum.ResponseStatDto(s.app, s.uri, count(s.ip))
        FROM Stat AS s
        WHERE s.timestamp BETWEEN :start AND :end AND s.uri IN :uris
        GROUP BY s.app, s.uri
        ORDER BY count(s.ip) DESC
    """)
    List<ResponseStatDto> getStatsWithUri(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end,
                                          @Param("uris") List<String> uris);

    @Query("""
        SELECT new ru.practicum.ResponseStatDto(s.app, s.uri, count(s.ip))
        FROM Stat AS s
        WHERE s.timestamp BETWEEN :start AND :end
        GROUP BY s.app, s.uri
        ORDER BY count(s.ip) DESC
    """)
    List<ResponseStatDto> getStatsWithoutUri(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);

    @Query("""
        SELECT new ru.practicum.ResponseStatDto(s.app, s.uri, count(DISTINCT s.ip))
        FROM Stat AS s
        WHERE s.timestamp BETWEEN :start AND :end AND s.uri IN :uris
        GROUP BY s.app, s.uri
        ORDER BY count(DISTINCT s.ip) DESC
    """)
    List<ResponseStatDto> getStatWithUriWithUniqueIp(@Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end,
                                                     @Param("uris") List<String> uris);

    @Query("""
        SELECT new ru.practicum.ResponseStatDto(s.app, s.uri, count(DISTINCT s.ip))
        FROM Stat AS s
        WHERE s.timestamp BETWEEN :start AND :end
        GROUP BY s.app, s.uri
        ORDER BY count(DISTINCT s.ip) DESC
    """)
    List<ResponseStatDto> getStatsWithoutUriWithUniqueIp(@Param("start") LocalDateTime start,
                                                         @Param("end") LocalDateTime end);
}