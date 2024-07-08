package com.lhc.mallchat.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.lhc.mallchat.common.user.dao.UserDao;
import com.lhc.mallchat.common.user.domain.entity.User;
import com.lhc.mallchat.common.user.service.IUserService;
import com.lhc.mallchat.common.user.service.WXMsgService;
import com.lhc.mallchat.common.user.service.adapter.TextBuilder;
import com.lhc.mallchat.common.user.service.adapter.UserAdapter;
import com.lhc.mallchat.common.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName WXMsgServiceImpl
 * @Description TODO
 * @Author Administrator
 * @Date 2024/7/6 10:53
 * @Version 1.0
 */
@Service
@Slf4j
public class WXMsgServiceImpl implements WXMsgService {

    @Autowired
    private WebSocketService webSocketService;

    /**
     * openId 和 登录code 的临时关系
     */
    private static final ConcurrentHashMap<String, Integer> WAIT_AUTHORIZE_MAP = new ConcurrentHashMap<>();

    @Value("${wx.mp.callback}")
    private String callback;

    public static final String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

    @Autowired
    private UserDao userDao;

    @Autowired
    private IUserService userService;

    @Autowired
    @Lazy
    private WxMpService wxMpService;

    /**
     * 用户扫码成功
     *
     * @param wxMpXmlMessage
     */
    @Override
    public WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage) {
        String openId = wxMpXmlMessage.getFromUser();
        Integer code = getEventKey(wxMpXmlMessage);
        if (Objects.isNull(code)) {
            return null;
        }
        User user = userDao.getByOpenId(openId);
        boolean registered = Objects.nonNull(user);
        boolean authorized = registered && StrUtil.isNotBlank(user.getAvatar());
        //1、用户已经注册并且授权
        if (registered && authorized) {
            //走登录成功逻辑：通过code找到用户channel 进行登录
            webSocketService.scanLoginSuccess(code,user.getId());
            return null;
        }
        //2、用户未注册，去注册
        if (!registered) {
            User insert = UserAdapter.buildUserSave(openId);
            userService.register(insert);
        }
        //3、用户授权
        WAIT_AUTHORIZE_MAP.put(openId, code); //临时保存 openId 和 code 的关联
        webSocketService.withAuthorize(code); //给微信发送一个 等待授权的消息
        //推送链接给用户授权
        String authorizeUrl = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack"));
        return TextBuilder.build("请点击登录："+ "</a href=\"" + authorizeUrl + "\">登录</a>",wxMpXmlMessage);

    }

    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {
        String openid = userInfo.getOpenid();
        User user = userDao.getByOpenId(openid);
        //更新用户信息
        if (StrUtil.isBlank(user.getAvatar())){
            fillUserInfo(user.getId(),userInfo);
        }
        //通过code找到用户channel 进行登录
        Integer code = WAIT_AUTHORIZE_MAP.remove(openid);
        webSocketService.scanLoginSuccess(code,user.getId());
    }

    private void fillUserInfo(Long uid, WxOAuth2UserInfo userInfo) {
        User user = UserAdapter.buidAuthorizeUser(uid, userInfo);
        userDao.updateById(user);
    }

    private Integer getEventKey(WxMpXmlMessage wxMpXmlMessage) {
        try {
            String eventKey = wxMpXmlMessage.getEventKey();
            String code = eventKey.replaceAll("qrscene", "");
            return Integer.parseInt(code);
        } catch (Exception e) {
            log.error("getEventKey error eventKey:{}", wxMpXmlMessage.getEventKey(), e);
            return null;
        }
    }
}
