package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ResponseStatDto;
import ru.practicum.StatDto;
import ru.practicum.exception.DateTimeException;
import ru.practicum.mapper.StatMapper;
import ru.practicum.model.Stat;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatRepository statRepository;
    private final StatMapper statMapper;

    @Override
    @Transactional
    public StatDto saveRequest(StatDto statDto) {
        log.info("Save request: {}", statDto);
        Stat stat = statMapper.toEntity(statDto);
        return statMapper.toDto(statRepository.save(stat));
    }

    @Override
    public List<ResponseStatDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("GetStat: {},{},{},{}", start, end, uris, unique);
        if (end.isBefore(start)) {
            throw new DateTimeException("The end date cannot be earlier than the start date");
        }

        if (uris.isEmpty()) {
            if (unique) {
                return statRepository.getStatsWithoutUriWithUniqueIp(start, end);
            } else {
                return statRepository.getStatsWithoutUri(start, end);
            }
        } else {
            if (unique) {
                return statRepository.getStatWithUriWithUniqueIp(start, end, uris);
            } else {
                return statRepository.getStatsWithUri(start, end, uris);
            }
        }
    }
}
