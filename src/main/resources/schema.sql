drop table if exists archive;
drop table if exists user;

create table user
(
    id int AUTO_INCREMENT,
    email varchar(255) not null,
    nickname  varchar(25) not null,

    primary key (ID)
);

create table archive
(
    id int AUTO_INCREMENT,
    user_id int not null,
    title varchar(255) not null,
    author varchar(25) not null,
    content text not null,
    image_size varchar(25) not null,
    background_color varchar(25) not null,
    font_style varchar(50) not null,
    font_color varchar(25) not null,
    created_dt datetime not null,
    modified_dt datetime,

    primary key (id),
    foreign key (user_id) REFERENCES user (id)

);
