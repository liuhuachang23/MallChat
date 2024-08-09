package com.lhc.mallchat.common.websocket;

import cn.hutool.core.net.url.UrlBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
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

            //1、取用户token
            //1）获取请求头uri中携带的token（uri eg: /?token=wrwefssdfr）
            HttpRequest request = (HttpRequest) msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());
            Optional<String> tokenOptional = Optional.of(urlBuilder)
                    .map(UrlBuilder::getQuery)
                    .map(k -> k.get("token"))
                    .map(CharSequence::toString);
            //2）如果token存在,就将token保存到 channel的attr附件中，后续监听到握手完成事件的时候再取出来，直接进行认证
            tokenOptional.ifPresent(f -> NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, f));
            //3）将uri设置成不带参数的（uri: /?token=wrwefssdfr -> /）
            request.setUri(urlBuilder.getPath().toString());

            //2、取用户ip
            //1）取出ip
            String ip = request.headers().get("X-Real-IP"); //先去nginx中获取
            if (StringUtils.isBlank(ip)) {                  //没有，说明是直连的，直接获取直连的ip
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                ip = address.getAddress().getHostAddress();
            }
            //2）将ip保存到 channel的attr附件中、后续监听到用户上线成功事件的时候取出来 解析并保存用户的ip归属地信息
            NettyUtil.setAttr(ctx.channel(), NettyUtil.IP, ip);

            //处理器只需要用一次
            ctx.pipeline().remove(this);
        }
        //触发责任链中后续事件的执行
        ctx.fireChannelRead(msg);
    }
}
