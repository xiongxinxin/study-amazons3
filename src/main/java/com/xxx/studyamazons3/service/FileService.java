package com.xxx.studyamazons3.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    /**
     * 上传文件
     */
    void uploadFile(MultipartFile targetFile);
}
