package ru.yandex.practicum.filmorate.storage.film;

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
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> get() {
        List<Film> films = new ArrayList<>();
        String sql = "select * from films";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql);
        if (filmRows.next()) {
            films.add(makeFilm(filmRows));
        } else {
            log.info("В списке фильмов отсутствуют записи");
            return films;
        }
        log.info("Количество фильмов в базе: {}", films.size());
        return films;
    }

    private Film makeFilm(SqlRowSet filmRows) {
        int filmId = filmRows.getInt("id");
        String releaseDate = filmRows.getString("release_date");
        String[] partDate = releaseDate.split("-");
        Map<String, Integer> filmRating = new HashMap<>();
        filmRating.put("id", filmRows.getInt("rating_id"));
        Film film = Film.builder()
                .id(filmId)
                .name(filmRows.getString("name"))
                .description(filmRows.getString("description"))
                .releaseDate(LocalDate.of(Integer.parseInt(partDate[0]),
                        Integer.parseInt(partDate[1]),
                        Integer.parseInt(partDate[2])))
                .duration(filmRows.getLong("duration"))
                .likesByUsers(getLikes(filmId))
                .genre(getFilmGenre(filmId))
                .mpa(filmRating)
                .build();
        return film;
    }

    private Set<Integer> getFilmGenre(int filmId) {
        Set<Integer> filmGenre = new HashSet<>();
        String sql = "select genre_id from film_genres where film_id = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql, filmId);
        if (genreRows.next()) {
            filmGenre.add(genreRows.getInt("genre_id"));
        }
        return filmGenre;
    }

    private Set<Integer> getLikes(int filmId) {
        Set<Integer> userLikes = new HashSet<>();
        String sql = "select user_id from likes where film_id = ?";
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet(sql, filmId);
        if (likesRows.next()) {
            userLikes.add(likesRows.getInt("user_id"));
        }
        return userLikes;
    }

    @Override
    public Film create(Film film) {
        validate(film);
        if (film.getLikesByUsers() == null) {
            film.setLikesByUsers(new HashSet<>());
        }
        if (film.getGenre() == null) {
            film.setGenre(new HashSet<>());
        }
        String sqlQuery = "insert into films (name, description, release_date, duration, rating_id) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setString(3, film.getReleaseDate().toString());
                stmt.setLong(4, film.getDuration());
                stmt.setInt(5, film.getMpa().get("id"));
                return stmt;
            }
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    private static void validate(Film film) {
        String name = film.getName();
        String description = film.getDescription();
        LocalDate releaseDate = film.getReleaseDate();
        long duration = film.getDuration();
        if (name == null || name.isEmpty()) {
            log.info("Название фильма пустое");
            throw new ValidationException();
        } else if (description.length() > 200) {
            log.info("Описание фильма {} больше 200 символов", name);
            throw new ValidationException();
        } else if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Дата релиза фильма {} раньше 28 декабря 1895 года", name);
            throw new ValidationException();
        } else if (duration < 0) {
            log.info("Продолжительность фильма {} отрицательная", name);
            throw new ValidationException();
        }
    }

    @Override
    public Film update(Film film) {
        validate(film);
        if (film.getLikesByUsers() == null) {
            film.setLikesByUsers(new HashSet<>());
        }
        if (film.getGenre() == null) {
            film.setGenre(new HashSet<>());
        }
        String sqlQuery = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?" +
                "where id = ?";
        int totalUpdate = jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getMpa().get("id")
                , film.getId());
        if (totalUpdate == 0) {
            throw new NotFoundException();
        }
        log.info("Обновлено записей: {}", totalUpdate);
        return film;
    }

    @Override
    public Film getFilmById(Integer filmId) {
        String sql = "select * from films where id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, filmId);
        if (filmRows.next()) {
            Film film = makeFilm(filmRows);
            log.info("Найден фильм в базе: {}", film);
            return film;
        } else {
            log.info("В списке отсутствует фильм с id: {}", filmId);
            throw new NotFoundException();
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
        List<Film> films = new ArrayList<>();
        String sql = "select * from films where id in (select film_id from films group by film_id order by count(user_id) desc limit ?)";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, count);
        if (filmRows.next()) {
            films.add(makeFilm(filmRows));
        } else {
            log.info("В списке фильмов отсутствуют записи");
            return films;
        }
        log.info("Количество фильмов в базе: {}", films.size());
        return films;
    }
}
