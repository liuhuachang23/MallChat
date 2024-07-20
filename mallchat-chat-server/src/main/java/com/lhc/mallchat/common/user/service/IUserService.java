package com.lhc.mallchat.common.user.service;

import com.lhc.mallchat.common.user.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lhc.mallchat.common.user.domain.vo.resp.UserInfoResp;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2024-06-17
 */
public interface IUserService {

    Long register(User insert);

    UserInfoResp getUserInfo(Long uid);
}
