package com.xxx.studyamazons3.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.DownloadFileRequest;
import com.aliyun.oss.model.DownloadFileResult;
import com.xxx.studyamazons3.service.FileDownloadService;
import org.springframework.stereotype.Service;

@Service
public class FileDownloadServiceImpl implements FileDownloadService {
    public void downloadFile() {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "http://182.92.69.6:9090";
// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = "root";
        String accessKeySecret = "12345678";
        String bucketName = "test1118";
        String objectName = "Navicat Premium 12免安装.rar";

// 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

// 下载请求，10个任务并发下载，启动断点续传。
        DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, objectName);
        downloadFileRequest.setDownloadFile("C:\\Users\\HP\\Desktop\\targetFile.rar");
        downloadFileRequest.setPartSize(1 * 1024 * 1024);
        downloadFileRequest.setTaskNum(10);
        downloadFileRequest.setEnableCheckpoint(true);
        downloadFileRequest.setCheckpointFile("C:\\Users\\HP\\Desktop\\a.txt");

// 下载文件。
        DownloadFileResult downloadRes = null;
        try {
            downloadRes = ossClient.downloadFile(downloadFileRequest);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
// 下载成功时，会返回文件元信息。
        downloadRes.getObjectMetadata();

// 关闭OSSClient。
        ossClient.shutdown();

    }
}
