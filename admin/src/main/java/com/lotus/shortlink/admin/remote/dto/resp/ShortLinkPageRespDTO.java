package com.lotus.shortlink.admin.remote.dto.resp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ShortLinkPageRespDTO extends Page {
    /**
     * id
     */
    private Long id;

    /**
     * 域名
     */
    private String domain;

    /**
     * 短链接
     */
    private String shortUri;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 点击量
     */
    private Integer clickNum;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 启用标识 0：启用 1：未启用
     */
    private Integer enableStatus;

    /**
     * 创建类型 0：接口创建 1：控制台创建
     */
    private Integer createdType;

    /**
     * 有效期类型 0：永久有效 1：自定义
     */
    private Integer validDateType;

    /**
     * 有效期
     */
    private Date validDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 描述
     */
    @TableField("`describe`")
    private String describe;

    /**
     * 网站标识
     */
    private String favicon;

//    /**
//     * 历史PV
//     */
//    private Integer totalPv;
//
//    /**
//     * 历史UV
//     */
//    private Integer totalUv;
//
//    /**
//     * 历史UIP
//     */
//    private Integer totalUip;
//
//    /**
//     * 今日PV
//     */
//    @TableField(exist = false)
//    private Integer todayPv;
//
//    /**
//     * 今日UV
//     */
//    @TableField(exist = false)
//    private Integer todayUv;
//
//    /**
//     * 今日UIP
//     */
//    @TableField(exist = false)
//    private Integer todayUip;
}
