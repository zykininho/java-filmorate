# java-filmorate
Repository for Filmorate project.

Entity-Relationship Diagram:

![ERD of Filmorate](https://github.com/zykininho/java-filmorate/blob/main/Filmorate%20ERD.jpg)

Examples for SQL-queries:

1. to get info about user with id=1

  *SELECT *
  FROM USER
  WHERE user_id = 1*
  
2. to get likes from users for film with id=13

  *SELECT user_id
  FROM likes
  WHERE film_id = 13*
  
3. to get films with rating 'PG'

  *SELECT f.name
  FROM film AS f
  INNER JOIN rating AS r ON f.Rating_ID = r.Rating_ID
  WHERE r.name = 'PG'*
