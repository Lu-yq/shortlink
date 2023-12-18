package com.lotus.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lotus.shortlink.project.dao.entity.ShortLinkDO;
import com.lotus.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.lotus.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.lotus.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.lotus.shortlink.project.dto.resp.ShortLinkPageRespDTO;

public interface ShortLinkService extends IService<ShortLinkDO> {

        /**
        * 创建短链接
        *
        * @param reqParam
        * @return
        */
        ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO reqParam);
        IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO reqParam);

}
