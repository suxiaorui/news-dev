package com.rui.user.service.impl;

import com.rui.enums.Sex;
import com.rui.enums.UserStatus;
import com.rui.exception.GraceException;
import com.rui.grace.result.GraceJSONResult;
import com.rui.grace.result.ResponseStatusEnum;
import com.rui.pojo.AppUser;
import com.rui.pojo.bo.UpdateUserInfoBO;
import com.rui.user.mapper.AppUserMapper;
import com.rui.user.service.UserService;
import com.rui.utils.DateUtil;
import com.rui.utils.DesensitizationUtil;
import com.rui.utils.RedisOperator;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;



/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/5 00:22
 * @Version 1.0
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    public AppUserMapper appUserMapper;

    @Autowired
    public Sid sid;

    @Autowired
    public RedisOperator redis;

    private static final String USER_FACE0 = "http://pic.imeitou.com/uploads/allimg/230704/10-230F41A553.jpg";
    private static final String USER_FACE1 = "http://pic.imeitou.com/uploads/allimg/230704/10-230F41A040.jpg";
    private static final String USER_FACE2 = "http://pic.imeitou.com/uploads/allimg/230704/10-230F41H438.png";


    @Override
    public AppUser queryMobileIsExist(String mobile) {

        Example userExample = new Example(AppUser.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("mobile", mobile);
        AppUser user = appUserMapper.selectOneByExample(userExample);
        return user;
    }

    @Transactional
    @Override
    public AppUser createUser(String mobile) {
        /**
         * 互联网项目都要考虑可扩展性
         * 如果未来的业务激增，那么就需要分库分表
         * 那么数据库表主键id必须保证全局（全库）唯一，不得重复
         */
        String userId = sid.nextShort();

        AppUser user = new AppUser();

        user.setId(userId);
        user.setMobile(mobile);
        user.setNickname("用户：" + DesensitizationUtil.commonDisplay(mobile));
        user.setFace(USER_FACE0);

        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
        user.setSex(Sex.secret.type);
        user.setActiveStatus(UserStatus.INACTIVE.type);

        user.setTotalIncome(0);
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        appUserMapper.insert(user);

        return user;
    }

    @Override
    public AppUser getUser(String userId) {
        return appUserMapper.selectByPrimaryKey(userId);
    }

    @Override
    public void updateUserInfo(UpdateUserInfoBO updateUserInfoBO) {

        AppUser userInfo = new AppUser();
        BeanUtils.copyProperties(updateUserInfoBO,userInfo);

        userInfo.setUpdatedTime(new Date());
        userInfo.setActiveStatus(UserStatus.ACTIVE.type);

        int result =  appUserMapper.updateByPrimaryKeySelective(userInfo);
        if (result != 1){
            GraceException.display(ResponseStatusEnum.USER_UPDATE_ERROR);
        }

    }

}
