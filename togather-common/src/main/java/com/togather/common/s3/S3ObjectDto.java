package com.togather.common.s3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class S3ObjectDto {
    private final String fileKey;
    private final String S3ResourceUrl;
}
