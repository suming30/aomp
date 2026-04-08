# Design System Document: The Architect’s Blueprint

## 1. Overview & Creative North Star
### Creative North Star: "The Orchestrated Monolith"
In the world of O&M and DevOps, chaos is the enemy. This design system moves away from the "cluttered dashboard" trope and toward **The Orchestrated Monolith**. We aim for a UI that feels like a precision-engineered instrument—solid, unwavering, and hyper-efficient. 

We break the "template" look by rejecting the standard 1px border-grid. Instead, we use **Intentional Asymmetry** and **Tonal Depth**. By shifting the focus from structural lines to volumetric surfaces, we create a sense of secure, physical layers. The interface doesn't just display data; it houses it within a sophisticated, stable environment that commands authority.

---

## 2. Colors & Surface Philosophy
The palette is anchored in deep, professional blues and high-contrast technical accents. We utilize the Material Design 3 token logic but apply it with an editorial eye.

### The "No-Line" Rule
**Explicit Instruction:** Designers are prohibited from using 1px solid borders for sectioning or layout containment. Boundaries must be defined solely through background color shifts.
*   **Method:** A `surface-container-low` section sitting on a `surface` background creates a natural edge. 
*   **Result:** A cleaner, more sophisticated interface that reduces visual noise in high-density DevOps environments.

### Surface Hierarchy & Nesting
Treat the UI as a series of physical layers—like stacked sheets of obsidian or frosted glass.
*   **Level 0 (Base):** `surface` (#131313) - The bedrock of the application.
*   **Level 1 (Navigation/Sidebars):** `surface-container-low` (#1C1B1B) - Subtle separation for primary navigation.
*   **Level 2 (Main Workspace):** `surface-container` (#201F1F) - The primary canvas for tables and logs.
*   **Level 3 (Interactive Elements/Cards):** `surface-container-high` (#2A2A2A) - Draws the eye to actionable modules.

### The "Glass & Gradient" Rule
To escape the "flat enterprise" trap, floating elements (Modals, Hover-cards, Popovers) must use **Glassmorphism**:
*   **Fill:** `surface-container` at 80% opacity.
*   **Effect:** `backdrop-blur: 20px`.
*   **Signature Texture:** Use a subtle linear gradient on primary CTAs (from `primary` #B4C5FF to `primary-container` #0F62FE) to give buttons a "lithographic" glow rather than a flat plastic look.

---

## 3. Typography: The Technical Editorial
We pair the utilitarian nature of DevOps with high-end editorial scales.

*   **UI Labels & Interface:** **Inter (Sans-Serif)**. Chosen for its exceptional legibility at small sizes (`label-sm`: 0.6875rem) and its neutral, authoritative tone.
*   **Data & Logs:** **JetBrains Mono or IBM Plex Mono (Monospace)**. Reserved for code blocks, log consoles, and IP addresses. This creates a "Visual Context Shift"—the user instantly knows when they are looking at "System Truth" vs. "UI Meta-data."

**Hierarchy as Identity:** 
Large `display-sm` (2.25rem) headers should be used for dashboard overviews to provide a sense of "Command and Control," while `title-sm` (1rem) is used for widget headers to maintain high information density without sacrificing breathing room.

---

## 4. Elevation & Depth: Tonal Layering
Traditional drop shadows are too "soft" for a technical O&M platform. We use **Tonal Layering**.

*   **The Layering Principle:** Depth is achieved by stacking the `surface-container` tiers. Place a `surface-container-highest` card on a `surface-container-low` background to create a "lift" that feels integrated into the architecture.
*   **Ambient Shadows:** For floating elements, use a "Shadow-Tint." Instead of `#000000`, use a 15% opacity version of `on-surface` (#E5E2E1) with a 40px blur. This mimics a soft laboratory glow.
*   **The "Ghost Border" Fallback:** If a container must have a stroke (e.g., for accessibility in high-density tables), use the **Ghost Border**: `outline-variant` (#424656) at **15% opacity**. Never use 100% opacity borders.

---

## 5. Components

### Large Data Tables
*   **Style:** No vertical lines. Horizontal lines use the "Ghost Border" (15% `outline-variant`). 
*   **Header:** `surface-container-highest` with `label-md` uppercase text.
*   **Density:** Tight vertical padding (`0.5rem`) to ensure maximum data visibility.

### Real-Time Log Consoles
*   **Background:** `surface-container-lowest` (#0E0E0E).
*   **Text:** `body-sm` in Monospace. Use `primary` (#B4C5FF) for timestamps and `on-surface-variant` (#C3C6D8) for standard output.
*   **Edge:** A 2px left-accent border in `primary` to denote an active "stream."

### Status Badges (The "Signal" System)
*   **Success:** `primary-container` background with `on-primary-container` text.
*   **Error/Alert:** `error_container` (#93000A) background with `on-error-container` text.
*   **Processing:** `secondary_container` (#2D458A) with a pulse animation (0.6 opacity pulse).

### Buttons
*   **Primary:** Gradient fill (Primary to Primary-Container). `lg` (0.5rem) roundedness. 
*   **Secondary:** Ghost style. `outline` token at 20% opacity.
*   **Tertiary:** Text only, `primary` color, `label-md` bold.

### Form Wizards
*   **Progress Trackers:** Use thick 4px horizontal bars. Active steps use `primary`, inactive steps use `surface-variant`. Avoid "circles with numbers"—use a minimalist "Bar & Label" approach.

---

## 6. Do’s and Don’ts

### Do
*   **Do** use vertical white space (from the spacing scale) to separate card content instead of divider lines.
*   **Do** use `tertiary` (#FFB59D) for "War Room" or "High-Priority" alerts—it breaks the blue/gray monotony.
*   **Do** nest components using the surface hierarchy (Lowest -> Low -> Container -> High -> Highest).

### Don’t
*   **Don’t** use a 1px solid border to wrap the main sidebar. Use a `surface-container-low` background shift.
*   **Don’t** use pure black (#000000) for backgrounds. Use `surface` (#131313) to maintain tonal depth.
*   **Don’t** use standard "Drop Shadows." Use tonal shifts and high-blur ambient glows.
*   **Don’t** use bright "Traffic Light" greens. Use our `primary` blue variants for "Healthy" to maintain the "Orchestrated Monolith" aesthetic. Reserve `error` red only for critical failures.