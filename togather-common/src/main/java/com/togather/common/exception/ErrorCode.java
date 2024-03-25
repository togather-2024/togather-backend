package com.togather.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    JOIN_DUPLICATE_MEMBER("member.join.duplicate.id"),
    INTERNAL_SERVER_ERROR("fail");

    private final String messageKey;
}
