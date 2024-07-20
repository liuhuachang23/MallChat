package com.lhc.mallchat.common.user.controller;


import cn.hutool.db.PageResult;
import com.lhc.mallchat.common.common.domain.vo.resp.ApiResult;
import com.lhc.mallchat.common.common.utils.RequestHolder;
import com.lhc.mallchat.common.user.dao.UserDao;
import com.lhc.mallchat.common.user.domain.entity.User;
import com.lhc.mallchat.common.user.domain.vo.resp.UserInfoResp;
import com.lhc.mallchat.common.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2024-06-17
 */
@RestController
@RequestMapping("/capi/user")
@Api(tags = "用户管理相关接口")
public class UserController {


    @Resource
    private IUserService userService;

    @GetMapping("/userInfo")
    @ApiOperation("用户详情")
    public ApiResult<UserInfoResp> userInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }
}

