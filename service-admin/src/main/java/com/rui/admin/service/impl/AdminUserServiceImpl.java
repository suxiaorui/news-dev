package com.rui.admin.service.impl;

import com.rui.admin.mapper.AdminUserMapper;
import com.rui.admin.service.AdminUserService;
import com.rui.pojo.AdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/8 16:55
 * @Version 1.0
 */

@Service
public class AdminUserServiceImpl implements AdminUserService {


    @Autowired
    public AdminUserMapper adminUserMapper;



    /**
     * 根据"管理员用户名"，查询管理员用户；
     * 所以，在admin_user表中，我们要保证username字段的唯一；即管理员用户不允许重名；
     *
     * @param username
     * @return
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

}
