package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAll() {
        List<Genre> genres = new ArrayList<>();
        String sql = "select * from genres";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql);
        while (genreRows.next()) {
            genres.add(makeGenre(genreRows));
        }
        log.info("Количество жанров фильмов в базе: {}", genres.size());
        return genres;
    }

    @Override
    public Genre findGenreById(Integer genreId) {
        String sql = "select * from genres where id = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql, genreId);
        if (genreRows.next()) {
            Genre genre = makeGenre(genreRows);
            log.info("Найден жанр фильма в базе: {}", genre);
            return genre;
        } else {
            log.info("В базе отсутствует жанр фильма с id: {}", genreId);
            throw new NotFoundException();
        }
    }

    private Genre makeGenre(SqlRowSet genreRows) {
        Genre genre = Genre.builder()
                .id(genreRows.getInt("id"))
                .name(genreRows.getString("name"))
                .build();
        return genre;
    }
}