create table files
(
    id      bigint       not null auto_increment,
    path    varchar(255) not null,
    user_id bigint       not null,

    primary key (id),
    foreign key (user_id)
        references users (id)
        on delete cascade
)