package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int id;

    @Override
    public List<User> get() {
        log.info("Текущее количество пользователей: {}", users.size());
        return users.values().parallelStream().collect(Collectors.toList());
    }

    @Override
    public User create(User user) {
        validate(user);
        String name = user.getName();
        String login = user.getLogin();
        if (name == null || name.isEmpty()) {
            user.setName(login);
            log.info("Для пользователя с логином {} установлено новое имя {}", login, user.getName());
        }
        user.setId(++this.id);
        if (user.getFriends() == null) {
            user.setFriends(new HashMap<>());
        }
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        validate(user);
        int userId = user.getId();
        if (!users.containsKey(userId)) {
            log.info("Не найден пользователь в списке с id: {}", userId);
            throw new NotFoundException();
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashMap<>());
        }
        users.put(userId, user);
        log.info("Обновлены данные пользователя с id {}. Новые данные: {}", userId, user);
        return user;
    }

    @Override
    public User getUserById(Integer userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new NotFoundException();
        }
    }

    private static void validate(User user) {
        String email = user.getEmail();
        String login = user.getLogin();
        LocalDate birthday = user.getBirthday();
        if (email == null || email.isEmpty() || !email.contains("@")) {
            log.info("Электронная почта не указана или не указан символ '@'");
            throw new ValidationException();
        } else if (login == null || login.isEmpty() || login.contains(" ")) {
            log.info("Логин пользователя с электронной почтой {} не указан или содержит пробел", email);
            throw new ValidationException();
        } else if (birthday.isAfter(LocalDate.now())) {
            log.info("Дата рождения пользователя с логином {} указана будущим числом", login);
            throw new ValidationException();
        }
    }

    @Override
    public List<User> addToFriends(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        update(user);
        update(friend);
        Set<Integer> friendsId = user.getFriends().keySet();
        List<User> friends = new ArrayList<>();
        for (Integer id : friendsId) {
            friends.add(getUserById(id));
        }
        return friends;
    }

    @Override
    public void deleteFromFriends(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        user.deleteFromFriends(friendId);
    }

    @Override
    public List<User> getFriends(Integer userId) {
        List<User> friends = new ArrayList<>();
        User user = getUserById(userId);
        Set<Integer> friendsId = user.getFriends().keySet();
        for (Integer friendId : friendsId) {
            friends.add(getUserById(friendId));
        }
        return friends;
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        List<User> friends = new ArrayList<>();
        User user = getUserById(userId);
        Set<Integer> userFriendsId = user.getFriends().keySet();
        User friend = getUserById(friendId);
        Set<Integer> friendsId = friend.getFriends().keySet();
        List<Integer> commonId = userFriendsId.stream().filter(friendsId::contains).collect(Collectors.toList());
        for (Integer id : commonId) {
            friends.add(getUserById(id));
        }
        return friends;
    }
}
