package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id;

    @GetMapping("/films")
    public List<Film> get() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films.values().parallelStream().collect(Collectors.toList());
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        boolean isCorrect = validate(film);
        if (!isCorrect) {
            throw new ValidationException();
        }
        film.setId(++this.id);
        films.put(film.getId(), film);
        log.debug("Добавлен новый фильм: {}", film);
        return film;
    }

    public static boolean validate(Film film) {
        String name = film.getName();
        String description = film.getDescription();
        LocalDate releaseDate = film.getReleaseDate();
        long duration = film.getDuration();
        if (name == null || name.isEmpty()) {
            log.debug("Название фильма пустое");
            return false;
        } else if (description.length() > 200) {
            log.debug("Описание фильма {} больше 200 символов", name);
            return false;
        } else if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Дата релиза фильма {} раньше 28 декабря 1895 года", name);
            return false;
        } else if (duration < 0) {
            log.debug("Продолжительность фильма {} отрицательная", name);
            return false;
        }
        return true;
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) throws ValidationException {
        int filmId = film.getId();
        if (!films.containsKey(filmId)) {
            log.debug("Не найден фильм в списке с id: {}", filmId);
            throw new ValidationException();
        }
        films.put(filmId, film);
        log.debug("Обновлены данные фильма с id {}. Новые данные: {}", filmId, film);
        return film;
    }
}
