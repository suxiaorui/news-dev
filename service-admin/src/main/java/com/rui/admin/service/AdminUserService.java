package com.rui.admin.service;

import com.rui.pojo.AdminUser;
import com.rui.pojo.bo.NewAdminBO;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/7 19:53
 * @Version 1.0
 */


public interface AdminUserService {


    /**
     * 获得管理员的用户信息
     */
    public AdminUser queryAdminByUsername(String username);



    /**
     * 新增admin管理员账号；
     */
    void createAdminUser(NewAdminBO newAdminBO);
}
