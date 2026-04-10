package com.lsm.aomp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.dto.TempAuthCreateDTO;
import com.lsm.aomp.entity.SysTempAuth;
import com.lsm.aomp.mapper.SysTempAuthMapper;
import com.lsm.aomp.service.SysTempAuthService;
import com.lsm.aomp.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysTempAuthServiceImpl implements SysTempAuthService {

    private final SysTempAuthMapper sysTempAuthMapper;

    @Override
    public Result<List<SysTempAuth>> listByUser(Long userId) {
        List<SysTempAuth> list = sysTempAuthMapper.selectList(
                new LambdaQueryWrapper<SysTempAuth>()
                        .eq(SysTempAuth::getUserId, userId)
                        .orderByDesc(SysTempAuth::getCreateTime));
        return Result.success(list);
    }

    @Override
    public Result<Void> create(TempAuthCreateDTO dto) {
        SysTempAuth auth = new SysTempAuth();
        auth.setUserId(dto.getUserId());
        auth.setRoleIds(String.join(",", dto.getRoleIds().stream().map(String::valueOf).toList()));
        if (dto.getResourceIds() != null) {
            auth.setResourceIds(String.join(",", dto.getResourceIds().stream().map(String::valueOf).toList()));
        }
        auth.setReason(dto.getReason());
        auth.setAuthBy(Long.valueOf(UserContext.getCurrentUserId()));
        auth.setStartTime(dto.getStartTime());
        auth.setEndTime(dto.getEndTime());
        auth.setStatus("active");
        sysTempAuthMapper.insert(auth);
        return Result.success();
    }

    @Override
    public Result<Void> revoke(Long id) {
        SysTempAuth auth = sysTempAuthMapper.selectById(id);
        if (auth == null) return Result.error(com.lsm.aomp.common.ResultCode.NOT_FOUND);
        auth.setStatus("revoked");
        sysTempAuthMapper.updateById(auth);
        return Result.success();
    }

    @Override
    @Scheduled(fixedRate = 60000)
    public void checkAndExpire() {
        List<SysTempAuth> expired = sysTempAuthMapper.selectList(
                new LambdaQueryWrapper<SysTempAuth>()
                        .eq(SysTempAuth::getStatus, "active")
                        .le(SysTempAuth::getEndTime, LocalDateTime.now()));
        for (SysTempAuth auth : expired) {
            auth.setStatus("expired");
            sysTempAuthMapper.updateById(auth);
        }

        List<SysTempAuth> pending = sysTempAuthMapper.selectList(
                new LambdaQueryWrapper<SysTempAuth>()
                        .eq(SysTempAuth::getStatus, "pending")
                        .le(SysTempAuth::getStartTime, LocalDateTime.now()));
        for (SysTempAuth auth : pending) {
            auth.setStatus("active");
            sysTempAuthMapper.updateById(auth);
        }
    }
}
