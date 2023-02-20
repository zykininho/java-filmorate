package ru.yandex.practicum.filmorate.storage.rating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class RatingDbStorage implements RatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Rating> findAll() {
        List<Rating> ratings = new ArrayList<>();
        String sql = "select * from ratings";
        SqlRowSet ratingRows = jdbcTemplate.queryForRowSet(sql);
        while (ratingRows.next()) {
            ratings.add(makeRating(ratingRows));
        }
        log.info("Количество рейтингов фильмов в базе: {}", ratings.size());
        return ratings;
    }

    @Override
    public Rating findRatingById(Integer ratingId) {
        String sql = "select * from ratings where id = ?";
        SqlRowSet ratingRows = jdbcTemplate.queryForRowSet(sql, ratingId);
        if (ratingRows.next()) {
            Rating rating = makeRating(ratingRows);
            log.info("Найден рейтинг фильма в базе: {}", rating);
            return rating;
        } else {
            log.info("В базе отсутствует рейтинг фильма с id: {}", ratingId);
            throw new NotFoundException();
        }
    }

    private Rating makeRating(SqlRowSet ratingRows) {
        Rating rating = Rating.builder()
                .id(ratingRows.getInt("id"))
                .name(ratingRows.getString("name"))
                .build();
        return rating;
    }
}