package com.xxx.studyamazons3.service.impl;

import com.xxx.studyamazons3.service.FileService;
import com.xxx.studyamazons3.util.BucketConstant;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(BucketConstant.IMAGE_NAME).build());
            if(!bucketExists){
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BucketConstant.IMAGE_NAME).region("us-west-1").build());
            }

            minioClient.putObject()
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidBucketNameException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        } catch (RegionConflictException e) {
            e.printStackTrace();
        }
    }
}
