package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserControllerTest {

    private static UserController controller;
    private static User user;
    private static User userInvalidEmail;
    private static User userInvalidLogin;
    private static User userInvalidBirthday;

    @BeforeAll
    static void beforeAll() {
        userInvalidEmail = User.builder()
                .email("email.com")
                .login("Login")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();
        userInvalidLogin = User.builder()
                .email("my@email.com")
                .login("Log in")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();
        userInvalidBirthday = User.builder()
                .email("my@email.com")
                .login("Login")
                .birthday(LocalDate.of(2023, 12, 12))
                .build();
    }

    @BeforeEach
    void setUp() {
        controller = new UserController(new UserService(new InMemoryUserStorage()));
        user = User.builder()
                .email("my@email.com")
                .login("Login")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();
    }

    @Test
    void getEmptyListOfUsers() {
        List<User> users = controller.get();
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    void getListOfUsers() {
        controller.create(user);
        List<User> users = controller.get();
        assertNotNull(users);
        assertEquals(1, users.size());
    }

    @Test
    void createAndValidateUserInvalidEmail() {
        Throwable thrown = catchThrowable(() -> {
            controller.create(userInvalidEmail);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void validateUserInvalidLogin() {
        Throwable thrown = catchThrowable(() -> {
            controller.create(userInvalidLogin);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void validateUserInvalidBirthday() {
       Throwable thrown = catchThrowable(() -> {
           controller.create(userInvalidBirthday);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void createAndUpdateUserNoErrors() {
        controller.create(user);
        List<User> users = controller.get();
        assertNotNull(users);
        assertEquals(1, users.size());
        user.setName("Zykin Mikhail");
        controller.update(user);
        assertNotNull(users);
        assertEquals(1, users.size());
    }

    @Test
    void createAndUpdateUserInvalidEmail() {
        controller.create(user);
        List<User> users = controller.get();
        assertNotNull(users);
        assertEquals(1, users.size());
        user.setEmail("email.com");
        Throwable thrown = catchThrowable(() -> {
            controller.update(user);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void createAndUpdateUserInvalidLogin() {
        controller.create(user);
        List<User> users = controller.get();
        assertNotNull(users);
        assertEquals(1, users.size());
        user.setLogin("Log in");
        Throwable thrown = catchThrowable(() -> {
            controller.update(user);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void createAndUpdateUserInvalidBirthday() {
        controller.create(user);
        List<User> users = controller.get();
        assertNotNull(users);
        assertEquals(1, users.size());
        user.setBirthday(LocalDate.of(2023, 12, 12));
        Throwable thrown = catchThrowable(() -> {
            controller.update(user);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void createAndUpdateUserInvalidId() {
        controller.create(user);
        List<User> users = controller.get();
        assertNotNull(users);
        assertEquals(1, users.size());
        user.setId(2);
        Throwable thrown = catchThrowable(() -> {
            controller.update(user);
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }
}
