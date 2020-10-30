package com.xxx.studyamazons3.service.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.xxx.studyamazons3.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private AmazonS3 amazonS3Client;

    @Value("${minio.eadfasdfadndpoint}")
    private String endPoint;
    @Value("a${minio.acafdasfcessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;

    /**
     * 上传文件
     */
    @Override
    public void uploadFile() {
        String bucketName = "image-name";
        // <yourObjectName>表示上传文件到OSS时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
        final File sampleFile = new File("C:\\Users\\HP\\Desktop\\cirros.qcow2");
        String objectName = sampleFile.getName();
        if(!amazonS3Client.doesBucketExistV2(bucketName))
            amazonS3Client.createBucket(bucketName);

        // 创建InitiateMultipartUploadRequest对象。
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectName);  //这儿实际应该是一个imageId

        // 初始化分片。
        InitiateMultipartUploadResult upresult = amazonS3Client.initiateMultipartUpload(request);
        // 返回uploadId，它是分片上传事件的唯一标识，您可以根据这个uploadId发起相关的操作，如取消分片上传、查询分片上传等。
        String uploadId = upresult.getUploadId();

        // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
        List<PartETag> partETags = new ArrayList<>();

        // 计算文件有多少个分片。
        final long partSize = 500 * 1024 * 1024L;   // 500MB
        System.out.println("sampleFile.size:" + sampleFile.length()/1024/1024/1024 + "G");
        long fileLength = sampleFile.length();
        int partCount = (int) (fileLength / partSize);
        if (fileLength % partSize != 0) {  //这个计算方式是不是有问题
            partCount++;
        }

        // 遍历分片上传。
        for (int i = 0; i < partCount; i++) {
            long startPos = i * partSize;
            long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
            InputStream instream = null;
            try {
                instream = new FileInputStream(sampleFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 跳过已经上传的分片。
            try {
                instream.skip(startPos);
            } catch (IOException e) {
                e.printStackTrace();
            }
            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(bucketName);
            uploadPartRequest.setKey(objectName);
            uploadPartRequest.setUploadId(uploadId);
            uploadPartRequest.setInputStream(instream);
            // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
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
        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName, objectName, uploadId, partETags);
        System.out.println("文件的位置：" + amazonS3Client.getUrl(bucketName, objectName));
        // 完成上传。
        amazonS3Client.completeMultipartUpload(completeMultipartUploadRequest);

        // 关闭client。
        amazonS3Client.shutdown();
    }
}
