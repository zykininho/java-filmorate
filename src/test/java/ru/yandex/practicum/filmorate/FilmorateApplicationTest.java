package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTest {

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final GenreDbStorage genreStorage;
    private final RatingDbStorage ratingStorage;

    @Test
    void testAddUser1() {
        User newUser = User.builder()
                .name("user1")
                .email("user1@ya.ru")
                .login("login1")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        Optional<User> userOptional = Optional.ofNullable(userStorage.create(newUser));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "user1"))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "user1@ya.ru"))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "login1"))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 1, 1)))
        ;
    }

    @Test
    void testFindUserById() {
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void testTryAddUserWithWrongEmail() {
        User newUser = User.builder()
                .name("user2")
                .email("user2ya.ru")
                .login("login2")
                .birthday(LocalDate.of(2000, 2, 2))
                .build();
        Throwable thrown = catchThrowable(() -> {
            userStorage.create(newUser);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void testTryAddUserWithWrongLogin() {
        User newUser = User.builder()
                .name("user2")
                .email("user2@ya.ru")
                .login("login 2")
                .birthday(LocalDate.of(2000, 2, 2))
                .build();
        Throwable thrown = catchThrowable(() -> {
            userStorage.create(newUser);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void testTryAddUserWithWrongBirthday() {
        User newUser = User.builder()
                .name("user2")
                .email("user2@ya.ru")
                .login("login2")
                .birthday(LocalDate.of(2222, 2, 2))
                .build();
        Throwable thrown = catchThrowable(() -> {
            userStorage.create(newUser);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void testAddUser2() {
        User newUser = User.builder()
                .name("user2")
                .email("user2@ya.ru")
                .login("login2")
                .birthday(LocalDate.of(2000, 2, 2))
                .build();
        Optional<User> userOptional = Optional.ofNullable(userStorage.create(newUser));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "user2"))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "user2@ya.ru"))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "login2"))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 2, 2)))
        ;
    }

    @Test
    void testAddNewUserUpdate() {
        User newUser = User.builder()
                .name("user3")
                .email("user3@ya.ru")
                .login("login3")
                .birthday(LocalDate.of(2010, 3, 3))
                .build();
        Optional<User> userOptional = Optional.ofNullable(userStorage.create(newUser));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "user3"))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "user3@ya.ru"))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "login3"))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("birthday", LocalDate.of(2010, 3, 3)))
        ;
        User userCreated = userOptional.get();
        userCreated.setName("Updated");
        Optional<User> userUpdated = Optional.ofNullable(userStorage.update(userCreated));
        assertThat(userUpdated)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "Updated"));
    }

    @Test
    void testAddFriend() {
        User user = User.builder()
                .name("friend1")
                .email("friend1@ya.ru")
                .login("loginFriend1")
                .birthday(LocalDate.of(2020, 4, 4))
                .build();
        Optional<User> userOptional = Optional.ofNullable(userStorage.create(user));
        assertThat(userOptional).isPresent();
        User friend = User.builder()
                .name("friend2")
                .email("friend2@ya.ru")
                .login("loginFriend2")
                .birthday(LocalDate.of(2021, 5, 5))
                .build();
        Optional<User> friendOptional = Optional.ofNullable(userStorage.create(friend));
        assertThat(friendOptional).isPresent();
        List<User> friends = userStorage.addToFriends(userOptional.get().getId(), friendOptional.get().getId());
        assertNotNull(friends);
        assertEquals(1, friends.size());
        assertEquals("friend2", friends.get(0).getName());
    }

    @Test
    void testDeleteFriend() {
        User user = User.builder()
                .name("friend3")
                .email("friend3@ya.ru")
                .login("loginFriend3")
                .birthday(LocalDate.of(2020, 4, 4))
                .build();
        Optional<User> userOptional = Optional.ofNullable(userStorage.create(user));
        assertThat(userOptional).isPresent();
        User friend = User.builder()
                .name("friend4")
                .email("friend4@ya.ru")
                .login("loginFriend4")
                .birthday(LocalDate.of(2021, 5, 5))
                .build();
        Optional<User> friendOptional = Optional.ofNullable(userStorage.create(friend));
        assertThat(friendOptional).isPresent();
        List<User> friends = userStorage.addToFriends(userOptional.get().getId(), friendOptional.get().getId());
        assertNotNull(friends);
        assertEquals(1, friends.size());
        assertEquals("friend4", friends.get(0).getName());
        userStorage.deleteFromFriends(userOptional.get().getId(), friendOptional.get().getId());
        List<User> emptyListOfFriends = userStorage.getFriends(userOptional.get().getId());
        assertNotNull(emptyListOfFriends);
        assertEquals(0, emptyListOfFriends.size());
    }

    @Test
    void testGetFriends() {
        User user = User.builder()
                .name("friend5")
                .email("friend5@ya.ru")
                .login("loginFriend5")
                .birthday(LocalDate.of(2020, 4, 4))
                .build();
        Optional<User> userOptional = Optional.ofNullable(userStorage.create(user));
        assertThat(userOptional).isPresent();

        List<User> emptyListOfFriends = userStorage.getFriends(userOptional.get().getId());
        assertNotNull(emptyListOfFriends);
        assertEquals(0, emptyListOfFriends.size());

        User friend = User.builder()
                .name("friend6")
                .email("friend6@ya.ru")
                .login("loginFriend6")
                .birthday(LocalDate.of(2015, 7, 7))
                .build();
        Optional<User> friendOptional = Optional.ofNullable(userStorage.create(friend));
        assertThat(friendOptional).isPresent();
        List<User> friends = userStorage.addToFriends(userOptional.get().getId(), friendOptional.get().getId());
        assertNotNull(friends);
        assertEquals(1, friends.size());
        assertEquals("friend6", friends.get(0).getName());

        User newFriend = User.builder()
                .name("friend7")
                .email("friend7@ya.ru")
                .login("loginFriend7")
                .birthday(LocalDate.of(2018, 8, 8))
                .build();
        Optional<User> newFriendOptional = Optional.ofNullable(userStorage.create(newFriend));
        assertThat(newFriendOptional).isPresent();
        friends = userStorage.addToFriends(userOptional.get().getId(), newFriendOptional.get().getId());
        assertNotNull(friends);
        assertEquals(2, friends.size());
        assertEquals("friend7", friends.get(1).getName());
    }

    @Test
    void testGetCommonFriends() {
        User user4 = User.builder()
                .name("user4")
                .email("user4@ya.ru")
                .login("loginUser4")
                .birthday(LocalDate.of(2004, 4, 4))
                .build();
        Optional<User> user4Optional = Optional.ofNullable(userStorage.create(user4));
        assertThat(user4Optional).isPresent();

        User user5 = User.builder()
                .name("user5")
                .email("user5@ya.ru")
                .login("loginUser5")
                .birthday(LocalDate.of(2005, 5, 5))
                .build();
        Optional<User> user5Optional = Optional.ofNullable(userStorage.create(user5));
        assertThat(user5Optional).isPresent();

        User user6 = User.builder()
                .name("user6")
                .email("user6@ya.ru")
                .login("loginUser6")
                .birthday(LocalDate.of(2006, 6, 6))
                .build();
        Optional<User> user6Optional = Optional.ofNullable(userStorage.create(user6));
        assertThat(user6Optional).isPresent();

        User user7 = User.builder()
                .name("user7")
                .email("user7@ya.ru")
                .login("loginUser7")
                .birthday(LocalDate.of(2007, 7, 7))
                .build();
        Optional<User> user7Optional = Optional.ofNullable(userStorage.create(user7));
        assertThat(user7Optional).isPresent();

        userStorage.addToFriends(user4Optional.get().getId(), user6Optional.get().getId());
        List<User> friendsOfUser4 = userStorage.addToFriends(user4Optional.get().getId(), user7Optional.get().getId());
        assertNotNull(friendsOfUser4);
        assertEquals(2, friendsOfUser4.size());

        List<User> friendsOfUser5 = userStorage.addToFriends(user5Optional.get().getId(), user7Optional.get().getId());
        assertNotNull(friendsOfUser5);
        assertEquals(1, friendsOfUser5.size());

        List<User> commonFriends = userStorage.getCommonFriends(user4Optional.get().getId(), user5Optional.get().getId());
        assertNotNull(commonFriends);
        assertEquals(1, commonFriends.size());
        assertEquals("user7", commonFriends.get(0).getName());
    }

    @Test
    void testAddFilm() {
        Film newFilm = Film.builder()
                .name("film1")
                .description("Description of film1")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(90)
                .mpa(Rating.builder().id(1).build())
                .build();
        Optional<Film> optionalFilm = Optional.ofNullable(filmStorage.create(newFilm));
        assertThat(optionalFilm)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "film1"))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description", "Description of film1"))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("duration", 90L))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2001, 1, 1)))
        ;
    }

    @Test
    void testFindFilmById() {
        Film newFilm = Film.builder()
                .name("film2")
                .description("Description of film2")
                .releaseDate(LocalDate.of(2002, 2, 2))
                .duration(120)
                .mpa(Rating.builder().id(2).build())
                .build();
        Optional<Film> optionalFilm = Optional.ofNullable(filmStorage.create(newFilm));
        assertThat(optionalFilm).isPresent();

        Optional<Film> filmFoundOptional = Optional.ofNullable(filmStorage.getFilmById(optionalFilm.get().getId()));
        assertThat(filmFoundOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", optionalFilm.get().getId()));
    }

    @Test
    void testUpdateFilm() {
        Film newFilm = Film.builder()
                .name("film3")
                .description("Description of film3")
                .releaseDate(LocalDate.of(2003, 3, 3))
                .duration(60)
                .mpa(Rating.builder().id(3).build())
                .build();
        Optional<Film> optionalFilm = Optional.ofNullable(filmStorage.create(newFilm));
        assertThat(optionalFilm).isPresent();

        Film filmToUpdate = optionalFilm.get();
        filmToUpdate.setDescription("New description of film3");
        Optional<Film> filmUpdatedOptional = Optional.ofNullable(filmStorage.update(filmToUpdate));
        assertThat(filmUpdatedOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description", "New description of film3"));
    }

    @Test
    void testGetFilms() {
        jdbcTemplate.update("delete from films");

        Film newFilm = Film.builder()
                .name("film4")
                .description("Description of film4")
                .releaseDate(LocalDate.of(2004, 4, 4))
                .duration(150)
                .mpa(Rating.builder().id(4).build())
                .build();
        Optional<Film> optionalFilm = Optional.ofNullable(filmStorage.create(newFilm));
        assertThat(optionalFilm).isPresent();

        newFilm = Film.builder()
                .name("film5")
                .description("Description of film5")
                .releaseDate(LocalDate.of(2005, 5, 5))
                .duration(180)
                .mpa(Rating.builder().id(5).build())
                .build();
        optionalFilm = Optional.ofNullable(filmStorage.create(newFilm));
        assertThat(optionalFilm).isPresent();

        List<Film> films = filmStorage.get();
        assertNotNull(films);
        assertEquals(2, films.size());
    }

    @Test
    void testAddLike() {
        Film newFilm = Film.builder()
                .name("film6")
                .description("Description of film6")
                .releaseDate(LocalDate.of(2006, 6, 6))
                .duration(210)
                .mpa(Rating.builder().id(1).build())
                .build();
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.create(newFilm));
        assertThat(filmOptional).isPresent();

        User user = User.builder()
                .name("user8")
                .email("user8@ya.ru")
                .login("loginUser8")
                .birthday(LocalDate.of(2008, 8, 8))
                .build();
        Optional<User> userOptional = Optional.ofNullable(userStorage.create(user));
        assertThat(userOptional).isPresent();

        filmStorage.addLike(filmOptional.get().getId(), userOptional.get().getId());

        Film filmWithLike = filmStorage.getFilmById(filmOptional.get().getId());
        Set<Integer> likesByUsers = filmWithLike.getLikesByUsers();
        assertNotNull(likesByUsers);
        assertEquals(1, likesByUsers.size());
        assertTrue(likesByUsers.contains(userOptional.get().getId()));
    }

    @Test
    void testDeleteLike() {
        Film newFilm = Film.builder()
                .name("film7")
                .description("Description of film7")
                .releaseDate(LocalDate.of(2007, 7, 7))
                .duration(195)
                .mpa(Rating.builder().id(2).build())
                .build();
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.create(newFilm));
        assertThat(filmOptional).isPresent();

        User user = User.builder()
                .name("user9")
                .email("user9@ya.ru")
                .login("loginUser9")
                .birthday(LocalDate.of(2009, 9, 9))
                .build();
        Optional<User> userOptional = Optional.ofNullable(userStorage.create(user));
        assertThat(userOptional).isPresent();

        filmStorage.addLike(filmOptional.get().getId(), userOptional.get().getId());

        Film filmWithLike = filmStorage.getFilmById(filmOptional.get().getId());
        Set<Integer> likesByUsers = filmWithLike.getLikesByUsers();
        assertNotNull(likesByUsers);
        assertEquals(1, likesByUsers.size());
        assertTrue(likesByUsers.contains(userOptional.get().getId()));

        filmStorage.deleteLike(filmOptional.get().getId(), userOptional.get().getId());

        filmWithLike = filmStorage.getFilmById(filmOptional.get().getId());
        likesByUsers = filmWithLike.getLikesByUsers();
        assertNotNull(likesByUsers);
        assertEquals(0, likesByUsers.size());
        assertFalse(likesByUsers.contains(userOptional.get().getId()));
    }

    @Test
    void testGetPopularFilm() {
        Film film1 = Film.builder()
                .name("film8")
                .description("Description of film8")
                .releaseDate(LocalDate.of(2008, 8, 8))
                .duration(100)
                .mpa(Rating.builder().id(3).build())
                .build();
        Optional<Film> film1Optional = Optional.ofNullable(filmStorage.create(film1));
        assertThat(film1Optional).isPresent();

        Film film2 = Film.builder()
                .name("film9")
                .description("Description of film9")
                .releaseDate(LocalDate.of(2009, 9, 9))
                .duration(80)
                .mpa(Rating.builder().id(4).build())
                .build();
        Optional<Film> film2Optional = Optional.ofNullable(filmStorage.create(film2));
        assertThat(film2Optional).isPresent();

        User user1 = User.builder()
                .name("user10")
                .email("user10@ya.ru")
                .login("loginUser10")
                .birthday(LocalDate.of(2010, 10, 10))
                .build();
        Optional<User> user1Optional = Optional.ofNullable(userStorage.create(user1));
        assertThat(user1Optional).isPresent();

        User user2 = User.builder()
                .name("user11")
                .email("user11@ya.ru")
                .login("loginUser11")
                .birthday(LocalDate.of(2011, 11, 11))
                .build();
        Optional<User> user2Optional = Optional.ofNullable(userStorage.create(user2));
        assertThat(user2Optional).isPresent();

        filmStorage.addLike(film1Optional.get().getId(), user1Optional.get().getId());
        filmStorage.addLike(film1Optional.get().getId(), user2Optional.get().getId());

        Film film1WithLike = filmStorage.getFilmById(film1Optional.get().getId());
        Set<Integer> likesByUsersFilm1 = film1WithLike.getLikesByUsers();
        assertNotNull(likesByUsersFilm1);
        assertEquals(2, likesByUsersFilm1.size());
        assertTrue(likesByUsersFilm1.contains(user1Optional.get().getId()));
        assertTrue(likesByUsersFilm1.contains(user2Optional.get().getId()));

        filmStorage.addLike(film2Optional.get().getId(), user1Optional.get().getId());

        Film film2WithLike = filmStorage.getFilmById(film2Optional.get().getId());
        Set<Integer> likesByUsersFilm2 = film2WithLike.getLikesByUsers();
        assertNotNull(likesByUsersFilm2);
        assertEquals(1, likesByUsersFilm2.size());
        assertTrue(likesByUsersFilm2.contains(user1Optional.get().getId()));

        List<Film> popularFilm = filmStorage.getPopularFilms(1);
        assertNotNull(popularFilm);
        assertEquals(1, popularFilm.size());
        assertTrue(popularFilm.contains(filmStorage.getFilmById(film1Optional.get().getId())));
    }

    @Test
    void testGetAllGenre() {
        List<Genre> genres = genreStorage.findAll();
        assertNotNull(genres);
        assertEquals(6, genres.size());
    }

    @Test
    void testGetGenre() {
        Optional<Genre> genreOptional = Optional.ofNullable(genreStorage.findGenreById(1));
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1))
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия"))
        ;
    }

    @Test
    void testGetAllMPA() {
        List<Rating> ratings = ratingStorage.findAll();
        assertNotNull(ratings);
        assertEquals(5, ratings.size());
    }

    @Test
    void testGetMPA() {
        Optional<Rating> ratingOptional = Optional.ofNullable(ratingStorage.findRatingById(5));
        assertThat(ratingOptional)
                .isPresent()
                .hasValueSatisfying(rating ->
                        assertThat(rating).hasFieldOrPropertyWithValue("id", 5))
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("name", "NC-17"))
        ;
    }
}