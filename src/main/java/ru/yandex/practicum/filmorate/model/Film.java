package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

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
    private Set<Genre> genres;
    private Rating mpa;

    public void addLike(Integer userId) {
        this.likesByUsers.add(userId);
    }
    public void deleteLike(Integer userId) {
        this.likesByUsers.remove(userId);
    }
}