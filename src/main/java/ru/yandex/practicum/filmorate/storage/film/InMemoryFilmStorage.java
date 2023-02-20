package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id;

    @Override
    public List<Film> get() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films.values().parallelStream().collect(Collectors.toList());
    }

    @Override
    public Film create(Film film) {
        validate(film);
        film.setId(++this.id);
        if (film.getLikesByUsers() == null) {
            film.setLikesByUsers(new HashSet<Integer>());
        }
        films.put(film.getId(), film);
        log.debug("Добавлен новый фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        validate(film);
        int filmId = film.getId();
        if (!films.containsKey(filmId)) {
            log.debug("Не найден фильм в списке с id: {}", filmId);
            throw new NotFoundException();
        }
        if (film.getLikesByUsers() == null) {
            film.setLikesByUsers(new HashSet<Integer>());
        }
        films.put(filmId, film);
        log.debug("Обновлены данные фильма с id {}. Новые данные: {}", filmId, film);
        return film;
    }

    @Override
    public Film getFilmById(Integer filmId) {
        if (films.containsKey(filmId)) {
            return films.get(filmId);
        } else {
            throw new NotFoundException();
        }
    }

    private static void validate(Film film) {
        String name = film.getName();
        String description = film.getDescription();
        LocalDate releaseDate = film.getReleaseDate();
        long duration = film.getDuration();
        if (name == null || name.isEmpty()) {
            log.debug("Название фильма пустое");
            throw new ValidationException();
        } else if (description.length() > 200) {
            log.debug("Описание фильма {} больше 200 символов", name);
            throw new ValidationException();
        } else if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Дата релиза фильма {} раньше 28 декабря 1895 года", name);
            throw new ValidationException();
        } else if (duration < 0) {
            log.debug("Продолжительность фильма {} отрицательная", name);
            throw new ValidationException();
        }
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        Film film = getFilmById(filmId);
        film.addLike(userId);
        update(film);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        Film film = getFilmById(filmId);
        film.deleteLike(userId);
        update(film);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return films.values().stream()
                .sorted((f0, f1) -> compare(f0, f1))
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film f0, Film f1) {
        int result = f1.getLikesByUsers().size() - f0.getLikesByUsers().size();
        return result;
    }
}
