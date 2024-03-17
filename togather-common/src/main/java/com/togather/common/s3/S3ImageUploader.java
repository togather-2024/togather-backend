package com.togather.common.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.togather.common.util.FilenameUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3ImageUploader {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public S3ObjectDto uploadFile(MultipartFile file, String fileKey) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try {
            amazonS3Client.putObject(bucketName, fileKey, file.getInputStream(), objectMetadata);
        } catch (IOException | RuntimeException exception) {
            log.error("[S3ImageUploader] failed to upload image.", exception);
            throw new RuntimeException(exception.getMessage(), exception.getCause());
        }
        log.info("[S3ImageUploader] successfully to upload image. fileKey = {}", fileKey);
        return new S3ObjectDto(fileKey, amazonS3Client.getResourceUrl(bucketName, fileKey));
    }

    public S3ObjectDto uploadFileWithRandomFilename(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String ext = FilenameUtils.getExtension(originalFilename);

        String randomFileKey = FilenameUtils.addExtension(UUID.randomUUID().toString(), ext);
        return uploadFile(file, randomFileKey);
    }

    public String getResourceUrl(String fileKey) {
        return amazonS3Client.getResourceUrl(bucketName, fileKey);
    }
}
