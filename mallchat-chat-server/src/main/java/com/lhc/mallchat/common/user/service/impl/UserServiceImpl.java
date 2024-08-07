package com.lhc.mallchat.common.user.service.impl;

import com.lhc.mallchat.common.common.utils.AssertUtil;
import com.lhc.mallchat.common.common.utils.sensitiveWord.SensitiveWordBs;
import com.lhc.mallchat.common.user.dao.UserBackpackDao;
import com.lhc.mallchat.common.user.dao.UserDao;
import com.lhc.mallchat.common.user.domain.entity.User;
import com.lhc.mallchat.common.user.domain.entity.UserBackpack;
import com.lhc.mallchat.common.user.domain.enums.ItemEnum;
import com.lhc.mallchat.common.user.domain.vo.req.ModifyNameReq;
import com.lhc.mallchat.common.user.domain.vo.resp.UserInfoResp;
import com.lhc.mallchat.common.user.mapper.UserMapper;
import com.lhc.mallchat.common.user.service.IUserService;
import com.lhc.mallchat.common.user.service.adapter.UserAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName IUserServiceImpl
 * @Description TODO
 * @Author Administrator
 * @Date 2024/7/7 9:34
 * @Version 1.0
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserBackpackDao userBackpackDao;
    @Autowired
    private SensitiveWordBs sensitiveWordBs;

    @Override
    public Long register(User insert) {
        boolean save = userDao.save(insert);
        //todo 用户注册的事件
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
        AssertUtil.isFalse(sensitiveWordBs.hasSensitiveWord(newName), "名字中包含敏感词，请重新输入"); // 判断名字中有没有敏感词
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
            userCache.userInfoChange(uid);
        }
    }
}
