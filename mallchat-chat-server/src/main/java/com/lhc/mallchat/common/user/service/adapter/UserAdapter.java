package com.lhc.mallchat.common.user.service.adapter;

import com.lhc.mallchat.common.user.domain.entity.User;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;


/**
 * @ClassName UserAdapter
 * @Description TODO
 * @Author Administrator
 * @Date 2024/7/7 9:30
 * @Version 1.0
 */

public class UserAdapter {
    public static User buildUserSave(String openId) {
        return User.builder().openId(openId).build();
    }

    public static User buidAuthorizeUser(Long uid, WxOAuth2UserInfo userInfo) {
        User user = new User();
        user.setId(uid);
        user.setName(userInfo.getNickname());
        user.setAvatar(userInfo.getHeadImgUrl());
        return user;
    }
}
