package com.togather.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class LocalDateTimeUtils {

    private static final String KST_ZONE_ID = "Asia/Seoul";

    public static LocalDateTime convertFromUtc(LocalDateTime utc, ZoneId zoneId) {
        ZonedDateTime zonedDateTime = utc.atZone(ZoneId.of("UTC"));
        return zonedDateTime.withZoneSameInstant(zoneId).toLocalDateTime();
    }

    // UTC로 입력된 시간을 KST로 변환해주는 로직
    public static LocalDateTime convertToKstFromUtc(LocalDateTime utc) {
        return convertFromUtc(utc, ZoneId.of(KST_ZONE_ID));
    }
}
