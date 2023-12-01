package com.lotus.shortlink.admin.controller;

import com.lotus.shortlink.admin.common.convention.result.Result;
import com.lotus.shortlink.admin.common.convention.result.Results;
import com.lotus.shortlink.admin.dto.req.GroupSaveReqDTO;
import com.lotus.shortlink.admin.dto.req.GroupSortReqDTO;
import com.lotus.shortlink.admin.dto.req.GroupUpdateReqDTO;
import com.lotus.shortlink.admin.dto.resp.GroupRespDTO;
import com.lotus.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/api/short-link/admin/v1/group")
    public Result<Void> saveGroup(@RequestBody GroupSaveReqDTO reqParam) {
        groupService.saveGroup(reqParam.getName());
        return Results.success();
    }

    @GetMapping("/api/short-link/admin/v1/group")
    public Result<List<GroupRespDTO>> listGroup() {
        return Results.success(groupService.listGroup());
    }

    @PutMapping("/api/short-link/admin/v1/group")
    public Result<Void> updateGroup(@RequestBody GroupUpdateReqDTO reqParam) {
        groupService.updateGroup(reqParam);
        return Results.success();
    }

    @DeleteMapping("/api/short-link/admin/v1/group")
    public Result<Void> deleteGroup(@RequestParam String gid) {
        groupService.deleteGroup(gid);
        return Results.success();
    }

    @PostMapping("/api/short-link/admin/v1/group/sort")
    public Result<Void> sortGroup(@RequestBody List<GroupSortReqDTO> reqParam) {
        groupService.sortGroup(reqParam);
        return Results.success();
    }
}
