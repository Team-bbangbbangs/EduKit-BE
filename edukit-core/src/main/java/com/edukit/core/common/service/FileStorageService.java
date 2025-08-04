package com.edukit.core.common.service;

import com.edukit.core.common.service.response.UploadPresignedUrlResponse;

public interface FileStorageService {

    UploadPresignedUrlResponse createUploadPresignedUrl(String path, String fileName);
}
