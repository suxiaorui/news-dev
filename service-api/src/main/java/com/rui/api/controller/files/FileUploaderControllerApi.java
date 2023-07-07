package com.rui.api.controller.files;

import com.rui.grace.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/7 14:11
 * @Version 1.0
 */


@Api(value = "文件上传的Controller", tags = {"文件上传的Controller"})
@RequestMapping("fs")
public interface FileUploaderControllerApi {


    /**
     * 上传单文件
     * @param userId
     * @param file
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "上传用户头像", notes = "上传用户头像", httpMethod = "POST")
    @PostMapping("/uploadFace")
    public GraceJSONResult uploadFace(@RequestParam String userId,
                                      MultipartFile file) throws Exception;


}
