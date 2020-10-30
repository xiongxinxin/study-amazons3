package com.xxx.studyamazons3.controller;

import com.xxx.studyamazons3.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {
    @Autowired
    private FileService fileService;

    @RequestMapping("/uploadFile")
    public void uploadFile() {
        long startTime = System.currentTimeMillis();
        fileService.uploadFile();
        long endTime = System.currentTimeMillis();
        System.out.println("总耗时：" + (endTime - startTime)/1000 + " s");
    }
}
