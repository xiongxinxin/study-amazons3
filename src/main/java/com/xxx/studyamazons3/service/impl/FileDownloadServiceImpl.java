package com.xxx.studyamazons3.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.xxx.studyamazons3.service.FileDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class FileDownloadServiceImpl implements FileDownloadService {

    @Autowired
    private AmazonS3 amazonS3Client;


    public void downloadFile() {

        String bucket_name = "test1118";
        String key_name = "kodexporer_stable.tar";
        String file_path = "/root/xiong/kodexporer_stable.tar";

        ObjectMetadata objectMetadata = amazonS3Client.getObjectMetadata(bucket_name, key_name);
        System.out.println(objectMetadata.getPartCount());


//        String bucket_name = "meo-vm-image-bucket";
//        String key_name = "1a474338851c40ddba19b4b4eb4d986f";
//        String file_path = "D:\\programer\\tmp.rar";

        File f = new File(file_path);
        TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(amazonS3Client).withDisableParallelDownloads(false).build();
//                TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(amazonS3Client)
//                .withDisableParallelDownloads(false).withMinimumUploadPartSize(Long.valueOf(5 * 1024 * 1024))
//                .withMultipartUploadThreshold(Long.valueOf(16 * 1024 * 1024)).withMultipartCopyPartSize(Long.valueOf(5 * 1024 * 1024))
//                .withMultipartCopyThreshold(Long.valueOf(100 * 1024 * 1024))
//                .withExecutorFactory(() -> createExecutorService(100)).build();
        Long start = System.currentTimeMillis();
        try {
            xfer_mgr.upload("","",new File(""));
            Download xfer = xfer_mgr.download(bucket_name, key_name, f);
            do {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
                TransferProgress progress = xfer.getProgress();
                long so_far = progress.getBytesTransferred();
                long total = progress.getTotalBytesToTransfer();
                double pct = progress.getPercentTransferred();
                System.out.println("当前下载进度: " + pct);
            } while (!xfer.isDone());
            System.out.println("xfer.isDone()");
            xfer.waitForCompletion();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Long end = System.currentTimeMillis();
        System.out.println("总消耗时间：" + (end-start)/1000 + " s");
        System.out.println("已完成");
//        xfer_mgr.shutdownNow();



//        Long start = System.currentTimeMillis();
//
//        final GetObjectRequest request = new GetObjectRequest(bucket_name, key_name);
//
//        request.setGeneralProgressListener(progressEvent -> {
//            String transferredBytes = "Downloaded bytes: " + progressEvent.getBytesTransferred();
//            System.out.println(transferredBytes);
//        });
//
//        TransferManager tm = TransferManagerBuilder.standard().withS3Client(amazonS3Client)
//                .withDisableParallelDownloads(false).withMinimumUploadPartSize(Long.valueOf(5 * 1024 * 1024))
//                .withMultipartUploadThreshold(Long.valueOf(16 * 1024 * 1024)).withMultipartCopyPartSize(Long.valueOf(5 * 1024 * 1024))
//                .withMultipartCopyThreshold(Long.valueOf(100 * 1024 * 1024))
//                .withExecutorFactory(() -> createExecutorService(20)).build();
//
//        Download download = tm.download(request, new File(file_path));
//
//        try {
//            download.waitForCompletion();
//        } catch (AmazonServiceException e) {
//            System.out.println(e.getMessage());
//        } catch (AmazonClientException e) {
//            System.out.println(e.getMessage());
//        } catch (InterruptedException e) {
//            System.out.println(e.getMessage());
//        }
//        Long end = System.currentTimeMillis();
//        System.out.println("总消耗时间：" + (end-start)/1000 + " s");
//        System.out.println("已完成");



    }


    private ThreadPoolExecutor createExecutorService(int threadNumber)
    {
        ThreadFactory threadFactory = new ThreadFactory()
        {
            private int threadCount = 1;

            public Thread newThread(Runnable r)
            {
                //LOGGER.info("createExecutorService ");
                Thread thread = new Thread(r);
                thread.setName("jsa-amazon-s3-transfer-manager-worker-" + threadCount++);
                return thread;
            }
        };
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNumber, threadFactory);
    }
}
