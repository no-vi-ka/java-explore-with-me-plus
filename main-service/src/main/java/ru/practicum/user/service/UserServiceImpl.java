package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.DataAlreadyInUseException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers(List<Long> ids, Integer from, Integer size) {
        log.info("Started get all users.");
        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(PageRequest.of(from, size)).stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        }
        List<User> users = userRepository.findAllByIdIn(ids, PageRequest.of(from, size));
        log.info("Got all users.");
        return users.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        log.info("Starting create user.");
        if (newUserRequest.getName().length() < 2 || newUserRequest.getName().length() > 250 || newUserRequest.getEmail().length() < 6 || newUserRequest.getEmail().length() > 254) {
            throw new ValidationException("Length of name or email is out of bounds.");
        }
        if (userRepository.findByEmail(newUserRequest.getEmail()) != null) {
            throw new DataAlreadyInUseException("Email " + newUserRequest.getEmail() + " already in use.");
        }
        User newUser = userMapper.toUser(newUserRequest);
        User created = userRepository.save(newUser);
        log.info("User with id = " + created.getId() + " created.");
        return userMapper.toUserDto(created);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Started delete user with id = " + userId + ".");
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found.");
        }
        userRepository.deleteById(userId);
        log.info("User with id = " + userId + " deleted.");
    }
}
