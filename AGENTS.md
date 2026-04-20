# AOM Project Guide

This file defines the working contract for AI coding agents and human contributors in this repository.

## Project Layout

- `aomp-admin`: Vue 3 + Vite frontend admin app.
- `aomp-server`: Spring Boot 3.2 + Java 21 backend service.
- `docs/product`: feature requirements, acceptance notes, and product decisions.
- `docs/architecture`: technical design, coding standards, and architectural decisions.
- `docs/testing`: test plans, test cases, and verification notes.
- `docs/progress`: milestone logs and delivery summaries.
- `artifacts`: screenshots, reports, imports, and generated evidence files.

## Default Workflow

For any non-trivial change, use this sequence:

1. Clarify the request and identify whether the change is frontend, backend, or cross-cutting.
2. If behavior changes, update or create design notes under `docs/product` or `docs/architecture` before implementation.
3. Make focused changes in the relevant module only.
4. Verify the changed module before claiming completion.
5. Record meaningful outcomes in `docs/progress` when the change is milestone-worthy.

## Change Boundaries

- Keep frontend code changes inside `aomp-admin`.
- Keep backend code changes inside `aomp-server`.
- Do not place temporary notes, screenshots, or reports in the repo root.
- Keep root-level files limited to repository coordination files such as `README.md`, `AGENTS.md`, `task_plan.md`, and `progress.md`.

## Coding Standards

Follow the detailed standards in `docs/architecture/coding-standards.md`.

High-level rules:

- Prefer small, local changes over broad refactors.
- Match existing patterns before introducing new abstractions.
- Separate API, UI, and domain concerns clearly.
- Add comments only when they explain intent or non-obvious decisions.
- Do not introduce new dependencies unless the change clearly benefits from them.

## Frontend Rules

- Stack: Vue 3, Vite, Element Plus, Vue Router, Vue I18n.
- Prefer composition-style reusable logic in `src/composables` or `src/utils`.
- Keep page-level logic in `src/views`, shared UI in `src/components`, and API wrappers in `src/api`.
- Avoid embedding request logic directly in large view components when a small API helper will do.

## Backend Rules

- Stack: Spring Boot 3.2, Java 21, MyBatis-Plus, Validation, Security, Redis, Quartz.
- Keep controllers thin and move business logic into service-layer classes.
- Use DTOs or request objects for controller boundaries instead of leaking persistence objects directly.
- Validate inputs at request boundaries.
- Keep scheduled jobs, websocket logic, and security concerns isolated from core business services.

## Verification Commands

Run only what is relevant to the files you changed.

Frontend:

- `cd aomp-admin; npm run build`

Backend:

- `cd aomp-server; mvn test`
- `cd aomp-server; mvn -q -DskipTests package`

If a task changes both frontend and backend behavior, verify both sides.

## Documentation Expectations

- New feature behavior: `docs/product`
- Technical decisions and implementation constraints: `docs/architecture`
- Test strategy or acceptance evidence: `docs/testing`
- Delivery summaries and checkpoint notes: `docs/progress`

## Agent Behavior

- Do not assume requirements that are not written down.
- When multiple implementation paths exist, prefer the simpler one unless the repository already uses a stronger pattern.
- If a requested change conflicts with these rules, follow the explicit request and document the tradeoff.
