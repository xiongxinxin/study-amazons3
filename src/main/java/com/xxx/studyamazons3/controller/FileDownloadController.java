package com.xxx.studyamazons3.controller;

import com.xxx.studyamazons3.service.FileDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileDownloadController {

    @Autowired
    private FileDownloadService downloadService;

    @RequestMapping(value = "/downloadFile")
    public void downloadFile(){
        downloadService.downloadFile();
    }
}
