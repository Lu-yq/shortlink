package com.lotus.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.lotus.shortlink.admin.common.convention.result.Result;
import com.lotus.shortlink.admin.common.convention.result.Results;
import com.lotus.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.lotus.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.lotus.shortlink.admin.dto.resp.ActualUserRespDTO;
import com.lotus.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.lotus.shortlink.admin.dto.resp.UserRespDTO;
import com.lotus.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/short-link/admin/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        return Results.success(userService.getUserByUsername(username));
    }

    @GetMapping("/api/short-link/admin/v1/actual/user/{username}")
    public Result<ActualUserRespDTO> getActualUserByUsername(@PathVariable("username") String username) {
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username), ActualUserRespDTO.class));
    }

    @GetMapping("/api/short-link/admin/v1/user/has-username/{username}")
    public Result<Boolean> hasUsername(@PathVariable("username") String username) {
        return Results.success(userService.hasUserName(username));
    }

    @PostMapping("/api/short-link/admin/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDTO reqParam) {
        userService.register(reqParam);
        return Results.success();
    }

    @PutMapping("/api/short-link/admin/v1/user")
    public Result<Void> update(@RequestBody UserUpdateReqDTO reqParam) {
        userService.update(reqParam);
        return Results.success();
    }

    @PostMapping("/api/short-link/admin/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserRegisterReqDTO reqParam) {
        return Results.success(userService.login(reqParam));
    }

    @GetMapping("/api/short-link/admin/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("username") String username, @RequestParam("token") String token) {
        return Results.success(userService.checkLogin(username, token));
    }

    @DeleteMapping("/api/short-link/admin/v1/user/logout")
    public Result<Void> logout(@RequestParam("username") String username, @RequestParam("token") String token) {
        userService.logout(username, token);
        return Results.success();
    }
}
