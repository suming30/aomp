package com.lsm.aomp.service;

import com.lsm.aomp.common.Result;
import com.lsm.aomp.vo.DashboardVO;

public interface DashboardService {

    Result<DashboardVO> getDashboard();
}
