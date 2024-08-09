package com.lhc.mallchat.common.websocket.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.lhc.mallchat.common.common.event.UserOnlineEvent;
import com.lhc.mallchat.common.user.dao.UserDao;
import com.lhc.mallchat.common.user.domain.entity.User;
import com.lhc.mallchat.common.user.service.LoginService;
import com.lhc.mallchat.common.websocket.NettyUtil;
import com.lhc.mallchat.common.websocket.domain.vo.resp.WSBaseResp;
import com.lhc.mallchat.common.websocket.domain.dto.WSChannelExtraDTO;
import com.lhc.mallchat.common.websocket.service.WebSocketService;
import com.lhc.mallchat.common.websocket.service.adapter.WebSocketAdapter;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName WebSocketServiceImpl
 * @Description 专门管理WebSocket的逻辑，包括推拉
 * @Author Administrator
 * @Date 2024/7/6 9:20
 * @Version 1.0
 */
@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    @Lazy
    private WxMpService wxMpService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 管理所有在线用户的链接（登录态/游客）
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WE_MAP = new ConcurrentHashMap<>();

    public static final int MAXIMUM_SIZE = 1000;
    public static final Duration DURATION = Duration.ofHours(1);
    /**
     * 临时保存 登录code和channel的映射关系
     */
    private static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .maximumSize(MAXIMUM_SIZE)
            .expireAfterWrite(DURATION)
            .build();

    @Override
    public void connect(Channel channel) {
        ONLINE_WE_MAP.put(channel, new WSChannelExtraDTO());
    }

    @SneakyThrows
    @Override
    public void handleLoginReq(Channel channel) {
        //生成随机码
        Integer code = generateLoginCode(channel);
        //找微信申请待参二维码
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) DURATION.getSeconds());
        //把二维码推送给前端
        sendMsg(channel, WebSocketAdapter.buildResp(wxMpQrCodeTicket));
    }

    @Override
    public void remove(Channel channel) {
        ONLINE_WE_MAP.remove(channel);
        //todo 用户下线
    }

    @Override
    public void scanLoginSuccess(Integer code, Long uid) {
        //确认链接在机器上
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return;
        }
        User user = userDao.getById(uid);
        //移除code
        WAIT_LOGIN_MAP.invalidate(code);
        //调用登录模块获取token
        String token = loginService.login(uid);
        //用户登录成功
        loginSuccess(channel,user,token);
    }

    @Override
    public void withAuthorize(Integer code) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return;
        }
        sendMsg(channel, WebSocketAdapter.buildWaitAuthorizeResp());
    }

    @Override
    public void authorize(Channel channel, String token) {
        Long uid = loginService.getValidUid(token);
        if (Objects.nonNull(uid)) {
            User user = userDao.getById(uid);
            //用户登录成功
            loginSuccess(channel,user,token);
        } else {
            //发送一个token失效的消息
            sendMsg(channel,WebSocketAdapter.buildInvalidTokenResp());
        }

    }

    private void loginSuccess(Channel channel, User user, String token) {
        //保存channel对应的uid
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WE_MAP.get(channel);
        wsChannelExtraDTO.setUid(user.getId());
        //发送一个登录成功的消息
        sendMsg(channel,WebSocketAdapter.buildResp(user,token));
        //用户上线成功的事件
        user.setLastOptTime(new Date());
        user.refreshIp(NettyUtil.getAttr(channel, NettyUtil.IP));
        applicationEventPublisher.publishEvent(new UserOnlineEvent(this,user));
    }

    private void sendMsg(Channel channel, WSBaseResp<?> resp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(resp)));
    }

    private Integer generateLoginCode(Channel channel) {
        Integer code;
        do {
            code = RandomUtil.randomInt(MAXIMUM_SIZE);
        } while (!Objects.isNull(WAIT_LOGIN_MAP.asMap().putIfAbsent(code, channel)));
        return code;
    }

}
