DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS email_verification;

-- 회원
CREATE TABLE `member` (
                          `member_srl` bigint NOT NULL,
                          `member_name` varchar(50) NOT NULL,
                          `password` varchar(50) NOT NULL,
                          `role` ENUM('GUEST', 'HOST', 'ADMIN') NOT NULL,
                          `email` varchar(50) NOT NULL UNIQUE,
                          `profile_pic_file` varchar(50),
                          `email_verified` boolean,
                          PRIMARY KEY (`member_srl`)
);

-- 이메일 인증
CREATE TABLE `email_verification` (
                                      `email_verification_id` bigint NOT NULL,
                                      `email` varchar(50) NOT NULL,
                                      `verification_code` varchar(50) NOT NULL,
                                      `verification_expiration_time` datetime NOT NULL,
                                      PRIMARY KEY (`email_verification_id`)
);
