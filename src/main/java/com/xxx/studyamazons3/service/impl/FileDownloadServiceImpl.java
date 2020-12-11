package com.xxx.studyamazons3.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.xxx.studyamazons3.constant.BucketConstant;
import com.xxx.studyamazons3.service.FileDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FileDownloadServiceImpl implements FileDownloadService {

    @Autowired
    private AmazonS3 amazonS3Client;


    public void downloadFile() {

//        String bucket_name = BucketConstant.IMAGE_NAME;
//        String key_name = "wc16_zip.qcow2";
//        String file_path = "C:\\Users\\HP\\Desktop\\tmp.rar";

        String bucket_name = "meo-vm-image-bucket";
        String key_name = "1a474338851c40ddba19b4b4eb4d986f";
        String file_path = "D:\\programer\\tmp.rar";

        File f = new File(file_path);
        TransferManagerBuilder builder = TransferManagerBuilder.standard().withS3Client(amazonS3Client).withDisableParallelDownloads(true);
        TransferManager xfer_mgr = builder.build();
        Long start = System.currentTimeMillis();
        try {
            Download xfer = xfer_mgr.download(bucket_name, key_name, f);
            do {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
                // Note: so_far and total aren't used, they're just for
                // documentation purposes.
                TransferProgress progress = xfer.getProgress();
                long so_far = progress.getBytesTransferred();
                long total = progress.getTotalBytesToTransfer();
                double pct = progress.getPercentTransferred();
                System.out.println("当前下载进度: " + pct);
            } while (xfer.isDone() == false);
            xfer.waitForCompletion();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Long end = System.currentTimeMillis();
        System.out.println("总消耗时间：" + (end-start)/1000 + " s");
        System.out.println("已完成");
        xfer_mgr.shutdownNow();
    }
}
