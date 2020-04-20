CREATE TABLE USERS (
    id serial primary key,
    email varchar(100) unique,
    firebaseId varchar(100),
)

