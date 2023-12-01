package com.lotus.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lotus.shortlink.admin.common.biz.user.UserContext;
import com.lotus.shortlink.admin.dao.entity.GroupDO;
import com.lotus.shortlink.admin.dao.mapper.GroupMapper;
import com.lotus.shortlink.admin.dto.req.GroupSortReqDTO;
import com.lotus.shortlink.admin.dto.req.GroupUpdateReqDTO;
import com.lotus.shortlink.admin.dto.resp.GroupRespDTO;
import com.lotus.shortlink.admin.service.GroupService;
import com.lotus.shortlink.admin.toolkit.RandomGenerator;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    @Override
    public void saveGroup(String groupName) {
        String gid;
        do {
            gid = RandomGenerator.generateRandomString();
        } while (isGidExist(gid));

        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .name(groupName)
                .username(UserContext.getUsername())
                .sortOrder(0)
                .build();
        baseMapper.insert(groupDO);
    }

    private boolean isGidExist(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, UserContext.getUsername());
        GroupDO gidExist = baseMapper.selectOne(queryWrapper);
        return gidExist != null;
    }

    @Override
    public List<GroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
        return BeanUtil.copyToList(groupDOList, GroupRespDTO.class);
    }

    @Override
    public void updateGroup(GroupUpdateReqDTO reqParam) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, reqParam.getGid())
                .eq(GroupDO::getUsername, UserContext.getUsername());
        GroupDO groupDO = baseMapper.selectOne(queryWrapper);
        if (groupDO == null) {
            return;
        }
        groupDO.setName(reqParam.getName());
        baseMapper.updateById(groupDO);
    }

    @Override
    public void deleteGroup(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = baseMapper.selectOne(queryWrapper);
        if (groupDO == null) {
            return;
        }
        groupDO.setDelFlag(1);
        baseMapper.updateById(groupDO);
    }

    @Override
    public void sortGroup(List<GroupSortReqDTO> reqParam) {
        for (GroupSortReqDTO groupSortReqDTO : reqParam) {
            LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                    .eq(GroupDO::getGid, groupSortReqDTO.getGid())
                    .eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getDelFlag, 0);
            GroupDO groupDO = baseMapper.selectOne(queryWrapper);
            if (groupDO == null) {
                continue;
            }
            groupDO.setSortOrder(groupSortReqDTO.getSortOrder());
            baseMapper.updateById(groupDO);
        }
    }

}
