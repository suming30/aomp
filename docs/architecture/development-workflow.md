# Development Workflow

This workflow is designed for day-to-day work with Codex, Trae CN, Cursor, and human contributors.

## 1. Intake

- Classify the task as frontend, backend, or cross-cutting.
- Confirm whether it changes behavior, only fixes defects, or only restructures code.
- If requirements are unclear, clarify before implementation.

## 2. Plan Before Code

Use this lightweight standard:

- Small task: write a short plan in the active session.
- Medium or large task: update `task_plan.md` and note affected modules.
- Behavior-changing task: add or update supporting notes in `docs/product` or `docs/architecture`.

## 3. Implement in the Right Module

- Frontend-only tasks stay in `aomp-admin`.
- Backend-only tasks stay in `aomp-server`.
- Shared behavior changes should be implemented separately in each module with clear contracts.

## 4. Verify

Choose the narrowest meaningful verification:

- Frontend: `cd aomp-admin && npm run build`
- Backend: `cd aomp-server && mvn test`
- Packaging check when relevant: `cd aomp-server && mvn -q -DskipTests package`

## 5. Record

When a task meaningfully changes product behavior, architecture, or release readiness:

- Update `docs/product`, `docs/architecture`, or `docs/testing`
- Add milestone notes to `docs/progress` if the change closes a phase or deliverable

## 6. Preferred Agent Workflow

Recommended skill order for this repository:

1. `brainstorming` for new features or behavior changes
2. `writing-plans` for multi-step implementation
3. `software-architecture` when the change affects structure or boundaries
4. `frontend-design` for UI-heavy work in `aomp-admin`
5. `test-driven-development` or `systematic-debugging` when fixing defects

## 7. What To Avoid

- Editing frontend and backend in one pass without clarifying the contract between them
- Adding root-level scratch files
- Large refactors without a written reason
- Claiming success before module-level verification
