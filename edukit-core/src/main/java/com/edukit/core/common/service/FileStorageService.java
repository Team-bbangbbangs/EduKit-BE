package com.edukit.core.common.service;

import com.edukit.core.common.service.response.UploadPresignedUrlResponse;
import java.util.List;

public interface FileStorageService {

    UploadPresignedUrlResponse createUploadPresignedUrl(String path, String fileName);

    void moveFiles(List<String> fileUrls, String sourcePath, String targetPath);

    void deleteFiles(List<String> deletedImageUrls);
}
