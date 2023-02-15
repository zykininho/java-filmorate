package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.List;

@Service
public class RatingService {

    private final RatingStorage ratingStorage;

    @Autowired
    public RatingService(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public List<Rating> findAll() {
        return ratingStorage.findAll();
    }

    public Rating findRatingById(Integer ratingId) {
        return ratingStorage.findRatingById(ratingId);
    }
}
