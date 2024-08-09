package com.lhc.mallchat.common.common.event.listener;

import com.lhc.mallchat.common.common.event.UserBlackEvent;
import com.lhc.mallchat.common.user.dao.UserDao;
import com.lhc.mallchat.common.user.service.cache.UserCache;
import com.lhc.mallchat.common.websocket.domain.enums.WSRespTypeEnum;
import com.lhc.mallchat.common.websocket.domain.vo.resp.WSBaseResp;
import com.lhc.mallchat.common.websocket.domain.vo.resp.WSBlack;
import com.lhc.mallchat.common.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 用户拉黑监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class UserBlackListener {
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserCache userCache;

    @Async
    @EventListener(classes = UserBlackEvent.class)
    public void sendPush(UserBlackEvent event) {
        Long uid = event.getUser().getId();
        WSBaseResp<WSBlack> resp = new WSBaseResp<>();
        WSBlack black = new WSBlack(uid);
        resp.setData(black);
        resp.setType(WSRespTypeEnum.BLACK.getType());
        //给所有在线用户发送通知（告诉所有人 这个用户被拉黑了。前端就可以用来屏蔽这个人发送的所有消息等）
        webSocketService.sendToAllOnline(resp, uid);
    }


    @Async
    @EventListener(classes = UserBlackEvent.class)
    public void changeUserStatus(UserBlackEvent event) {
        //将用户状态改完拉黑
        userDao.invalidUid(event.getUser().getId());
    }

    @Async
    @EventListener(classes = UserBlackEvent.class)
    public void refreshRedis(UserBlackEvent event) {
        //清空 黑名单缓存
        userCache.evictBlackMap();
    }
}
