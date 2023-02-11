package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.enums.Genre;
import ru.yandex.practicum.filmorate.enums.Rating;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class Film {
    private int id;
    @NotNull
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private Set<Integer> likesByUsers; // список пользователей, кто поставил лайк
    private Set<Genre> genre;
    private Rating rating;

    public void addLike(Integer userId) {
        this.likesByUsers.add(userId);
    }
    public void deleteLike(Integer userId) {
        this.likesByUsers.remove(userId);
    }
}
