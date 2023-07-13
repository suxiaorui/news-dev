package com.rui.api.controller.files;

import com.rui.grace.result.GraceJSONResult;
import com.rui.pojo.bo.NewAdminBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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


    /**
     * 【多文件文件，接口】
     * @param userId:用户id;
     * @param files:前端传过来的文件;
     * @return
     * @throws Exception
     */
    @PostMapping("/uploadSomeFiles") //设置路由，这个是需要前后端约定好的；
    public GraceJSONResult uploadSomeFiles(@RequestParam("userId") String userId,
                                           MultipartFile[] files) throws Exception;


    /**
     * 【文件上传到MongoDB的GridFS】
     * （1）和【AdminMngControllerApi中的，新增admin账号，接口】一样；我们还是使用NewAdminBO来承接
     * 前端传过来的参数；然后，具体的人脸数据会存在NewAdminBO的img64属性中；(PS:后端之所以可以这么干，前端肯定做了对应的设置的)
     * （2）这个接口，我们是不能够通过Swagger进行调用的？所以，Swagger的@ApiOperation()我们就不设置了；
     * （3）其实，不仅人脸数据可以上传到GridFS，一些其他文件，只要需要我们也可以上传到GridFS;
     * @param newAdminBO
     * @return
     * @throws Exception
     */
    @PostMapping("/uploadToGridFS") //设置路由，这个是需要前后端约定好的；
    public GraceJSONResult uploadToGridFS(@RequestBody NewAdminBO newAdminBO) throws Exception;

    /**
     * 【从MongoDB的GridFS中，获取文件】
     * @param faceId
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping("/readInGridFS") //设置路由，这个是需要前后端约定好的；
    public void readInGridFS(@RequestParam String faceId,
                             HttpServletRequest request,
                             HttpServletResponse response) throws Exception;

    /**
     * 【从gridfs中读取图片内容，并且返回base64】
     * @param faceId
     * @param request
     * @param response
     * @return
     * @throws Exception
     */


    @GetMapping("/readFace64InGridFS")
    public GraceJSONResult readFace64InGridFS(@RequestParam String faceId,
                             HttpServletRequest request,
                             HttpServletResponse response) throws Exception;


}
