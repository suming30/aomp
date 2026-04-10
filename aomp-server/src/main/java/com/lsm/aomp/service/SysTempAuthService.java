package com.lsm.aomp.service;

import com.lsm.aomp.common.Result;
import com.lsm.aomp.dto.TempAuthCreateDTO;
import com.lsm.aomp.entity.SysTempAuth;

import java.util.List;

public interface SysTempAuthService {

    Result<List<SysTempAuth>> listByUser(Long userId);

    Result<Void> create(TempAuthCreateDTO dto);

    Result<Void> revoke(Long id);

    void checkAndExpire();
}
