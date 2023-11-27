package com.lotus.shortlink.admin.controller;

import com.lotus.shortlink.admin.common.convention.result.Result;
import com.lotus.shortlink.admin.common.convention.result.Results;
import com.lotus.shortlink.admin.dto.req.GroupSaveReqDTO;
import com.lotus.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/api/short-link/admin/v1/group")
    public Result<Void> saveGroup(@RequestBody GroupSaveReqDTO reqParam) {
        groupService.saveGroup(reqParam.getName());
        return Results.success();
    }
}
