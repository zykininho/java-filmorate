package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @ResponseBody
    public List<User> get() {
        return userService.get();
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable("id") Integer userId) {
        return userService.getUserById(userId);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public List<User> addToFriends(@PathVariable("id") Integer userId, @PathVariable Integer friendId) {
        return userService.addToFriends(userId, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable("id") Integer userId, @PathVariable Integer friendId) {
        userService.deleteFromFriends(userId, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable("id") Integer userId) {
        return userService.getFriends(userId);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") Integer userId, @PathVariable("otherId") Integer friendId) {
        return userService.getCommonFriends(userId, friendId);
    }
}
