package com.lotus.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lotus.shortlink.project.common.convention.result.Result;
import com.lotus.shortlink.project.common.convention.result.Results;
import com.lotus.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.lotus.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.lotus.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.lotus.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.lotus.shortlink.project.service.impl.ShortLinkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkServiceImpl shortLinkServiceImpl;

    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO reqParam) {
        return Results.success(shortLinkServiceImpl.createShortLink(reqParam));
    }

    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO reqParam) {
        return Results.success(shortLinkServiceImpl.pageShortLink(reqParam));
    }


}
