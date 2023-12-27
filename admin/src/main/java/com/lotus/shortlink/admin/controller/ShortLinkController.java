package com.lotus.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lotus.shortlink.admin.common.convention.result.Result;
import com.lotus.shortlink.admin.common.convention.result.Results;
import com.lotus.shortlink.admin.remote.ShortLinkRemoteService;
import com.lotus.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.lotus.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.lotus.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.lotus.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.lotus.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.springframework.web.bind.annotation.*;

@RestController
public class ShortLinkController {

    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO reqParam) {
        return ShortLinkRemoteService.createShortLink(reqParam);
    }

    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO reqParam) {
        return ShortLinkRemoteService.pageShortLink(reqParam);
    }

    @PostMapping("/api/short-link/admin/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO reqParam) {
        ShortLinkRemoteService.updateShortLink(reqParam);
        return Results.success();
    }
}
