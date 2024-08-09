package com.lhc.mallchat.common.common.event.listener;

import com.lhc.mallchat.common.common.domain.enums.IdempotentEnum;
import com.lhc.mallchat.common.common.event.UserOnlineEvent;
import com.lhc.mallchat.common.common.event.UserRegisterEvent;
import com.lhc.mallchat.common.user.dao.UserDao;
import com.lhc.mallchat.common.user.domain.entity.User;
import com.lhc.mallchat.common.user.domain.enums.ChatActiveStatusEnum;
import com.lhc.mallchat.common.user.domain.enums.ItemEnum;
import com.lhc.mallchat.common.user.service.IUserBackpackService;
import com.lhc.mallchat.common.user.service.IpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @ClassName UserRegisterListener
 * @Description 用户上线成功的事件 监听者
 * @Author Administrator
 * @Date 2024/8/8 19:20
 * @Version 1.0
 */
@Slf4j
@Component
public class UserOnlineListener {
    @Autowired
    private UserDao userDao;
    @Autowired
    private IpService ipService;

    //当UserRegisterEvent事件发生的时候执行这个方法
    @EventListener(classes = UserOnlineEvent.class)
    public void saveDB(UserOnlineEvent event) {
        User user = event.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setIpInfo(user.getIpInfo());
        update.setActiveStatus(ChatActiveStatusEnum.ONLINE.getStatus());
        userDao.updateById(update);
        //更新用户ip详情
        ipService.refreshIpDetailAsync(user.getId());
    }

}
