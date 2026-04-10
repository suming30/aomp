package com.lsm.aomp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsm.aomp.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {
}
