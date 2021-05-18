create table users
(
    id       bigint       not null auto_increment,
    username varchar(255) not null unique,
    status   varchar(30),

    primary key (id)
)