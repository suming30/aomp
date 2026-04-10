package com.lsm.aomp.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsm.aomp.common.BusinessException;
import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.common.ResultCode;
import com.lsm.aomp.dto.ScriptCreateDTO;
import com.lsm.aomp.entity.DangerousCommand;
import com.lsm.aomp.entity.ScriptInfo;
import com.lsm.aomp.entity.ScriptVersion;
import com.lsm.aomp.mapper.DangerousCommandMapper;
import com.lsm.aomp.mapper.ScriptInfoMapper;
import com.lsm.aomp.mapper.ScriptVersionMapper;
import com.lsm.aomp.service.ScriptInfoService;
import com.lsm.aomp.util.UserContext;
import com.lsm.aomp.vo.ScriptInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScriptInfoServiceImpl implements ScriptInfoService {

    private final ScriptInfoMapper scriptInfoMapper;
    private final ScriptVersionMapper scriptVersionMapper;
    private final DangerousCommandMapper dangerousCommandMapper;

    @Override
    public Result<PageResult<ScriptInfoVO>> page(Integer pageNum, Integer pageSize, String keyword, String scriptType, String scope, String createBy) {
        Page<ScriptInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ScriptInfo> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(ScriptInfo::getScriptName, keyword)
                    .or().like(ScriptInfo::getDescription, keyword));
        }
        if (StrUtil.isNotBlank(scriptType)) wrapper.eq(ScriptInfo::getScriptType, scriptType);
        if (StrUtil.isNotBlank(scope)) wrapper.eq(ScriptInfo::getScope, scope);
        if (StrUtil.isNotBlank(createBy)) wrapper.eq(ScriptInfo::getCreateBy, createBy);
        wrapper.orderByDesc(ScriptInfo::getCreateTime);

        Page<ScriptInfo> result = scriptInfoMapper.selectPage(page, wrapper);
        List<ScriptInfoVO> voList = result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(PageResult.of(voList, result.getTotal(), pageNum, pageSize));
    }

    @Override
    public Result<ScriptInfoVO> getById(Long id) {
        ScriptInfo script = scriptInfoMapper.selectById(id);
        if (script == null) return Result.error(ResultCode.NOT_FOUND);
        return Result.success(convertToVO(script));
    }

    @Override
    @Transactional
    public Result<Void> create(ScriptCreateDTO dto) {
        checkDangerousCommands(dto.getContent());

        ScriptInfo script = new ScriptInfo();
        script.setScriptName(dto.getScriptName());
        script.setScriptType(dto.getScriptType());
        script.setScope(dto.getScope());
        script.setContent(dto.getContent());
        script.setDescription(dto.getDescription());
        script.setParamsSchema(dto.getParamsSchema());
        script.setIsDraft(dto.getIsDraft());
        script.setVersion(1);
        script.setRunCount(0);
        script.setSuccessCount(0);
        script.setFailCount(0);
        script.setAuditStatus("public".equals(dto.getScope()) ? "pending" : "approved");
        script.setCreateBy(UserContext.getCurrentUsername());
        scriptInfoMapper.insert(script);

        ScriptVersion version = new ScriptVersion();
        version.setScriptId(script.getId());
        version.setVersionNo(1);
        version.setContent(dto.getContent());
        version.setChangeLog("Initial version");
        version.setCreateBy(UserContext.getCurrentUsername());
        scriptVersionMapper.insert(version);

        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> update(Long id, ScriptCreateDTO dto) {
        ScriptInfo script = scriptInfoMapper.selectById(id);
        if (script == null) return Result.error(ResultCode.NOT_FOUND);

        checkDangerousCommands(dto.getContent());

        script.setScriptName(dto.getScriptName());
        script.setScriptType(dto.getScriptType());
        script.setScope(dto.getScope());
        script.setContent(dto.getContent());
        script.setDescription(dto.getDescription());
        script.setParamsSchema(dto.getParamsSchema());
        script.setIsDraft(dto.getIsDraft());
        script.setVersion(script.getVersion() + 1);
        if ("public".equals(dto.getScope()) && !"approved".equals(script.getAuditStatus())) {
            script.setAuditStatus("pending");
        }
        scriptInfoMapper.updateById(script);

        ScriptVersion version = new ScriptVersion();
        version.setScriptId(id);
        version.setVersionNo(script.getVersion());
        version.setContent(dto.getContent());
        version.setChangeLog("Updated to version " + script.getVersion());
        version.setCreateBy(UserContext.getCurrentUsername());
        scriptVersionMapper.insert(version);

        return Result.success();
    }

    @Override
    public Result<Void> delete(Long id) {
        scriptInfoMapper.deleteById(id);
        return Result.success();
    }

    @Override
    public Result<Void> publish(Long id) {
        ScriptInfo script = scriptInfoMapper.selectById(id);
        if (script == null) return Result.error(ResultCode.NOT_FOUND);
        script.setIsDraft(false);
        script.setAuditStatus("pending");
        scriptInfoMapper.updateById(script);
        return Result.success();
    }

    @Override
    public Result<Void> audit(Long id, String status, String remark) {
        ScriptInfo script = scriptInfoMapper.selectById(id);
        if (script == null) return Result.error(ResultCode.NOT_FOUND);
        script.setAuditStatus(status);
        script.setAuditRemark(remark);
        script.setAuditBy(Long.valueOf(UserContext.getCurrentUserId()));
        script.setAuditTime(java.time.LocalDateTime.now());
        scriptInfoMapper.updateById(script);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> copy(Long sourceId, String newName) {
        ScriptInfo source = scriptInfoMapper.selectById(sourceId);
        if (source == null) return Result.error(ResultCode.NOT_FOUND);

        ScriptInfo copy = new ScriptInfo();
        copy.setScriptName(newName);
        copy.setScriptType(source.getScriptType());
        copy.setScope("private");
        copy.setContent(source.getContent());
        copy.setDescription(source.getDescription());
        copy.setParamsSchema(source.getParamsSchema());
        copy.setIsDraft(true);
        copy.setVersion(1);
        copy.setRunCount(0);
        copy.setSuccessCount(0);
        copy.setFailCount(0);
        copy.setAuditStatus("approved");
        copy.setCreateBy(UserContext.getCurrentUsername());
        scriptInfoMapper.insert(copy);
        return Result.success();
    }

    @Override
    public Result<ScriptInfoVO> getDraft(Long id) {
        ScriptInfo script = scriptInfoMapper.selectOne(
                new LambdaQueryWrapper<ScriptInfo>().eq(ScriptInfo::getId, id).eq(ScriptInfo::getIsDraft, true));
        if (script == null) return Result.error(ResultCode.SCRIPT_DRAFT_NOT_FOUND);
        return Result.success(convertToVO(script));
    }

    @Override
    public Result<Void> autoSaveDraft(Long id, String content) {
        ScriptInfo script = scriptInfoMapper.selectById(id);
        if (script == null) return Result.error(ResultCode.NOT_FOUND);
        script.setContent(content);
        script.setIsDraft(true);
        scriptInfoMapper.updateById(script);
        return Result.success();
    }

    private void checkDangerousCommands(String content) {
        List<DangerousCommand> commands = dangerousCommandMapper.selectList(
                new LambdaQueryWrapper<DangerousCommand>().eq(DangerousCommand::getEnabled, true));
        for (DangerousCommand cmd : commands) {
            if (Pattern.compile(cmd.getCommandPattern()).matcher(content).find()) {
                if ("block".equals(cmd.getLevel())) {
                    throw new BusinessException(ResultCode.DANGEROUS_COMMAND_BLOCKED,
                            "Blocked command: " + cmd.getCommandName());
                }
            }
        }
    }

    private ScriptInfoVO convertToVO(ScriptInfo script) {
        ScriptInfoVO vo = new ScriptInfoVO();
        vo.setId(script.getId());
        vo.setScriptName(script.getScriptName());
        vo.setScriptType(script.getScriptType());
        vo.setScope(script.getScope());
        vo.setContent(script.getContent());
        vo.setDescription(script.getDescription());
        vo.setVersion(script.getVersion());
        vo.setParamsSchema(script.getParamsSchema());
        vo.setIsDraft(script.getIsDraft());
        vo.setAuditStatus(script.getAuditStatus());
        vo.setRunCount(script.getRunCount());
        vo.setSuccessCount(script.getSuccessCount());
        vo.setFailCount(script.getFailCount());
        vo.setCreateBy(script.getCreateBy());
        vo.setCreateTime(script.getCreateTime());
        vo.setUpdateTime(script.getUpdateTime());
        return vo;
    }
}
