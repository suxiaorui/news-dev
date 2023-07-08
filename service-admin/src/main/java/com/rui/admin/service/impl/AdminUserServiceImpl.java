package com.rui.admin.service.impl;

import com.rui.admin.mapper.AdminUserMapper;
import com.rui.admin.service.AdminUserService;
import com.rui.exception.GraceException;
import com.rui.grace.result.ResponseStatusEnum;
import com.rui.pojo.AdminUser;
import com.rui.pojo.bo.NewAdminBO;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/8 16:55
 * @Version 1.0
 */

@Service
public class AdminUserServiceImpl  implements AdminUserService {


    @Autowired
    public AdminUserMapper adminUserMapper;

    @Autowired
    private Sid sid;

    /**
     * 根据"管理员用户名"，查询管理员用户；
     * 所以，在admin_user表中，我们要保证username字段的唯一；即管理员用户不允许重名；
     */


    @Override
    public AdminUser queryAdminByUsername(String username) {

        //1.利用tkmybatis的Example，自定义查询条件；
        // 1.1先创建一个查询实例，这个实例，是针对AdminUser作查询的；
        Example adminExample = new Example(AdminUser.class);
        // 1.2给上面的查询实例，增加查询条件；
        Example.Criteria criteria = adminExample.createCriteria();
        //第一个参数：AdminUser实体类中的username；第二个参数：queryAdminByUsername这个方法的username参数；
        criteria.andEqualTo("username", username);

        //2.利用创建好的查询条件，调用方法，去查数据库；
        AdminUser adminUser = adminUserMapper.selectOneByExample(adminExample);
        return adminUser;

    }


    /**
     * 新增admin管理员账号；
     */

    @Transactional
    @Override
    public void createAdminUser(NewAdminBO newAdminBO) {

        String adminId = sid.nextShort();//调用Sid，基于雪花算法，生成一个主键id；

        //创建一个AdminUser（这个和数据库直接打交道的pojo实体类）对象；
        AdminUser adminUser = new AdminUser();
        adminUser.setId(adminId);//设置主键id
        adminUser.setUsername(newAdminBO.getUsername());//設置username，即用户名；
        adminUser.setAdminName(newAdminBO.getAdminName());//设置adminName，即使用该管理员账号的、人的真实姓名；
        //如果password不为空，说明其不是人脸入库，而是用户名+密码入库；那么，此时我们需要对密码进行加密；
        if (StringUtils.isNotBlank(newAdminBO.getPassword())) {
            String pwd = BCrypt.hashpw(newAdminBO.getPassword(), BCrypt.gensalt());
            adminUser.setPassword(pwd);//设置password密码
        }
        /**
         * 如果是人脸入库的话；其实，用户在前端就会上传一张人脸照片；（这个工作，后续再说）
         * 然后，人脸上传到MongoDB后，前端会拿到一个Base64字符串，这是人脸图片的地址；
         * 然后，前端再来请求【创建admin账号，接口】时，参数中的img64和faceId就是非空的了；
         */
        if (StringUtils.isNotBlank(newAdminBO.getFaceId())) {
            adminUser.setFaceId(newAdminBO.getFaceId());
        }
        adminUser.setCreatedTime(new Date());//设置createTime创建时间
        adminUser.setUpdatedTime(new Date());//设置updateTime更新时间

        // 调用tkmybatis帮我们准备好的插入方法，把这个用户信息插入数据库；
        int result = adminUserMapper.insertSelective(adminUser);
        if (result != 1) {
            //如果插入失败，就抛出一个信息是"添加管理员失败！"的自定义异常；
            GraceException.display(ResponseStatusEnum.ADMIN_CREATE_ERROR);
        }
    }
}
