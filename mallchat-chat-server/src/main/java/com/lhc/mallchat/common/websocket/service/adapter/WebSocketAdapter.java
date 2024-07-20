package com.lhc.mallchat.common.websocket.service.adapter;

import com.lhc.mallchat.common.user.domain.entity.User;
import com.lhc.mallchat.common.websocket.domain.enums.WSRespTypeEnum;
import com.lhc.mallchat.common.websocket.domain.vo.resp.WSBaseResp;
import com.lhc.mallchat.common.websocket.domain.vo.resp.WSLoginSuccess;
import com.lhc.mallchat.common.websocket.domain.vo.resp.WSLoginUrl;
import lombok.Data;
import me.chanjar.weixin.mp.api.WxMpQrcodeService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

/**
 * @ClassName WebSocketAdapter
 * @Description TODO
 * @Author Administrator
 * @Date 2024/7/6 10:19
 * @Version 1.0
 */

public class WebSocketAdapter {

    public static WSBaseResp<?> buildResp(WxMpQrCodeTicket wxMpQrCodeTicket){
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        resp.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        return resp;
    }

    //登录成功
    public static WSBaseResp<?> buildResp(User user, String token) {
        WSBaseResp<WSLoginSuccess> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess wsLoginSuccess = WSLoginSuccess.builder()
                .avatar(user.getAvatar())
                .token(token)
                .uid(user.getId())
                .build();
        resp.setData(wsLoginSuccess);
        return resp;
    }

    //用户扫描成功等待授权
    public static WSBaseResp<?> buildWaitAuthorizeResp() {
        WSBaseResp<WSLoginSuccess> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return resp;
    }

    //token失效
    public static WSBaseResp<?> buildInvalidTokenResp() {
        WSBaseResp<WSLoginSuccess> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        return resp;
    }
}
