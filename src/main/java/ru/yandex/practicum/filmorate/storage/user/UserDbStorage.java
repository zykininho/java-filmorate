package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> get() {
        List<User> users = new ArrayList<>();
        String sql = "select * from users";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql);
        while (userRows.next()) {
            users.add(makeUser(userRows));
        }
        log.info("Количество пользователей в базе: {}", users.size());
        return users;
    }

    private User makeUser(SqlRowSet userRows) {
        int userId = userRows.getInt("id");
        Map<Integer, Boolean> friendships = new HashMap<>();
        String sql = "select friend_id, status from friendships where user_id = ?";
        SqlRowSet friendshipRows = jdbcTemplate.queryForRowSet(sql, userId);
        while (friendshipRows.next()) {
            friendships.put(friendshipRows.getInt("friend_id"), friendshipRows.getBoolean("status"));
        }
        return User.builder()
                .id(userId)
                .email(userRows.getString("email"))
                .login(userRows.getString("login"))
                .name(userRows.getString("name"))
                .birthday(userRows.getDate("birthday").toLocalDate())
                .friends(friendships)
                .build();
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
        if (user.getFriends() == null) {
            user.setFriends(new HashMap<>());
        }
        String sqlQuery = "insert into users (email, login, name, birthday) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setString(1, user.getEmail());
                stmt.setString(2, user.getLogin());
                stmt.setString(3, user.getName());
                stmt.setDate(4, Date.valueOf(user.getBirthday()));
                return stmt;
            }
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        createFriendships(user.getId(), user.getFriends());
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    private void createFriendships(int userId, Map<Integer, Boolean> friends) {
        String sqlFriends = "insert into friendships (user_id, friend_id, status) " +
                "values (?, ?, ?)";
        for (Map.Entry<Integer, Boolean> entry : friends.entrySet()) {
            jdbcTemplate.update(sqlFriends,
                    userId,
                    entry.getKey(),
                    entry.getValue());
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
    public User update(User user) {
        validate(user);
        int userId = user.getId();
        if (user.getFriends() == null) {
            user.setFriends(new HashMap<>());
        }
        String sqlQuery = "update users set " +
                "email = ?, login = ?, name = ?, birthday = ?" +
                "where id = ?";
        int totalUpdate = jdbcTemplate.update(sqlQuery
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday()
                , userId);
        if (totalUpdate == 0) {
            log.info("Не найден пользователь в списке с id: {}", userId);
            throw new NotFoundException();
        }
        sqlQuery = "delete from friendships where user_id = ? ";
        jdbcTemplate.update(sqlQuery, userId);
        addFriendship(user);
        log.info("Обновлены данные пользователя с id {}. Новые данные: {}", userId, user);
        return user;
    }

    private void addFriendship(User user) {
        String sqlQuery = "insert into friendships (user_id, friend_id, status) " +
                "values (?, ?, ?)";
        int userId = user.getId();
        Map<Integer, Boolean> friends = user.getFriends();
        for (Map.Entry<Integer, Boolean> friendship : friends.entrySet()) {
            jdbcTemplate.update(sqlQuery,
                    userId,
                    friendship.getKey(),
                    friendship.getValue());
        }
    }

    @Override
    public User getUserById(Integer userId) {
        String sql = "select * from users where id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, userId);
        if (userRows.next()) {
            User user = makeUser(userRows);
            log.info("Найден пользователь в базе: {}", user);
            return user;
        } else {
            log.info("В списке отсутствует пользователь с id: {}", userId);
            throw new NotFoundException();
        }
    }

    @Override
    public List<User> addToFriends(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.addFriend(friendId);
        update(user);
        return getFriends(userId); // вернём список всех друзей (включая нового друга с friendId) пользователя с userId
    }

    @Override
    public void deleteFromFriends(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.deleteFromFriends(friendId);
        update(user);
    }

    @Override
    public List<User> getFriends(Integer userId) {
        List<User> friends = new ArrayList<>();
        String sqlQuery = "select * from users where id in (select distinct friend_id id from friendships where user_id = ?)";
        SqlRowSet friendshipRows = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        while (friendshipRows.next()) {
            User user = makeUser(friendshipRows);
            friends.add(user);
            log.info("В список друзей добавлен пользователь: {}", user);
        }
        log.info("Количество пользователей в списке друзей: {}", friends.size());
        return friends;
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        List<User> commonFriends = new ArrayList<>();
        String sqlQuery = "select * from users where id in (select friend_id from friendships where user_id = ? and friend_id in " +
            "(select distinct friend_id from friendships where user_id = ?))";
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
        if (friendsRows.next()) {
            User user = makeUser(friendsRows);
            commonFriends.add(user);
            log.info("В общий список друзей добавлен пользователь: {}", user);
        }
        log.info("В списке общих друзей {} пользователей", commonFriends.size());
        return commonFriends;
    }
}