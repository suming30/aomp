package com.lsm.aomp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.entity.Notification;
import com.lsm.aomp.mapper.NotificationMapper;
import com.lsm.aomp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public Result<PageResult<Notification>> page(Long userId, Integer pageNum, Integer pageSize, Integer isRead) {
        Page<Notification> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        if (isRead != null) wrapper.eq(Notification::getIsRead, isRead == 1);
        wrapper.orderByDesc(Notification::getCreateTime);
        Page<Notification> result = notificationMapper.selectPage(page, wrapper);
        return Result.success(PageResult.of(result.getRecords(), result.getTotal(), pageNum, pageSize));
    }

    @Override
    public Result<Void> send(Long userId, String title, String content, String type, String sourceType, String sourceId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setNotificationType(type);
        notification.setIsRead(false);
        notification.setSourceType(sourceType);
        notification.setSourceId(sourceId);
        notificationMapper.insert(notification);
        return Result.success();
    }

    @Override
    public Result<Void> markRead(Long id) {
        Notification notification = notificationMapper.selectById(id);
        if (notification != null) {
            notification.setIsRead(true);
            notificationMapper.updateById(notification);
        }
        return Result.success();
    }

    @Override
    public Result<Void> markAllRead(Long userId) {
        List<Notification> unread = notificationMapper.selectList(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .eq(Notification::getIsRead, false));
        for (Notification n : unread) {
            n.setIsRead(true);
            notificationMapper.updateById(n);
        }
        return Result.success();
    }

    @Override
    public Result<Long> unreadCount(Long userId) {
        Long count = notificationMapper.selectCount(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .eq(Notification::getIsRead, false));
        return Result.success(count);
    }
}
