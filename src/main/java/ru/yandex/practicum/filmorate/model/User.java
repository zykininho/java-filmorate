package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class User {
    private int id;
    @Email
    @NotNull
    @NotBlank
    private String email;
    @NotNull
    @NotBlank
    private String login;
    private String name;
    private LocalDate birthday;
    private Map<Integer, Boolean> friends; // список друзей со статусом

    public void addFriend(Integer friendId) {
        this.friends.put(friendId, false);
    }

    public void deleteFromFriends(Integer friendId) {
        this.friends.remove(friendId);
    }
}
