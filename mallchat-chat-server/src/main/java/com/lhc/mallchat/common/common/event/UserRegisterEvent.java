package com.lhc.mallchat.common.common.event;

import com.lhc.mallchat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @ClassName UserRegisterEvent
 * @Description 用户注册事件
 * @Author Administrator
 * @Date 2024/8/8 19:18
 * @Version 1.0
 */
@Getter
public class UserRegisterEvent extends ApplicationEvent {

    private User user;

    public UserRegisterEvent(Object source,User user) {
        super(source);
        this.user = user;
    }
}
