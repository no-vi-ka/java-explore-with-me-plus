package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class PrivateUserController {
    private final UserService userService;

}
