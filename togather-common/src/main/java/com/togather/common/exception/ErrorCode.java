package com.togather.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    // Member
    JOIN_DUPLICATE_MEMBER("member.join.duplicate.id"),
    PREVIOUS_PASSWORD_MISMATCH("member.update.previous_password.mismatch"),
    NEW_PASSWORD_MISMATCH("member.update.new_password.mismatch"),
    PASSWORD_MISMATCH("member.withdraw.password.mismatch"),
    EXIST_RESERVATION("member.withdraw.reservation.exist"),
    HOST_WITHDRAWAL_DISABLED("member.withdraw.role.host"),

    // Email Verification
    EMAIL_SEND_DUPLICATE_MEMBER("email.send.duplicate.member"),
    EMAIL_VERIFY_NO_TRIAL("email.verify.no.trial"),
    EMAIL_VERIFY_EXPIRED_CODE("email.verify.expired.code"),
    EMAIL_VERIFY_WRONG_CODE("email.verify.wrong.code"),


    // Bookmark
    NOT_FOUND_BOOKMARK("partyroom.bookmark.not_found"),
    DUPLICATED_BOOKMARK("partyroom.bookmark.duplicate"),


    // Review
    REVIEW_NOT_FOUND("review.not.found"),
    REVIEW_DIFFERENT_MEMBER("review.different.member"),
    REVIEW_RESERVATION_NOT_COMPLETE("review.reservation.not.complete"),
    DUPLICATE_REVIEW_TRIAL("review.already.finished"),


    INTERNAL_SERVER_ERROR("fail");

    private final String messageKey;
}
