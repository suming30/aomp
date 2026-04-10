package com.lsm.aomp.controller;

import com.lsm.aomp.common.Result;
import com.lsm.aomp.service.DashboardService;
import com.lsm.aomp.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public Result<DashboardVO> getDashboard() {
        return dashboardService.getDashboard();
    }
}
