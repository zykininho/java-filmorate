package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.*;
import ru.yandex.practicum.filmorate.model.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

@SpringBootTest
class FilmorateApplicationTests {

	@Test
	void validateFilm() {
		Film film = Film.builder()
				.name("Супер боевик")
				.description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
						"Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
						"а именно 20 миллионов. о Куглов, который за время отсу")
				.releaseDate(LocalDate.of(1895, 12, 28))
				.duration(0)
				.build();
		assertEquals(true, FilmController.validate(film));
	}

	@Test
	void validateFilmInvalidName() {
		Film film = Film.builder()
				.description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
						"Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
						"а именно 20 миллионов. о Куглов, который за время отсу")
				.releaseDate(LocalDate.of(1896, 12, 28))
				.duration(120)
				.build();
		assertEquals(false, FilmController.validate(film));
	}

	@Test
	void validateFilmInvalidDescription() {
		Film film = Film.builder()
				.name("Супер боевик")
				.description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
						"Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
						"а именно 20 миллионов. о Куглов, который за время отсут")
				.releaseDate(LocalDate.of(1896, 12, 28))
				.duration(120)
				.build();
		assertEquals(false, FilmController.validate(film));
	}

	@Test
	void validateFilmInvalidReleaseDate() {
		Film film = Film.builder()
				.name("Супер боевик")
				.description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
						"Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
						"а именно 20 миллионов. о Куглов, который за время отсу")
				.releaseDate(LocalDate.of(1890, 12, 28))
				.duration(120)
				.build();
		assertEquals(false, FilmController.validate(film));
	}

	@Test
	void validateFilmInvalidDuration() {
		Film film = Film.builder()
				.name("Супер боевик")
				.description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
						"Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
						"а именно 20 миллионов. о Куглов, который за время отсу")
				.releaseDate(LocalDate.of(1896, 12, 28))
				.duration(-120)
				.build();
		assertEquals(false, FilmController.validate(film));
	}

	@Test
	void validateUser() {
		User user = User.builder()
				.email("my@email.com")
				.login("Login")
				.birthday(LocalDate.of(2023, 01, 18))
				.build();
		assertEquals(true, UserController.validate(user));
	}

	@Test
	void validateUserInvalidEmail() {
		User user = User.builder()
				.email("email.com")
				.login("Login")
				.birthday(LocalDate.of(2022, 12, 12))
				.build();
		assertEquals(false, UserController.validate(user));
	}

	@Test
	void validateUserInvalidLogin() {
		User user = User.builder()
				.email("my@email.com")
				.login("Log in")
				.birthday(LocalDate.of(2022, 12, 12))
				.build();
		assertEquals(false, UserController.validate(user));
	}

	@Test
	void validateUserInvalidBirthday() {
		User user = User.builder()
				.email("my@email.com")
				.login("Login")
				.birthday(LocalDate.of(2023, 12, 12))
				.build();
		assertEquals(false, UserController.validate(user));
	}
}
