alter table party_room
    modify party_room_id bigint auto_increment comment 'PK';

alter table party_room
    auto_increment = 1;

alter table party_room_custom_tags
    modify tag_id bigint auto_increment comment 'PK';

alter table party_room_custom_tags
    auto_increment = 1;

alter table party_room_images
    modify party_room_image_id bigint auto_increment comment 'PK';

alter table party_room_images
    auto_increment = 1;

alter table party_room_location
    modify party_room_location_id bigint auto_increment comment 'PK';

alter table party_room_location
    auto_increment = 1;

alter table party_room_operation_days
    modify operation_days_id bigint auto_increment comment 'PK';

alter table party_room_operation_days
    auto_increment = 1;

alter table party_room_reservation
    modify reservation_id bigint auto_increment comment 'PK';

alter table party_room_reservation
    auto_increment = 1;

alter table party_room_review
    modify review_id bigint auto_increment comment 'PK';

alter table party_room_review
    auto_increment = 1;


alter table party_room_tags_rel
    modify party_room_tag_id bigint auto_increment comment 'PK';

alter table party_room_tags_rel
    auto_increment = 1;


alter table email_verification
    modify email_verification_id bigint auto_increment comment 'PK';

alter table email_verification
    auto_increment = 1;


alter table member
    modify member_srl bigint auto_increment comment 'PK';

alter table member
    auto_increment = 1;