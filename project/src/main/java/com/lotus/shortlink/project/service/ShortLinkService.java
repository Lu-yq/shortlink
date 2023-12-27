package com.lotus.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lotus.shortlink.project.dao.entity.ShortLinkDO;
import com.lotus.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.lotus.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.lotus.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.lotus.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.lotus.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.lotus.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;
import java.util.List;

public interface ShortLinkService extends IService<ShortLinkDO> {

        /**
        * 创建短链接
        *
        * @param reqParam
        * @return
        */
        ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO reqParam);
        IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO reqParam);

        List<ShortLinkGroupCountQueryRespDTO> pageShortLinkGroupCount(List<String> reqParam);

        void updateShortLink(ShortLinkUpdateReqDTO reqParam);

        void restoreUri(String shortUri, ServletRequest request, ServletResponse response) throws IOException;
}
