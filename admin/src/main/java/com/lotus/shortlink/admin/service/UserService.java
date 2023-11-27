package com.lotus.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lotus.shortlink.admin.dao.entity.UserDO;
import com.lotus.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.lotus.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.lotus.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.lotus.shortlink.admin.dto.resp.UserRespDTO;

/**
 * @author lucas
 */
public interface UserService extends IService<UserDO> {
    UserRespDTO getUserByUsername(String username);
    Boolean hasUserName(String username);
    void register(UserRegisterReqDTO reqParam);
    void update(UserUpdateReqDTO reqParam);
    UserLoginRespDTO login(UserRegisterReqDTO reqParam);
    Boolean checkLogin(String username, String token);
    void logout(String username, String token);
}
