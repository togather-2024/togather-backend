package com.togather.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    // Join
    JOIN_DUPLICATE_MEMBER("member.join.duplicate.id"),


    // Review
    REVIEW_NOT_FOUND("review.not.found"),
    REVIEW_DIFFERENT_MEMBER("review.different.member"),
    REVIEW_RESERVATION_NOT_COMPLETE("review.reservation.not.complete"),
    DUPLICATE_REVIEW_TRIAL("review.already.finished"),

    INTERNAL_SERVER_ERROR("fail");

    private final String messageKey;
}
