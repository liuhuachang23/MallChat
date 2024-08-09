package com.lhc.mallchat.common.websocket.service;

import com.lhc.mallchat.common.websocket.domain.vo.resp.WSBaseResp;
import com.lhc.mallchat.common.websocket.domain.vo.resp.WSBlack;
import io.netty.channel.Channel;

/**
 * @ClassName WebSocketService
 * @Description TODO
 * @Author Administrator
 * @Date 2024/7/6 9:20
 * @Version 1.0
 */
public interface WebSocketService {

    void connect(Channel channel);

    void handleLoginReq(Channel channel);

    void remove(Channel channel);

    void scanLoginSuccess(Integer code, Long uid);

    void withAuthorize(Integer code);

    void authorize(Channel channel, String token);

    void sendToAllOnline(WSBaseResp<?> wsBaseResp, Long skipUid);
}
