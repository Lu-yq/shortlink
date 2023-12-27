package com.lotus.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lotus.shortlink.project.common.convention.result.Result;
import com.lotus.shortlink.project.common.convention.result.Results;
import com.lotus.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.lotus.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.lotus.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.lotus.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.lotus.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.lotus.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.lotus.shortlink.project.service.impl.ShortLinkServiceImpl;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkServiceImpl shortLinkServiceImpl;

    @PostMapping("/api/short-link/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO reqParam) {
        return Results.success(shortLinkServiceImpl.createShortLink(reqParam));
    }

    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO reqParam) {
        return Results.success(shortLinkServiceImpl.pageShortLink(reqParam));
    }

    @GetMapping("/api/short-link/v1/count")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> listShortLinkGroupCount(@RequestParam("reqParam") List<String> reqParam) {
        return Results.success(shortLinkServiceImpl.pageShortLinkGroupCount(reqParam));
    }

    @PostMapping("/api/short-link/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO reqParam) {
        shortLinkServiceImpl.updateShortLink(reqParam);
        return Results.success();
    }

    @GetMapping("/{short-uri}")
    public void restoreUrl(@PathVariable("short-uri") String shortUri, ServletRequest request, ServletResponse response) throws IOException {
        shortLinkServiceImpl.restoreUri(shortUri, request, response);
    }

}
