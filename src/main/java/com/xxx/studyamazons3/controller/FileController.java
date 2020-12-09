package com.xxx.studyamazons3.controller;

import com.xxx.studyamazons3.service.FileService;
import com.xxx.studyamazons3.util.DockerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
public class FileController {
    @Autowired
    private FileService fileService;

    @RequestMapping("/uploadFile")
    public void uploadFile(MultipartFile targetFile) {
        long startTime = System.currentTimeMillis();
        fileService.uploadFile(targetFile);
        long endTime = System.currentTimeMillis();
        System.out.println("总耗时：" + (endTime - startTime)/1000 + " s");
    }


//    @Autowired
//    private DockerUtils dockerUtils;
//
//    @PostMapping("/uploadImages")
//    public void uploadImages(String projectName, String imageName, String tag, String filePath) throws Exception {
////        String imageNames = HOST_HOST + "/" + projectName + "/" + imageName + ":" + tag;
//        String imageNames = null;
//        dockerUtils.uploadImage(new File(filePath), imageNames);
//    }
}
