package ru.practicum.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User toUser(NewUserRequest newUserRequest);

    User toUser(UserDto userDto);

    User toUser(UserShortDto userShortDto);

    NewUserRequest toNewUserRequest(User user);

    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);
}
