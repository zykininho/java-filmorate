# java-filmorate
Репозиторий проекта Filmorate. **Ты можешь сохранять, искать и просматривать фильмы, которые нравятся тебе и твоим друзьям больше всего!**

Entity-Relationship Diagram:

![ERD of Filmorate](https://github.com/zykininho/java-filmorate/blob/main/Filmorate%20ERD%20v.4.jpg)

Примеры SQL-запросов:

1. получить информацию по пользователю с id=1

  *SELECT *
  FROM USER
  WHERE user_id = 1*
  
2. узнать, кто поставил лайки среди друзей фильму с id=13

  *SELECT user_id
  FROM likes
  WHERE film_id = 13*
  
3. получить все фильмы с рейтингом 'PG'

  *SELECT f.name
  FROM film AS f
  INNER JOIN rating AS r ON f.Rating_ID = r.Rating_ID
  WHERE r.name = 'PG'*

----------------------------------------------------------------------------------

Стек технологий, используемых в проекте: ***Java 11, H2 Database, Maven, Lombok***
