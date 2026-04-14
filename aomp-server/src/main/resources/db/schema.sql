CREATE DATABASE IF NOT EXISTS `aomp` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `aomp`;

-- 用户表
CREATE TABLE `sys_user` (
    `id` bigint NOT NULL COMMENT '用户ID',
    `user_account` varchar(50) NOT NULL COMMENT '登录账号',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `password` varchar(200) NOT NULL COMMENT '密码(BCrypt加密)',
    `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
    `status` varchar(20) NOT NULL DEFAULT 'enabled' COMMENT '账号状态: enabled/disabled/locked',
    `force_change_pwd` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否强制改密',
    `password_change_time` datetime DEFAULT NULL COMMENT '最后修改密码时间',
    `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP',
    `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
    `login_fail_count` int NOT NULL DEFAULT 0 COMMENT '连续登录失败次数',
    `lock_time` datetime DEFAULT NULL COMMENT '锁定时间',
    `manager_id` bigint DEFAULT NULL COMMENT '负责人ID',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_account` (`user_account`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
CREATE TABLE `sys_role` (
    `id` bigint NOT NULL COMMENT '角色ID',
    `role_name` varchar(50) NOT NULL COMMENT '角色名称',
    `role_code` varchar(50) NOT NULL COMMENT '角色编码',
    `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
    `role_type` varchar(20) NOT NULL DEFAULT 'custom' COMMENT '角色类型: builtin/custom',
    `data_scope` varchar(20) NOT NULL DEFAULT 'self' COMMENT '数据权限: self/team/all',
    `status` varchar(20) NOT NULL DEFAULT 'enabled' COMMENT '状态: enabled/disabled',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`),
    KEY `idx_role_type` (`role_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE `sys_user_role` (
    `id` bigint NOT NULL COMMENT 'ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 权限表(菜单+按钮)
CREATE TABLE `sys_permission` (
    `id` bigint NOT NULL COMMENT '权限ID',
    `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父权限ID',
    `permission_name` varchar(100) NOT NULL COMMENT '权限名称',
    `permission_code` varchar(100) NOT NULL COMMENT '权限编码',
    `permission_type` varchar(20) NOT NULL COMMENT '类型: menu/button',
    `path` varchar(200) DEFAULT NULL COMMENT '菜单路径',
    `icon` varchar(100) DEFAULT NULL COMMENT '图标',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
    `visible` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否可见',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`permission_code`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE `sys_role_permission` (
    `id` bigint NOT NULL COMMENT 'ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `permission_id` bigint NOT NULL COMMENT '权限ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 密码历史表
CREATE TABLE `sys_password_history` (
    `id` bigint NOT NULL COMMENT 'ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `password` varchar(200) NOT NULL COMMENT '历史密码(BCrypt)',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='密码历史表';

-- 临时授权表
CREATE TABLE `sys_temp_auth` (
    `id` bigint NOT NULL COMMENT '授权ID',
    `user_id` bigint NOT NULL COMMENT '被授权用户ID',
    `role_ids` varchar(500) NOT NULL COMMENT '临时授予的角色ID列表(逗号分隔)',
    `resource_ids` varchar(1000) DEFAULT NULL COMMENT '限定资源ID(主机/主机组,逗号分隔)',
    `reason` varchar(500) NOT NULL COMMENT '授权原因',
    `auth_by` bigint NOT NULL COMMENT '授权人ID',
    `start_time` datetime NOT NULL COMMENT '生效时间',
    `end_time` datetime NOT NULL COMMENT '失效时间',
    `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending/active/expired/revoked',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_end_time` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='临时授权表';

-- 主机表
CREATE TABLE `asset_host` (
    `id` bigint NOT NULL COMMENT '主机ID',
    `ip_address` varchar(50) NOT NULL COMMENT 'IP地址',
    `ssh_port` int NOT NULL DEFAULT 22 COMMENT 'SSH端口',
    `hostname` varchar(100) DEFAULT NULL COMMENT '主机名',
    `alias` varchar(100) DEFAULT NULL COMMENT '主机别名',
    `os_type` varchar(50) DEFAULT NULL COMMENT '操作系统',
    `cpu_cores` int DEFAULT NULL COMMENT 'CPU核心数',
    `memory_gb` int DEFAULT NULL COMMENT '内存大小(GB)',
    `disk_gb` int DEFAULT NULL COMMENT '磁盘容量(GB)',
    `ssh_user` varchar(50) DEFAULT NULL COMMENT 'SSH用户名',
    `ssh_auth_type` varchar(20) DEFAULT 'password' COMMENT '认证方式: password/key',
    `ssh_password` varchar(500) DEFAULT NULL COMMENT 'SSH密码(AES加密)',
    `ssh_key` text DEFAULT NULL COMMENT 'SSH私钥(AES加密)',
    `ssh_key_passphrase` varchar(500) DEFAULT NULL COMMENT '密钥密码(AES加密)',
    `manager_id` bigint DEFAULT NULL COMMENT '负责人ID',
    `online_time` datetime DEFAULT NULL COMMENT '上线时间',
    `status` varchar(20) NOT NULL DEFAULT 'offline' COMMENT '主机状态: online/offline/deleted',
    `last_check_time` datetime DEFAULT NULL COMMENT '最后检测时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ip_address` (`ip_address`),
    KEY `idx_status` (`status`),
    KEY `idx_manager_id` (`manager_id`),
    KEY `idx_os_type` (`os_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='主机表';

-- 主机组表
CREATE TABLE `asset_host_group` (
    `id` bigint NOT NULL COMMENT '主机组ID',
    `group_name` varchar(100) NOT NULL COMMENT '组名称',
    `group_type` varchar(20) NOT NULL DEFAULT 'static' COMMENT '组类型: static/dynamic',
    `dynamic_rule` varchar(1000) DEFAULT NULL COMMENT '动态组规则(JSON)',
    `description` varchar(500) DEFAULT NULL COMMENT '描述',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_group_type` (`group_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='主机组表';

-- 主机组关联表
CREATE TABLE `asset_host_group_rel` (
    `id` bigint NOT NULL COMMENT 'ID',
    `host_id` bigint NOT NULL COMMENT '主机ID',
    `group_id` bigint NOT NULL COMMENT '主机组ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_host_group` (`host_id`, `group_id`),
    KEY `idx_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='主机组关联表';

-- 标签表
CREATE TABLE `asset_tag` (
    `id` bigint NOT NULL COMMENT '标签ID',
    `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
    `tag_color` varchar(20) DEFAULT NULL COMMENT '标签颜色',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 主机标签关联表
CREATE TABLE `asset_host_tag_rel` (
    `id` bigint NOT NULL COMMENT 'ID',
    `host_id` bigint NOT NULL COMMENT '主机ID',
    `tag_id` bigint NOT NULL COMMENT '标签ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_host_tag` (`host_id`, `tag_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='主机标签关联表';

-- 脚本表
CREATE TABLE `script_info` (
    `id` bigint NOT NULL COMMENT '脚本ID',
    `script_name` varchar(200) NOT NULL COMMENT '脚本名称',
    `script_type` varchar(20) NOT NULL DEFAULT 'shell' COMMENT '脚本类型: shell/python/powershell',
    `scope` varchar(20) NOT NULL DEFAULT 'private' COMMENT '权限范围: private/team/public',
    `content` longtext NOT NULL COMMENT '脚本内容',
    `description` varchar(1000) DEFAULT NULL COMMENT '脚本描述',
    `version` int NOT NULL DEFAULT 1 COMMENT '当前版本号',
    `params_schema` text DEFAULT NULL COMMENT '参数定义(JSON)',
    `is_draft` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否草稿',
    `audit_status` varchar(20) DEFAULT 'pending' COMMENT '审核状态: pending/approved/rejected',
    `audit_by` bigint DEFAULT NULL COMMENT '审核人ID',
    `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
    `audit_remark` varchar(500) DEFAULT NULL COMMENT '审核备注',
    `run_count` int NOT NULL DEFAULT 0 COMMENT '执行次数',
    `success_count` int NOT NULL DEFAULT 0 COMMENT '成功次数',
    `fail_count` int NOT NULL DEFAULT 0 COMMENT '失败次数',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_script_type` (`script_type`),
    KEY `idx_scope` (`scope`),
    KEY `idx_create_by` (`create_by`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='脚本表';

-- 脚本版本表
CREATE TABLE `script_version` (
    `id` bigint NOT NULL COMMENT 'ID',
    `script_id` bigint NOT NULL COMMENT '脚本ID',
    `version_no` int NOT NULL COMMENT '版本号',
    `content` longtext NOT NULL COMMENT '脚本内容',
    `change_log` varchar(500) DEFAULT NULL COMMENT '变更说明',
    `create_by` varchar(50) DEFAULT NULL COMMENT '提交人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_script_id` (`script_id`),
    KEY `idx_version_no` (`script_id`, `version_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='脚本版本表';

-- 脚本标签关联表
CREATE TABLE `script_tag_rel` (
    `id` bigint NOT NULL COMMENT 'ID',
    `script_id` bigint NOT NULL COMMENT '脚本ID',
    `tag_id` bigint NOT NULL COMMENT '标签ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_script_tag` (`script_id`, `tag_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='脚本标签关联表';

-- 任务表
CREATE TABLE `task_info` (
    `id` bigint NOT NULL COMMENT '任务ID',
    `task_name` varchar(200) NOT NULL COMMENT '任务名称',
    `script_id` bigint NOT NULL COMMENT '脚本ID',
    `script_version` int DEFAULT NULL COMMENT '脚本版本号',
    `execute_mode` varchar(20) NOT NULL DEFAULT 'concurrent' COMMENT '执行方式: concurrent/serial/batch',
    `batch_size` int DEFAULT NULL COMMENT '分批大小',
    `batch_interval` int DEFAULT NULL COMMENT '分批间隔(秒)',
    `task_params` text DEFAULT NULL COMMENT '执行参数(JSON)',
    `timeout` int NOT NULL DEFAULT 3600 COMMENT '超时时间(秒)',
    `is_dry_run` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否Dry Run',
    `is_silent` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否静默执行',
    `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT '任务状态',
    `total_hosts` int NOT NULL DEFAULT 0 COMMENT '目标主机总数',
    `success_hosts` int NOT NULL DEFAULT 0 COMMENT '成功主机数',
    `fail_hosts` int NOT NULL DEFAULT 0 COMMENT '失败主机数',
    `running_hosts` int NOT NULL DEFAULT 0 COMMENT '执行中主机数',
    `start_time` datetime DEFAULT NULL COMMENT '开始时间',
    `end_time` datetime DEFAULT NULL COMMENT '结束时间',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_script_id` (`script_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_by` (`create_by`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- 任务目标主机关联表
CREATE TABLE `task_host_rel` (
    `id` bigint NOT NULL COMMENT 'ID',
    `task_id` bigint NOT NULL COMMENT '任务ID',
    `host_id` bigint NOT NULL COMMENT '主机ID',
    `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT '执行状态: pending/running/success/failed/terminated',
    `exit_code` int DEFAULT NULL COMMENT '退出码',
    `start_time` datetime DEFAULT NULL COMMENT '开始时间',
    `end_time` datetime DEFAULT NULL COMMENT '结束时间',
    `duration` int DEFAULT NULL COMMENT '执行时长(秒)',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_host_id` (`host_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务目标主机关联表';

-- 执行日志表
CREATE TABLE `task_exec_log` (
    `id` bigint NOT NULL COMMENT 'ID',
    `task_id` bigint NOT NULL COMMENT '任务ID',
    `host_id` bigint NOT NULL COMMENT '主机ID',
    `log_type` varchar(20) NOT NULL DEFAULT 'stdout' COMMENT '日志类型: stdout/stderr',
    `content` longtext COMMENT '日志内容',
    `line_no` int DEFAULT NULL COMMENT '行号',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_host` (`task_id`, `host_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='执行日志表';

-- 任务审批表
CREATE TABLE `task_approval` (
    `id` bigint NOT NULL COMMENT 'ID',
    `task_id` bigint NOT NULL COMMENT '任务ID',
    `submit_by` bigint NOT NULL COMMENT '提交人ID',
    `approve_by` bigint DEFAULT NULL COMMENT '审批人ID',
    `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT '审批状态: pending/approved/rejected/expired',
    `reason` varchar(500) DEFAULT NULL COMMENT '审批原因',
    `approve_remark` varchar(500) DEFAULT NULL COMMENT '审批备注',
    `expire_time` datetime NOT NULL COMMENT '审批过期时间',
    `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_submit_by` (`submit_by`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务审批表';

-- 巡检项表
CREATE TABLE `inspection_item` (
    `id` bigint NOT NULL COMMENT '巡检项ID',
    `item_name` varchar(200) NOT NULL COMMENT '巡检项名称',
    `item_code` varchar(100) NOT NULL COMMENT '巡检项编码',
    `item_type` varchar(20) NOT NULL DEFAULT 'custom' COMMENT '类型: builtin/custom',
    `script_id` bigint DEFAULT NULL COMMENT '关联脚本ID',
    `check_command` text COMMENT '检查命令',
    `threshold_config` text DEFAULT NULL COMMENT '阈值配置(JSON)',
    `description` varchar(500) DEFAULT NULL COMMENT '描述',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_item_code` (`item_code`),
    KEY `idx_item_type` (`item_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡检项表';

-- 巡检任务表
CREATE TABLE `inspection_task` (
    `id` bigint NOT NULL COMMENT '巡检任务ID',
    `task_name` varchar(200) NOT NULL COMMENT '任务名称',
    `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending/running/completed/failed',
    `total_hosts` int NOT NULL DEFAULT 0 COMMENT '巡检主机总数',
    `normal_hosts` int NOT NULL DEFAULT 0 COMMENT '正常主机数',
    `warning_hosts` int NOT NULL DEFAULT 0 COMMENT '告警主机数',
    `error_hosts` int NOT NULL DEFAULT 0 COMMENT '异常主机数',
    `start_time` datetime DEFAULT NULL COMMENT '开始时间',
    `end_time` datetime DEFAULT NULL COMMENT '结束时间',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡检任务表';

-- 巡检任务关联主机表
CREATE TABLE `inspection_task_host` (
    `id` bigint NOT NULL COMMENT 'ID',
    `inspection_task_id` bigint NOT NULL COMMENT '巡检任务ID',
    `host_id` bigint NOT NULL COMMENT '主机ID',
    `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending/running/completed/failed',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_inspection_task_id` (`inspection_task_id`),
    KEY `idx_host_id` (`host_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡检任务关联主机表';

-- 巡检结果明细表
CREATE TABLE `inspection_result` (
    `id` bigint NOT NULL COMMENT 'ID',
    `inspection_task_id` bigint NOT NULL COMMENT '巡检任务ID',
    `host_id` bigint NOT NULL COMMENT '主机ID',
    `item_id` bigint NOT NULL COMMENT '巡检项ID',
    `status` varchar(20) NOT NULL DEFAULT 'normal' COMMENT '结果状态: normal/warning/error',
    `actual_value` varchar(500) DEFAULT NULL COMMENT '实际值',
    `threshold_value` varchar(500) DEFAULT NULL COMMENT '阈值',
    `detail` text DEFAULT NULL COMMENT '详细信息',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_inspection_task_id` (`inspection_task_id`),
    KEY `idx_host_id` (`host_id`),
    KEY `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡检结果明细表';

-- 审计日志表
CREATE TABLE `audit_log` (
    `id` bigint NOT NULL COMMENT 'ID',
    `user_id` bigint DEFAULT NULL COMMENT '操作用户ID',
    `username` varchar(50) DEFAULT NULL COMMENT '操作用户名',
    `module` varchar(50) NOT NULL COMMENT '操作模块',
    `action` varchar(100) NOT NULL COMMENT '操作动作',
    `target_type` varchar(50) DEFAULT NULL COMMENT '目标类型',
    `target_id` varchar(100) DEFAULT NULL COMMENT '目标ID',
    `detail` text DEFAULT NULL COMMENT '操作详情',
    `level` varchar(20) NOT NULL DEFAULT 'normal' COMMENT '日志级别: normal/important/critical',
    `ip` varchar(50) DEFAULT NULL COMMENT '操作IP',
    `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
    `result` varchar(20) NOT NULL DEFAULT 'success' COMMENT '操作结果: success/failed',
    `error_msg` varchar(500) DEFAULT NULL COMMENT '错误信息',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_module` (`module`),
    KEY `idx_level` (`level`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';

-- 危险命令黑名单表
CREATE TABLE `dangerous_command` (
    `id` bigint NOT NULL COMMENT 'ID',
    `command_pattern` varchar(200) NOT NULL COMMENT '命令匹配模式(正则)',
    `command_name` varchar(100) NOT NULL COMMENT '命令名称',
    `level` varchar(20) NOT NULL DEFAULT 'block' COMMENT '拦截级别: block/approval/warning',
    `description` varchar(500) DEFAULT NULL COMMENT '描述',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='危险命令黑名单表';

-- 通知表
CREATE TABLE `notification` (
    `id` bigint NOT NULL COMMENT 'ID',
    `user_id` bigint NOT NULL COMMENT '接收用户ID',
    `title` varchar(200) NOT NULL COMMENT '通知标题',
    `content` text COMMENT '通知内容',
    `notification_type` varchar(50) NOT NULL COMMENT '通知类型',
    `is_read` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
    `source_type` varchar(50) DEFAULT NULL COMMENT '来源类型',
    `source_id` varchar(100) DEFAULT NULL COMMENT '来源ID',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_is_read` (`is_read`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- WebHook配置表
CREATE TABLE `webhook_config` (
    `id` bigint NOT NULL COMMENT 'ID',
    `name` varchar(100) NOT NULL COMMENT 'WebHook名称',
    `url` varchar(500) NOT NULL COMMENT '回调URL',
    `channel` varchar(50) NOT NULL DEFAULT 'custom' COMMENT '渠道: dingtalk/wechat/feishu/custom',
    `secret` varchar(200) DEFAULT NULL COMMENT '签名密钥',
    `enabled_events` varchar(500) DEFAULT NULL COMMENT '启用事件(JSON数组)',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='WebHook配置表';

-- ========================================
-- 初始数据
-- ========================================

-- 内置角色
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`, `role_type`, `data_scope`, `status`) VALUES
(1, '管理员', 'admin', '系统管理员，拥有全量权限', 'builtin', 'all', 'enabled'),
(2, '高级运维', 'senior_ops', '高级运维人员，拥有生产环境操作权限', 'builtin', 'team', 'enabled'),
(3, '普通运维', 'ops', '普通运维人员，权限范围有限', 'builtin', 'self', 'enabled'),
(4, '开发人员', 'developer', '仅查看权限，无执行权限', 'builtin', 'self', 'enabled');

-- 默认管理员账号 (密码: Admin@2026)
INSERT INTO `sys_user` (`id`, `user_account`, `username`, `email`, `password`, `status`, `force_change_pwd`, `password_change_time`) VALUES
(1, 'admin', '系统管理员', 'admin@aomp.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'enabled', 0, NOW());

-- 管理员角色关联
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES (1, 1, 1);

-- 权限菜单
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `path`, `sort_order`) VALUES
(1, 0, '控制台', 'dashboard', 'menu', '/dashboard', 1),
(2, 0, '资产中心', 'asset', 'menu', '/assets', 2),
(3, 2, '主机列表', 'asset:host:list', 'menu', '/assets/hosts', 1),
(4, 2, '主机添加', 'asset:host:add', 'button', NULL, 2),
(5, 2, '主机编辑', 'asset:host:edit', 'button', NULL, 3),
(6, 2, '主机删除', 'asset:host:delete', 'button', NULL, 4),
(7, 2, '主机导入', 'asset:host:import', 'button', NULL, 5),
(8, 2, '主机导出', 'asset:host:export', 'button', NULL, 6),
(9, 2, '主机连通性检测', 'asset:host:check', 'button', NULL, 7),
(10, 0, '脚本仓库', 'script', 'menu', '/scripts', 3),
(11, 10, '脚本列表', 'script:list', 'menu', '/scripts/list', 1),
(12, 10, '脚本添加', 'script:add', 'button', NULL, 2),
(13, 10, '脚本编辑', 'script:edit', 'button', NULL, 3),
(14, 10, '脚本删除', 'script:delete', 'button', NULL, 4),
(15, 10, '脚本审核', 'script:audit', 'button', NULL, 5),
(16, 0, '任务执行', 'task', 'menu', '/tasks', 4),
(161, 16, '任务列表', 'task:list', 'menu', '/tasks/list', 0),
(17, 16, '创建任务', 'task:create', 'button', NULL, 1),
(18, 16, '任务执行', 'task:execute', 'button', NULL, 2),
(19, 16, '任务暂停', 'task:pause', 'button', NULL, 3),
(20, 16, '任务终止', 'task:terminate', 'button', NULL, 4),
(21, 16, '任务重试', 'task:retry', 'button', NULL, 5),
(22, 0, '一键巡检', 'inspection', 'menu', '/inspection', 5),
(23, 22, '巡检执行', 'inspection:execute', 'button', NULL, 1),
(24, 22, '巡检项配置', 'inspection:config', 'button', NULL, 2),
(25, 22, '巡检报告查看', 'inspection:report', 'button', NULL, 3),
(26, 22, '巡检报告导出', 'inspection:export', 'button', NULL, 4),
(27, 0, '审计日志', 'audit', 'menu', '/audit', 6),
(28, 27, '日志查看', 'audit:view', 'button', NULL, 1),
(29, 27, '日志导出', 'audit:export', 'button', NULL, 2),
(30, 0, '用户与权限', 'permission', 'menu', '/permission', 7),
(31, 30, '用户管理', 'permission:user:list', 'menu', '/permission/users', 1),
(32, 30, '用户新增', 'permission:user:add', 'button', NULL, 2),
(33, 30, '用户编辑', 'permission:user:edit', 'button', NULL, 3),
(34, 30, '用户禁用', 'permission:user:disable', 'button', NULL, 4),
(35, 30, '密码重置', 'permission:user:resetPwd', 'button', NULL, 5),
(36, 30, '角色管理', 'permission:role:list', 'menu', '/permission/roles', 6),
(37, 30, '角色新增', 'permission:role:add', 'button', NULL, 7),
(38, 30, '角色编辑', 'permission:role:edit', 'button', NULL, 8),
(39, 30, '角色删除', 'permission:role:delete', 'button', NULL, 9),
(40, 0, '个人设置', 'settings', 'menu', '/settings', 8),
(41, 0, '审批管理', 'approval', 'menu', '/approval', 9),
(42, 41, '审批操作', 'approval:operate', 'button', NULL, 1),
(43, 0, '通知管理', 'notification', 'menu', '/notification', 10),
(44, 0, '临时授权', 'temp_auth', 'menu', '/temp-auth', 11),
(45, 44, '临时授权管理', 'temp_auth:manage', 'button', NULL, 1);

-- 管理员拥有全部权限
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`)
SELECT ROW_NUMBER() OVER (ORDER BY id) + 100, 1, id FROM `sys_permission`;

-- 高级运维权限
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES
(200, 2, 1), (201, 2, 2), (202, 2, 3), (203, 2, 4), (204, 2, 5), (205, 2, 7), (206, 2, 8), (207, 2, 9),
(210, 2, 10), (211, 2, 11), (212, 2, 12), (213, 2, 13), (214, 2, 14),
(220, 2, 16), (221, 2, 17), (222, 2, 18), (223, 2, 19), (224, 2, 20), (225, 2, 21),
(230, 2, 22), (231, 2, 23), (232, 2, 24), (233, 2, 25), (234, 2, 26),
(240, 2, 27), (241, 2, 28), (242, 2, 29),
(250, 2, 40), (251, 2, 43);

-- 普通运维权限
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES
(300, 3, 1), (301, 3, 2), (302, 3, 3), (303, 3, 8),
(310, 3, 10), (311, 3, 11), (312, 3, 12), (313, 3, 13),
(320, 3, 16), (321, 3, 17), (322, 3, 18),
(330, 3, 22), (331, 3, 23), (332, 3, 25),
(340, 3, 40), (341, 3, 43);

-- 开发人员权限
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES
(400, 4, 1), (401, 4, 2), (402, 4, 3),
(410, 4, 10), (411, 4, 11),
(420, 4, 22), (421, 4, 25),
(430, 4, 40), (431, 4, 43);

-- 危险命令黑名单
INSERT INTO `dangerous_command` (`id`, `command_pattern`, `command_name`, `level`, `description`) VALUES
(1, 'rm\\s+(-[a-zA-Z]*f[a-zA-Z]*\\s+)?/($|\\s)', 'rm -rf /', 'block', '删除根目录，极其危险'),
(2, 'mkfs', 'mkfs 格式化', 'block', '格式化文件系统'),
(3, 'dd\\s+.*of=/dev/', 'dd 写设备', 'block', '直接写设备文件'),
(4, 'halt', 'halt 关机', 'approval', '关闭系统'),
(5, 'reboot', 'reboot 重启', 'approval', '重启系统'),
(6, 'shutdown', 'shutdown 关机', 'approval', '关闭系统'),
(7, 'init\\s+[06]', 'init 切换运行级别', 'approval', '切换系统运行级别'),
(8, ':(){ :|:& };:', 'fork炸弹', 'block', 'Fork炸弹，耗尽系统资源'),
(9, 'chmod\\s+(-R\\s+)?777\\s+/', 'chmod 777 根目录', 'warning', '递归修改根目录权限为777'),
(10, 'rm\\s+(-[a-zA-Z]*f[a-zA-Z]*\\s+)?/etc', 'rm /etc', 'block', '删除系统配置目录');

-- 内置巡检项
INSERT INTO `inspection_item` (`id`, `item_name`, `item_code`, `item_type`, `check_command`, `threshold_config`, `description`, `sort_order`) VALUES
(1, 'CPU使用率', 'cpu_usage', 'builtin', 'top -bn1 | grep "Cpu(s)" | awk \'{print $2}\'', '{"warning":80,"error":95}', '检查CPU使用率', 1),
(2, '内存使用率', 'memory_usage', 'builtin', 'free | grep Mem | awk \'{printf("%.1f", $3/$2*100)}\'', '{"warning":80,"error":95}', '检查内存使用率', 2),
(3, '磁盘使用率', 'disk_usage', 'builtin', 'df -h / | tail -1 | awk \'{print $5}\' | tr -d "%"', '{"warning":80,"error":95}', '检查根分区磁盘使用率', 3),
(4, '系统负载', 'load_avg', 'builtin', 'cat /proc/loadavg | awk \'{print $1}\'', '{"warning":5,"error":10}', '检查系统1分钟平均负载', 4),
(5, '网络连接数', 'network_connections', 'builtin', 'ss -s | grep estab | awk \'{print $2}\'', '{"warning":5000,"error":10000}', '检查ESTABLISHED网络连接数', 5),
(6, '僵尸进程', 'zombie_processes', 'builtin', 'ps aux | awk \'{if($8=="Z") print}\' | wc -l', '{"warning":5,"error":50}', '检查僵尸进程数量', 6),
(7, 'SWAP使用率', 'swap_usage', 'builtin', 'free | grep Swap | awk \'{if($2==0) print 0; else printf("%.1f",$3/$2*100)}\'', '{"warning":50,"error":80}', '检查SWAP使用率', 7),
(8, 'SSH服务状态', 'ssh_service', 'builtin', 'systemctl is-active sshd 2>/dev/null || systemctl is-active ssh 2>/dev/null', '{"error":"inactive"}', '检查SSH服务是否运行', 8);
