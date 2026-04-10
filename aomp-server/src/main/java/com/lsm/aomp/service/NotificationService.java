package com.lsm.aomp.service;

import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.entity.Notification;

public interface NotificationService {

    Result<PageResult<Notification>> page(Long userId, Integer pageNum, Integer pageSize, Integer isRead);

    Result<Void> send(Long userId, String title, String content, String type, String sourceType, String sourceId);

    Result<Void> markRead(Long id);

    Result<Void> markAllRead(Long userId);

    Result<Long> unreadCount(Long userId);
}
