package com.rui.files.service.impl;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.rui.files.service.UploaderService;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/7 14:05
 * @Version 1.0
 */
@Service
public class UploaderServiceImpl implements UploaderService {

    @Autowired
    public FastFileStorageClient fastFileStorageClient;


    @Override
    public String uploadFdfs(MultipartFile file, String fileExtName) throws Exception {
        InputStream inputStream = file.getInputStream();

        StorePath storePath = fastFileStorageClient.uploadFile(inputStream,
                file.getSize(),
                fileExtName,
                null);

        inputStream.close();

        return storePath.getFullPath();
    }
}
