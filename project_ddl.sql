create table bowling_center
(
    center_id      bigint auto_increment
        primary key,
    name           varchar(64)               null,
    state          varchar(32)               not null comment '(도 / 광역시)',
    city           varchar(32)               not null comment '(시 / 군 / 구)',
    district       varchar(32)               not null comment '(읍 / 면 / 동)',
    detail_address varchar(32)               not null comment '상세 주소',
    tel_number     varchar(11)               null,
    created_at     timestamp default (now()) not null,
    updated_at     timestamp default (now()) not null on update CURRENT_TIMESTAMP
)
    comment '볼링장';

create table lane
(
    lane_id     bigint auto_increment
        primary key,
    center_id   bigint                              not null,
    lane_number int                                 null comment '해당 볼링장의 레인 순번',
    status      varchar(6)                          null comment '사용 상태: 정상(NORMAL) / 고장(ERROR) / 마감(CLOSED)',
    updated_at  timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint lane_ibfk_1
        foreign key (center_id) references bowling_center (center_id)
)
    comment '센터 내 레인';

create table staff
(
    staff_id      int auto_increment
        primary key,
    center_id     bigint                    null,
    account       varchar(64)               not null,
    pw            varchar(64)               not null,
    name          varchar(8)                null,
    phone_number  varchar(11)               null,
    created_at    timestamp default (now()) not null,
    updated_at    timestamp default (now()) not null on update CURRENT_TIMESTAMP,
    refresh_token varchar(255)              null comment '리프레시 토큰 임시 저장',
    constraint staff_unique
        unique (account),
    constraint staff_ibfk_1
        foreign key (center_id) references bowling_center (center_id)
)
    comment '직원';

create table user
(
    user_id       bigint auto_increment
        primary key,
    account       varchar(24)               not null,
    pw            varchar(64)               not null comment '해시 암호화되어 저장',
    name          varchar(24)               not null,
    nickname      varchar(24)               not null,
    phone_number  varchar(11)               not null,
    created_at    timestamp default (now()) not null,
    updated_at    timestamp default (now()) not null on update CURRENT_TIMESTAMP,
    refresh_token varchar(255)              null comment '리프레시 토큰 임시 저장',
    constraint UK_user_account
        unique (account),
    constraint user_pk
        unique (nickname)
)
    comment '사용자';

create table center_note
(
    note_id    bigint auto_increment
        primary key,
    user_id    bigint                    not null,
    center_id  bigint                    not null,
    content    text                      null,
    created_at timestamp default (now()) not null,
    updated_at timestamp default (now()) not null on update CURRENT_TIMESTAMP,
    constraint center_note_ibfk_1
        foreign key (user_id) references user (user_id),
    constraint center_note_ibfk_2
        foreign key (center_id) references bowling_center (center_id)
);

create index user_id
    on center_note (user_id);

create table favorite_center
(
    favorite_id bigint auto_increment
        primary key,
    user_id     bigint                    not null,
    center_id   bigint                    not null,
    created_at  timestamp default (now()) not null,
    constraint favorite_center_bowling_center_center_id_fk
        foreign key (center_id) references bowling_center (center_id),
    constraint favorite_center_ibfk_1
        foreign key (user_id) references user (user_id),
    constraint favorite_center_ibfk_2
        foreign key (center_id) references bowling_center (center_id)
);

create index user_id
    on favorite_center (user_id);

create table game
(
    game_id      bigint auto_increment
        primary key,
    user_id      bigint                    null,
    center_id    bigint                    null,
    created_at   timestamp default (now()) not null,
    updated_at   timestamp default (now()) not null on update CURRENT_TIMESTAMP,
    total_score  int                       null comment '총 점수',
    strike_count int                       null comment '스트라이크 수',
    spare_count  int                       null comment '스페어 수',
    gutter_count int                       null comment '게터 수',
    constraint game_ibfk_1
        foreign key (user_id) references user (user_id),
    constraint game_ibfk_2
        foreign key (center_id) references bowling_center (center_id)
)
    comment '게임 정보';

create table frame
(
    frame_id     bigint auto_increment
        primary key,
    game_id      bigint               not null,
    frame_number int                  null,
    frame_score  int                  null comment '프레임 누적 점수',
    is_completed tinyint(1) default 0 null comment '프레임 완료 여부',
    constraint frame_UNIQUE
        unique (game_id, frame_number),
    constraint frame_ibfk_1
        foreign key (game_id) references game (game_id)
)
    comment '게임 당 프레임';

create index user_id
    on game (user_id);

create table roll
(
    roll_id     bigint auto_increment
        primary key,
    frame_id    bigint not null,
    roll_number int    null,
    pins        int    null,
    constraint roll_UNIQUE
        unique (frame_id, roll_number),
    constraint roll_ibfk_1
        foreign key (frame_id) references frame (frame_id)
)
    comment '상세 점수';

create table visit_log
(
    log_id     bigint auto_increment
        primary key,
    user_id    bigint                    not null,
    center_id  bigint                    not null,
    created_at timestamp default (now()) not null,
    constraint visit_log_ibfk_1
        foreign key (user_id) references user (user_id),
    constraint visit_log_ibfk_2
        foreign key (center_id) references bowling_center (center_id)
);

create index user_id
    on visit_log (user_id);

create table waiting_queue
(
    queue_id     bigint auto_increment
        primary key,
    user_id      bigint                       not null,
    center_id    bigint                       not null,
    people_count int                          not null,
    order_no     int                          not null comment '대기 순번',
    status       varchar(8) default 'WAITING' not null comment 'WAITING / CANCELED / DONE',
    created_at   timestamp  default (now())   not null,
    constraint waiting_queue_ibfk_1
        foreign key (user_id) references user (user_id),
    constraint waiting_queue_ibfk_2
        foreign key (center_id) references bowling_center (center_id)
);

create table lane_assignment
(
    assign_id   bigint auto_increment
        primary key,
    lane_id     bigint    not null,
    assigned_at timestamp null,
    finished_at timestamp null,
    queue_id    bigint    not null comment '대기열 ID',
    constraint fk_lane_assignment_queue
        foreign key (queue_id) references waiting_queue (queue_id)
            on update cascade on delete cascade,
    constraint lane_assignment_ibfk_1
        foreign key (lane_id) references lane (lane_id)
);

create index user_id
    on waiting_queue (user_id);

create
    definer = admin@`%` procedure generate_lanes()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_center_id BIGINT;
    DECLARE v_lane_count INT;
    DECLARE i INT;

    -- 커서 선언
    DECLARE cur CURSOR FOR
        SELECT center_id, lane_count
        FROM bowling.bowling_center;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO v_center_id, v_lane_count;

        IF done THEN
            LEAVE read_loop;
        END IF;

        SET i = 1;

        WHILE i <= v_lane_count DO
            INSERT INTO bowling.lane (center_id, lane_number, status)
            VALUES (v_center_id, i, 'NORMAL');

            SET i = i + 1;
        END WHILE;

    END LOOP;

    CLOSE cur;
END;

