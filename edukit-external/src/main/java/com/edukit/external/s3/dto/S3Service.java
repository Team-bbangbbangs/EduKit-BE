package com.edukit.external.s3.dto;

import com.edukit.external.s3.AwsS3Properties;
import com.edukit.external.s3.dto.exception.S3ErrorCode;
import com.edukit.external.s3.dto.exception.S3Exception;
import com.edukit.external.s3.dto.response.PresignedUrlCreateResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final AwsS3Properties s3Properties;

    private static final Pattern FILENAME_PATTERN = Pattern.compile("^.+\\..+$");
    private static final Duration PRESIGNED_URL_EXPIRATION = Duration.ofMinutes(5);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final Map<String, String> CONTENT_TYPE_MAP = Map.of(
            "png", "image/png",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "gif", "image/gif",
            "webp", "image/webp"
    );

    public PresignedUrlCreateResponse createPresignedUrl(String path, String filename) {
        validateFilename(filename);
        String key = generateS3Key(path, filename);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.bucket())
                .key(key)
                .contentType(getContentType(filename))
                .build();
        PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(PRESIGNED_URL_EXPIRATION)
                .putObjectRequest(putObjectRequest)
                .build();
        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(putObjectPresignRequest);
        return PresignedUrlCreateResponse.of(
                presignedPutObjectRequest.url().toString(),
                getImageUrl(key)
        );
    }
    private void validateFilename(String filename) {
        if (!StringUtils.hasText(filename) || !FILENAME_PATTERN.matcher(filename).matches()) {
            throw new S3Exception(S3ErrorCode.INVALID_FILE_NAME);
        }
    }

    private String generateS3Key(String folder, String filename) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
        String extension = extractExtension(filename);
        return String.format("%s/%s_%s%s", folder, timestamp, uuid, extension);
    }

    private String extractExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return (idx != -1) ? filename.substring(idx) : "";
    }

    private String getContentType(String filename) {
        String ext = extractExtension(filename).toLowerCase().replace(".", "");
        if (!CONTENT_TYPE_MAP.containsKey(ext)) {
            throw new S3Exception(S3ErrorCode.INVALID_FILE_EXTENSION);
        }
        return CONTENT_TYPE_MAP.get(ext);
    }

    private String getImageUrl(String key) {
        return String.format("%s/%s", s3Properties.cdnUrl(), key);
    }
}
