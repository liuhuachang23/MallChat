package com.lhc.mallchat.common.user.service;

/**
 * @ClassName LoginService
 * @Description TODO
 * @Author Administrator
 * @Date 2024/7/7 10:17
 * @Version 1.0
 */
public interface LoginService {

    /**
     * 校验token是不是有效
     *
     * @param token
     * @return
     */
    boolean verify(String token);

    /**
     * 刷新token有效期
     *
     * @param token
     */
    void renewalTokenIfNecessary(String token);

    /**
     * 登录成功，获取token
     *
     * @param uid
     * @return 返回token
     */
    String login(Long uid);

    /**
     * 如果token有效，返回uid
     *
     * @param token
     * @return
     */
    Long getValidUid(String token);
}
