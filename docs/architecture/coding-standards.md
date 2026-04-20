# Coding Standards

This document turns repository expectations into concrete implementation rules for `aomp-admin` and `aomp-server`.

## 1. General Principles

- Prefer the smallest change that fully solves the problem.
- Keep module boundaries clear: frontend in `aomp-admin`, backend in `aomp-server`, documentation in `docs`.
- Avoid speculative abstractions.
- Name files and symbols by business meaning, not by generic technical terms.
- If a file becomes hard to scan, split it by responsibility instead of stacking more conditionals into it.

## 2. Frontend Standards

### 2.1 Structure

- `src/views`: page-level screens and route containers.
- `src/components`: shared presentational and interactive components.
- `src/api`: API request wrappers and endpoint-oriented functions.
- `src/composables`: reusable view logic and stateful helpers.
- `src/utils`: stateless helpers and formatting logic.
- `src/stores`: app-level or domain-level shared state.

### 2.2 Component Rules

- Keep a component focused on one screen concern.
- Extract repeated logic before repeating it in a second screen.
- Prefer explicit props and emitted events over hidden side effects.
- Avoid mixing request orchestration, formatting, and UI state in one large block.

### 2.3 API Rules

- Centralize HTTP calls in `src/api`.
- Keep request payload mapping near the API wrapper.
- Do not scatter raw endpoint strings across views.

## 3. Backend Standards

### 3.1 Layering

- Controllers: request mapping, validation entry, response shaping.
- Services: business orchestration and domain rules.
- Mappers or persistence layer: database access only.
- Scheduled jobs or websocket handlers: call services instead of duplicating business logic.

### 3.2 Request Boundaries

- Validate all externally supplied data at controller boundaries.
- Prefer dedicated request and response models when controller contracts differ from persistence models.
- Keep authentication and authorization checks close to the entry point.

### 3.3 Error Handling

- Return clear, consistent error responses.
- Do not swallow exceptions silently.
- Log failures with enough context to diagnose them later.

## 4. Documentation Rules

- Product intent belongs in `docs/product`.
- Implementation constraints and technical decisions belong in `docs/architecture`.
- Verification evidence belongs in `docs/testing`.
- Milestone notes belong in `docs/progress`.

## 5. Verification Rules

Frontend changes:

- At minimum, ensure `npm run build` passes in `aomp-admin`.

Backend changes:

- At minimum, ensure `mvn test` or the narrowest relevant Maven verification command passes in `aomp-server`.

Cross-cutting changes:

- Verify both modules and document any skipped verification explicitly.

## 6. AI Agent Rules

- Read nearby files before editing.
- Match existing patterns unless there is a clear project-level reason to improve them.
- Prefer updating docs when behavior, workflow, or architecture changes.
- Do not use repository root as a dumping ground for generated notes or assets.
