# AOM Project Organization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Standardize the repository layout so product docs, testing docs, reports, screenshots, and import assets have predictable homes without changing frontend or backend source modules.

**Architecture:** Keep `aomp-admin` and `aomp-server` intact, add repository-level `docs/` and `artifacts/` folders, then move scattered root and backend documentation into the new structure. Preserve top-level operational files such as `README.md`, `task_plan.md`, and `progress.md`.

**Tech Stack:** Git repository layout, PowerShell file operations, Markdown documentation

---

### Task 1: Create shared documentation and artifact directories

**Files:**
- Create: `docs/product/`
- Create: `docs/architecture/`
- Create: `docs/testing/`
- Create: `docs/progress/`
- Create: `artifacts/screenshots/`
- Create: `artifacts/reports/`
- Create: `artifacts/imports/`

- [ ] **Step 1: Create the directories**

Run: `New-Item -ItemType Directory -Force 'D:\javaCode\aom\docs\product','D:\javaCode\aom\docs\architecture','D:\javaCode\aom\docs\testing','D:\javaCode\aom\docs\progress','D:\javaCode\aom\artifacts\screenshots','D:\javaCode\aom\artifacts\reports','D:\javaCode\aom\artifacts\imports'`
Expected: PowerShell reports the created directories without errors.

- [ ] **Step 2: Verify the directories exist**

Run: `Get-ChildItem 'D:\javaCode\aom\docs'; Get-ChildItem 'D:\javaCode\aom\artifacts'`
Expected: The output lists `product`, `architecture`, `testing`, `progress`, `screenshots`, `reports`, and `imports`.

### Task 2: Move product and testing materials into the shared structure

**Files:**
- Modify: `docs/product/自动化运维脚本执行平台 - 完整 PRD（产品需求文档）.md`
- Modify: `docs/testing/前后端联调测试方案.md`
- Modify: `docs/testing/前后端联调验收结论.md`
- Modify: `docs/testing/权限交叉测试汇总表.md`
- Modify: `docs/testing/测试用例-模块1-2.md`
- Modify: `docs/testing/测试用例-模块3-4.md`
- Modify: `docs/testing/测试用例-模块5-6.md`
- Modify: `docs/testing/测试用例-模块7-10.md`
- Modify: `artifacts/reports/TEST-REPORT.md`
- Modify: `artifacts/imports/import-tracking-data.xlsx`
- Modify: `artifacts/screenshots/test-screenshots/`

- [ ] **Step 1: Move the root product and artifact files**

Run: `Move-Item -Force 'D:\javaCode\aom\自动化运维脚本执行平台 - 完整 PRD（产品需求文档）.md' 'D:\javaCode\aom\docs\product\'; Move-Item -Force 'D:\javaCode\aom\TEST-REPORT.md' 'D:\javaCode\aom\artifacts\reports\'; Move-Item -Force 'D:\javaCode\aom\import-tracking-data.xlsx' 'D:\javaCode\aom\artifacts\imports\'; Move-Item -Force 'D:\javaCode\aom\test-screenshots' 'D:\javaCode\aom\artifacts\screenshots\'`
Expected: Each item is moved without conflict errors.

- [ ] **Step 2: Move backend testing documents into the shared testing folder**

Run: `Move-Item -Force 'D:\javaCode\aom\aomp-server\docs\*.md' 'D:\javaCode\aom\docs\testing\'`
Expected: The markdown files appear in `docs/testing`.

- [ ] **Step 3: Verify the moved files**

Run: `Get-ChildItem 'D:\javaCode\aom\docs\product'; Get-ChildItem 'D:\javaCode\aom\docs\testing'; Get-ChildItem 'D:\javaCode\aom\artifacts\reports'; Get-ChildItem 'D:\javaCode\aom\artifacts\imports'; Get-ChildItem 'D:\javaCode\aom\artifacts\screenshots'`
Expected: The new folders contain the moved files and subdirectories.

### Task 3: Capture the repository organization rules in README

**Files:**
- Modify: `README.md`

- [ ] **Step 1: Add a concise repository structure section**

Document the purpose of `aomp-admin`, `aomp-server`, `docs/`, and `artifacts/` so future work follows the new layout.

- [ ] **Step 2: Verify the README change**

Run: `Get-Content 'D:\javaCode\aom\README.md'`
Expected: The README includes the repository structure guidance.
