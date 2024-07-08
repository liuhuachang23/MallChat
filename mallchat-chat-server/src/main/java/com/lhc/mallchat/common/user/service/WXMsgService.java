package com.lhc.mallchat.common.user.service;

import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * @ClassName WXMsgService
 * @Description TODO
 * @Author Administrator
 * @Date 2024/7/6 10:53
 * @Version 1.0
 */
public interface WXMsgService {

    /**
     * 用户扫码
     * @param wxMpXmlMessage
     * @return
     */
    WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage);

    /**
     * 用户授权
     * @param userInfo
     */
    void authorize(WxOAuth2UserInfo userInfo);
}
