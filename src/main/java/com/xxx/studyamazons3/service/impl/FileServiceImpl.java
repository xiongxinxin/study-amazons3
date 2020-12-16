package com.xxx.studyamazons3.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.xxx.studyamazons3.service.FileService;
import com.xxx.studyamazons3.constant.BucketConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private AmazonS3 amazonS3Client;

    /**
     * 上传文件
     */
    @Override
    public void uploadFile(MultipartFile targetFile) {
        String objectName = targetFile.getOriginalFilename();
        if (!amazonS3Client.doesBucketExistV2(BucketConstant.IMAGE_NAME))
            amazonS3Client.createBucket(BucketConstant.IMAGE_NAME);

        // 创建InitiateMultipartUploadRequest对象。
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(BucketConstant.IMAGE_NAME, objectName);

        // 初始化分片。
        InitiateMultipartUploadResult upresult = amazonS3Client.initiateMultipartUpload(request);
        // 返回uploadId，它是分片上传事件的唯一标识，您可以根据这个uploadId发起相关的操作，如取消分片上传、查询分片上传等。
        String uploadId = upresult.getUploadId();

        // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
        List<PartETag> partETags = new ArrayList<>();

        // 计算文件有多少个分片。
        final long partSize = 20 * 1024 * 1024L;   // 每个分片的大小500MB
        System.out.println("你所上传的文件大小为:" + targetFile.getSize() / 1024 / 1024  + " MB");
        long fileLength = targetFile.getSize();
        int partCount = (int) (fileLength / partSize);
        if (fileLength % partSize != 0) {  //这个计算方式是不是有问题
            partCount++;
        }
        System.out.println("partCount:" + partCount);

        // 遍历分片上传。
        for (int i = 0; i < partCount; i++) {
            long startPos = i * partSize;
            long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
            InputStream instream = null;
            try {
                instream = targetFile.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 跳过已经上传的分片。
            try {
                instream.skip(startPos);
            } catch (IOException e) {
                e.printStackTrace();
            }
            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(BucketConstant.IMAGE_NAME);
            uploadPartRequest.setKey(objectName);
            uploadPartRequest.setUploadId(uploadId);
            uploadPartRequest.setInputStream(instream);
            // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为500 MB。
            uploadPartRequest.setPartSize(curPartSize);
            // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出这个范围，OSS将返回InvalidArgument的错误码。
            uploadPartRequest.setPartNumber(i + 1);
            // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
            UploadPartResult uploadPartResult = amazonS3Client.uploadPart(uploadPartRequest);
            // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
            partETags.add(uploadPartResult.getPartETag());
        }

        // 创建CompleteMultipartUploadRequest对象。
        // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(BucketConstant.IMAGE_NAME, objectName, uploadId, partETags);
        System.out.println("文件的位置：" + amazonS3Client.getUrl(BucketConstant.IMAGE_NAME, objectName));
        // 完成上传。
        amazonS3Client.completeMultipartUpload(completeMultipartUploadRequest);

        // 关闭client。
        //amazonS3Client.shutdown();
    }
}
