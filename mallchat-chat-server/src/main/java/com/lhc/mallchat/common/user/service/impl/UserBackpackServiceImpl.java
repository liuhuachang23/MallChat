package com.lhc.mallchat.common.user.service.impl;

import com.lhc.mallchat.common.common.annotation.RedissonLock;
import com.lhc.mallchat.common.common.domain.enums.IdempotentEnum;
import com.lhc.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.lhc.mallchat.common.common.utils.AssertUtil;
import com.lhc.mallchat.common.user.dao.UserBackpackDao;
import com.lhc.mallchat.common.user.domain.entity.ItemConfig;
import com.lhc.mallchat.common.user.domain.entity.UserBackpack;
import com.lhc.mallchat.common.user.domain.enums.ItemTypeEnum;
import com.lhc.mallchat.common.user.service.IUserBackpackService;
import com.lhc.mallchat.common.user.service.cache.ItemCache;
import org.checkerframework.checker.units.qual.A;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 *
 * @since 2023-03-19
 */
@Service
public class UserBackpackServiceImpl implements IUserBackpackService {

    @Autowired
    private ItemCache itemCache;
    @Autowired
    private UserBackpackDao userBackpackDao;
    @Autowired
    @Lazy
    private UserBackpackServiceImpl userBackpackService;

    @Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        //组装幂等号
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        //发放物品
        //同类调用，避免aop失效 方法1：自己引入自己 userBackpackService
        userBackpackService.doAcquireItem(uid, itemId, idempotent);
        //方法2：获取这个类的aop代理，使用代理去调用
        //((UserBackpackServiceImpl)AopContext.currentProxy()).doAcquireItem(uid, itemId, idempotent);
    }

    @RedissonLock(key = "#idempotent", waitTime = 5000)//相同幂等如果同时发奖，需要排队等上一个执行完，取出之前数据返回
    public void doAcquireItem(Long uid, Long itemId, String idempotent) {
        UserBackpack userBackpack = userBackpackDao.getByIdp(idempotent);
        //幂等检查
        if (Objects.nonNull(userBackpack)) {
            return;
        }
        //业务检查
        ItemConfig itemConfig = itemCache.getById(itemId);
        if (ItemTypeEnum.BADGE.getType().equals(itemConfig.getType())) {//徽章类型做唯一性检查
            Integer countByValidItemId = userBackpackDao.getCountByValidItemId(uid, itemId);
            if (countByValidItemId > 0) {//已经有徽章了不发
                return;
            }
        }
        //发物品
        UserBackpack insert = UserBackpack.builder()
                .uid(uid)
                .itemId(itemId)
                .status(YesOrNoEnum.NO.getStatus())
                .idempotent(idempotent)
                .build();
        userBackpackDao.save(insert);
        //用户收到物品的事件
        //applicationEventPublisher.publishEvent(new ItemReceiveEvent(this, insert));
    }


    //组装幂等号
    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s", itemId, idempotentEnum.getType(), businessId);
    }
}
