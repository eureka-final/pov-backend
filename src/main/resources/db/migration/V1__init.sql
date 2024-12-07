-- 1. 독립적인 테이블 먼저 생성
CREATE TABLE common_code_group
(
    common_code_group_description varchar(255) null,
    common_code_group_name        varchar(255) null,
    is_active                     bit          not null,
    group_code                    varchar(255) not null primary key
);

CREATE TABLE movie
(
    id          bigint auto_increment primary key,
    released    date         null,
    tmdb_id     int          null unique,
    plot        varchar(255) null,
    poster      varchar(255) null,
    title       varchar(255) null,
    backdrop    varchar(255) null,
    film_rating varchar(10)  null
);

CREATE TABLE club
(
    id               binary(16)   not null primary key,
    is_public        bit          not null,
    max_participants int          null,
    created_at       datetime     null,
    description      varchar(255) null,
    name             varchar(255) null unique,
    club_image       varchar(255) null
);

CREATE TABLE member
(
    birth            date                     null,
    is_notice_active bit                      not null,
    created_at       datetime                 null,
    deleted_at       datetime                 null,
    id               binary(16)               not null primary key,
    email            varchar(255)             null,
    nickname         varchar(255)             null unique,
    profile_image    varchar(255)             null,
    role_type        enum ('ADMIN', 'USER')   null,
    social_type      enum ('GOOGLE', 'NAVER') null
);

CREATE TABLE premiere
(
    is_payment_required bit          not null,
    id                  bigint auto_increment primary key,
    start_at            datetime     null,
    content             text         null,
    event_image         varchar(255) null,
    title               varchar(255) null
);

-- 2. 외래키를 가진 테이블들 생성
CREATE TABLE common_code
(
    code                    varchar(255) not null,
    common_code_description varchar(255) null,
    common_code_name        varchar(255) null,
    group_code              varchar(255) not null,
    is_active               bit          not null,
    PRIMARY KEY (group_code, code),
    CONSTRAINT FK_common_code_group FOREIGN KEY (group_code) REFERENCES common_code_group (group_code)
);

CREATE TABLE people
(
    id        bigint auto_increment primary key,
    name      varchar(255) null,
    image_url varchar(255) null,
    tmdb_id   int          null unique
);

CREATE TABLE movie_crew
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    people_id BIGINT NOT NULL,
    movie_id  BIGINT NOT NULL,
    role      VARCHAR(255),
    CONSTRAINT fk_movie_crew_people FOREIGN KEY (people_id) REFERENCES people (id) ON DELETE CASCADE,
    CONSTRAINT fk_movie_crew_movie FOREIGN KEY (movie_id) REFERENCES movie (id) ON DELETE CASCADE
);

CREATE TABLE movie_cast
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    people_id     BIGINT NOT NULL,
    movie_id      BIGINT NOT NULL,
    role_name     VARCHAR(255),
    display_order INT,
    CONSTRAINT fk_movie_cast_people FOREIGN KEY (people_id) REFERENCES people (id) ON DELETE CASCADE,
    CONSTRAINT fk_movie_cast_movie FOREIGN KEY (movie_id) REFERENCES movie (id) ON DELETE CASCADE
);


