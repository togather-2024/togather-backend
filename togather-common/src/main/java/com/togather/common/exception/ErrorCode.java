package com.togather.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    JOIN_DUPLICATE_MEMBER("member.join.duplicate.id"),
    INTERNAL_SERVER_ERROR("fail"),
    NOT_FOUND_BOOKMARK("partyroom.bookmark.not_found");

    private final String messageKey;
}
