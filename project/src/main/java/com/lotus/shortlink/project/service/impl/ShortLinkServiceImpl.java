package com.lotus.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.StrBuilder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lotus.shortlink.project.common.convention.exception.ServerException;
import com.lotus.shortlink.project.dao.entity.ShortLinkDO;
import com.lotus.shortlink.project.dao.mapper.ShortLinkMapper;
import com.lotus.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.lotus.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.lotus.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.lotus.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.lotus.shortlink.project.service.ShortLinkService;
import com.lotus.shortlink.project.toolkit.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO reqParam) {
        String suffix = generateSuffix(reqParam);
        String fullShortUrl = StrBuilder.create()
                .append(reqParam.getDomain())
                .append("/")
                .append(suffix)
                .toString();
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(reqParam.getDomain())
                .originUrl(reqParam.getOriginUrl())
                .gid(reqParam.getGid())
                .createdType(reqParam.getCreatedType())
                .validDate(reqParam.getValidDate())
                .validDateType(reqParam.getValidDateType())
                .describe(reqParam.getDescribe())
                .shortUri(suffix)
                .enableStatus(0)
                .fullShortUrl(fullShortUrl)
                .build();

        try {
            baseMapper.insert(shortLinkDO);
        } catch (DuplicateKeyException e) {
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl);
            ShortLinkDO existShortLinkDO = baseMapper.selectOne(queryWrapper);
            if (existShortLinkDO != null) {
                log.warn("短连接{}重复入库，重试一次", fullShortUrl);
                throw new ServerException("短连接生成重复！");
            }
        }
        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLinkDO.getFullShortUrl())
                .originUrl(reqParam.getOriginUrl())
                .gid(reqParam.getGid())
                .build();
    }

    private String generateSuffix(ShortLinkCreateReqDTO reqParam) {
        int customGenerateCount = 0;
        String shortUri;
        while (true) {
            if (customGenerateCount > 10) {
                throw new RuntimeException("频繁创建短连接，请稍后重试！");
            }
            shortUri = HashUtil.hashToBase62(reqParam.getOriginUrl() + System.currentTimeMillis());
            if (!shortUriCreateCachePenetrationBloomFilter.contains(reqParam.getOriginUrl() + "/" + shortUri)) {
                break;
            }
            customGenerateCount++;
        }
        return shortUri;
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO reqParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, reqParam.getGid())
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0)
                .orderByDesc(ShortLinkDO::getCreateTime);
        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(reqParam, queryWrapper);
        return resultPage.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
    }
}
