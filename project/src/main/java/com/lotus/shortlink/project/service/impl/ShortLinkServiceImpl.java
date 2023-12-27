package com.lotus.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.StrBuilder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lotus.shortlink.project.common.convention.exception.ClientException;
import com.lotus.shortlink.project.common.convention.exception.ServerException;
import com.lotus.shortlink.project.common.enums.ValidDateTypeEnum;
import com.lotus.shortlink.project.dao.entity.ShortLinkDO;
import com.lotus.shortlink.project.dao.entity.ShortLinkGotoDO;
import com.lotus.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import com.lotus.shortlink.project.dao.mapper.ShortLinkMapper;
import com.lotus.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.lotus.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.lotus.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.lotus.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.lotus.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.lotus.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.lotus.shortlink.project.service.ShortLinkService;
import com.lotus.shortlink.project.toolkit.HashUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    private final ShortLinkMapper shortLinkMapper;
    private final ShortLinkGotoMapper shortLinkGotoMapper;

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
        ShortLinkGotoDO shortLinkGotoDO = ShortLinkGotoDO.builder()
                .gid(reqParam.getGid())
                .fullShortUrl(fullShortUrl)
                .build();

        try {
            baseMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(shortLinkGotoDO);
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
                .fullShortUrl("http://" + shortLinkDO.getFullShortUrl())
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
        return resultPage.convert(each -> {
            ShortLinkPageRespDTO shortLinkPageRespDTO = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            shortLinkPageRespDTO.setFullShortUrl("http://" + each.getFullShortUrl());
            return shortLinkPageRespDTO;
        });
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> pageShortLinkGroupCount(List<String> reqParam) {
        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.query(new ShortLinkDO())
                .select("gid as gid", "count(*) as shortLinkCount")
                .in("gid", reqParam)
                .eq("enable_status", 0)
                .groupBy("gid");
        List<Map<String, Object>> shortLinkDOList = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(shortLinkDOList, ShortLinkGroupCountQueryRespDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO reqParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, reqParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, reqParam.getFullShortLink())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);
        if (hasShortLinkDO == null) {
            throw new ClientException("短连接不存在！");
        }

        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(hasShortLinkDO.getDomain())
                .shortUri(hasShortLinkDO.getShortUri())
                .clickNum(hasShortLinkDO.getClickNum())
                .favicon(hasShortLinkDO.getFavicon())
                .createdType(hasShortLinkDO.getCreatedType())
                .gid(reqParam.getGid())
                .originUrl(reqParam.getOriginUrl())
                .describe(reqParam.getDescribe())
                .validDateType(reqParam.getValidDateType())
                .validDate(reqParam.getValidDate())
                .build();

        if (Objects.equals(hasShortLinkDO.getGid(), reqParam.getGid())) {
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, reqParam.getFullShortLink())
                    .eq(ShortLinkDO::getGid, reqParam.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .set(Objects.equals(reqParam.getValidDateType(), ValidDateTypeEnum.PERMANENT.getType()), ShortLinkDO::getValidDate, null);
            baseMapper.update(shortLinkDO, updateWrapper);
        } else {
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, reqParam.getFullShortLink())
                    .eq(ShortLinkDO::getGid, reqParam.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            baseMapper.delete(updateWrapper);
            shortLinkDO.setGid(reqParam.getGid());
            baseMapper.insert(shortLinkDO);
        }
    }

    @Override
    public void restoreUri(String shortUri, ServletRequest request, ServletResponse response) throws IOException {
        String serverName = request.getServerName();
        String fullShortUrl = StrBuilder.create()
                .append(serverName)
                .append("/")
                .append(shortUri)
                .toString();

        LambdaQueryWrapper<ShortLinkGotoDO> gotoLinkQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
        ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(gotoLinkQueryWrapper);
        if (shortLinkGotoDO == null) {
            return;
        }
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, shortLinkGotoDO.getGid())
                .eq(ShortLinkDO::getFullShortUrl, shortLinkGotoDO.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);
        if (shortLinkDO != null) {
            ((HttpServletResponse) response).sendRedirect(shortLinkDO.getOriginUrl());
        }
    }
}
