package com.lhc.mallchat.common.user.service.impl;

import com.lhc.mallchat.common.user.dao.UserDao;
import com.lhc.mallchat.common.user.domain.entity.User;
import com.lhc.mallchat.common.user.domain.vo.resp.UserInfoResp;
import com.lhc.mallchat.common.user.mapper.UserMapper;
import com.lhc.mallchat.common.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName IUserServiceImpl
 * @Description TODO
 * @Author Administrator
 * @Date 2024/7/7 9:34
 * @Version 1.0
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserDao userDao;


    @Override
    public Long register(User insert) {
        boolean save = userDao.save(insert);
        //todo 用户注册的事件
        return insert.getId();
    }

    @Override
    public UserInfoResp getUserInfo(Long uid) {
        return null;
    }
}
