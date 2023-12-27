package com.lotus.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lotus.shortlink.admin.dao.entity.GroupDO;
import com.lotus.shortlink.admin.dto.req.GroupSortReqDTO;
import com.lotus.shortlink.admin.dto.req.GroupUpdateReqDTO;
import com.lotus.shortlink.admin.dto.resp.GroupRespDTO;

import java.util.List;

public interface GroupService extends IService<GroupDO> {
    void saveGroup(String groupName);

    void saveGroup(String username, String groupName);

    List<GroupRespDTO> listGroup();

    void updateGroup(GroupUpdateReqDTO reqParam);

    void deleteGroup(String gid);

    void sortGroup(List<GroupSortReqDTO> reqParam);
}
