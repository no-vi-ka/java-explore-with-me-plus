package ru.practicum.event.mapper;

import org.mapstruct.*;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    Event toEvent(EventNewDto eventNewDto, Category category, User initiator);

    EventFullDto toFullDto(Event event);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "eventDate", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event toEventFromEventUserUpdateDto(EventUserUpdateDto dto, @MappingTarget Event event);

    List<EventShortDto> toEventShortDtoList(List<Event> events);

    List<EventFullDto> toEventFullDtoList(List<Event> events);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "eventDate", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event toEventFromEventAdminUpdateDto(EventAdminUpdateDto dto, @MappingTarget Event event);
}
