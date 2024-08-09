package com.lhc.mallchat.common.common.intercepter;

import cn.hutool.core.collection.CollectionUtil;
import com.lhc.mallchat.common.common.domain.dto.RequestInfo;
import com.lhc.mallchat.common.common.exception.HttpErrorEnum;
import com.lhc.mallchat.common.common.utils.RequestHolder;
import com.lhc.mallchat.common.user.domain.enums.BlackTypeEnum;
import com.lhc.mallchat.common.user.service.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 黑名单拦截
 * 拦截 所有黑名单中的用户 发送的请求
 */
@Order(2)
@Slf4j
@Component
public class BlackInterceptor implements HandlerInterceptor {

    @Autowired
    private UserCache userCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //拉黑列表 k: 拉黑类型（"ip"/"uid"） v：拉黑目标(ip/uid 的值)
        Map<Integer, Set<String>> blackMap = userCache.getBlackMap();
        RequestInfo requestInfo = RequestHolder.get();

        //判断 这个 uid 是否在黑名单中
        if (inBlackList(requestInfo.getUid(), blackMap.get(BlackTypeEnum.UID.getType()))) {
            //如果是 直接返回错误
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
        //判断 这个 ip 是否在黑名单中
        if (inBlackList(requestInfo.getIp(), blackMap.get(BlackTypeEnum.IP.getType()))) {
            //如果是 直接返回错误
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
        return true;
    }

    //判断 这个 uid/ip 是否在黑名单中
    private boolean inBlackList(Object target, Set<String> blackSet) {
        if (Objects.isNull(target) || CollectionUtil.isEmpty(blackSet)) {
            return false;
        }
        return blackSet.contains(target.toString());
    }

}