CREATE TABLE member_club
(
    is_leader  bit        not null,
    created_at datetime   null,
    id         bigint auto_increment primary key,
    club_id    binary(16) null,
    member_id  binary(16) null,
    CONSTRAINT FK_member_club_club FOREIGN KEY (club_id) REFERENCES club (id),
    CONSTRAINT FK_member_club_member FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE club_favor_genre
(
    genre_code varchar(2) null,
    id         bigint auto_increment primary key,
    club_id    binary(16) not null,
    CONSTRAINT FK_club_favor_genre_club FOREIGN KEY (club_id) REFERENCES club (id)
);

CREATE TABLE club_movie
(
    created_at datetime   null,
    id         bigint auto_increment primary key,
    movie_id   bigint     not null,
    club_id    binary(16) not null,
    CONSTRAINT FK_club_movie_movie FOREIGN KEY (movie_id) REFERENCES movie (id),
    CONSTRAINT FK_club_movie_club FOREIGN KEY (club_id) REFERENCES club (id)
);

CREATE TABLE curation
(
    created_at  datetime                                                                    null,
    id          bigint auto_increment primary key,
    member_id   binary(16)                                                                  not null,
    description varchar(255)                                                                null,
    theme       varchar(255)                                                                null,
    title       varchar(255)                                                                null,
    category    enum ('ACTOR', 'AWARD', 'COUNTRY', 'DIRECTOR', 'GENRE', 'OTHER', 'RELEASE') null,
    start_time  datetime                                                                    null,
    CONSTRAINT FK_curation_member FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE entry
(
    amount      int        null,
    quantity    int        null,
    created_at  datetime   null,
    id          bigint auto_increment primary key,
    premiere_id bigint     not null,
    member_id   binary(16) not null,
    CONSTRAINT FK_entry_member FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT FK_entry_premiere FOREIGN KEY (premiere_id) REFERENCES premiere (id)
);

CREATE TABLE member_favor_genre
(
    genre_code varchar(2) null,
    id         bigint auto_increment primary key,
    member_id  binary(16) not null,
    CONSTRAINT FK_member_favor_genre_member FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE movie_content
(
    id           bigint auto_increment primary key,
    movie_id     bigint                    null,
    content      varchar(255)              null,
    content_type enum ('IMAGE', 'YOUTUBE') null,
    CONSTRAINT FK_movie_content_movie FOREIGN KEY (movie_id) REFERENCES movie (id)
);

CREATE TABLE movie_genre
(
    genre_code varchar(2) null,
    id         bigint auto_increment primary key,
    movie_id   bigint     null,
    CONSTRAINT FK_movie_genre_movie FOREIGN KEY (movie_id) REFERENCES movie (id)
);

CREATE TABLE movie_like
(
    created_at datetime   null,
    id         bigint auto_increment primary key,
    movie_id   bigint     null,
    member_id  binary(16) null,
    is_liked   boolean default false,
    CONSTRAINT FK_movie_like_member FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT FK_movie_like_movie FOREIGN KEY (movie_id) REFERENCES movie (id)
);

CREATE TABLE movie_like_count
(
    like_count bigint null,
    movie_id   bigint not null primary key,
    CONSTRAINT FK_movie_like_count_movie FOREIGN KEY (movie_id) REFERENCES movie (id)
);

CREATE TABLE daily_movie_like
(
    id               bigint auto_increment primary key,
    movie_id         bigint   null,
    total_like_count bigint   null,
    created_at       datetime null,
    CONSTRAINT FK_daily_movie_like_movie FOREIGN KEY (movie_id) REFERENCES movie (id)
);

create table payment
(
    amount      int          null,
    approved_at datetime     null,
    created_at  datetime     null,
    id          bigint auto_increment
        primary key,
    member_id   binary(16)   null,
    payment_key varchar(255) null,
    vendor      varchar(255) null,
    constraint FK_payment_member
        foreign key (member_id) references member (id)
);

create table payment_transaction
(
    amount          int                                   null,
    created_at      datetime                              null,
    id              bigint auto_increment
        primary key,
    payment_id      bigint                                null,
    transaction_key varchar(255)                          null,
    status          enum ('FAILED', 'PENDING', 'SUCCESS') null,
    type            enum ('CANCEL', 'PAY', 'REFUND')      null,
    constraint UK_payment_transaction
        unique (payment_id),
    constraint FK_payment_transaction_payment
        foreign key (payment_id) references payment (id)
);

create table temp_payment
(
    amount     int             null,
    created_at datetime        null,
    id         bigint auto_increment
        primary key,
    member_id  binary(16)      null,
    order_id   varchar(255)    null,
    type       enum ('NORMAL') null,
    constraint UK_temp_payment_member
        unique (member_id),
    constraint FK_temp_payment_member
        foreign key (member_id) references member (id)
);

CREATE TABLE recommended_movie
(
    created_at datetime   null,
    id         bigint auto_increment primary key,
    movie_id   bigint     null,
    member_id  binary(16) null,
    CONSTRAINT FK_recommended_movie_movie FOREIGN KEY (movie_id) REFERENCES movie (id),
    CONSTRAINT FK_recommended_movie_member FOREIGN KEY (member_id) REFERENCES member (id)
);

create table review
(
    disabled    bit          not null,
    is_spoiler  bit          not null,
    created_at  datetime     null,
    deleted_at  datetime     null,
    id          bigint auto_increment
        primary key,
    modified_at datetime     null,
    member_id   binary(16)   null,
    contents    text         null,
    title       varchar(255) null,
    preference  varchar(255) null,
    thumbnail   varchar(255) null,
    movie_id    bigint       not null,
    CONSTRAINT FK_review_member FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT FK_review_movie FOREIGN KEY (movie_id) REFERENCES movie (id)
);

CREATE TABLE review_keyword_link
(
    review_keyword_code varchar(2) null,
    id                  bigint auto_increment primary key,
    review_id           bigint     null,
    CONSTRAINT FK_review_keyword_link_review FOREIGN KEY (review_id) REFERENCES review (id)
);

CREATE TABLE review_like
(
    created_at datetime   null,
    id         bigint auto_increment primary key,
    review_id  bigint     null,
    member_id  binary(16) null,
    is_liked   boolean default false,
    CONSTRAINT FK_review_like_review FOREIGN KEY (review_id) REFERENCES review (id),
    CONSTRAINT FK_review_like_member FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE review_like_count
(
    review_id         bigint not null primary key,
    review_like_count bigint null,
    CONSTRAINT FK_review_like_count_review FOREIGN KEY (review_id) REFERENCES review (id)
);

create table notice
(
    is_active      bit                     not null,
    notice_type    enum ('REVIEW', 'CLUB') null,
    created_at     datetime                null,
    id             bigint auto_increment
        primary key,
    member_id      binary(16)              null,
    description    varchar(255)            null,
    notice_content text                    null,
    notice_title   varchar(255)            null
);

create table notice_receive
(
    is_read        bit                     not null,
    notice_type    enum ('REVIEW', 'CLUB') null,
    created_at     datetime                null,
    id             bigint auto_increment
        primary key,
    notice_send_id bigint                  null,
    member_id      binary(16)              null,
    notice_content text                    null,
    notice_title   varchar(255)            null,
    constraint FK_notice_receive_member
        foreign key (member_id) references member (id)
);

create table notice_send
(
    is_succeed            bit      not null,
    created_at            datetime null,
    id                    bigint auto_increment
        primary key,
    notice_id             bigint   null,
    notice_content_detail text     null,
    constraint FK_notice_send_notice
        foreign key (notice_id) references notice (id)
);

create table country
(
    id   bigint auto_increment primary key,
    name varchar(255) not null
);

create table movie_country
(
    id         bigint auto_increment primary key,
    country_id bigint not null,
    movie_id   bigint not null,
    constraint FK_country foreign key (country_id) references country (id),
    constraint FK_movie foreign key (movie_id) references movie (id)
)

