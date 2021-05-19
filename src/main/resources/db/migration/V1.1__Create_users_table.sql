create table users
(
    id       bigint       not null auto_increment,
    username varchar(255) not null,
    status   varchar(30),

    primary key (id)
)
