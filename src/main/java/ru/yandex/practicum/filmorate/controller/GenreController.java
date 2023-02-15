package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/genres")
    public List<Genre> findAll() {
        return genreService.findAll();
    }

    @GetMapping("/genres/{id}")
    public Genre findGenreById(@PathVariable("id") Integer genreId) {
        return genreService.findGenreById(genreId);
    }
}
