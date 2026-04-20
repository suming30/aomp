# 多模块可用性补全说明

日期：2026-04-17

## 目标

将“主机管理”改造方式扩展到其他关键模块，统一为：

- 真实接口优先
- 接口异常时自动回退到演示数据
- 关键交互具备可执行 CRUD 或可执行读取流程

## 覆盖模块

- `aomp-admin/src/views/assets/GroupListView.vue`
- `aomp-admin/src/views/assets/TagListView.vue`
- `aomp-admin/src/views/scripts/ScriptListView.vue`
- `aomp-admin/src/views/tasks/TaskLogView.vue`
- `aomp-admin/src/views/tasks/TaskResultView.vue`
- `aomp-admin/src/views/inspection/InspectionReportView.vue`

## 配套改动

- 新增演示数据存储：
  - `aomp-admin/src/utils/assetMetaMockStore.js`
  - `aomp-admin/src/utils/scriptMockStore.js`
  - `aomp-admin/src/utils/taskMockStore.js`
  - `aomp-admin/src/utils/inspectionMockStore.js`
- 补充巡检 API 封装：
  - `getInspectionTaskById`
  - `getInspectionTaskResults`

## 约束与边界

- 主机组与标签后端控制器当前未提供，本次以前端演示数据回退保证可用性。
- 任务结果与巡检报告优先使用已有任务/结果接口组装视图，不额外扩展后端表结构。
