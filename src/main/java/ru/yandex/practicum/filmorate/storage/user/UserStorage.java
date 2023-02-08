package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> get();

    User create(User user);

    User update(User user);

    User getUserById(Integer userId);

    List<User> addToFriends(Integer userId, Integer friendId);

    void deleteFromFriends(Integer userId, Integer friendId);

    List<User> getFriends(Integer userId);

    List<User> getCommonFriends(Integer userId, Integer friendId);
}
