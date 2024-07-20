package com.lhc.mallchat.common.websocket;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;


/**
 * @ClassName NettyUtil
 * @Description TODO
 * @Author Administrator
 * @Date 2024/7/13 9:52
 * @Version 1.0
 */
public class NettyUtil {

    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("channel");

    public static <T> void setAttr(Channel channel, AttributeKey<T> key, T value) {
        Attribute<T> attr = channel.attr(key);
        attr.set(value);
    }

    public static <T> T getAttr(Channel channel, AttributeKey<T> key) {
        Attribute<T> attr = channel.attr(key);
        return attr.get();
    }
}
