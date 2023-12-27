package com.lotus.shortlink.project.dto.resp;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

@Data
public class ShortLinkGroupCountQueryRespDTO extends Page {
    /**
     * 分组标识
     */
    private String gid;

    private Integer shortLinkCount;
}
