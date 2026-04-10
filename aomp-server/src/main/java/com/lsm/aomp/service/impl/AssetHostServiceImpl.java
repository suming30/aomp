package com.lsm.aomp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsm.aomp.common.BusinessException;
import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.common.ResultCode;
import com.lsm.aomp.dto.HostCreateDTO;
import com.lsm.aomp.dto.HostUpdateDTO;
import com.lsm.aomp.entity.*;
import com.lsm.aomp.mapper.*;
import com.lsm.aomp.service.AssetHostService;
import com.lsm.aomp.util.SshUtil;
import com.lsm.aomp.util.UserContext;
import com.lsm.aomp.vo.HostInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetHostServiceImpl implements AssetHostService {

    private final AssetHostMapper assetHostMapper;
    private final AssetHostGroupRelMapper assetHostGroupRelMapper;
    private final AssetHostTagRelMapper assetHostTagRelMapper;
    private final AssetHostGroupMapper assetHostGroupMapper;
    private final AssetTagMapper assetTagMapper;
    private final SshUtil sshUtil;

    @Value("${aomp.ssh.connect-timeout:10000}")
    private int connectTimeout;

    @Override
    public Result<PageResult<HostInfoVO>> page(Integer pageNum, Integer pageSize, String keyword, String status, String osType, Long managerId) {
        Page<AssetHost> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AssetHost> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(AssetHost::getIpAddress, keyword)
                    .or().like(AssetHost::getAlias, keyword)
                    .or().like(AssetHost::getHostname, keyword));
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(AssetHost::getStatus, status);
        }
        if (StrUtil.isNotBlank(osType)) {
            wrapper.like(AssetHost::getOsType, osType);
        }
        if (managerId != null) {
            wrapper.eq(AssetHost::getManagerId, managerId);
        }
        wrapper.orderByDesc(AssetHost::getCreateTime);

        Page<AssetHost> result = assetHostMapper.selectPage(page, wrapper);
        List<HostInfoVO> voList = result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(PageResult.of(voList, result.getTotal(), pageNum, pageSize));
    }

    @Override
    public Result<HostInfoVO> getById(Long id) {
        AssetHost host = assetHostMapper.selectById(id);
        if (host == null) return Result.error(ResultCode.NOT_FOUND);
        return Result.success(convertToVO(host));
    }

    @Override
    @Transactional
    public Result<Void> create(HostCreateDTO dto) {
        Long count = assetHostMapper.selectCount(
                new LambdaQueryWrapper<AssetHost>().eq(AssetHost::getIpAddress, dto.getIpAddress()));
        if (count > 0) {
            throw new BusinessException(ResultCode.HOST_IP_EXISTS);
        }

        AssetHost host = new AssetHost();
        host.setIpAddress(dto.getIpAddress());
        host.setSshPort(dto.getSshPort());
        host.setHostname(dto.getHostname());
        host.setAlias(dto.getAlias());
        host.setOsType(dto.getOsType());
        host.setCpuCores(dto.getCpuCores());
        host.setMemoryGb(dto.getMemoryGb());
        host.setDiskGb(dto.getDiskGb());
        host.setSshUser(dto.getSshUser());
        host.setSshAuthType(dto.getSshAuthType());
        if (dto.getSshPassword() != null) {
            host.setSshPassword(encryptSensitive(dto.getSshPassword()));
        }
        if (dto.getSshKey() != null) {
            host.setSshKey(encryptSensitive(dto.getSshKey()));
        }
        if (dto.getSshKeyPassphrase() != null) {
            host.setSshKeyPassphrase(encryptSensitive(dto.getSshKeyPassphrase()));
        }
        host.setManagerId(dto.getManagerId());
        if (dto.getOnlineTime() != null) {
            host.setOnlineTime(LocalDateTime.parse(dto.getOnlineTime(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        host.setRemark(dto.getRemark());
        host.setCreateBy(UserContext.getCurrentUsername());
        host.setStatus("offline");

        boolean connected = testConnectivity(host);
        host.setStatus(connected ? "online" : "offline");
        host.setLastCheckTime(LocalDateTime.now());

        assetHostMapper.insert(host);

        saveGroupAndTagRelations(host.getId(), dto.getGroupIds(), dto.getTagIds());
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> update(Long id, HostUpdateDTO dto) {
        AssetHost host = assetHostMapper.selectById(id);
        if (host == null) return Result.error(ResultCode.NOT_FOUND);

        if (dto.getSshPort() != null) host.setSshPort(dto.getSshPort());
        if (dto.getHostname() != null) host.setHostname(dto.getHostname());
        if (dto.getAlias() != null) host.setAlias(dto.getAlias());
        if (dto.getOsType() != null) host.setOsType(dto.getOsType());
        if (dto.getCpuCores() != null) host.setCpuCores(dto.getCpuCores());
        if (dto.getMemoryGb() != null) host.setMemoryGb(dto.getMemoryGb());
        if (dto.getDiskGb() != null) host.setDiskGb(dto.getDiskGb());
        if (dto.getSshUser() != null) host.setSshUser(dto.getSshUser());
        if (dto.getSshAuthType() != null) host.setSshAuthType(dto.getSshAuthType());
        if (dto.getSshPassword() != null) host.setSshPassword(encryptSensitive(dto.getSshPassword()));
        if (dto.getSshKey() != null) host.setSshKey(encryptSensitive(dto.getSshKey()));
        if (dto.getSshKeyPassphrase() != null) host.setSshKeyPassphrase(encryptSensitive(dto.getSshKeyPassphrase()));
        if (dto.getManagerId() != null) host.setManagerId(dto.getManagerId());
        if (dto.getRemark() != null) host.setRemark(dto.getRemark());

        assetHostMapper.updateById(host);

        if (dto.getGroupIds() != null || dto.getTagIds() != null) {
            if (dto.getGroupIds() != null) {
                assetHostGroupRelMapper.delete(
                        new LambdaQueryWrapper<AssetHostGroupRel>().eq(AssetHostGroupRel::getHostId, id));
            }
            if (dto.getTagIds() != null) {
                assetHostTagRelMapper.delete(
                        new LambdaQueryWrapper<AssetHostTagRel>().eq(AssetHostTagRel::getHostId, id));
            }
            saveGroupAndTagRelations(id, dto.getGroupIds(), dto.getTagIds());
        }
        return Result.success();
    }

    @Override
    public Result<Void> delete(Long id) {
        assetHostMapper.deleteById(id);
        return Result.success();
    }

    @Override
    public Result<Void> checkConnectivity(Long id) {
        AssetHost host = assetHostMapper.selectById(id);
        if (host == null) return Result.error(ResultCode.NOT_FOUND);

        boolean connected = testConnectivity(host);
        host.setStatus(connected ? "online" : "offline");
        host.setLastCheckTime(LocalDateTime.now());
        assetHostMapper.updateById(host);

        if (!connected) {
            return Result.error(ResultCode.HOST_CONNECT_FAILED);
        }
        return Result.success();
    }

    @Override
    public Result<Void> batchCheckConnectivity(List<Long> ids) {
        for (Long id : ids) {
            AssetHost host = assetHostMapper.selectById(id);
            if (host != null) {
                boolean connected = testConnectivity(host);
                host.setStatus(connected ? "online" : "offline");
                host.setLastCheckTime(LocalDateTime.now());
                assetHostMapper.updateById(host);
            }
        }
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> batchImport(List<HostCreateDTO> hosts) {
        for (HostCreateDTO dto : hosts) {
            create(dto);
        }
        return Result.success();
    }

    @Override
    public Result<List<HostInfoVO>> export(String status, String keyword) {
        LambdaQueryWrapper<AssetHost> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(status)) wrapper.eq(AssetHost::getStatus, status);
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(AssetHost::getIpAddress, keyword)
                    .or().like(AssetHost::getAlias, keyword));
        }
        List<AssetHost> list = assetHostMapper.selectList(wrapper);
        List<HostInfoVO> voList = list.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @Override
    public Result<List<HostInfoVO>> listByGroupId(Long groupId) {
        List<AssetHostGroupRel> rels = assetHostGroupRelMapper.selectList(
                new LambdaQueryWrapper<AssetHostGroupRel>().eq(AssetHostGroupRel::getGroupId, groupId));
        if (rels.isEmpty()) return Result.success(Collections.emptyList());
        List<Long> hostIds = rels.stream().map(AssetHostGroupRel::getHostId).collect(Collectors.toList());
        List<AssetHost> hosts = assetHostMapper.selectBatchIds(hostIds);
        List<HostInfoVO> voList = hosts.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @Override
    public Result<List<HostInfoVO>> listByTagId(Long tagId) {
        List<AssetHostTagRel> rels = assetHostTagRelMapper.selectList(
                new LambdaQueryWrapper<AssetHostTagRel>().eq(AssetHostTagRel::getTagId, tagId));
        if (rels.isEmpty()) return Result.success(Collections.emptyList());
        List<Long> hostIds = rels.stream().map(AssetHostTagRel::getHostId).collect(Collectors.toList());
        List<AssetHost> hosts = assetHostMapper.selectBatchIds(hostIds);
        List<HostInfoVO> voList = hosts.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    private boolean testConnectivity(AssetHost host) {
        try {
            String password = host.getSshPassword() != null ? decryptSensitive(host.getSshPassword()) : null;
            String key = host.getSshKey() != null ? decryptSensitive(host.getSshKey()) : null;
            String keyPassphrase = host.getSshKeyPassphrase() != null ? decryptSensitive(host.getSshKeyPassphrase()) : null;
            return sshUtil.testConnection(host.getIpAddress(), host.getSshPort(), host.getSshUser(), password, key, keyPassphrase, connectTimeout);
        } catch (Exception e) {
            return false;
        }
    }

    private void saveGroupAndTagRelations(Long hostId, List<Long> groupIds, List<Long> tagIds) {
        if (groupIds != null) {
            for (Long groupId : groupIds) {
                AssetHostGroupRel rel = new AssetHostGroupRel();
                rel.setHostId(hostId);
                rel.setGroupId(groupId);
                assetHostGroupRelMapper.insert(rel);
            }
        }
        if (tagIds != null) {
            for (Long tagId : tagIds) {
                AssetHostTagRel rel = new AssetHostTagRel();
                rel.setHostId(hostId);
                rel.setTagId(tagId);
                assetHostTagRelMapper.insert(rel);
            }
        }
    }

    private HostInfoVO convertToVO(AssetHost host) {
        HostInfoVO vo = new HostInfoVO();
        vo.setId(host.getId());
        vo.setIpAddress(host.getIpAddress());
        vo.setSshPort(host.getSshPort());
        vo.setHostname(host.getHostname());
        vo.setAlias(host.getAlias());
        vo.setOsType(host.getOsType());
        vo.setCpuCores(host.getCpuCores());
        vo.setMemoryGb(host.getMemoryGb());
        vo.setDiskGb(host.getDiskGb());
        vo.setSshUser(host.getSshUser());
        vo.setSshAuthType(host.getSshAuthType());
        vo.setManagerId(host.getManagerId());
        vo.setOnlineTime(host.getOnlineTime());
        vo.setStatus(host.getStatus());
        vo.setLastCheckTime(host.getLastCheckTime());
        vo.setRemark(host.getRemark());
        vo.setCreateTime(host.getCreateTime());
        vo.setUpdateTime(host.getUpdateTime());

        List<AssetHostGroupRel> groupRels = assetHostGroupRelMapper.selectList(
                new LambdaQueryWrapper<AssetHostGroupRel>().eq(AssetHostGroupRel::getHostId, host.getId()));
        if (!groupRels.isEmpty()) {
            vo.setGroupIds(groupRels.stream().map(AssetHostGroupRel::getGroupId).collect(Collectors.toList()));
            List<AssetHostGroup> groups = assetHostGroupMapper.selectBatchIds(vo.getGroupIds());
            vo.setGroupNames(groups.stream().map(AssetHostGroup::getGroupName).collect(Collectors.toList()));
        }

        List<AssetHostTagRel> tagRels = assetHostTagRelMapper.selectList(
                new LambdaQueryWrapper<AssetHostTagRel>().eq(AssetHostTagRel::getHostId, host.getId()));
        if (!tagRels.isEmpty()) {
            vo.setTagIds(tagRels.stream().map(AssetHostTagRel::getTagId).collect(Collectors.toList()));
            List<AssetTag> tags = assetTagMapper.selectBatchIds(vo.getTagIds());
            vo.setTagNames(tags.stream().map(AssetTag::getTagName).collect(Collectors.toList()));
        }
        return vo;
    }

    private String encryptSensitive(String plain) {
        return SecureUtil.aes("aomp2026secretke".getBytes()).encryptHex(plain);
    }

    private String decryptSensitive(String encrypted) {
        return SecureUtil.aes("aomp2026secretke".getBytes()).decryptStr(encrypted);
    }
}
