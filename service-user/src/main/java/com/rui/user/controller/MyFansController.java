package com.rui.user.controller;

import com.rui.api.BaseController;
import com.rui.api.controller.user.MyFansControllerApi;
import com.rui.enums.Sex;
import com.rui.grace.result.GraceJSONResult;
import com.rui.pojo.vo.FansCountsVO;
import com.rui.user.service.MyFanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/17 21:07
 * @Version 1.0
 */

@RestController
public class MyFansController extends BaseController implements MyFansControllerApi {

    final static Logger logger = LoggerFactory.getLogger(MyFansController.class);

    @Autowired
    private MyFanService myFanService;

    @Override
    public GraceJSONResult isMeFollowThisWriter(String writerId,
                                                String fanId) {
        boolean res = myFanService.isMeFollowThisWriter(writerId, fanId);
        return GraceJSONResult.ok(res);
    }

    @Override
    public GraceJSONResult follow(String writerId, String fanId) {
        myFanService.follow(writerId, fanId);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult unfollow(String writerId, String fanId) {
        myFanService.unfollow(writerId, fanId);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult queryAll(String writerId,
                                    Integer page,
                                    Integer pageSize) {

        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        return GraceJSONResult.ok(myFanService.queryMyFansList(writerId,
                page,
                pageSize));
    }

    @Override
    public GraceJSONResult queryRatio(String writerId) {

        int manCounts = myFanService.queryFansCounts(writerId, Sex.man);
        int womanCounts = myFanService.queryFansCounts(writerId, Sex.woman);

        FansCountsVO fansCountsVO = new FansCountsVO();
        fansCountsVO.setManCounts(manCounts);
        fansCountsVO.setWomanCounts(womanCounts);

        return GraceJSONResult.ok(fansCountsVO);
    }

}
