package com.lhc.mallchat.common.user.service.impl;

import com.lhc.mallchat.common.common.constant.RedisKey;
import com.lhc.mallchat.common.user.dao.UserDao;
import com.lhc.mallchat.common.user.service.LoginService;
import com.lhc.mallchat.common.common.utils.JwtUtils;
import com.lhc.mallchat.common.common.utils.RedisUtils;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName LoginServiceImpl
 * @Description TODO
 * @Author Administrator
 * @Date 2024/7/7 10:17
 * @Version 1.0
 */
@Service
public class LoginServiceImpl implements LoginService {

    public static final int TOKEN_EXPIRE_DAYS = 3;
    public static final int TOKEN_RENEWAL_DAYS = 1;

    @Autowired
    private JwtUtils jwtUtils;

    ThreadPoolExecutor executor;

    @Override
    public boolean verify(String token) {
        return false;
    }

    @Override
    public void renewalTokenIfNecessary(String token) {
        executor.execute(()->{
            Long uid = getValidUid(token);
            String userTokenKey = getUserTokenKey(uid);
            Long expireDays = RedisUtils.getExpire(userTokenKey, TimeUnit.DAYS);
            if (expireDays == -2){  //不存在的key
                return;
            }
            if (expireDays < TOKEN_RENEWAL_DAYS){ //小于指定天数就续期
                RedisUtils.set(getUserTokenKey(uid), token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
            }
        });
    }

    @Override
    public String login(Long uid) {
        String token = jwtUtils.createToken(uid);
        RedisUtils.set(getUserTokenKey(uid), token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        return token;
    }

    @Override
    public Long getValidUid(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if (Objects.isNull(uid)){
            return null;
        }
        String oldToken = RedisUtils.getStr(getUserTokenKey(uid));
        if (StringUtils.isBlank(oldToken)){
            return null;
        }
        return Objects.equals(oldToken, token) ? uid : null;
    }

    private String getUserTokenKey(Long uid){
        return RedisKey.getKey(RedisKey.USER_TOKEN_STRING,uid);
    }
}
