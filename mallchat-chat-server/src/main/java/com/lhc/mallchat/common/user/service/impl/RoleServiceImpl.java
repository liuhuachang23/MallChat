package com.lhc.mallchat.common.user.service.impl;

import com.lhc.mallchat.common.user.domain.enums.RoleEnum;
import com.lhc.mallchat.common.user.service.IRoleService;
import com.lhc.mallchat.common.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

/**
 * @ClassName RoleServiceImpl
 * @Description TODO
 * @Author Administrator
 * @Date 2024/8/9 18:27
 * @Version 1.0
 */
@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private UserCache userCache;

    @Override
    public boolean hasPower(Long uid, RoleEnum roleEnum) {//超级管理员无敌的好吧，后期做成权限=》资源模式
        Set<Long> roleSet = userCache.getRoleSet(uid);
        return isAdmin(roleSet) || roleSet.contains(roleEnum.getId());
    }

    private boolean isAdmin(Set<Long> roleSet) {
        return Objects.requireNonNull(roleSet).contains(RoleEnum.ADMIN.getId());
    }
}
