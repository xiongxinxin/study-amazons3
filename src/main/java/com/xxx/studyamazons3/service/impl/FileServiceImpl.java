package com.xxx.studyamazons3.service.impl;

import com.xxx.studyamazons3.service.FileService;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public class FileServiceImpl implements FileService {

    @Autowired
    private MinioClient minioClient;

    /**
     * 上传文件
     *
     * @param targetFile 目标文件
     */
    @Override
    public void uploadFile(MultipartFile targetFile) {
    }
}
