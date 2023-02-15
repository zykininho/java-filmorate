package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private static FilmController controller;
    private static Film film;
    private static Film filmInvalidName;
    private static Film filmInvalidDescription;
    private static Film filmInvalidReleaseDate;
    private static Film filmInvalidDuration;

    @BeforeAll
    static void beforeAll() {
        filmInvalidName = Film.builder()
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                        "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
                        "а именно 20 миллионов. о Куглов, который за время отсу")
                .releaseDate(LocalDate.of(1896, 12, 28))
                .duration(120)
                .build();
        filmInvalidDescription = Film.builder()
                .name("Супер боевик")
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                        "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
                        "а именно 20 миллионов. о Куглов, который за время отсут")
                .releaseDate(LocalDate.of(1896, 12, 28))
                .duration(120)
                .build();
        filmInvalidReleaseDate = Film.builder()
                .name("Супер боевик")
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                        "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
                        "а именно 20 миллионов. о Куглов, который за время отсу")
                .releaseDate(LocalDate.of(1890, 12, 28))
                .duration(120)
                .build();
        filmInvalidDuration = Film.builder()
                .name("Супер боевик")
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                        "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
                        "а именно 20 миллионов. о Куглов, который за время отсу")
                .releaseDate(LocalDate.of(1896, 12, 28))
                .duration(-120)
                .build();
    }

    @BeforeEach
    void setUp() {
        controller = new FilmController(new FilmService(new InMemoryFilmStorage(), new UserService(new InMemoryUserStorage())));
        film = Film.builder()
                .name("Супер боевик")
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                        "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
                        "а именно 20 миллионов. о Куглов, который за время отсу")
                .releaseDate(LocalDate.of(1896, 12, 28))
                .duration(120)
                .build();
    }

    @Test
    void getEmptyListOfFilms() {
        List<Film> films = controller.get();
        assertNotNull(films);
        assertEquals(0, films.size());
    }

    @Test
    void getListOfFilms() {
        controller.create(film);
        List<Film> films = controller.get();
        assertNotNull(films);
        assertEquals(1, films.size());
    }

    @Test
    void createAndValidateFilmInvalidName() {
        Throwable thrown = catchThrowable(() -> {
            controller.create(filmInvalidName);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void createAndValidateFilmInvalidDescription() {
        Throwable thrown = catchThrowable(() -> {
            controller.create(filmInvalidDescription);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void createAndValidateFilmInvalidReleaseDate() {
        Throwable thrown = catchThrowable(() -> {
            controller.create(filmInvalidReleaseDate);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void createAndValidateFilmInvalidDuration() {
        Throwable thrown = catchThrowable(() -> {
            controller.create(filmInvalidDuration);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void createAndUpdateFilmNoErrors() {
        controller.create(film);
        List<Film> films = controller.get();
        assertNotNull(films);
        assertEquals(1, films.size());
        film.setDuration(240);
        controller.update(film);
        assertNotNull(films);
        assertEquals(1, films.size());
    }

    @Test
    void createAndUpdateFilmInvalidName() {
        controller.create(film);
        List<Film> films = controller.get();
        assertNotNull(films);
        assertEquals(1, films.size());
        film.setName("");
        Throwable thrown = catchThrowable(() -> {
            controller.update(film);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void createAndUpdateFilmInvalidDescription() {
        controller.create(film);
        List<Film> films = controller.get();
        assertNotNull(films);
        assertEquals(1, films.size());
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
                "а именно 20 миллионов. о Куглов, который за время отсут");
        Throwable thrown = catchThrowable(() -> {
            controller.update(film);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void createAndUpdateFilmInvalidReleaseDate() {
        controller.create(film);
        List<Film> films = controller.get();
        assertNotNull(films);
        assertEquals(1, films.size());
        film.setReleaseDate(LocalDate.of(1890, 12, 28));
        Throwable thrown = catchThrowable(() -> {
            controller.update(film);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void createAndUpdateFilmInvalidDuration() {
        controller.create(film);
        List<Film> films = controller.get();
        assertNotNull(films);
        assertEquals(1, films.size());
        film.setDuration(-100);
        Throwable thrown = catchThrowable(() -> {
            controller.update(film);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void createAndUpdateFilmInvalidId() {
        controller.create(film);
        List<Film> films = controller.get();
        assertNotNull(films);
        assertEquals(1, films.size());
        film.setId(2);
        Throwable thrown = catchThrowable(() -> {
            controller.update(film);
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }
}
