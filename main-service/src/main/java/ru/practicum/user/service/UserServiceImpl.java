package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.errors.exceptions.DataAlreadyInUseException;
import ru.practicum.errors.exceptions.NotFoundException;
import ru.practicum.errors.exceptions.ValidationException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserAdminParam;
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
    public List<UserDto> getAllUsers(UserAdminParam param) {
        int from = param.getFrom();
        int size = param.getSize();
        log.info("Starting get all users with params: from = {}, size = {}.", from, size);
        if (CollectionUtils.isEmpty(param.getIds())) {
            return userRepository.findAll(PageRequest.of(from, size)).stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        }
        List<User> users = userRepository.findAllByIdIn(param.getIds(), PageRequest.of(from, size));
        log.info("Got all users, count = {}.", users.size());
        return userMapper.toUserDtoList(users);
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest newUserRequest) {
        log.info("Starting create user with name = {}, email = {}.", newUserRequest.getName(),
                newUserRequest.getEmail());
        if (newUserRequest.getName().length() < 2 || newUserRequest.getName().length() > 250 ||
                newUserRequest.getEmail().length() < 6 || newUserRequest.getEmail().length() > 254) {
            throw new ValidationException("Length of name or email is out of bounds.");
        }
        if (userRepository.findByEmail(newUserRequest.getEmail()) != null) {
            throw new DataAlreadyInUseException("Email " + newUserRequest.getEmail() + " already in use.");
        }
        User newUser = userMapper.toUser(newUserRequest);
        User created = userRepository.save(newUser);
        log.info("User with id = {} created.", created.getId());
        return userMapper.toUserDto(created);
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found.");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public User findById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id = " + userId + " not found."));
        log.info("User was found. ID = {}", user.getId());
        return user;
    }
}
