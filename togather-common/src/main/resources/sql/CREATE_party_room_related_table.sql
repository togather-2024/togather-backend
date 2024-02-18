CREATE TABLE IF NOT EXISTS `party_room`(
        `party_room_id`             bigint              COMMENT 'PK',
        `party_room_name`           varchar(100)        COMMENT 'party_room_name',
        `party_room_host_srl`       bigint              COMMENT 'member_srl of party_room host',
        `party_room_desc`           text                COMMENT 'description of party_room',
        `party_room_view_count`     bigint              COMMENT 'view count of party_room',
        `opening_hour`              int                 COMMENT 'opening time of party_room. value: 0-24',
        `closing_hour`              int                 COMMENT 'closing time of party_room. value: 0-24',
        `price`                     bigint              COMMENT 'price of party_room per hour',
        `guest_capacity`            int                 COMMENT 'maximum number of guests party_room can allow',
        PRIMARY KEY (`party_room_id`),
        INDEX (`party_room_name`),
        INDEX (`party_room_host_srl`),
        INDEX (`party_room_view_count`),
        INDEX (`opening_hour`, `closing_hour`, `price`, `guest_capacity`)
);

CREATE TABLE IF NOT EXISTS `party_room_location` (
        `party_room_location_id`    bigint              COMMENT 'PK',
        `party_room_id`             bigint              COMMENT 'party_room_pk',
        `sido`                      varchar(100)        COMMENT 'sido of party_room',
        `sigungu`                   varchar(100)        COMMENT 'sigungu of party_room',
        `road_name`                 varchar(255)        COMMENT 'road name of party_room',
        `road_address`              varchar(255)        COMMENT 'road address of party_room',
        `jibun_address`             varchar(255)        COMMENT 'jibun address of party_room',
        PRIMARY KEY (`party_room_location_id`),
        INDEX (`party_room_id`),
        INDEX (`sido`, `sigungu`, `road_name`)
);

CREATE TABLE IF NOT EXISTS `party_room_images` (
        `party_room_image_id`       bigint              COMMENT 'PK',
        `party_room_id`             bigint              COMMENT 'party_room PK',
        `image_file_name`           varchar(255)        COMMENT 'file name of image',
        `type`                      varchar(100)        COMMENT 'type of file. allow only one Main image per party room',
        PRIMARY KEY (`party_room_image_id`),
        INDEX (`party_room_id`)
);

CREATE TABLE IF NOT EXISTS `party_room_reservation` (
        `reservation_id`            bigint              COMMENT 'PK',
        `party_room_id`             bigint              COMMENT 'PK of reservation target party room',
        `guest_srl`                 bigint              COMMENT 'member_srl of reservation guest',
        `guest_count`               int                 COMMENT 'number of guests using this party room',
        `start_time`                datetime            COMMENT 'start time of reservation',
        `end_time`                  datetime            COMMENT 'end time of reservation',
        `payment_status`            varchar(100)        COMMENT 'payment status',
        PRIMARY KEY (`reservation_id`),
        INDEX (`party_room_id`),
        INDEX (`guest_srl`)
);

CREATE TABLE IF NOT EXISTS `party_room_operation_days` (
        `operation_days_id`         bigint              COMMENT 'PK',
        `party_room_id`             bigint              COMMENT 'PK of party room',
        `operation_day`             varchar(20)         COMMENT 'one of SUNDAY to SATURDAY',
        PRIMARY KEY (`operation_days_id`),
        INDEX (`party_room_id`)
);

CREATE TABLE IF NOT EXISTS `party_room_review` (
        `review_id`                 bigint          COMMENT 'PK',
        `party_room_id`             bigint          COMMENT 'PK of review party room',
        `reviewer_srl`              bigint          COMMENT 'member_srl of reviewer',
        `review_desc`               text            COMMENT 'review text',
        `created_at`                datetime        COMMENT 'datetime of review creation',
        PRIMARY KEY (`review_id`),
        INDEX (`party_room_id`),
        INDEX  (`reviewer_srl`)
);

CREATE TABLE IF NOT EXISTS `party_room_custom_tags` (
        `tag_id`                    bigint          COMMENT 'PK',
        `tag_content`               varchar(100)    COMMENT 'content of custom tag',
        `tag_count`                 bigint          COMMENT 'number of party rooms with this tag',
        PRIMARY KEY (`tag_id`),
        INDEX (`tag_count`)
);

CREATE TABLE `party_room_tags_rel` (
        `party_room_tag_id`         bigint          COMMENT 'PK',
        `party_room_id`             bigint          COMMENT 'pk of party room',
        `tag_id`                    bigint          COMMENT 'pk of custom tag',
        PRIMARY KEY (`party_room_tag_id`),
        INDEX (`party_room_id`)
);