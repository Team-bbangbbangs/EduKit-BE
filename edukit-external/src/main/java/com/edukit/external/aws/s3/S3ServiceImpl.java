package com.edukit.external.aws.s3;

import com.edukit.core.common.service.FileStorageService;
import com.edukit.core.common.service.response.UploadPresignedUrlResponse;
import com.edukit.external.aws.s3.exception.S3ErrorCode;
import com.edukit.external.aws.s3.exception.S3Exception;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements FileStorageService {

    private final S3Client s3Client;
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

    public UploadPresignedUrlResponse createUploadPresignedUrl(final String directory, final String originalFileName) {
        validateFilename(originalFileName);
        String s3Key = generateS3Key(directory, originalFileName);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.bucket())
                .key(s3Key)
                .contentType(getContentType(originalFileName))
                .build();
        PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(PRESIGNED_URL_EXPIRATION)
                .putObjectRequest(putObjectRequest)
                .build();
        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(putObjectPresignRequest);
        String fileUrl = getFileUrl(s3Key);
        String fileName = getFileName(s3Key);
        return UploadPresignedUrlResponse.of(
                presignedPutObjectRequest.url().toString(), fileUrl, s3Properties.cdnUrl(), fileName
        );
    }

    private void validateFilename(final String filename) {
        if (!StringUtils.hasText(filename) || !FILENAME_PATTERN.matcher(filename).matches()) {
            throw new S3Exception(S3ErrorCode.INVALID_FILE_NAME);
        }
    }

    private String generateS3Key(final String directory, final String filename) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, UUID_LENGTH);
        String extension = extractExtension(filename);
        return String.format("%s/%s_%s%s", directory, timestamp, uuid, extension);
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
        return s3Properties.cdnUrl() + "/" + s3Key;
    }

    public String getFileName(String s3Key) {
        return s3Key.substring(s3Key.lastIndexOf('/') + 1);
    }

    public void moveFiles(final List<String> fileKeys, final String sourceDirectory, final String targetDirectory) {
        fileKeys.forEach(key -> moveFile(extractFileName(key), sourceDirectory, targetDirectory));
    }

    public void deleteFiles(final List<String> fileKeys) {
        fileKeys.forEach(this::deleteFile);
    }

    private void moveFile(final String fileName, final String sourceDirectory, final String targetDirectory) {
        String sourceKey = sourceDirectory + "/" + fileName;        // tmp/abc.jpg
        String targetKey = targetDirectory + "/" + fileName;        // notices/abc.jpg
        copyFile(sourceKey, targetKey);
        //tmp에 남아있는 파일은 수명 주기 규칙에 의해 자동 삭제
    }

    private void copyFile(final String sourceKey, final String targetKey) {
        try {
            s3Client.copyObject(builder -> builder
                    .sourceBucket(s3Properties.bucket())
                    .sourceKey(sourceKey)
                    .destinationBucket(s3Properties.bucket())
                    .destinationKey(targetKey));
        } catch (Exception e) {
            log.error("S3 파일 복사 실패: sourceKey={}, targetKey={}", sourceKey, targetKey, e);
            throw new S3Exception(S3ErrorCode.FILE_COPY_FAILED);
        }
    }

    private void deleteFile(final String key) {
        try {
            s3Client.deleteObject(builder -> builder
                    .bucket(s3Properties.bucket())
                    .key(key));
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: key={}", key, e);
            throw new S3Exception(S3ErrorCode.FILE_DELETE_FAILED);
        }
    }

    private String extractFileName(final String url) {
        int lastSlash = url.lastIndexOf('/');
        return url.substring(lastSlash + 1);
    }
}
