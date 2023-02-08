package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

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
    private Set<Integer> friends; // список друзей

    public void addFriend(Integer friendId) {
        this.friends.add(friendId);
    }

    public void deleteFromFriends(Integer friendId) {
        this.friends.remove(friendId);
    }
}
