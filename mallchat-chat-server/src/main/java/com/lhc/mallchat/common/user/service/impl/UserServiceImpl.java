package com.lhc.mallchat.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.lhc.App;
import com.lhc.mallchat.common.common.event.UserBlackEvent;
import com.lhc.mallchat.common.common.event.UserRegisterEvent;
import com.lhc.mallchat.common.common.utils.AssertUtil;
import com.lhc.mallchat.common.user.dao.BlackDao;
import com.lhc.mallchat.common.user.dao.ItemConfigDao;
import com.lhc.mallchat.common.user.dao.UserBackpackDao;
import com.lhc.mallchat.common.user.dao.UserDao;
import com.lhc.mallchat.common.user.domain.entity.*;
import com.lhc.mallchat.common.user.domain.enums.BlackTypeEnum;
import com.lhc.mallchat.common.user.domain.enums.ItemEnum;
import com.lhc.mallchat.common.user.domain.enums.ItemTypeEnum;
import com.lhc.mallchat.common.user.domain.vo.req.BlackReq;
import com.lhc.mallchat.common.user.domain.vo.req.ModifyNameReq;
import com.lhc.mallchat.common.user.domain.vo.req.WearingBadgeReq;
import com.lhc.mallchat.common.user.domain.vo.resp.BadgeResp;
import com.lhc.mallchat.common.user.domain.vo.resp.UserInfoResp;
import com.lhc.mallchat.common.user.service.IUserService;
import com.lhc.mallchat.common.user.service.adapter.UserAdapter;
import com.lhc.mallchat.common.user.service.cache.ItemCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @ClassName IUserServiceImpl
 * @Description TODO
 * @Author Administrator
 * @Date 2024/7/7 9:34
 * @Version 1.0
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserBackpackDao userBackpackDao;
    @Autowired
    private ItemCache itemCache;
    @Autowired
    private ItemConfigDao itemConfigDao;
    @Autowired
    private BlackDao blackDao;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Long register(User insert) {
        boolean save = userDao.save(insert);
        //用户注册的事件
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this,insert));
        return insert.getId();
    }

    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User userInfo = userDao.getById(uid);
        Integer countByValidItemId = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        return UserAdapter.buildUserInfoResp(userInfo, countByValidItemId);
    }

    @Override
    @Transactional
    public void modifyName(Long uid, ModifyNameReq req) {
        //判断名字是不是重复
        String newName = req.getName();
        //AssertUtil.isFalse(sensitiveWordBs.hasSensitiveWord(newName), "名字中包含敏感词，请重新输入"); // 判断名字中有没有敏感词
        User oldUser = userDao.getByName(newName);
        AssertUtil.isEmpty(oldUser, "名字已经被抢占了，请换一个哦~~");
        //判断改名卡够不够
        UserBackpack firstValidItem = userBackpackDao.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(firstValidItem, "改名次数不够了，等后续活动送改名卡哦");
        //使用改名卡
        boolean useSuccess = userBackpackDao.invalidItem(firstValidItem.getId());
        if (useSuccess) {//用乐观锁，就不用分布式锁了
            //改名
            userDao.modifyName(uid, req.getName());
            //删除缓存
            //userCache.userInfoChange(uid);
        }
    }

    @Override
    public List<BadgeResp> badges(Long uid) {
        //查询所有徽章
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        //查询用户拥有的徽章
        List<UserBackpack> backpacks = userBackpackDao.getByItemIds(uid, itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList()));
        //查询用户当前佩戴的标签
        User user = userDao.getById(uid);
        return UserAdapter.buildBadgeResp(itemConfigs, backpacks, user);
    }

    @Override
    public void wearingBadge(Long uid, WearingBadgeReq req) {
        //确保有这个徽章
        UserBackpack firstValidItem = userBackpackDao.getFirstValidItem(uid, req.getBadgeId());
        AssertUtil.isNotEmpty(firstValidItem, "您没有这个徽章哦，快去达成条件获取吧");
        //确保物品类型是徽章
        ItemConfig itemConfig = itemConfigDao.getById(firstValidItem.getItemId());
        AssertUtil.equal(itemConfig.getType(), ItemTypeEnum.BADGE.getType(), "该徽章不可佩戴");
        //佩戴徽章
        userDao.wearingBadge(uid, req.getBadgeId());
        //删除用户缓存
        //userCache.userInfoChange(uid);
    }

    @Override
    public void black(BlackReq req) {
        Long uid = req.getUid();
        Black black = new Black();
        black.setTarget(uid.toString());
        black.setType(BlackTypeEnum.UID.getType());
        blackDao.save(black);
        User user = userDao.getById(uid);
        blackIp(Optional.ofNullable(user.getIpInfo()).map(IpInfo::getCreateIp).orElse(null));
        blackIp(Optional.ofNullable(user.getIpInfo()).map(IpInfo::getUpdateIp).orElse(null));
        applicationEventPublisher.publishEvent(new UserBlackEvent(this, user));
    }

    public void blackIp(String ip) {
        if (StrUtil.isBlank(ip)) {
            return;
        }
        try {
            Black user = new Black();
            user.setTarget(ip);
            user.setType(BlackTypeEnum.IP.getType());
            blackDao.save(user);
        } catch (Exception e) {
            log.error("duplicate black ip:{}", ip);
        }
    }
}
