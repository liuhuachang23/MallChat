package com.lhc.mallchat.common.user.service;

import com.lhc.mallchat.common.user.domain.enums.RoleEnum;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *<a href="https://github.com/liuhuachang23/MallChat">lhc</a>
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-06-04
 */
public interface IRoleService {

    /**
     * 是否有某个权限，临时做法
     *
     * @return
     */
    boolean hasPower(Long uid, RoleEnum roleEnum);

}
