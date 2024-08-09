package com.lhc.mallchat.common.websocket;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.lhc.mallchat.common.websocket.domain.enums.WSReqTypeEnum;
import com.lhc.mallchat.common.websocket.domain.enums.WSRespTypeEnum;
import com.lhc.mallchat.common.websocket.domain.vo.req.WSBaseReq;
import com.lhc.mallchat.common.websocket.service.WebSocketService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName NettyWebSocketServerHandler
 * @Description TODO
 * @Author Administrator
 * @Date 2024/6/4 19:37
 * @Version 1.0
 */
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Logger log = LoggerFactory.getLogger(NettyWebSocketServerHandler.class);
    private WebSocketService webSocketService;


    /**
     * 当Channel被激活（‌例如建立连接）‌时，‌会调用此方法。‌它允许开发者在Channel就绪时执行特定的操作
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        webSocketService = SpringUtil.getBean(WebSocketService.class);
        webSocketService.connect(ctx.channel());
    }

    /**
     * handlerAdded和handlerRemoved方法：‌
     * 这两个方法分别在 SimpleChannelInboundHandler被添加到ChannelPipeline中和从ChannelPipeline中移除时被调用。‌
     * 它们允许开发者跟踪和处理Handler的生命周期事件
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        userOffline(ctx.channel());
    }

    /**
     * 用户事件触发
     * 在Netty中，‌userEventTriggered方法是一个重要的回调方法，‌它会在特定的用户事件发生时被调用。‌
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {

        //握手完成事件
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            System.out.println("握手完成");
            //从channel的attr中取出token，进行握手认证
            String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);
            if (StringUtils.isNotEmpty(token)) {
                webSocketService.authorize(ctx.channel(), token);
            }
        }
        //用户下线事件
        else if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                System.out.println("读空闲");
                //用户下线
                userOffline(ctx.channel());
            }
        }
    }

    /**
     * 异常处理
     * 当出现Throwable对象时，‌这个方法会被调用。‌通常用于处理异常情
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught", cause);
        super.exceptionCaught(ctx,cause);
    }

    /**
     * 用户下线统一处理
     *
     * @param channel
     */
    public void userOffline(Channel channel) {
        webSocketService.remove(channel);
        channel.close();
    }

    /**
     * SimpleChannelInboundHandler是Netty中一个非常有用的处理器，它提供了类型安全的消息处理方法，
     * 简化了入站数据的处理过程。通过继承这个类并实现channelRead0方法，你可以轻松地处理特定类型的消息，
     * 并将处理逻辑与消息类型紧密地绑定在一起。这使得代码更加清晰、易于维护和理解。
     *
     * @param msg 需要处理的消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        String text = msg.text();
        WSBaseReq wsBaseReq = JSONUtil.toBean(text, WSBaseReq.class);
        switch (WSReqTypeEnum.of(wsBaseReq.getType())) {
            case LOGIN:         //登录 请求登录二维码
                webSocketService.handleLoginReq(ctx.channel());
                break;
            case HEARTBEAT:     //心跳包
                break;
            case AUTHORIZE:     //登录认证
                webSocketService.authorize(ctx.channel(), wsBaseReq.getData());
        }
    }


}
