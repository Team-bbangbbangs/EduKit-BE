package com.edukit.core.common.port;

import com.edukit.core.common.service.response.UploadPresignedUrlResponse;
import java.util.List;

public interface FileStorageService {

    UploadPresignedUrlResponse createUploadPresignedUrl(String directory, String fileName);

    void moveFiles(List<String> fileKeys, String sourceDirectory, String targetDirectory);

    void deleteFiles(List<String> fileKeys);
}
