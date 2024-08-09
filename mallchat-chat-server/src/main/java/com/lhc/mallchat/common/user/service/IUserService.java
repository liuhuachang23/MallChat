package com.lhc.mallchat.common.user.service;

import com.lhc.mallchat.common.user.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lhc.mallchat.common.user.domain.vo.req.BlackReq;
import com.lhc.mallchat.common.user.domain.vo.req.ModifyNameReq;
import com.lhc.mallchat.common.user.domain.vo.req.WearingBadgeReq;
import com.lhc.mallchat.common.user.domain.vo.resp.BadgeResp;
import com.lhc.mallchat.common.user.domain.vo.resp.UserInfoResp;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author <a href="https://github.com/liuhuachang23/MallChat">lhc</a>
 * @since 2024-06-17
 */
public interface IUserService {

    Long register(User insert);

    UserInfoResp getUserInfo(Long uid);

    void modifyName(Long uid, ModifyNameReq req);

    List<BadgeResp> badges(Long uid);

    void wearingBadge(Long uid, WearingBadgeReq req);

    void black(BlackReq req);
}
