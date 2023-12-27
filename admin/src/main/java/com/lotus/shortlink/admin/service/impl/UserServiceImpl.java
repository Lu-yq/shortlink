package com.lotus.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lotus.shortlink.admin.common.convention.exception.ClientException;
import com.lotus.shortlink.admin.common.enums.UserCodeEnum;
import com.lotus.shortlink.admin.dao.entity.UserDO;
import com.lotus.shortlink.admin.dao.mapper.UserMapper;
import com.lotus.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.lotus.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.lotus.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.lotus.shortlink.admin.dto.resp.UserRespDTO;
import com.lotus.shortlink.admin.service.GroupService;
import com.lotus.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.lotus.shortlink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final GroupService groupService;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException(UserCodeEnum.USER_NULL);
        }
        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDO, result);
        return result;
    }

    @Override
    public Boolean hasUserName(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO reqParam) {
        if (hasUserName(reqParam.getUsername())) {
            throw new ClientException(UserCodeEnum.USER_NAME_EXIST);
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + reqParam.getUsername());
        try {
            if (lock.tryLock()) {
                int insert = baseMapper.insert(BeanUtil.toBean(reqParam, UserDO.class));
                if (insert < 1) {
                    throw new ClientException(UserCodeEnum.USER_SAVE_FAILED);
                }
                userRegisterCachePenetrationBloomFilter.add(reqParam.getUsername());
                groupService.saveGroup(reqParam.getUsername(), "default");
                return;
            }
            throw new ClientException(UserCodeEnum.USER_NAME_EXIST);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void update(UserUpdateReqDTO reqParam) {
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, reqParam.getUsername());
        baseMapper.update(BeanUtil.toBean(reqParam, UserDO.class), updateWrapper);
    }

    @Override
    public UserLoginRespDTO login(UserRegisterReqDTO reqParam) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, reqParam.getUsername())
                .eq(UserDO::getPassword, reqParam.getPassword())
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException(UserCodeEnum.USER_NULL);
        }

        Boolean login = stringRedisTemplate.hasKey("login_" + reqParam.getUsername());
        if (Boolean.TRUE.equals(login)) {
            throw new ClientException(UserCodeEnum.USER_LOGIN_EXIST);
        }

        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put("login_" + reqParam.getUsername(), uuid, JSON.toJSONString(userDO));
        stringRedisTemplate.expire("login_" + reqParam.getUsername(), 30, TimeUnit.DAYS);
        return new UserLoginRespDTO(uuid);
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        return stringRedisTemplate.opsForHash().get("login_" + username, token) != null;
    }

    @Override
    public void logout(String username, String token) {
        if (!checkLogin(username, token)) {
            throw new ClientException(UserCodeEnum.USER_NULL);
        }
        stringRedisTemplate.opsForHash().delete("login_" + username, token);
    }

}
