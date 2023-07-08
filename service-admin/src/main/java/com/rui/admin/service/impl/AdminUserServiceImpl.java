package com.rui.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rui.admin.mapper.AdminUserMapper;
import com.rui.admin.service.AdminUserService;
import com.rui.exception.GraceException;
import com.rui.grace.result.ResponseStatusEnum;
import com.rui.pojo.AdminUser;
import com.rui.pojo.bo.NewAdminBO;
import com.rui.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

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


    /**
     * 分页查询，admin_user管理员列表
     */

    @Override
    public PagedGridResult queryAdminList(Integer page, Integer pageSize) {

        // 1.开启分页
        PageHelper.startPage(page, pageSize);

        // 2.利用tkmybatis的Example，自定义查询条件；利用上面设置的分页信息，去查询admin_user表；
        // 2.1先创建一个查询实例，这个实例，是针对AdminUser作查询的；
        Example adminExample = new Example(AdminUser.class);
        //Example可以不设置查询条件；同时，也可以追加一些参数；比如，这儿我们按照创建时间倒序排列；
        adminExample.orderBy("createdTime").desc();
        // 我们这儿是：不附加任何条件的，去查询admin_user表；换句话说，(如果我们没有在1中设置分页信息的话)
        // 我们就是要查询admin_user表的所有内容；
        List<AdminUser> adminUserList = adminUserMapper.selectByExample(adminExample);

        // 3.然后，根据前端的要求，组装一个满足要求的分页对象；我们，这儿根据前端的要求，创建了一
        // 个PagedGridResult对象；我们将会使用这个对象，来组装返回给前端的分页对象；
        return setterPagedGrid(adminUserList, page);
    }

    /**
     * 工具方法：根据分页查询数据库的结果，组装成（满足前端要求的）分页对象；
     * 对于我们这个项目来说，我们创建了一个PagedGridResult对象；这个对象，可以满足前端对分页数据格式的要求；
     *
     * @param adminUserList: 根据分页条件，查询数据库，得到的当前页的数据；
     * @param page：想要查询第几页；
     * @return
     */

    private PagedGridResult setterPagedGrid(List<?> adminUserList,
                                            Integer page) {
        /**
         * 利用PageHelper官方提供的PageInfo：根据上面分页查询的结果，得到PageInfo分页对象；
         * 这个PageInfo分页对象，会帮我们计算出很多有关分页的信息；
         */
        PageInfo<?> pageInfo = new PageInfo<>(adminUserList);

        //创建PagedGridResult对象，并组装；
        PagedGridResult pagedGridResult = new PagedGridResult();
        pagedGridResult.setRows(adminUserList);// 把【根据分页条件查询数据库，得到的当前页的数据；】设置进去；
        pagedGridResult.setPage(page);//设置当前是第几页
        pagedGridResult.setRecords(pageInfo.getTotal());// Todo 通过pageInfo，获取总记录数；
        pagedGridResult.setTotal(pageInfo.getPages());// Todo 通过pageInfo，获取总页码数；

        //返回，组装好的PagedGridResult对象；
        return pagedGridResult;
    }

}
