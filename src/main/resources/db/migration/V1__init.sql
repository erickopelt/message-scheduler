CREATE TABLE MESSAGE
(
    id        varchar(36) primary key,
    schedule  timestamp,
    recipient varchar(255),
    body      varchar(2000),
    channel   varchar(30),
    status    varchar(30)
)