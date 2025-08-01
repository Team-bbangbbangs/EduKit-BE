package com.edukit.external.aws.s3;

import com.edukit.external.aws.s3.exception.S3ErrorCode;
import com.edukit.external.aws.s3.exception.S3Exception;
import com.edukit.external.aws.s3.response.UploadPresignedUrlResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;
    private final AwsS3Properties s3Properties;

    private static final Pattern FILENAME_PATTERN = Pattern.compile("^.+\\..+$");
    private static final Duration PRESIGNED_URL_EXPIRATION = Duration.ofMinutes(5);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final int UUID_LENGTH = 8;
    private static final Map<String, String> IMAGE_CONTENT_TYPE_MAP = Map.of(
            "png", "image/png",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "gif", "image/gif",
            "webp", "image/webp"
    );

    public UploadPresignedUrlResponse createUploadPresignedUrl(final String path, final String filename) {
        validateFilename(filename);
        String s3Key = generateS3Key(path, filename);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.bucket())
                .key(s3Key)
                .contentType(getContentType(filename))
                .build();
        PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(PRESIGNED_URL_EXPIRATION)
                .putObjectRequest(putObjectRequest)
                .build();
        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(putObjectPresignRequest);
        return UploadPresignedUrlResponse.of(presignedPutObjectRequest.url().toString(), getFileUrl(s3Key), s3Key);
    }

    private void validateFilename(final String filename) {
        if (!StringUtils.hasText(filename) || !FILENAME_PATTERN.matcher(filename).matches()) {
            throw new S3Exception(S3ErrorCode.INVALID_FILE_NAME);
        }
    }

    private String generateS3Key(final String path, final String filename) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, UUID_LENGTH);
        String extension = extractExtension(filename);
        return String.format("%s/%s_%s%s", path, timestamp, uuid, extension);
    }

    private String extractExtension(final String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx == -1) {
            return "";
        }
        return filename.substring(idx);
    }

    private String getContentType(final String filename) {
        String ext = extractExtension(filename).toLowerCase().replace(".", "");
        if (!IMAGE_CONTENT_TYPE_MAP.containsKey(ext)) {
            throw new S3Exception(S3ErrorCode.INVALID_FILE_EXTENSION);
        }
        return IMAGE_CONTENT_TYPE_MAP.get(ext);
    }

    private String getFileUrl(final String s3Key) {
        return String.format("%s/%s", s3Properties.cdnUrl(), s3Key);
    }
}
