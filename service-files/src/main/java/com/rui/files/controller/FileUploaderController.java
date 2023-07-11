package com.rui.files.controller;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.rui.api.controller.files.FileUploaderControllerApi;
import com.rui.exception.GraceException;
import com.rui.files.resource.FileResource;
import com.rui.files.service.UploaderService;
import com.rui.grace.result.GraceJSONResult;
import com.rui.grace.result.ResponseStatusEnum;
import com.rui.pojo.bo.NewAdminBO;
import com.rui.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/7 14:25
 * @Version 1.0
 */

@RestController
public class FileUploaderController implements FileUploaderControllerApi {

    final static Logger logger = LoggerFactory.getLogger(FileUploaderController.class);

    @Autowired
    private UploaderService uploaderService;

    @Autowired
    private FileResource fileResource;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Override
    public GraceJSONResult uploadFace(String userId,
                                      MultipartFile file) throws Exception {
        String path = "";
        if (file != null) {
            // 获得文件上传的名称
            String fileName = file.getOriginalFilename();

            // 判断文件名不能为空
            if (StringUtils.isNotBlank(fileName)) {
                String fileNameArr[] = fileName.split("\\.");
                // 获得后缀
                String suffix = fileNameArr[fileNameArr.length - 1];
                // 判断后缀符合我们的预定义规范
                if (!suffix.equalsIgnoreCase("png") &&
                        !suffix.equalsIgnoreCase("jpg") &&
                        !suffix.equalsIgnoreCase("jpeg")
                ) {
                    return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_FORMATTER_FAILD);
                }

                // 执行上传
//                path = uploaderService.uploadFdfs(file, suffix);
                path = uploaderService.uploadOSS(file, userId, suffix);

            } else {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
            }
        } else {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        logger.info("path = " + path);


        String finalPath = "";
        if (StringUtils.isNotBlank(path)){
//            finalPath = fileResource.getHost() + path;
            finalPath = fileResource.getOssHost() + path;
        }else {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        return GraceJSONResult.ok(finalPath);
    }

    @Override
    public GraceJSONResult uploadToGridFS(NewAdminBO newAdminBO) throws Exception {

        // 1.1 获得img64属性值，这个值其实就是文件（人脸图片文件）的Base64编码后的字符串；
        String file64 = newAdminBO.getImg64();
        // 1.2 把base64格式的字符串，转换成byte数组；
        byte[] bytes = new BASE64Decoder().decodeBuffer(file64.trim());
        // 1.3 把byte数组，转成InputStream输入流；
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        /**
         * 2. 把文件上传到MongoDB的GridFS中；
         * 第一个参数：上传后的文件名；我们这儿使用【管理员用户名.png】的名字；因为管理员用户名不能重复，所以这种命名方式是OK的；
         * 第二个参数：文件的InputStream输入流；
         * 返回值：其返回值类型是ObjectId（org.bson.types）；
         */
        ObjectId fileId = gridFSBucket.uploadFromStream(newAdminBO.getAdminName() + ".png", byteArrayInputStream);

        // 3. 获得文件在GridFS中的主键id；
        String fileIdStr = fileId.toString();

        // 4. 把文件上传到GridFS中后的id，回传给前端；
        return GraceJSONResult.ok(fileIdStr);
    }

    @Override
    public void readInGridFS(String faceId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // 0.如果前端传过来的FaceId是空，或者是"null"；直接抛一个"你所查看的文件不存在！"的自定义的MyCustomException异常；
        if (StringUtils.isBlank(faceId) || faceId.equalsIgnoreCase("null")) {
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }

        // 1. 从GridFS中读取文件;
        File file = readGridFSByFaceId(faceId);

        // 2. 把文件放到response中；
        FileUtils.downloadFileByStream(response, file);

    }



    private File readGridFSByFaceId(String faceId) throws Exception {

        /**
         * 1.通过gridFSBucket.find()方法，从GridFS中，查询文件；
         * 第一个参数"_id"表示，我们此次查询，是根据id查询；
         * 第二个参数是，我们具体此次查询的id号；
         * PS：这个查询结果可能包含多个结果（比如我们根据其他条件查询的时候，结果可能有多个），而且其是可以进行循环的；
         */

        GridFSFindIterable gridFSFindIterable = gridFSBucket.find(Filters.eq("_id", new ObjectId(faceId)));

        // 2. 获取查询到的单个文件的对象；因为，我们心里清楚，上面根据id去查询，最多只能查到一个；所以，可以直接只看查到结果的第一个；
        GridFSFile gridFSFile = gridFSFindIterable.first();

        // 3. 如果查询的结果为空，直接抛一个"你所查看的文件不存在！"的自定义的MyCustomException异常；
        if (gridFSFile == null) {
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }

        // 4.获得文件名；
        String fileName = gridFSFile.getFilename();

        // 5.把文件保存到服务器本地；
        // 5.1 创建服务器本地保存图片文件的文件夹
        File fileTemp = new File("d:/temp_face");
        if (!fileTemp.exists()) { //如果上面的路径不存在，就去创建；
            fileTemp.mkdirs();
        }

        // 5.2 获得文件流
        File myFile = new File("d:/temp_face/" + fileName);
        // 5.3 创建文件输出流
        OutputStream os = new FileOutputStream(myFile);//这个会报FileNotFoundException异常；
        /**
         * 5.4 利用gridFSBucket.downloadToStream()方法，把文件保存到服务器本地；
         * 第一个参数：文件保存到本地的名称；我们这儿以"文件在GridFS中的id"，作为"文件保存到本地的名称";
         * 第二个参数：文件是输出流；
         */
        gridFSBucket.downloadToStream(new ObjectId(faceId), os);//downloadToStream()方法有其他的重构方式，利用其他的重构的方法也是可以的;
        // 6. 返回该文件，在本地服务器的file对象；
        return myFile;

    }

    @Override
    public GraceJSONResult readFace64InGridFS(String faceId,
                                              HttpServletRequest request,
                                              HttpServletResponse response)
            throws Exception {

        // 0. 获得gridfs中人脸文件
        File myface = readGridFSByFaceId(faceId);

        // 1. 转换人脸为base64
        String base64Face = FileUtils.fileToBase64(myface);

        return GraceJSONResult.ok(base64Face);


    }

}
