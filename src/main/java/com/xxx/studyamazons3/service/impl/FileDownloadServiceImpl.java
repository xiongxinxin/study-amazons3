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

        String bucket_name = BucketConstant.IMAGE_NAME;
        String key_name = "Navicat Premium 12免安装.rar";
        String file_path = "C:\\Users\\HP\\Desktop\\tmp.rar";
        // snippet-start:[s3.java1.s3_xfer_mgr_download.single]
        File f = new File(file_path);
//        TransferManager xfer_mgr = TransferManagerBuilder.standard().build();
        TransferManagerBuilder builder = TransferManagerBuilder.standard();
        builder.setS3Client(amazonS3Client);
        TransferManager xfer_mgr = builder.build();
        try {
            Download xfer = xfer_mgr.download(bucket_name, key_name, f);
            // loop with Transfer.isDone()
            //XferMgrProgress.showTransferProgress(xfer);
            do {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return;
                }
                // Note: so_far and total aren't used, they're just for
                // documentation purposes.
                TransferProgress progress = xfer.getProgress();
                long so_far = progress.getBytesTransferred();
                long total = progress.getTotalBytesToTransfer();
                double pct = progress.getPercentTransferred();
            } while (xfer.isDone() == false);

            xfer.waitForCompletion();
            // or block with Transfer.waitForCompletion()
            //XferMgrProgress.waitForCompletion(xfer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("已完成");
        xfer_mgr.shutdownNow();
        // snippet-end:[s3.java1.s3_xfer_mgr_download.single]
    }
}
