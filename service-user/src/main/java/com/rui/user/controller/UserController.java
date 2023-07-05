package com.rui.user.controller;

import com.rui.api.controller.user.HelloControllerApi;
import com.rui.api.controller.user.UserControllerApi;
import com.rui.grace.result.GraceJSONResult;
import com.rui.grace.result.ResponseStatusEnum;
import com.rui.grace.result.RuiJSONResult;
import com.rui.pojo.AppUser;
import com.rui.pojo.vo.UserAccountInfoVO;
import com.rui.user.service.UserService;
import com.rui.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/5 17:22
 * @Version 1.0
 */

@RestController
public class UserController implements UserControllerApi {

    final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Override
    public GraceJSONResult getAccountInfo(String userId) {

        // 判断参数不能为空
        if (StringUtils.isBlank(userId)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }

        // 根据userId查询用户的信息
        AppUser user = getUser(userId);

        // 返回用户信息
        UserAccountInfoVO accountInfoVO = new UserAccountInfoVO();
        BeanUtils.copyProperties(user,accountInfoVO);

        return GraceJSONResult.ok(accountInfoVO);
    }

    private AppUser getUser(String userId){
        // 后续再扩展
        AppUser user = userService.getUser(userId);
        return user;
    }

}
