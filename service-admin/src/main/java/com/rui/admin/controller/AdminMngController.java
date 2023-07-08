package com.rui.admin.controller;

import com.rui.admin.service.AdminUserService;
import com.rui.api.BaseController;
import com.rui.api.controller.admin.AdminMngControllerApi;
import com.rui.exception.GraceException;
import com.rui.grace.result.GraceJSONResult;
import com.rui.grace.result.ResponseStatusEnum;
import com.rui.pojo.AdminUser;
import com.rui.pojo.bo.AdminLoginBO;
import com.rui.pojo.bo.NewAdminBO;
import com.rui.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/7 19:50
 * @Version 1.0
 */

@RestController
public class AdminMngController extends BaseController implements AdminMngControllerApi {

    final static Logger logger = LoggerFactory.getLogger(AdminMngController.class);


    @Autowired
    private RedisOperator redis;

    @Autowired
    private AdminUserService adminUserService;

    @Override
    public GraceJSONResult adminLogin(AdminLoginBO adminLoginBO,
                                      BindingResult result,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {


        //0.判断BindingResult中是否保存了验证失败的错误信息，如果有，说明前端的输入是有问题的(用户名或者密码，至少有一个没输入)；
        // 那么，我们就获取这个错误信息，并构建一个GraceJSONResult统一返回对象，返回；
        if (result.hasErrors()) {
            Map<String, String> map = getErrorsFromBindingResult(result);
            return GraceJSONResult.errorMap(map);
        }

        // 1. 查询admin用户的信息
        AdminUser admin = adminUserService.queryAdminByUsername(adminLoginBO.getUsername());

        // 2.判断adminUser是否为空；如果为空，则表示没有这个用户；
        if (admin == null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);
        }

        /**
         * 3.使用【org.springframework.security.crypto.bcrypt.BCrypt;】提供的工具类BCrypt来检查密码是否OK；
         * 第一个参数：前台输入的密码；  第二个参数：根据"前台输入的用户名"去数据库中查得的AdminUser的密码；
         */
        boolean isPwdMatch = BCrypt.checkpw(adminLoginBO.getPassword(), admin.getPassword());
        //如果密码是OK的，直接返回OK的GraceJSONResult;
        if (isPwdMatch) {
            //首先，调用doLoginSettings方法，去把token信息保存到redis；token，id，name信息设置进cookie；
            doLoginSettings(admin,request,response);
            return GraceJSONResult.ok();
        } else {//如果密码不OK，
            //返回一个包含"管理员不存在或密码错误！"信息的，GraceJSONResult；
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);
        }
    }


    /**
     * 用于admin用户登录过后的基本信息设置
     * @param admin
     * @param request
     * @param response
     */
    private void doLoginSettings(AdminUser admin,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        // 保存token放入到redis中
        String token = UUID.randomUUID().toString();
        redis.set(REDIS_ADMIN_TOKEN + ":" + admin.getId(), token);

        // 保存admin登录基本token信息到cookie中
        setCookie(request, response, "atoken", token, COOKIE_MONTH);
        setCookie(request, response, "aid", admin.getId(), COOKIE_MONTH);
        setCookie(request, response, "aname", admin.getAdminName(), COOKIE_MONTH);
    }



    /**
     * 查询admin用户名，是否已存在;
     * @param username
     * @return
     */
    @Override
    public GraceJSONResult adminIsExist(String username) {
        checkAdminExist(username);//【判断admin用户名，是否已存在】的逻辑，单独抽成了一个方法；
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult addNewAdmin(NewAdminBO newAdminBO, BindingResult result, HttpServletRequest request, HttpServletResponse response) {

        /**
         * 0. BindingResult中是否保存了验证失败的错误信息，如果有，说明前端的输入是有问题的，比如登录名、负责人名等没有输入)；
         * 那么，就获取这个错误信息，并构建一个GraceJSONResult统一返回对象，返回；
         */
        if (result.hasErrors()) {
            Map<String, String> map = getErrorsFromBindingResult(result);
            return GraceJSONResult.errorMap(map);
        }

        // 1. 如果img64为空，就表示前端不是人脸入库; 那么，就一定需要password和confirmPassword;
        if (StringUtils.isBlank(newAdminBO.getImg64())) {
            // 1.1 如果此时，password和confirmPassword但凡有一个为空，就返回一个内容是"密码不能为空！"的GraceJSONResult；
            if (StringUtils.isBlank(newAdminBO.getPassword()) ||
                    StringUtils.isBlank(newAdminBO.getConfirmPassword())) {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_PASSWORD_NULL_ERROR);
            }
        }


        // 2. 如果password不为空，那么password和confirmPassword必须要一致；
        if (StringUtils.isNotBlank(newAdminBO.getPassword())) {
            // 2.1 如果此时，password和confirmPassword不一致，就返回一个内容是"密码不能为空或者两次输入不一致！"的GraceJSONResult;
            if (!newAdminBO.getPassword().equalsIgnoreCase(newAdminBO.getConfirmPassword())) {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_PASSWORD_ERROR);
            }
        }

        // 3.因为admin_user表的username不允许重复（即，管理员的用户名不允许重复）；所以我们需要去校验一下，admin用户名，是否已存在；
        checkAdminExist(newAdminBO.getUsername());

        // 4.调用Service层的方法，去新增管理员
        adminUserService.createAdminUser(newAdminBO);

        // 5.如果能执行到这儿，说明上面一切OK；就返回一个OK的GraceJSONResult;
        return GraceJSONResult.ok();
    }

    /**
     * 工具方法：判断admin用户名，是否已存在；
     * @param username
     */
    private void checkAdminExist(String username) {
        // 1.调用Service层的方法，根据用户名，尝试去数据库中查，看是否有这个用户；
        AdminUser adminUser = adminUserService.queryAdminByUsername(username);
        if (adminUser != null) {
            //如果该管理员name已存在，直接抛一个包含"管理员登录名已存在！"信息的MyCustomException自定义异常；
            GraceException.display(ResponseStatusEnum.ADMIN_USERNAME_EXIST_ERROR);
        }
    }

}
