create table events
(
    id          bigint      not null auto_increment,
    upload_date varchar(20) not null,
    user_id     bigint      not null,
    file_id     bigint      not null,

    primary key (id),
    foreign key (user_id)
        references users (id)
        on delete cascade,
    foreign key (file_id)
        references files (id)
        on delete cascade
)