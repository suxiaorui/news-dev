package com.rui.admin.repository;

import com.rui.pojo.mo.FriendLinkMO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/11 20:33
 * @Version 1.0
 */


public interface FriendLinkRepository extends MongoRepository<FriendLinkMO, String> {

    public List<FriendLinkMO> getAllByIsDelete(Integer isDelete);

}
