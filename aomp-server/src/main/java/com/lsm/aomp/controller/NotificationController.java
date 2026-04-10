package com.lsm.aomp.controller;

import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.entity.Notification;
import com.lsm.aomp.service.NotificationService;
import com.lsm.aomp.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public Result<PageResult<Notification>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) Integer isRead) {
        String userId = UserContext.getCurrentUserId();
        return notificationService.page(Long.valueOf(userId), pageNum, pageSize, isRead);
    }

    @PutMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id) {
        return notificationService.markRead(id);
    }

    @PutMapping("/read-all")
    public Result<Void> markAllRead() {
        String userId = UserContext.getCurrentUserId();
        return notificationService.markAllRead(Long.valueOf(userId));
    }

    @GetMapping("/unread-count")
    public Result<Long> unreadCount() {
        String userId = UserContext.getCurrentUserId();
        return notificationService.unreadCount(Long.valueOf(userId));
    }
}
