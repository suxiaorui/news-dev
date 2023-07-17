package com.rui.user.service;

import com.rui.utils.PagedGridResult;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/17 21:08
 * @Version 1.0
 */


public interface MyFanService {

    /**
     * 查询当前用户是否关注作家
     */
    public boolean isMeFollowThisWriter(String writerId, String fanId);


    /**
     * 关注成为粉丝
     */
    public void follow(String writerId, String fanId);


    /**
     * 粉丝取消关注
     */
    public void unfollow(String writerId, String fanId);


    /**
     * 查询我的粉丝数
     */
    public PagedGridResult queryMyFansList(String writerId,
                                           Integer page,
                                           Integer pageSize);

}
