package com.lhc.mallchat.common.user.service.impl;

import com.lhc.mallchat.common.user.dao.UserDao;
import com.lhc.mallchat.common.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName LoginServiceImpl
 * @Description TODO
 * @Author Administrator
 * @Date 2024/7/7 10:17
 * @Version 1.0
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserDao userDao;

    @Override
    public String login(Long uid) {
        return "";
    }
}
