package ru.practicum.user.service;

import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {
    void deleteUser(long userId);

    UserDto createUser(NewUserRequest newUserRequest);

    List<UserDto> getAllUsers(List<Long> ids, Integer from, Integer size);

    User findById(long userId);
}
