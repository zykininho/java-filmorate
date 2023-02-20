package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class RatingController {

    private final RatingService ratingService;

    @GetMapping("/mpa")
    public List<Rating> findAll() {
        return ratingService.findAll();
    }

    @GetMapping("/mpa/{id}")
    public Rating findRatingById(@PathVariable("id") Integer ratingId) {
        return ratingService.findRatingById(ratingId);
    }
}