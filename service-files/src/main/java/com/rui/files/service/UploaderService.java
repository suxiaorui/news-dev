package com.rui.files.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/7 14:04
 * @Version 1.0
 */


public interface UploaderService {


    /**
     * 使用fastdfs上传文件
     */
    public String uploadFdfs(MultipartFile file, String fileExtName) throws Exception;


    /**
     * 使用OSS上传文件
     */
    public String uploadOSS(MultipartFile file,
                            String userId,
                            String fileExtName) throws Exception;

}
