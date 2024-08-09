package com.lhc.mallchat.common.user.controller;


import cn.hutool.db.PageResult;
import com.lhc.mallchat.common.common.domain.vo.resp.ApiResult;
import com.lhc.mallchat.common.common.utils.AssertUtil;
import com.lhc.mallchat.common.common.utils.RequestHolder;
import com.lhc.mallchat.common.user.dao.UserDao;
import com.lhc.mallchat.common.user.domain.entity.User;
import com.lhc.mallchat.common.user.domain.enums.RoleEnum;
import com.lhc.mallchat.common.user.domain.vo.req.BlackReq;
import com.lhc.mallchat.common.user.domain.vo.req.ModifyNameReq;
import com.lhc.mallchat.common.user.domain.vo.req.WearingBadgeReq;
import com.lhc.mallchat.common.user.domain.vo.resp.BadgeResp;
import com.lhc.mallchat.common.user.domain.vo.resp.UserInfoResp;
import com.lhc.mallchat.common.user.service.IRoleService;
import com.lhc.mallchat.common.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author <a href="https://github.com/liuhuachang23/MallChat">lhc</a>
 * @since 2024-06-17
 */
@RestController
@RequestMapping("/capi/user")
@Api(tags = "用户管理相关接口")
public class UserController {


    @Resource
    private IUserService userService;

    @Resource
    private IRoleService iRoleService;

    @GetMapping("/userInfo")
    @ApiOperation("用户详情")
    public ApiResult<UserInfoResp> userInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }

    @PutMapping("/name")
    @ApiOperation("修改用户名")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq req) {
        userService.modifyName(RequestHolder.get().getUid(), req);
        return ApiResult.success();
    }

    @GetMapping("/badges")
    @ApiOperation("可选徽章预览")
    public ApiResult<List<BadgeResp>> badges() {
        return ApiResult.success(userService.badges(RequestHolder.get().getUid()));
    }

    @PutMapping("/badge")
    @ApiOperation("佩戴徽章")
    public ApiResult<Void> wearingBadge(@Valid @RequestBody WearingBadgeReq req) {
        userService.wearingBadge(RequestHolder.get().getUid(), req);
        return ApiResult.success();
    }

    @PutMapping("/black")
    @ApiOperation("拉黑用户")
    public ApiResult<Void> black(@Valid @RequestBody BlackReq req) {
        Long uid = RequestHolder.get().getUid();
        boolean hasPower = iRoleService.hasPower(uid, RoleEnum.ADMIN);
        AssertUtil.isTrue(hasPower, "没有权限");
        userService.black(req);
        return ApiResult.success();
    }
}

