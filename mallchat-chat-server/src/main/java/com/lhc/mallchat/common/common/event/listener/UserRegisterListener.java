package com.lhc.mallchat.common.common.event.listener;

import com.lhc.mallchat.common.common.domain.enums.IdempotentEnum;
import com.lhc.mallchat.common.common.event.UserRegisterEvent;
import com.lhc.mallchat.common.user.dao.UserDao;
import com.lhc.mallchat.common.user.domain.entity.User;
import com.lhc.mallchat.common.user.domain.enums.ItemEnum;
import com.lhc.mallchat.common.user.service.IUserBackpackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @ClassName UserRegisterListener
 * @Description 用户注册事件 监听者
 * @Author Administrator
 * @Date 2024/8/8 19:20
 * @Version 1.0
 */
@Slf4j
@Component
public class UserRegisterListener {
    @Autowired
    private UserDao userDao;
    @Autowired
    private IUserBackpackService userBackpackService;

    //当UserRegisterEvent事件发生的时候执行这个方法
    @EventListener(classes = UserRegisterEvent.class)
    public void sendCard(UserRegisterEvent event) {
        User user = event.getUser();
        //发送改名卡
        userBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(), IdempotentEnum.UID, user.getId().toString());
    }

    @Async
    @TransactionalEventListener(classes = UserRegisterEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void sendBadge(UserRegisterEvent event) {
        //发送注册徽章
        User user = event.getUser();
        int count = userDao.count();// 性能瓶颈，等注册用户多了直接删掉
        if (count <= 10) {
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP10_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        } else if (count <= 100) {
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP100_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        }
    }


}
