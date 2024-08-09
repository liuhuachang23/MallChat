package com.lhc.mallchat.common.user.service;

/**
 * @ClassName IpService
 * @Description TODO
 * @Author Administrator
 * @Date 2024/8/9 12:08
 * @Version 1.0
 */
public interface IpService {

    /**
     * 异步更新用户ip详情
     *
     * @param uid
     */
    void refreshIpDetailAsync(Long uid);

}
