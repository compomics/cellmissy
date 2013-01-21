CREATE TABLE user
(
   userid int PRIMARY KEY NOT NULL,
   email varchar(255) NOT NULL,
   first_name varchar(255) NOT NULL,
   last_name varchar(255) NOT NULL,
   password varchar(255) NOT NULL,
   role varchar(255) NOT NULL
)
;