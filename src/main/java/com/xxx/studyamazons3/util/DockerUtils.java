package com.xxx.studyamazons3.util;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PushResponseItem;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.PushImageResultCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * docker工具类
 */
@Component
public class DockerUtils {

    //docker服务器
    @Value("${docker.url}")
    private String DOCKER_URL;

    //Harbor登录用户名
    @Value("${harbor.username}")
    private String HARBOR_USERNAME;

    //Harbor登录密码
    @Value("${harbor.password}")
    private String HARBOR_PASSWORD;

    //Harbor的登录地址
    @Value("${harbor.url}")
    private String HARBOR_LOGIN_ADDRESS;


    /**
     * 获取docker链接
     *
     * @return
     */
    public DockerClient getDockerClient() {
        DockerClient dockerClient = DockerClientBuilder.getInstance(DOCKER_URL).build();
        return dockerClient;
    }


    /**
     * 参考之前文章《docker---镜像的加载》，镜像如何保存到本地
     * 上传镜像
     *
     * @param file      镜像文件
     * @param imageName
     * @throws Exception
     */
    public void uploadImage(File file, String imageName) throws Exception {
        DockerClient dockerClient = getDockerClient();
        InputStream inputStream = new FileInputStream(file);
        //Harbor登录信息
        AuthConfig autoConfig = new AuthConfig().withRegistryAddress(HARBOR_LOGIN_ADDRESS).withUsername(HARBOR_USERNAME).withPassword(HARBOR_PASSWORD);
        //加载镜像
        dockerClient.loadImageCmd(inputStream).exec();
        //获取加载镜像的名称
        String uploadImageName = "";
        String imageFile = file.getName().substring(0, file.getName().lastIndexOf("."));
        String imageId = imageFile.substring(imageFile.lastIndexOf("_") + 1);
        List<Image> list = dockerClient.listImagesCmd().exec();
        for (Image image : list) {
            if (image.getId().contains(imageId)) {
                uploadImageName = image.getRepoTags()[0];
            }
        }
        //镜像打tag
        dockerClient.tagImageCmd(uploadImageName, imageName, imageName.split(":")[1]).exec();
        //push至镜像仓库
        PushImageResultCallback pushImageResultCallback = new PushImageResultCallback() {
            @Override
            public void onNext(PushResponseItem item) {
                super.onNext(item);
            }

            @Override
            public void onComplete() {
                super.onComplete();
            }
        };
        dockerClient.pushImageCmd(imageName).withAuthConfig(autoConfig).exec(pushImageResultCallback).awaitSuccess();
        //push成功后，删除本地加载的镜像
        dockerClient.removeImageCmd(imageName).exec();
        dockerClient.removeImageCmd(uploadImageName).exec();
        //关闭文件流
        if (inputStream != null) {
            inputStream.close();
        }
    }
}
