package com.lhc.mallchat.common.common.event;

import com.lhc.mallchat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @ClassName UserOnlineEvent
 * @Description 用户上线成功的事件
 * @Author Administrator
 * @Date 2024/8/9 11:26
 * @Version 1.0
 */
@Getter
public class UserOnlineEvent extends ApplicationEvent {

    private User user;

    public UserOnlineEvent(Object source,User user) {
        super(source);
        this.user = user;
    }
}
