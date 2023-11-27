package com.lotus.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lotus.shortlink.admin.dao.entity.GroupDO;
import com.lotus.shortlink.admin.dao.mapper.GroupMapper;
import com.lotus.shortlink.admin.service.GroupService;
import com.lotus.shortlink.admin.toolkit.RandomGenerator;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    @Override
    public void saveGroup(String groupName) {
        String gid;
        do {
            gid = RandomGenerator.generateRandomString();
        } while (isGroupExist(gid));

        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .name(groupName)
                .build();
        baseMapper.insert(groupDO);
    }

    private boolean isGroupExist(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, null);
        GroupDO gidExist = baseMapper.selectOne(queryWrapper);
        return gidExist != null;
    }
}
