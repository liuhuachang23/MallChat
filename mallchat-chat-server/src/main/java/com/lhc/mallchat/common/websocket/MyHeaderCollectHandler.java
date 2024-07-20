package com.lhc.mallchat.common.websocket;

import cn.hutool.core.net.url.UrlBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;

import java.util.Optional;

/**
 * @ClassName MyHandShakeHandler
 * @Description TODO 获取请求头中的token、将token保存到 channel的attr中，后续握手完成的时候 可以直接去认证
 * @Author Administrator
 * @Date 2024/7/13 9:46
 * @Version 1.0
 */
public class MyHeaderCollectHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            //获取请求头uri中携带的token
            //uri: /?token=wrwefssdfr
            HttpRequest request = (HttpRequest) msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());
            Optional<String> tokenOptional = Optional.of(urlBuilder)
                    .map(UrlBuilder::getQuery)
                    .map(k -> k.get("token"))
                    .map(CharSequence::toString);
            //如果token存在,就将token保存到 channel的attr中，后续监听到握手完成事件的时候再取出来，直接进行认证
            tokenOptional.ifPresent(f -> NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, f));
            //将uri设置成不带参数的
            // uri: /?token=wrwefssdfr -> /
            request.setUri(urlBuilder.getPath().toString());
        }
        //触发责任链中后续事件的执行
        ctx.fireChannelRead(msg);
    }
}
