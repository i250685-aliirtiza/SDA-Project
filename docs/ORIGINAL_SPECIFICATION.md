# GAMEHUB — IMPLEMENTATION.md

**Project:** GameHub — Gaming Café and Esports Tournament Management System  
**Course:** CS3004 Software Design and Analysis, FAST-NUCES Islamabad, Fall 2026  
**Team:** Ali Irtiza Ahmed (`i250685`) and Syed Muhammad Abdullah Khalil (`i250782`)  
**Status:** Execution checklist; **NOT** an assertion that implementation is finished.  
**Purpose:** A SELF-CONTAINED, source-reconciled executable specification for Codex to build the full JavaFX application. Annexes below preserve D2 product scope, D3 exact fully dressed use cases, D4 exact SSDs and Java-integration problem, D5 class relationships and twelve internal SDs, plus rubric. Source PDFs are NOT required in Codex workspace to begin.

> **CODEX: THIS FILE IS YOUR STANDALONE EXECUTION CONTRACT.** Read it **including the complete source annexes** before editing. Work from top to bottom. Do not stop after scaffolding. Implement the app, database, all twelve use cases, usable JavaFX screens, error paths, tests, and documentation. Every checked item must be supported by a reproducible result. Never fabricate test results. Do not mark incomplete work as complete. Do not replace business operations with hard-coded responses, mock repositories in the production app, or decorative buttons.


## GREENFIELD EXECUTION MODE — THE ONLY INPUT IS THIS FILE (READ FIRST)

**Actual starting state (confirmed by user):** The folder supplied to Codex is **EMPTY except for this `implementation.md` file**. There is no code, repository, Maven project, database, design PDF, JavaFX application, test suite, setup script, graphics, UML diagram file, prior README, or other required dependency supplied by the user. **Build everything from scratch.**

**This Markdown document is the complete functional implementation specification.** Its Annexes A–H embed the relevant Deliverable 2 proposal, Deliverable 3 fully dressed use cases, Deliverable 4 SSDs and academic Java inheritance requirement, Deliverable 5 class/interaction design, and final rubric. **NEVER ask the user to upload these PDFs, connect a database server, supply source code, pick a UI framework, or create a project before you can begin.** Original PDFs are optional external validation materials, not runtime/build prerequisites.

### Exact first actions for Codex (execute, do not merely describe)

- [ ] Confirm current directory contains `implementation.md` and no existing app files. Read this entire Markdown file **including all annexes**; if needed, read in sequential chunks rather than truncating it.
- [ ] Create `pom.xml`, `src/main/java/`, `src/main/resources/`, `src/test/java/`, `docs/`, `.gitignore`, `README.md`, and other files described in §2. **Do not wait for any files to be supplied.**
- [ ] Generate a reproducible Maven build with Java 21, JavaFX, SQLite JDBC, JUnit 5 and plugin configuration. Prefer Maven Wrapper (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/`). If Maven is installed, bootstrap wrapper using `mvn -N wrapper:wrapper` with a pinned plugin version; if wrapper cannot be obtained because the environment is offline, create and verify a standard `mvn` fallback and document the exact installation dependency. Do not create a fictitious working wrapper.
- [ ] Write the real app entry point and role-aware JavaFX shell; verify it compiles/launches when GUI is available. No decorative/static replacement of the actual app.
- [ ] Write the SQL migration(s), JDBC transaction layer, domain classes, repositories, business controllers and JavaFX screens; persist in a **new automatically initialized local SQLite file**.
- [ ] Implement UC-01 through UC-12 and all actionable D3 extensions, plus the **separate** D4 polymorphism/personnel demonstration. Run actual tests after each vertical slice.
- [ ] From these annexes, create `docs/TRACEABILITY.md`, `docs/DESIGN_TO_CODE.md`, `docs/DECISIONS.md`, and diagram source such as PlantUML `.puml` files. If rendering tools are available, export readable PNG/SVG for the report; otherwise keep editable diagram source and accurately flag missing image export. Do not claim to have regenerated pixel-identical instructor diagrams.
- [ ] Supply an idempotent demo seeding/bootstrap path, a tested README with build/run commands for Windows and Unix, an actual UI walkthrough, and a final evidence report with all unfinished boxes left unchecked.

### Greenfield rule changes relative to the generic checklist below

1. **No audit of nonexistent code:** inventory the empty folder, record that it was empty, then implement. Any instruction later saying “preserve existing code” is vacuously satisfied; do not search for nonexistent repository history.
2. **No source-file blocker:** record that original D2–D5/rubric files are not in Codex's folder while using the **embedded full Annexes A–H** as the specification. Never stop because PDFs are missing.
3. **No pre-existing Git repository required:** `git init` is allowed for local versioning if Git is installed, but do not publish or push; do not require Git credentials.
4. **No pre-existing graphics required:** build a polished UI using CSS, JavaFX vector shapes/icons and text. Avoid blocked external image downloads.
5. **No installed SQLite server required:** SQLite is an embedded JDBC dependency; create the DB automatically.
6. **No manual pre-configuration required:** first-launch manager bootstrap and explicit opt-in demo seed must work from a new machine with the documented Java/Maven prerequisites. No default public passwords.
7. **Report environment constraints precisely:** headless CI cannot prove a visible JavaFX window unless a headless UI test harness is configured and actually works. Still compile, test integration, and provide runnable commands; leave manual GUI checks unchecked until genuinely run.
8. **Use this document as the sole task input.** No subsequent user clarification is necessary for choices covered here. If an instructor template is absent, generate a complete reasonable report draft but **do not claim official template compliance**.

### Stop conditions and final result

Completion requires **real source files**, a working Maven project, a runnable JavaFX app, a persistent SQLite DB, full use-case logic, automated tests, demo data/bootstrap commands, and docs—**not merely a summary, generated scaffolding, a browser mockup, or checked boxes**. Codex should not stop after initializing a repository or finishing one UC; continue through the sequence and make the strongest verified deliverable possible in the current execution. Only the user can declare acceptance of any incomplete work.

---

## 0. Source of truth, conflicts, and operating rules

### 0.1 Reference artifacts

The following submitted materials were reconciled into this document before the Codex handoff. These originals are **not in the Codex workspace** (the user supplies only this file). The complete source-derived contractual details appear in Annexes A–H; absence of originals never blocks implementation:

1. **Deliverable 2:** `i250685_i250782_D.pdf` — original project proposal and scope (embedded in Annex A).
2. **Official final implementation rubric:** `SDA_Project_Rubric(1).pdf` (uploaded November 23, 2026). **Controlling source for grading.**
3. **Deliverable 3:** `GameHub_Deliverable_3_Use_Cases.pdf`. Defines **twelve exact use-case names**, actors, main success scenarios, extensions, preconditions and postconditions.
4. **Deliverable 4:** `i250685_i250782_D(1).pdf`. Defines **twelve black-box System Sequence Diagrams (SSDs)**, one per use case.
5. **Deliverable 5:** `Deliverable 5 diagrams.pdf`. Defines **three design class-model views**, controller/repository/policy design, and **twelve design sequence diagrams**.
6. **Deliverable 4 assignment question:** `Deliverable-04_Question.pdf` — includes separate mandatory polymorphic payroll code requirement (Annex E).
7. **Project context:** `GameHub_SDA_Master_Context.md`. Defines domain scope, team, architectural intent, terminology, and earlier decisions.

**Precedence by concern:** Official rubric controls mandatory grading constraints; D3 controls observable behavior; D4 controls system-event traceability; D5 controls responsibilities/relationships; master context controls project identity and nonconflicting scope. If two artifacts disagree, do not silently erase either: favor the appropriate concern above, record the conflict and implementation decision in `docs/DECISIONS.md`, and update `docs/TRACEABILITY.md`. Do not fabricate claims about an absent source; code against the concrete contract below, and flag any missing source in `docs/SOURCE_AUDIT.md`.

### 0.2 Mandatory Codex behavior

- [ ] Verify that the workspace is empty apart from `implementation.md`; record the baseline in `docs/IMPLEMENTATION_AUDIT.md`. If other files unexpectedly exist, inspect them before modifying.
- [ ] Generate fresh, understandable source code for this project. If a working file appears later, preserve its behavior when refactoring.
- [ ] Record the initial state in `docs/IMPLEMENTATION_AUDIT.md` as `ALL UC-01–UC-12 ABSENT; SOURCE CODE ABSENT; DATABASE ABSENT; BUILD ABSENT`, then update actual status as verified milestones complete.
- [ ] Create `docs/SOURCE_AUDIT.md`: mark this embedded specification as `inspected`, original reference PDFs as `not present in workspace`, and distinguish source-derived annex text from implementation defaults. **Do not request PDFs**.
- [ ] Implement vertically, delivering a working UI → business logic → DB flow per use case; do not build a disconnected pile of entities.
- [ ] **Autonomously make ordinary engineering decisions** needed to finish. The defaults in this document settle previously open technology/product choices for implementation, not retroactive claims about instructor approval. Record material deviations in `docs/DECISIONS.md`.
- [ ] Do not ask the user questions for routine missing values, naming, layout, or framework setup. Choose the documented default and proceed.
- [ ] Only stop for a truly blocking external dependency, unavailable credential, an unsatisfiable official constraint, or a destructive action not authorized here. In that case, finish every independent task and report the exact blocker with file/command evidence.
- [ ] Use real persistence and cross-screen consistency. No hidden app state that vanishes on restart except current login, unsaved forms, and temporary UI state.
- [ ] Keep changes within the project workspace; **do not push, publish, or rewrite Git history**. Commit only if repository policy/user explicitly authorizes it. Never embed secrets in Git.
- [ ] After each vertical slice, compile and test. Fix failures rather than simply reporting them.
- [ ] Update checkbox states **only after evidence exists**. Incomplete tests remain unchecked, even if code was written.

### 0.3 Fixed decisions for the final app

| Dimension | Implementation decision |
|---|---|
| Product | Single-machine, offline-first **desktop** management application; no cloud services required |
| Language | Java **21** (LTS), with normal object-oriented Java (no Spring or web server) |
| UI | **JavaFX 21**; official rubric explicitly requires JavaFX, not Swing |
| Build | Maven Wrapper (`./mvnw` / `mvnw.cmd`), Maven JavaFX plugin, reproducible dependency versions |
| DB | SQLite via JDBC; durable local DB at a configurable user-writable path; schema migrations |
| SQL | Prepared statements and transactions; foreign keys enabled per connection |
| Tests | JUnit 5; pure business tests plus integration tests against fresh temporary SQLite DBs; optional TestFX only if stable in available environment |
| Time | `java.time`: `Instant`/UTC for persisted timestamps, `Asia/Karachi` for displayed café date/time; no implicit machine timezone assumptions |
| Money | PKR domain amounts represented by `BigDecimal` scale 2 with `HALF_UP`, persisted as `long` integer paisa in SQLite via exact conversion; **never floating-point money** |
| Authentication | Hashed+salted passwords via PBKDF2-HMAC-SHA256 or stronger, no plaintext storage, role-based access |
| Supported actors | CUSTOMER, STAFF, ORGANIZER, MANAGER; role permissions enforced in business layer, not just by hiding screens |
| Financial processing | CASH with staff-confirmed receipt; optional MANUAL_EXTERNAL with staff-confirmed external reference. **Do not falsely imply actual card/bank API integration.** |
| Tournament formats | SOLO or TEAM registration; SINGLE_ELIMINATION and ROUND_ROBIN; no external esports API |
| UI design | Professional but simple JavaFX dashboard, sidebar/navigation, clear forms/tables/dialogs, validation messages and loading/empty/error states |
| Scope exclusions | No real online payment gateway, email/SMS service, web app, distributed server, notification infrastructure, AI features, or unsupported tournament formats |

**Dependencies:** Select and pin versions compatible with Java 21/JavaFX 21, and verify with `mvn dependency:tree` and a clean build; avoid `LATEST`, unbounded ranges, and snapshot dependencies. If an existing Java 17-compatible project is materially complete, migrating to 21 must not break it. Native JavaFX runtime/platform dependencies must resolve through Maven on the actual test machine. Do not claim Windows/macOS/Linux portability without checking; document tested platforms.

### 0.4 Definition of DONE — applies to *every* checkbox

A feature is **DONE** only when **all** apply: (1) it has an accessible JavaFX screen/action; (2) it invokes business logic rather than doing SQL in a UI controller; (3) it persists/reads the correct state in SQLite; (4) authorization and input validation work; (5) its positive case and at least one D3 extension/negative case have tests; (6) the screen gives intelligible results/errors; (7) state is still correct after app restart; and (8) `docs/TRACEABILITY.md` maps it to D3/D4/D5/design classes/tests. A checkmark is **evidence**, not optimism.

---

## 1. Grading contract — exact rubric requirements (100 points)

| Rubric criterion | Marks | Non-negotiable acceptance |
|---|---:|---|
| User Interface | **10** | Fully functional **JavaFX** interface; screens/events map to SSDs and fully dressed use cases |
| Business Logic Functionality | **15** | Correct real outputs; robust flow; no build/runtime errors |
| OOP Principles | **15** | Encapsulation, abstraction, inheritance and polymorphism used where meaningful, not artificially |
| Database | **10** | Correctly named tables, CRUD, exception handling, input validation, durable real data |
| Architecture & Integration | **10** | Presentation, business logic and data access in **separate packages**, fully integrated |
| Use Cases | **10** | **All assigned** use cases implemented; each member can demonstrate their contribution |
| Design-to-Code Consistency | **10** | 100% mapping back to approved design artifacts, or explicitly reconciled discrepancies |
| GRASP & GoF | **10** | Controller, Information Expert, Creator, Low Coupling, High Cohesion, Protected Variations correctly applied; GoF where applicable |
| Documentation | **10** | Detailed final documentation in instructor's template with readable model images, all sections complete |

**Completeness requirements from rubric:** all use cases must run; login/registration alone are **not** the main use cases; assessment is individual; the application must actually execute.

- [ ] Create `docs/RUBRIC_EVIDENCE.md` with one subsection per 9 rubric criteria, direct links to code/tests/screenshots and a 0–10/15 self-check (not invented instructor marks).
- [ ] Provide a **3–5-minute primary demonstration** proving UC-02 → UC-03 → UC-04 (reserve → session → payment) with a visibly changing SQLite-backed state; plus an esports demonstration for UC-06 → UC-09.
- [ ] Both members can individually demonstrate and explain their owned use cases and relevant class design.

---

## 2. Repository and startup requirements

The user is starting from an empty folder containing only this file. **Create this structure now** (and add any required subpackages/files); there is no existing layout to inspect:

```text
GameHub/
├── implementation.md
├── README.md
├── pom.xml
├── mvnw / mvnw.cmd / .mvn/wrapper/
├── .gitignore
├── docs/
│   ├── SOURCE_AUDIT.md
│   ├── IMPLEMENTATION_AUDIT.md
│   ├── DECISIONS.md
│   ├── TRACEABILITY.md
│   ├── RUBRIC_EVIDENCE.md
│   ├── TEST_REPORT.md
│   ├── DEMO_SCRIPT.md
│   ├── DATABASE.md
│   ├── DESIGN_TO_CODE.md
│   ├── USER_GUIDE.md
│   └── screenshots/
├── src/main/java/com/gamehub/
│   ├── GameHubApp.java
│   ├── bootstrap/             # composition root; migration + dependency wiring
│   ├── presentation/          # JavaFX screens, controllers, view models, navigation
│   ├── application/           # use-case controllers/services, ports, DTOs
│   ├── domain/                # entities, value objects, states, rules, policy interfaces
│   ├── infrastructure/        # JDBC repositories, migrations, hashing, exports
│   └── common/                # errors, validators, formatting, clock abstractions
├── src/main/resources/
│   ├── db/migrations/
│   ├── styles/
│   └── images/               # only if real, locally licensed assets are used
└── src/test/java/com/gamehub/
    ├── domain/
    ├── application/
    └── infrastructure/
```

**Architectural dependency rule:** `presentation → application → domain`; `infrastructure → domain/application ports`; `bootstrap → all for dependency wiring`. Domain must not import `javafx.*`, `java.sql.*`, view classes, or SQLite. A presentation controller must not create JDBC connections or embed SQL. Repositories must not create JavaFX widgets. Application-layer controllers/orchestrators are **distinct** from JavaFX event handlers.

### Startup and packaging

- [ ] `./mvnw clean verify` succeeds from a fresh checkout; on Windows, `mvnw.cmd clean verify`.
- [ ] `./mvnw javafx:run` launches the actual desktop app; document `mvnw.cmd javafx:run` for Windows.
- [ ] First launch creates/migrates the DB in a user-writable directory (`GAMEHUB_DB_PATH` override; otherwise `~/.gamehub/gamehub.db`) and never relies on CWD-relative working data.
- [ ] Startup validates DB connectivity and schema; if unavailable, show actionable error, no misleading success screen.
- [ ] A fresh setup offers a bootstrap path for the first Manager (one-time initialization, password set interactively) and optionally deterministic **development-only** demo seed data.
- [ ] Seed data must be opt-in (`GAMEHUB_DEMO_SEED=true`) and must never overwrite real existing records. Demo users/credentials must be clearly labeled in README and changed for normal use.
- [ ] Build/distribution path is documented. If native installer is not feasible, provide a verified Maven launch and reproducible build; do not declare installer completion without producing/running one.
- [ ] README contains prerequisites, platform, exact commands, first login/bootstrap, sample workflow, DB path, reset procedure, limitations and common troubleshooting.
- [ ] No missing resources or FXML loader exceptions; if FXML is used, view/controller paths are tested. Pure JavaFX view construction is equally acceptable.

---

## 3. Domain rules and invariants (must hold in business logic AND DB transactions)

### 3.1 Reservation + station + session

- [ ] A reservation covers a **half-open** interval `[startAt, endAt)`. `startAt < endAt`; equal adjacent endpoints **do not** overlap. Overlap condition: `existing.startAt < requested.endAt AND requested.startAt < existing.endAt`.
- [ ] Block overlapping **confirmed** reservations for same station and conflicting reservations for same customer (per D3 UC-02). Recheck overlaps inside the write transaction, not only when showing available stations.
- [ ] Station operational state (`AVAILABLE`, `IN_USE`, `MAINTENANCE`, `OUT_OF_SERVICE`) is independent of future reservation calendar. **Future reservation must NOT permanently mark the station IN_USE.**
- [ ] No reservation or session may use a station in MAINTENANCE or OUT_OF_SERVICE. Only one ACTIVE session per station. Session start checks station state and reservation eligibility.
- [ ] A walk-in session must be possible for an identifiable customer, if the station is usable and the requested/planned time does not conflict with upcoming reservations.
- [ ] Extension of a session must recheck future reservations; a rejection must leave the original session unchanged.
- [ ] Session end must record actual start/end, calculate positive billable duration under an explicit policy, create **one** charge, and release the station only if no fault/outage is active.
- [ ] Cancelling a reservation releases its interval without deleting financial/session history; cancellation of a completed/in-use reservation is prohibited or handled by an explicit authorized workflow.
- [ ] Maintenance activation identifies future affected bookings (alerts/report), prevents new allocations, and does **not silently delete or automatically reassign** reservations.

### 3.2 Money, membership and payments

- [ ] All amounts are nonnegative whole paisa; display formatted PKR with two decimals. Integer arithmetic or exact decimal arithmetic only; define rounding **HALF_UP to paisa** for percentage calculations.
- [ ] Base session charge = station hourly rate × billable minutes / 60, rounded to paisa once. Billable minutes = `max(1, ceil(actualSeconds / 60))` for a nonzero session; do not bill zero-duration sessions (reject/fix timestamp). Price quoted at booking is **estimate**, not a final charge.
- [ ] `PricingPolicy` is an interface; `StandardPricingPolicy` applies station rate plus configured membership discount if any. Default plan discount may be 0%; any configured discount must be applied consistently once. No invented automatic promotions.
- [ ] Charges and payments remain separate. `Charge.remainingBalance()` = `max(0, due - sum(successful payments))`; `PAID` only when zero; partial payment is supported and auditable.
- [ ] A confirmed payment is append-only. Never silently update/delete payment history; corrections use separate reversal/adjustment records with authorized reason and audit log.
- [ ] Payment confirmation, charge settlement and receipt creation occur in **one DB transaction**. Duplicate submit cannot create two identical payment operations (idempotency token per confirmation).
- [ ] **No payment gateway is simulated as a genuine bank verification.** CASH is explicitly staff-confirmed. MANUAL_EXTERNAL requires staff to confirm a reference. Unsupported online methods are disabled and labeled not integrated.
- [ ] Paid membership becomes active **only after required payment is confirmed**. Expiry convention: start inclusive, expiry exclusive in local calendar dates. Renewal starts at `max(today, currentExpiryDate)` if still active; adding plan duration to that date extends benefits without shortening prior coverage.
- [ ] Membership plan is a persistent entity (name, price, duration, optional discount); unsupported special benefits should not be advertised. Enrollment and payment are linked.

### 3.3 Teams, tournaments and matches

- [ ] Team names case-insensitively unique; one captain; creator joins as captain atomically. Membership roster stored as join records, not comma-separated fields.
- [ ] Joining an existing team is **approval-based** by captain (or explicitly confirmed invite). A request alone cannot add a member; approvals are authenticated and auditable.
- [ ] No duplicate active membership record in same team; roster changes that would invalidate confirmed tournament registration are blocked with explanation.
- [ ] Every TournamentRegistration references **exactly one** entrant: `customer_id XOR team_id` (per D5 model). Enforce via service validation and SQL CHECK constraint.
- [ ] Registration must recheck OPEN status, correct entrant type, eligibility, unique entrant, capacity and fee status at confirmation time. Capacity includes only valid/confirmed registrations; pending entries do not silently consume slots forever.
- [ ] Paid entry fee: create a pending registration and charge; confirm registration only when fee settled. Free entry: confirm immediately. Paid unpaid entries are visible as pending, not counted as confirmed.
- [ ] Tournament lifecycle: `DRAFT → REGISTRATION_OPEN → REGISTRATION_CLOSED → ACTIVE → COMPLETED`, with explicit `CANCELLED` allowed from pre-completion statuses. No invalid transitions; changes are logged.
- [ ] SOLO and TEAM are explicit tournament entrant types; captain authorization is required for TEAM. Changing entrant type after registrations exist is forbidden.
- [ ] SINGLE_ELIMINATION: generate deterministic bracket from confirmed entrant order after close, assign BYEs when necessary, and advance winner exactly once. Never allow draws in decisive matches.
- [ ] ROUND_ROBIN: schedule each pair exactly once (unless an explicitly documented repeat-leg setting exists); standings derived from completed matches with deterministic tie-breaking documented in app/README.
- [ ] A match can progress `SCHEDULED → IN_PROGRESS → COMPLETED`; result needs valid participants/scores, no duplication. Organizer corrections require reason, audit trail, and recompute downstream bracket or standings **consistently**. If correcting a knockout result would invalidate already-completed descendant matches, block it with a clear explanation until affected descendants are voided through an authorized audited workflow.
- [ ] Scheduling on a gaming station must not conflict with active sessions or incompatible station reservations/matches. External/no-station matches must be explicitly marked as such if allowed.

### 3.4 Maintenance, reports and access

- [ ] Equipment belongs to a station; maintenance tickets reference real equipment and reporting staff. Severity and status are enumerated and validated.
- [ ] Ticket lifecycle `REPORTED → IN_PROGRESS → RESOLVED` (with optional `OUT_OF_SERVICE` state/flag); a resolution requires notes and passing readiness check. Open tickets continue to block impacted equipment/station.
- [ ] Decommissioning preserves historical references; do not physically delete stations that have reservations, sessions, matches or maintenance history.
- [ ] Report aggregates are computed from actual persisted records, with **date filter inclusive start / exclusive end**. Empty set is shown as empty, never invented sample totals.
- [ ] Customer sees only their records and permitted public tournament info. STAFF operates sessions/payments/maintenance. ORGANIZER operates tournaments/matches. MANAGER administers stations, reports and authorized corrections; explicitly document any inherited staff rights.
- [ ] Every sensitive operation authorizes again in the application layer. Database queries for customer views are scoped by identity; users cannot edit someone else's data via crafted IDs.

---

## 4. Database contract

Codex must write numbered, idempotently applied SQL migrations under `src/main/resources/db/migrations/`. At minimum include these tables with meaningful primary keys, foreign keys, NOT NULL constraints, UNIQUE constraints, appropriate indexes, timestamps, and CHECKs:

| Table | Minimum purpose / important fields |
|---|---|
| `users` | user_id, login/email (unique, case-normalized), password_hash, password_salt/parameters, role, active, created_at |
| `customers` | customer_id, user_id unique FK, name, phone unique, created_at |
| `employees` | employee_id, user_id FK, display_name, employee_type, active |
| `gaming_stations` | station_id (unique visible ID), type, specs, hourly_rate_paisa, operational_status, active |
| `equipment` | equipment_id, station_id FK, type, name, condition |
| `reservations` | reservation_id, customer_id FK, station_id FK, start_at, end_at, status, created_at, canceled_at |
| `gaming_sessions` | session_id, customer_id FK, station_id FK, reservation_id nullable FK, started_at, planned_end_at, ended_at, status, charge_id nullable FK |
| `charges` | charge_id, customer_id nullable FK, source_type, source_id, amount_paisa, status, created_at |
| `payments` | payment_id, charge_id FK, recorded_by employee FK, amount_paisa, method, external_reference nullable, idempotency_key unique, created_at, status |
| `receipts` | receipt_id, payment_id unique FK, receipt_number unique, issued_at |
| `membership_plans` | plan_id, name unique, price_paisa, duration_days, discount_percent, active |
| `memberships` | membership_id, customer_id FK, plan_id FK, start_date, expiry_date, status, charge_id FK |
| `gaming_teams` | team_id, name unique normalized, captain_customer_id FK, status, created_at |
| `team_memberships` | team_id/customer_id composite key or unique pair, role, status, joined_at |
| `team_join_requests` | request_id, team_id, customer_id, status, decided_by, decision_at |
| `tournaments` | tournament_id, name, game, format, entrant_type, capacity, entry_fee_paisa, max_roster, start_at, end_at, status, organizer_id |
| `tournament_registrations` | registration_id, tournament_id, customer_id nullable, team_id nullable, status, fee_status, charge_id nullable; **CHECK exactly one entrant** |
| `tournament_matches` | match_id, tournament_id, round_no, slot_no, player_a registration FK, player_b registration FK, station_id nullable FK, scheduled_at, status, winner_registration_id nullable |
| `match_results` | result_id, match_id unique FK, score_a, score_b, winner_registration_id, confirmed_by, confirmed_at, revision_no |
| `maintenance_tickets` | ticket_id, equipment_id FK, reported_by FK, description, severity, status, resolution_notes, opened_at, resolved_at |
| `audit_events` | audit_id, actor_user_id nullable, entity_type, entity_id, operation, before_summary, after_summary, reason, created_at |
| `schema_migrations` | version unique, applied_at, checksum or equivalent |

Additional tables (e.g., `report_exports`, `match_result_revisions`, `tournament_roster_snapshots`, `payment_reversals`) are encouraged **when required to implement the constraints above correctly**. Prefer explicit migration evolution to recreating the DB. Foreign keys must use appropriate RESTRICT/NO ACTION; avoid cascade-delete of financial/history records. Use unique/partial indexes where SQLite permits; `BEGIN IMMEDIATE` or equivalent for atomic overlap/capacity/payment confirmations. Add indexes on reservation `(station_id, start_at, end_at, status)`, session `(station_id,status)`, payments `(charge_id)`, registrations `(tournament_id,status)` and time-filtered reports.

- [ ] `docs/DATABASE.md` includes actual schema, keys, constraints, migration instructions and ER diagram (Mermaid acceptable for developer documentation; do not replace required UML images in final submission).
- [ ] DB startup sets `PRAGMA foreign_keys=ON` **per connection**; sensible busy timeout. Avoid connections kept open indefinitely by screens.
- [ ] All SQL parameterized; no string concatenation of user values into queries.
- [ ] All writes use transaction boundaries matching the complete business operation, rollback on failure and release resources via try-with-resources.
- [ ] Database integration tests prove create/read/update, supported soft-delete/deactivation, constraint handling and restart durability.
- [ ] Source domain types do not depend on persistence classes; each repository interface is represented by a real JDBC adapter.
- [ ] Log useful exceptions without exposing passwords, credentials or unnecessary personal data.

---

## 5. D5 design-to-code class contracts

The implementation should contain recognizable counterparts to the source D5 design, not merely generic `ManagerService` or a god-class. **Extend designs only as necessary to realize D3 behavior.**

| D5 design concept | Implementation requirement |
|---|---|
| `GamingCafe`, `Customer`, `GamingStation`, `Reservation`, `GamingSession`, `Charge`, `Payment`, `Receipt` | Cohesive domain entities/value objects with validated state transitions |
| `MembershipPlan`, `Membership` | Plan fee, status, start/expiry/renew logic |
| `GamingTeam`, `TeamMembership`, `TournamentRegistration`, `Tournament`, `TournamentMatch`, `MatchResult` | Roster, XOR entrant, capacity, tournament/match workflow |
| `Employee` (abstract), `Staff`, `Manager`, `TournamentOrganizer` | Meaningful inheritance/polymorphism **where D5 models it**; do not duplicate authorization truth arbitrarily |
| `Equipment`, `MaintenanceTicket`, `ManagementReport` | Fault lifecycle and persisted reporting metadata/real aggregates |
| `PricingPolicy`, `StandardPricingPolicy` | Strategy (GoF) / Protected Variations; no pricing code in views |
| `PaymentProcessor`, `CashPaymentProcessor` | Strategy or Adapter for verified staff-confirmed payment handling; no fake gateway |
| `CustomerRepository`, `StationRepository`, `ReservationRepository`, `PaymentRepository`, `MembershipRepository`, `TeamRepository`, `TournamentRepository`, `MatchRepository`, `MaintenanceRepository`, `ReportRepository`, `RegistrationRepository` | Ports/interfaces in app/domain; JDBC implementations in infrastructure; add necessary ports for sessions/charges/equipment |
| `AccountController`, `ReservationController`, `SessionController`, `PaymentController`, `MembershipController`, `TeamController`, `RegistrationController`, `TournamentController`, `MatchController`, `StationController`, `MaintenanceController`, `ReportController` | **Application-layer** use-case orchestrators matching D5; keep distinct from JavaFX presentation controllers |
| `ReservationView`, `RegistrationView`, other per-use-case views | JavaFX boundaries with visible outcomes, no business rules or SQL |
| `PasswordHasher`, `ReportExporter` | Replaceable infrastructure ports; real secure hashing and CSV export |

**GRASP mapping:** Controller = each UC coordinator; Information Expert = `Reservation.overlaps`, `GamingStation.isOperational/canHost`, `Charge.remainingBalance`, `Tournament.hasSpace`, `MatchResult.validateScore`; Creator = cohesive aggregate creating its owned record (e.g. café/station, tournament/match); Low Coupling = repository/policy interfaces; High Cohesion = UI vs rules vs DB separated; Protected Variations = pricing/payment/export ports; Pure Fabrication = repository adapters. **GoF**: Strategy for PricingPolicy and PaymentProcessor, optionally Factory Method/Factory for processor construction if demonstrably useful; no gratuitous Singleton/Observer inheritance hierarchy.

- [ ] `docs/DESIGN_TO_CODE.md` maps every D5 class/port/controller/view actually used to its Java file and notes justified renames/additions.
- [ ] The three D5 class model views and twelve D5 design sequence diagrams remain recognizable; mismatches are enumerated and resolved, not ignored.
- [ ] Real polymorphic dispatch is tested (e.g., different pricing/payment strategies), not just claimed from interface declarations.

---

## 6. Implementation checklist — all 12 use cases, original exact names/order

For every UC, use its D3 fully dressed scenario and extensions for behavior, D4 SSD for input/observable response names, D5 design sequence diagram for object interactions. The checkboxes below are **not pre-checked**: each means complete end-to-end behavior as defined in §0.4.

### UC-01 — Create Customer Account (Customer / Gamer)

- [ ] Build **Registration** JavaFX view: full name, email, phone, password/confirmation; required-field and format validation; confirmation before save.
- [ ] `AccountController` validates unique normalized email and phone under DB constraints; stores salted password hash, unique customer ID and audit event; displays result.
- [ ] Duplicate email/phone, malformed input and cancel leave DB unchanged; login using the created account works; another customer cannot see private profile data.
- [ ] Add tests: success, invalid input, duplicate, cancellation, password not stored plaintext.

### UC-02 — Reserve Gaming Station (Customer / Gamer)

- [ ] Build **Station Search + Reservation** view: date, start, duration, PC/console category, available station list, price estimate, confirm, my bookings/cancel.
- [ ] `ReservationController` validates valid interval and customer, searches by interval and operational status, rechecks conflicts atomically, saves reservation ID and confirmation.
- [ ] Block same-station overlap, same-customer conflict, concurrent double booking, inactive/faulty station; allow adjacent reservations; cancellation releases timeslot without changing physical station to IN_USE.
- [ ] Tests include collision at boundary, concurrent confirmation, empty search, canceled slot becomes reusable, cross-screen status.

### UC-03 — Conduct Gaming Session (Staff Member)

- [ ] Build **Session Desk**: reservation lookup or walk-in, station/customer selection, start, active timer/status, request extension, close, optional fault report.
- [ ] `SessionController` checks identity and eligibility, starts one ACTIVE session per station, records actual timestamps, handles extension conflicts, closes and creates **one** session Charge.
- [ ] Start/close controls are staff-only. Walk-in, future reservation conflict, faulty station, duplicate start, duplicate close, and fault-at-close have correct outcomes.
- [ ] Tests verify persisted duration, exact billing, charge association, operational state and the UC-03 extension flows.

### UC-04 — Process Payment (Staff Member)

- [ ] Build **Checkout / Payments**: unpaid and partially paid charges, amount due, membership/fee links, CASH or labeled manually verified external payment, confirm, receipt, history.
- [ ] `PaymentController` validates authorization, amount, method and idempotency; persists payment, updates charge status and receipt atomically; displays remaining balance/change.
- [ ] Partial amount never marks fully paid. Negative/zero/overpayment rules must be explicit: reject nonpositive amounts; reject overpayment rather than silently creating credit (for CASH, show change as an informational amount only if staff explicitly entered tendered amount).
- [ ] Failed verification, cancel, duplicate click and DB fault do not create a successful record. Payment records remain auditable.
- [ ] Tests verify successful settlement, partial settlement, idempotency, unauthorized attempt and persisted receipt.

### UC-05 — Subscribe or Renew Membership (Customer / Gamer)

- [ ] Build **Membership** view: real plans, benefits/discount if configured, current status/expiry, subscribe/renew confirmation and payment status.
- [ ] `MembershipController` creates associated charge, waits for required payment, then activates or extends membership using defined expiry rules; show pending state honestly.
- [ ] Expired/disabled plan, early/invalid renewal under configured rules and failed/unpaid charge do not activate benefits.
- [ ] Tests verify date arithmetic, paid-only activation, renewal extension, discount consistency and persistence.

### UC-06 — Create or Join Gaming Team (Customer / Gamer)

- [ ] Build **Teams** view: create unique team, view roster, search/join by team ID, captain approvals/invitations, leave/remove where eligible.
- [ ] `TeamController` atomically creates team+captain roster row; handles pending approval, permissions and roster validation.
- [ ] Reject duplicate names, duplicate membership, unauthorized roster changes and changes to a roster frozen for a confirmed tournament.
- [ ] Tests cover creation, join request, captain decision, rejection, and membership persisted after app restart.

### UC-07 — Register for Tournament (Customer / Gamer or Team Captain)

- [ ] Build **Tournament Discovery / Registration** view: open tournaments, entrant type, eligibility, capacity, fees, participant/roster selection, confirmation.
- [ ] `RegistrationController` verifies OPEN status, max capacity, duplicate registration, solo/team eligibility and captain authority **again inside transaction**.
- [ ] Free registrations confirm immediately. Paid registrations remain pending until their charge is paid, then are confirmed atomically if still eligible/capacity available; on expiry/capacity conflict, do not collect undisclosed fees or falsely claim confirmation—document and implement a consistent cancel/refund-required state.
- [ ] Tests cover full capacity, duplicate entrant, invalid roster, unauthorized captain, close-before-submit race, unpaid entry.

### UC-08 — Manage Tournament (Tournament Organizer)

- [ ] Build **Tournament Administration**: create draft with name/game/format/entrant type/capacity/dates/fee; edit draft; open/close registration; cancel with reason and audit log.
- [ ] `TournamentController` enforces required fields, chronological dates, state transitions and protected config after registration; published tournament is shown to eligible customers.
- [ ] Unauthorized action, invalid dates/capacity, incomplete draft and disallowed transitions show errors and do not mutate data.
- [ ] Tests validate full status machine, actual DB state, registrant retention/cancellation flags, and audit events.

### UC-09 — Conduct Tournament Matches (Tournament Organizer)

- [ ] Build **Matches / Bracket or Standings** screen: tournament participants, generated match assignments, schedule and station conflicts, start, result entry, confirm, progression, audited correction.
- [ ] `MatchController` generates consistent knockout brackets (including byes) and round-robin fixtures, validates scores/winner, advances or recomputes state, finishes tournament when all necessary matches complete.
- [ ] Block result for unscheduled/unstarted match, duplicate result, invalid scores, unauthorised correction, conflicting station assignment, and corrections that would break already completed descendants.
- [ ] Tests: 3/4/5 entrant knockout, byes, 3/4 entrant round-robin, standings, corrections, completion, restart, audit history.

### UC-10 — Manage Gaming Stations (Manager / Administrator)

- [ ] Build **Station Inventory**: create station with unique ID/category/specs/rate; edit; view status; deactivate/decommission with required safeguards.
- [ ] `StationController` validates rate/status and reconciles active sessions/future bookings; persists updates and makes them immediately visible to reservations/sessions.
- [ ] Reject duplicate IDs and destructive changes that orphan current/future assignments; retain history via deactivation instead of deleting linked records.
- [ ] Tests: create/update, duplicate ID, inactive not reservable, safe decommission, historical relationships.

### UC-11 — Handle Equipment Maintenance (Staff Member)

- [ ] Build **Maintenance**: select station/equipment, report fault/severity/description, open ticket, progress updates, affected bookings, readiness check, resolve/reopen view.
- [ ] `MaintenanceController` creates real tickets, marks impacted equipment/station unavailable, blocks allocation, retains notes/history, restores service only when readiness verified and no other open blocking ticket remains.
- [ ] Duplicate/open fault warnings, failed readiness check and affected future bookings handled as described in D3; only authorized user closes ticket.
- [ ] Tests: fault blocks bookings, multiple open tickets, resolve restores availability conditionally, permissions, audit/history.

### UC-12 — Generate Management Reports (Manager / Administrator)

- [ ] Build **Reports**: date filters and categories for revenue, station utilization, bookings, memberships, maintenance, tournament participation/results; table/summary/chart only when based on real queries.
- [ ] `ReportController`/repositories compute consistent SQL-backed aggregates; CSV export uses proper escaping and user-chosen location; printed/PDF export only if truly implemented and tested.
- [ ] Invalid date range, empty data and export errors produce clear results without altering source data; financial/customer reports are manager-restricted.
- [ ] Tests compare report totals against seeded real transactions, including partial payments, date boundaries and empty periods.

---

## 7. UI screens, navigation and usability

**Required baseline navigation:** Login / first-time setup → role-specific dashboard → Customers/Registration → Stations → Reservations → Session Desk → Checkout → Memberships → Teams → Tournaments → Matches → Maintenance → Reports → Account/Logout. Hide unauthorized navigation *and* enforce application-layer security.

- [ ] Each UC is reachable through visible navigation or an explicit documented action (no secret developer-only path).
- [ ] Role-based sidebar/dashboard shows relevant actions and a meaningful overview derived from DB.
- [ ] UI actions map to D4 input events and D5 application controllers; success dialog includes ID/status; failure dialog says what to correct.
- [ ] Validate before submission but always revalidate business side. Disabled/invalid controls must not be the only security control.
- [ ] All tables refresh after modifications and changing screens; no stale reservation/station/tournament state.
- [ ] Forms show required markers, field-level errors, cancel/back controls, confirmation for irreversible changes, clear empty and loading states.
- [ ] Long-running DB/report work runs off the JavaFX Application Thread with safe UI updates; short actions remain responsive. Thread access/transaction context is safe.
- [ ] Layout works at a normal laptop resolution (at least 1366×768) without clipped critical buttons; keyboard tab navigation and reasonable contrasts.
- [ ] Logout clears active session context; switching users never reveals prior user's private data.
- [ ] Consistent naming/units/timezones/status wording across screens; no placeholder buttons, "coming soon" as if complete, or static fake dashboard charts.
- [ ] Optional visual polish occurs **after** all twelve UCs function. No UI styling choice may break business workflows.

---

## 8. Execution phases — do this in order, verify each gate

### Phase 0 — Audit and baseline

- [ ] Confirm empty greenfield workspace and create project from this specification; write `SOURCE_AUDIT.md`, `IMPLEMENTATION_AUDIT.md` and `DECISIONS.md`.
- [ ] Confirm twelve UC IDs/names match sources and create an initial traceability table with **all rows**, even if implementation is absent.
- [ ] Fresh Maven+JavaFX app launches with an obvious initial shell; until each use case is implemented, its navigation explicitly shows **not complete**, not fake data.

### Phase 1 — Boot, authentication, persistence, three layers

- [ ] Maven wrapper / dependencies / DB migrations / app boot + first-manager setup.
- [ ] Role-based login + password hashing; production app uses SQLite repositories, never in-memory fakes.
- [ ] Unit/integration test fixtures and first passing database transaction test.
- [ ] Package architecture obeys §2 imports and dependencies.

### Phase 2 — Café core vertical slices

- [ ] UC-10 Stations (prerequisite) → UC-01 Accounts → UC-02 Reservations → UC-03 Sessions → UC-04 Payments.
- [ ] All relevant UI-to-DB workflows and negative tests pass.
- [ ] Demonstration: reserve, start, end, see charge, pay, view receipt, restart and re-open history.

### Phase 3 — Memberships, equipment and teams

- [ ] UC-05 Memberships → UC-11 Maintenance → UC-06 Teams with appropriate permissions/tests.
- [ ] Membership does not activate before payment; maintenance blocks booking; captain approval controls roster.

### Phase 4 — Esports end-to-end

- [ ] UC-08 Tournament management → UC-07 Registration → UC-09 Match scheduling/results/progression.
- [ ] Demonstration with solo and team entrants, full registration status, fee handling, knockout/round robin, validated results.

### Phase 5 — Reports and closeout

- [ ] UC-12 real reports and export.
- [ ] Full integration tests, error handling, restarts, DB migration checks, UI interaction review.
- [ ] Documentation/traceability/rubric evidence/demo script complete; both members rehearse their owned flows.

**At every phase gate:** run `./mvnw clean verify` (or Windows equivalent), run the actual UI, exercise at least one success and one failure scenario, log the result in `docs/TEST_REPORT.md`. **Do not move a checkbox to done if only source code exists but the behavior has not been executed or proven.**

---

## 9. Minimum automated test matrix

Every test must assert a concrete state, return value, exception or persisted DB row. Unit tests must use fixed `Clock`/fixture dates to avoid timing flakiness. Integration tests use a clean temporary SQLite DB for each suite/test and must not touch the user's real DB.

- [ ] T01 — Account registration valid, duplicate email and phone, malformed fields, salted password hash.
- [ ] T02 — Authentication good/wrong password, inactive account, role permissions, cross-user access denied.
- [ ] T03 — Reservation overlap `[10:00,11:00)` vs `[10:30,11:30)` rejected; `[11:00,12:00)` accepted; cancelled reservation releases interval.
- [ ] T04 — Simultaneous same-station booking: at most one succeeds; consistent DB after commit/rollback.
- [ ] T05 — Station future reservation does not set physical state IN_USE.
- [ ] T06 — Session walk-in and reservation, double-start blocked, extension conflict blocked, final charge exactly once.
- [ ] T07 — Exact PKR rounding with 1/30/60/61 minutes and configured discount; no floating-point drift.
- [ ] T08 — Payment full/partial/duplicate request and receipt; unauthorized payment denied; rollback on injected failure.
- [ ] T09 — Membership inactive before payment, activated only after payment, expiry/renewal correct.
- [ ] T10 — Team unique name, captain auto-membership, join approval and restricted roster changes.
- [ ] T11 — Tournament registration capacity, closed state, duplicate entrant, team captain, fee-required pending status.
- [ ] T12 — Tournament status transitions, invalid status rejected, cancellation audit.
- [ ] T13 — Knockout bracket including byes, result-driven advancement, winner and completion.
- [ ] T14 — Round-robin all-pairs schedule, standings and deterministic tie-break.
- [ ] T15 — Unauthorized/duplicate/invalid match result, audited correction safeguards.
- [ ] T16 — Maintenance outage prevents booking; resolve only restores operational station when appropriate; affected bookings visible.
- [ ] T17 — Station deactivation preserves historical transactions; unique ID enforcement.
- [ ] T18 — Reports reconcile payment revenue and utilization against actual fixtures, no-data case, date filter boundaries.
- [ ] T19 — Data persists across closed/reopened connections and an application bootstrap/reload.
- [ ] T20 — Repository integration, SQL constraint failures produce recoverable user-facing errors, all three tiers wired correctly.
- [ ] T21 — Role-specific UI smoke tests or a documented **manual UI acceptance script** for all 12 screens, with run-date/platform evidence; do not claim JavaFX UI testing just because services pass.
- [ ] T22 — Regression: `./mvnw clean verify` on fresh checkout and manual app launch with freshly initialized DB.

---

## 10. Design and course evidence, not merely code

Maintain `docs/TRACEABILITY.md` with **exactly 12 top-level rows**, each:

| UC | Actor | D3 main + extensions | D4 SSD event(s) | D5 SD boundary/controller/entities/repositories | JavaFX screen | Java service/controller | SQL tables | Test IDs | Owner | Status |
|---|---|---|---|---|---|---|---|---|---|---|
| UC-01 … UC-12 | … | … | … | … | … | … | … | … | … | not started / partial / verified |

Also include a reverse class-to-design map in `docs/DESIGN_TO_CODE.md` and a list of any source/implementation divergences with justification and impact. **No fabricated names, test IDs, or screenshots.**

### Member ownership and individual defense

The team's historical roles remain Ali = team manager/designer/developer; Abdullah = analyst/developer/QA manager. **Implementation ownership allocation below is a practical proposed split**, not a claim that D3 assigned these specific UCs. Record the final actual contributions with commit hashes or code references in `docs/CONTRIBUTIONS.md`.

| Member | Primary implementation owner for demonstration/review |
|---|---|
| **Ali (`i250685`)** | UC-02 Reservations, UC-03 Sessions, UC-04 Payments, UC-05 Memberships, UC-10 Stations, UC-12 Reports; architecture/integration review |
| **Abdullah (`i250782`)** | UC-01 Accounts, UC-06 Teams, UC-07 Registration, UC-08 Tournaments, UC-09 Matches, UC-11 Maintenance; integration/QA review |

This is ownership for accountability, **not** permission to leave cross-feature dependencies disconnected. Both members must understand how UI calls controllers, where information-expert rules live, and how SQL transactions preserve invariants. If the actual team work differs, update `CONTRIBUTIONS.md` truthfully.

- [ ] `docs/USER_GUIDE.md`: install, bootstrap, permissions, workflows, common errors.
- [ ] `docs/DEMO_SCRIPT.md`: 10-minute walkthrough with exact clicks/data and a full resettable demo scenario; include failed overlap, paid membership, team/tournament match, maintenance and reports.
- [ ] `docs/TEST_REPORT.md`: commands, run environment, pass/fail counts, failures, actual UI test results and screenshots **captured from running app**, not fabricated renderings.
- [ ] `docs/RUBRIC_EVIDENCE.md`: show each category and actual evidence.
- [ ] `docs/CONTRIBUTIONS.md`: accurate individual responsibilities and contributions.
- [ ] Final report follows instructor's **actual template if provided**. Preserve readable model images and correct UML captions. If no template exists in workspace, draft a `docs/FINAL_REPORT_DRAFT.md` with placeholders explicitly labeled and list missing template in `SOURCE_AUDIT.md`; do not pretend template compliance.
- [ ] All diagrams and docs describe **implemented** functionality honestly; if implementations differ from source diagrams, document exact mapping and instructor-facing reconciliation.

---

## 11. Blocking failures and prohibited shortcuts

**Never:**

1. Claim "all twelve UCs done" if one is only a button, static table, in-memory mock, service with no UI, or untested.
2. Put business rules or JDBC SQL directly in JavaFX controllers.
3. Return fake success for payment, registration, match completion, export, or writes.
4. Ignore D3 extensions because they are "edge cases".
5. Invalidate an already stored reservation or match silently; hide the affected state instead of reporting it.
6. Store plain-text credentials; use `double` for currency; concatenate SQL strings with user input.
7. Physically delete financial audit records or cascade-delete historical domain records.
8. Add unnecessary frameworks, microservices, online accounts, or network dependencies.
9. Rewrite source UML documents secretly to make generated code look compliant.
10. Report unexecuted tests as passing, invent screenshots, or edit checkboxes based on intention.
11. Edit unrelated projects, create public repositories, push remote branches or make commits without authorization.

**When a command fails:** reproduce, read stack trace, identify root cause, fix, rerun the previously failing command **and** applicable regression tests. Record unresolved failures in `docs/TEST_REPORT.md` with exact commands and error excerpts.

---

## 12. Codex start-to-finish instructions — copyable mission

> You are implementing **GameHub**, a real JavaFX + Java 21 + SQLite desktop application for a two-person SDA semester project. `implementation.md` is your live plan and acceptance contract. First audit the repo and the five reference documents if available; use the rubric, D3's exact twelve scenarios/extensions, D4's twelve SSDs and D5's class/sequence designs. Then autonomously implement **all 12** use cases across JavaFX UI, business logic, repository interfaces, SQLite/JDBC persistence, security, tests and user documentation. Follow the architecture/import rules, domain invariants, and test gates in this file. Maintain checkboxes and evidence documents accurately. Use clear final working commands (`./mvnw clean verify`, `./mvnw javafx:run`) and run them. Fix failures. Do not stop after a plan, skeleton, or attractive dashboard. Do not ask about routine choices; defaults are already decided. Do not fake transactions or claim functionality you did not prove. Never push without explicit approval. When complete, report every UC's verified status, test results, actual launch result, deviations from D3/D4/D5, remaining risks, and demo instructions.

### Required Codex final handoff format

```text
GAMEHUB IMPLEMENTATION REPORT
1. Files changed/created: [real paths]
2. Build: [exact command and observed result]
3. Launch: [exact command and observed result; UI was/was not manually run]
4. Database: [path policy, migrations, tested persistence]
5. UC-01..UC-12: [VERIFIED | PARTIAL | NOT IMPLEMENTED] + evidence per UC
6. Tests: [actual count, failing tests, skipped tests]
7. Design mapping: [traceability doc path; known deviations]
8. Screens: [real tested screens and screenshots if available]
9. Security and finance constraints: [what was actually tested]
10. Known limitations/blockers: [explicit and concrete]
11. Demo account/bootstrap instructions: [without real secrets]
12. Next action: [only if unresolved, with files/commands]
```

---

## 13. Final acceptance gate — the only permission to say "finished"

- [ ] **All twelve UC checklists in §6 are checked with test and UI evidence.**
- [ ] **All 9 rubric criteria in §1 have actual evidence, not claims.**
- [ ] **Every integration test and build command is green** or documented failing with no false completion claim.
- [ ] **JavaFX app launches and remains usable** through a real end-to-end café flow and esports flow.
- [ ] **Fresh/restarted DB** preserves users, bookings, session/charges, payments, tournaments, matches, maintenance history.
- [ ] **Authorization/validation** tested for invalid inputs and each role.
- [ ] **Zero production stubs/fake successes**, zero broken navigation paths, zero crashing happy paths.
- [ ] **D3 ↔ D4 ↔ D5 ↔ Code ↔ DB ↔ Test** mapping is documented for every UC.
- [ ] **Actual documentation and both members' individual demo evidence** are ready.
- [ ] `docs/TEST_REPORT.md` records verified operating system, Java version, database path policy, test command output and application launch conditions.

**STOP CONDITION:** Only when every item in this section has verified evidence may Codex mark the product **READY FOR DEMO**. Otherwise explicitly report **PARTIALLY COMPLETE**, preserving all checked/unchecked status and specifying the next concrete action.

---

*Prepared as a build specification against GameHub's documented SDA artifacts and the uploaded final rubric. The implementation choices above resolve earlier open decisions; they are engineering decisions for this build, not statements that the course instructor separately mandated a particular Java, database, tournament, or payment implementation.*




---

# SOURCE-RECONCILED ANNEXES (REQUIRED READING)
These annexes make this checklist **standalone for Codex**. They preserve the uploaded requirements and the team’s submitted artifacts, including details lost when diagrams are reduced to a simple checklist. Core `§0–13` and these annexes **must both be satisfied**. The text extracted from diagram pages is included to preserve system events and operation names; use the actual UML images if they are available in your repo for visual confirmation. Do not treat line-wrap or drawing coordinates as method parameters.
## Annex A. Deliverable 2: original 2-page project overview, preserved source

```text
1. Project Overview

GameHub is a desktop-based Gaming Cafe and Esports Tournament Management System designed to
automate the major operational activities of a gaming cafe. The system will provide a centralized platform
for managing customers, gaming stations, reservations, active gaming sessions, payments,
memberships, tournaments, staff activities, and equipment maintenance.

The application will allow staff members to monitor the availability and status of gaming stations, register
customers, reserve stations, initiate and terminate gaming sessions, calculate charges, and maintain
transaction records. It will also provide dedicated functionality for organizing esports tournaments,
including player/team registration, match scheduling, result recording, and tournament progression.

The system will follow a structured object-oriented design and a three-tier architecture consisting of the
presentation layer, business logic layer, and data layer.

2. Motivation

Gaming cafes often handle several activities simultaneously, including station allocation, customer
reservations, billing, memberships, tournaments, and hardware maintenance. Managing these activities
manually or through disconnected records can lead to booking conflicts, incorrect billing, lost customer
information, and difficulty monitoring available resources.

Esports events introduce additional complexity because organizers must manage participants, teams,
matches, schedules, and results alongside normal cafe operations.

GameHub aims to combine these operations into a single organized system. By maintaining consistent
records and automating repetitive tasks, the system can reduce administrative effort, improve resource
utilization, and provide staff with a clearer view of ongoing cafe activities.

3. Objectives

The primary objectives of GameHub are to:

       Develop a centralized system for managing gaming cafe operations.

       Maintain customer profiles, memberships, and visit information.

      Manage gaming stations and their current availability or operational status.

       Support station reservations and prevent conflicting bookings.

      Record gaming sessions and automatically calculate session charges.

       Maintain payment and transaction records.

       Support esports tournament creation, participant/team registration, match scheduling, and result
    management.

      Record maintenance issues associated with gaming equipment or stations.

       Provide staff with useful operational reports and historical records.

       Apply object-oriented analysis, design principles, UML modeling, and three-tier software
    architecture during development.

4. Expected Outcomes

The completed system is expected to provide a functional desktop application through which authorized
staff can efficiently manage gaming cafe operations.

Users will be able to view available and occupied stations, create reservations, manage customer
sessions, calculate payments, maintain memberships, and access relevant historical information.
Tournament administrators will be able to create tournaments, register participating players or teams,
schedule matches, record results, and track tournament progress.

The system is also expected to maintain persistent and structured data through a relational database. Its
modular architecture should make individual components easier to understand, maintain, and extend.

5. Project Scope

The project will cover customer registration and management, gaming station management, reservations,
session tracking, billing, membership management, esports tournament administration, maintenance
records, staff operations, and basic reporting.

Gaming stations will have states such as available, reserved, occupied, or under maintenance.
Reservations will associate customers with stations and time periods. Gaming sessions will record usage
duration and calculate charges according to predefined rates.

The tournament module will support tournament creation, player or team registration, match scheduling,
and recording match results. The maintenance module will allow staff to report and track faults associated
with gaming stations or equipment.

The project will focus on the management software itself. Features requiring external hardware
integration, online game-server integration, real payment gateways, live game statistics, or large-scale
online multiplayer infrastructure will remain outside the scope of the current implementation.

6. Tools and Technologies

The application will be developed using Java as the primary programming language. The graphical user
interface will be implemented using Java Swing or JavaFX.

A relational database using SQL/Oracle will be used for persistent storage of customers, stations,
reservations, sessions, payments, tournaments, teams, matches, maintenance records, and related
information.

The application will follow a three-tier architecture consisting of the User Interface layer, Business Logic
layer, and Data Access layer. Object-oriented concepts including abstraction, encapsulation, inheritance,
polymorphism, associations, and other relevant class relationships will be applied where appropriate.

UML diagrams will be used during analysis and design, we will be using draw.io for that.
```
## Annex B. Final grading rubric (all criteria)

The **100-point** rubric requires: fully operational JavaFX UI (10), correct business logic (15), OOP encapsulation/inheritance/abstraction/polymorphism (15), properly validated/exception-handled persistent CRUD DB (10), independent and integrated UI/BL/DB packages (10), all assigned use cases (10), full design/code mapping (10), GRASP + applicable GoF (10), and complete template-aligned documentation with legible model images (10). All use cases must execute, login is not a main use case, and marking is individual. **The application must RUN.**
## Annex C. Deliverable 3: 12 fully dressed use cases — complete behavior and all extensions

Do not reduce a use case to its success scenario. This annex reproduces D3 preconditions, postconditions, steps, every numbered extension, special requirements, and technology/data variations. For each extension, create tests and a corresponding UI outcome.

### UC-01 — Create Customer Account

```text
UC-01 - Create Customer Account
 Use Case Name                                 Create Customer Account
    GameHub - Gaming Cafe & Esports Tournament Scope                                        Management System
 Level                                           User-Goal
 Primary Actor                              Customer / Gamer

Stakeholders and Interests
Customer / Gamer: Wants a quick, accurate registration process and secure access to GameHub services.
Staff Member: Needs reliable customer information for reservations, sessions, payments, and support.
Manager / Administrator: Wants unique, valid customer records and controlled access to the system.

Preconditions
 • The GameHub system is operational.
 • The customer does not already have an active account using the same unique email/phone identifier.

Postconditions
 • A new customer account is stored with a unique customer ID.
 • The account is available for authentication and authorized GameHub services.
 • The registration activity is recorded for audit purposes.

Main Success Scenario
 Step                         Actor Action                 System Response
    Customer requests to create a       System displays the registration 1
    GameHub account.                  form.
    Customer enters required personal                                                                 System validates required fields and
 2                             and contact information and                                                                      data formats.
    chooses credentials.
    System checks whether the                                Customer submits the registration
 3                                                                  email/phone identifier is already                                       request.
    registered.
    System creates the customer                                Customer confirms the provided
 4                                                                   account and assigns a unique                                     information.
    customer ID.
    System displays successful
    registration confirmation and 5                                                         -
    makes the account available for
    login.

    Ali Irtiza Ahmed | S.M.A. Khalil | Page 4



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
Extensions
 • 2a. Required information is missing or invalid: the system identifies the invalid fields and asks the customer to
   correct them; the flow resumes at Step 2.
 • 3a. An account already exists for the supplied identifier: the system rejects duplicate registration and informs
   the customer.
 • 4a. Customer cancels before confirmation: the system discards unsaved registration data and ends the use case.

Special Requirements
 • Passwords/credentials must not be displayed or stored in plain text.
 • The system must validate mandatory fields before account creation.
 • Personal information must only be accessible to authorized users.

Technology and Data Variations List
 • The customer identifier may be an email address or phone number, according to configured policy.
 • Registration may be completed directly by the customer or assisted by an authorized staff member.
 • Optional profile data may be added immediately or later.





    Ali Irtiza Ahmed | S.M.A. Khalil | Page 5



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
```

- [ ] `UC-01`: success scenario, all extensions and special requirements mapped to real screen actions, domain rules, DB writes and passing tests.

### UC-02 — Reserve Gaming Station

```text
UC-02 - Reserve Gaming Station
 Use Case Name                                Reserve Gaming Station
    GameHub - Gaming Cafe & Esports Tournament Scope                                        Management System
 Level                                           User-Goal
 Primary Actor                              Customer / Gamer

Stakeholders and Interests
Customer / Gamer: Wants an available station at the desired date and time without booking conflicts.
Staff Member: Needs accurate reservation information for customer check-in and station allocation.
Manager / Administrator: Wants efficient station utilization and no overlapping reservations.

Preconditions
 • The customer has an active GameHub account.
 • Gaming stations are registered in the system and have current availability/status information.

Postconditions
 • A valid reservation is stored with a unique reservation ID.
 • The selected station and time interval are unavailable for conflicting reservations.
 • Reservation details are available to the customer and authorized staff.

Main Success Scenario
 Step                         Actor Action                 System Response
    Customer requests to reserve a      System displays reservation criteria 1
    gaming station.                  and available booking options.
    Customer enters the desired date,                                                                 System searches for matching
 2                                        start time, duration, and station                                                                            available stations.
    type.
    Customer selects an available       System displays station details and 3
    station.                            estimated charges.
    System rechecks availability and
 4                               Customer confirms the reservation.   verifies that no conflicting
    reservation exists.
    System creates the reservation and 5                                                         -
    assigns a unique reservation ID.
    System updates station availability 6                                                         -
    for the reserved interval.
    System displays reservation 7                                                         -
    confirmation and booking details.

Extensions
 • 2a. No station matches the requested criteria: the system informs the customer and allows different
   date/time/station criteria; resume at Step 2.
 • 4a. The selected station became unavailable before confirmation: the system rejects that selection, refreshes
   availability, and resumes at Step 3.

    Ali Irtiza Ahmed | S.M.A. Khalil | Page 6



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
 • 4b. The customer has a conflicting reservation: the system shows the conflict and allows the customer to choose
   another time.
 • 7a. The customer later requests cancellation: the system checks cancellation eligibility, cancels the reservation,
  and releases the reserved interval.

Special Requirements
 • The system must prevent overlapping reservations for the same station.
 • Availability should update immediately after a successful booking or cancellation.
 • Only authorized users may create, alter, or cancel a reservation on behalf of another customer.

Technology and Data Variations List
 • Station type may be PC or console.
 • Booking duration may use predefined intervals or configured custom durations.
 • Pricing may vary by station type, duration, peak/off-peak period, and membership status.





    Ali Irtiza Ahmed | S.M.A. Khalil | Page 7



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
```

- [ ] `UC-02`: success scenario, all extensions and special requirements mapped to real screen actions, domain rules, DB writes and passing tests.

### UC-03 — Conduct Gaming Session

```text
UC-03 - Conduct Gaming Session
 Use Case Name                              Conduct Gaming Session
    GameHub - Gaming Cafe & Esports Tournament Scope                                        Management System
 Level                                           User-Goal
 Primary Actor                                        Staff Member

Stakeholders and Interests
Customer / Gamer: Wants the correct station, accurate session duration, and fair billing.
Staff Member: Wants a simple check-in/start/end workflow and current station status.
Manager / Administrator: Wants accurate utilization and session records for operations and reporting.

Preconditions
 • The customer is identifiable in the system or has a valid reservation.
 • The assigned gaming station is operational and not occupied by another active session.

Postconditions
 • The gaming session is recorded with actual start/end time and duration.
 • The station status is updated appropriately after the session.
 • Any payable session charge is recorded for payment.

Main Success Scenario
 Step                         Actor Action                 System Response
    Staff identifies the customer and     System displays customer,
 1                                        selects the reservation or walk-in     reservation, and station
    request.                             information.
    Staff confirms the station           System verifies that the station is 2
    assignment.                         operational and available.
    System records the start time and 3                                         Staff starts the session.
    marks the station as In Use.
    System maintains the active session 4                               Customer uses the assigned station.
    record and elapsed time.
    System records the end time and                                           Staff ends the session when the
 5                                                                            calculates actual usage duration and                                 customer finishes.
    applicable charges.
    System closes the session and
 6                                         Staff confirms session completion.    changes the station to Available,
    unless maintenance is required.

Extensions
 • 1a. Customer has no reservation: staff may create a walk-in session if a suitable station is available.
 • 2a. Assigned station is unavailable or faulty: the system shows alternative available stations; staff selects
   another station and resumes at Step 2.
 • 4a. Customer requests a session extension: the system checks later reservations; if no conflict exists, it extends
   the planned end time.


    Ali Irtiza Ahmed | S.M.A. Khalil | Page 8



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
 • 6a. A fault is reported at session end: the system marks the station unavailable and initiates the equipment-
  maintenance workflow.

Special Requirements
 • Session timestamps must be recorded consistently and accurately.
 • A station must not have more than one active session at the same time.
 • Only authorized staff may start or close a session.

Technology and Data Variations List
 • Session may originate from a reservation or a walk-in request.
 • Session may occur on a PC or console station.
 • Membership rules may affect allowed duration, discounts, or charges.





    Ali Irtiza Ahmed | S.M.A. Khalil | Page 9



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
```

- [ ] `UC-03`: success scenario, all extensions and special requirements mapped to real screen actions, domain rules, DB writes and passing tests.

### UC-04 — Process Payment

```text
UC-04 - Process Payment
 Use Case Name                                 Process Payment
    GameHub - Gaming Cafe & Esports Tournament Scope                                        Management System
 Level                                           User-Goal
 Primary Actor                                        Staff Member

Stakeholders and Interests
Customer / Gamer: Wants the correct amount, recognized discounts, and proof of payment.
Staff Member: Needs an accurate and fast payment-recording workflow.
Manager / Administrator: Wants complete revenue records and traceable transactions.

Preconditions
 • A payable transaction exists, such as a gaming session, membership, tournament fee, or other authorized
   charge.
 • The amount due has been calculated by the system or entered by an authorized user.

Postconditions
 • Successful payment is stored with a unique payment/receipt reference.
 • The related payable item is marked paid or updated with the remaining balance.
 • A receipt or confirmation is made available to the customer.

Main Success Scenario
 Step                         Actor Action                 System Response
    System displays the amount due,                                           Staff selects the customer
 1                                                                         applicable discounts, and available                                      transaction to be paid.
    payment methods.
    Staff selects the payment method    System validates the payment
 2                             and enters required payment        information and calculates any
    details.                             balance/change.
    System records the payment with
 3                                         Staff confirms receipt of payment.    date, amount, method, and
    reference.
    System updates the related
 4                                                         -                                    transaction status to Paid when the
    full amount is settled.
    System generates and displays a 5                                                         -
    receipt/confirmation.

Extensions
 • 2a. Entered amount is insufficient: the system shows the remaining balance and does not mark the transaction
   fully paid.
 • 2b. A non-cash payment is declined or cannot be verified: the system reports failure and allows another
  payment method.
 • 3a. Staff cancels before confirmation: no payment record is created.


    Ali Irtiza Ahmed | S.M.A. Khalil | Page 10



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
Special Requirements
 • Payment records must be auditable and must not be silently deleted or overwritten.
 • Only authorized staff may confirm a payment.
 • Financial totals must use consistent currency and rounding rules.

Technology and Data Variations List
 • Payment method may be cash, card, or another configured digital/manual method.
 • A transaction may be fully paid or, if project policy permits, partially paid.
 • Discounts may depend on membership, promotion, or tournament policy.





    Ali Irtiza Ahmed | S.M.A. Khalil | Page 11



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
```

- [ ] `UC-04`: success scenario, all extensions and special requirements mapped to real screen actions, domain rules, DB writes and passing tests.

### UC-05 — Subscribe or Renew Membership

```text
UC-05 - Subscribe or Renew Membership
 Use Case Name                                 Subscribe or Renew Membership
    GameHub - Gaming Cafe & Esports Tournament Scope                                        Management System
 Level                                           User-Goal
 Primary Actor                              Customer / Gamer

Stakeholders and Interests
Customer / Gamer: Wants to purchase or renew the appropriate membership and receive its benefits.
Staff Member: Needs clear membership status when serving the customer.
Manager / Administrator: Wants correct plan rules, revenue, expiration dates, and membership statistics.

Preconditions
 • The customer has an active GameHub account.
 • At least one membership plan is configured and available.

Postconditions
 • After successful completion, the selected membership is active with correct start and expiry dates.
 • The membership transaction and applicable payment status are recorded.

Main Success Scenario
 Step                         Actor Action                 System Response
    Customer requests membership     System displays available plans, 1
    plans.                                  prices, duration, and benefits.
    System displays the selected plan
 2                               Customer selects a plan.               details and checks current
    membership status.
    Customer confirms subscription or   System calculates the applicable fee 3
    renewal.                       and effective membership dates.
    Customer completes the required    System verifies that the required fee 4
    payment process.                  has been successfully recorded.
    System activates or extends the
 5                                                         -                              membership and stores the new
    expiry date.
    System displays membership 6                                                         -
    confirmation and updated status.

Extensions
 • 2a. Selected plan is no longer available: the system removes it from the available options and asks the customer
   to choose another plan.
 • 3a. Renewal is requested too early or violates configured plan rules: the system informs the customer and
   blocks invalid renewal.
 • 4a. Payment is unsuccessful: the membership remains unchanged and the customer may retry or choose
   another method.



    Ali Irtiza Ahmed | S.M.A. Khalil | Page 12



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
Special Requirements
 • Membership benefits and expiry calculations must follow configured plan rules.
 • The system must not activate a paid plan until the required payment condition is satisfied.
 • Membership status must be visible to authorized operational staff.

Technology and Data Variations List
 • Plans may differ by duration, price, included hours, discounts, or benefits.
 • The request may be a new subscription or renewal of an existing membership.
 • Promotional discounts may apply when configured.





    Ali Irtiza Ahmed | S.M.A. Khalil | Page 13



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
```

- [ ] `UC-05`: success scenario, all extensions and special requirements mapped to real screen actions, domain rules, DB writes and passing tests.

### UC-06 — Create or Join Gaming Team

```text
UC-06 - Create or Join Gaming Team
 Use Case Name                                 Create or Join Gaming Team
    GameHub - Gaming Cafe & Esports Tournament Scope                                        Management System
 Level                                           User-Goal
 Primary Actor                              Customer / Gamer

Stakeholders and Interests
Customer / Gamer: Wants to participate in team-based esports activities with an accurate roster.
Team Captain: Wants control over team identity, invitations, and roster membership.
Tournament Organizer: Needs valid team rosters for team-based tournament registration.

Preconditions
 • The customer has an active GameHub account.
 • The customer is eligible to participate in team features.

Postconditions
 • A new gaming team is created with the customer as captain, or the customer is added to an existing team
  through an authorized join process.
 • The resulting team roster is stored and available for tournament eligibility checks.

Main Success Scenario
 Step                         Actor Action                 System Response
    Customer opens the team           System displays options to create a 1
    participation feature.             team or join an existing team.
    Customer chooses Create Team and  System validates the team name and 2
    enters a unique team name.         checks that it is not already in use.
    System creates the team and assigns 3                               Customer confirms team creation.
    the customer as team captain.
    System records invitations or makes                                Customer selects members to invite
 4                                                                       the team available according to                                    or shares the team/join identifier.
    configured join rules.
    System displays the current team 5                                                         -
    roster and team status.

Extensions
 • 2a. Customer chooses Join Team instead: the system requests a team/join identifier or displays eligible teams;
   the customer selects a team; the system validates join rules and adds the customer after required approval.
 • 2b. Team name already exists: the system asks the customer to choose another unique name.
 • 4a. Invited member is already in an incompatible team or violates roster rules: the system rejects that member
  and explains the reason.

Special Requirements
 • Team names must be unique according to configured naming rules.
 • Roster changes must preserve tournament roster constraints when a team is already registered.
 • Only authorized team members/captains may perform restricted roster actions.

    Ali Irtiza Ahmed | S.M.A. Khalil | Page 14



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
Technology and Data Variations List
 • Teams may use invite-only joining or approval-based joining.
 • Maximum roster size may vary by tournament/game rules.
 • A customer may be restricted to one active team per game or competition category.





    Ali Irtiza Ahmed | S.M.A. Khalil | Page 15



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
```

- [ ] `UC-06`: success scenario, all extensions and special requirements mapped to real screen actions, domain rules, DB writes and passing tests.

### UC-07 — Register for Tournament

```text
UC-07 - Register for Tournament
 Use Case Name                                    Register for Tournament
    GameHub - Gaming Cafe & Esports Tournament Scope                                        Management System
 Level                                           User-Goal
 Primary Actor                              Customer / Gamer or Team Captain

Stakeholders and Interests
Customer / Gamer / Team Captain: Wants a valid registration and clear tournament requirements.
Tournament Organizer: Wants only eligible participants, correct rosters, and controlled capacity.
Manager / Administrator: Wants accurate tournament participation and fee records.

Preconditions
 • The participant has an active account.
 • The selected tournament exists and registration is open.
 • For team tournaments, the registering participant has authority to register the team.

Postconditions
 • A valid tournament registration is stored and linked to the player/team.
 • Tournament capacity/participant list is updated.
 • Required entry-fee status is recorded.

Main Success Scenario
 Step                         Actor Action                 System Response
    Participant requests available       System displays tournaments 1
    tournaments.                        currently open for registration.
    System displays format, schedule,
 2                                     Participant selects a tournament.      eligibility rules, capacity, and entry
    fee.
    System verifies eligibility, roster                                       Participant selects the player or
 3                                                                                 rules, duplicate registration, and                               team to register.
    available capacity.
    System calculates any required
 4                                     Participant confirms registration.    entry fee and presents the final
    registration summary.
    Participant satisfies the required fee  System verifies the payment/fee 5
    condition, if applicable.                status.
    System creates the tournament
    registration and adds the 6                                                         -
    participant/team to the tournament
    list.
    System displays registration 7                                                         -
    confirmation.

Extensions
 • 3a. Participant/team is ineligible: the system rejects registration and displays the failed eligibility rule.
    Ali Irtiza Ahmed | S.M.A. Khalil | Page 16



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
 • 3b. Tournament capacity has been reached: the system does not register the participant and may offer a wait-
    list if configured.
 • 3c. Participant/team is already registered: the system prevents duplicate registration.
 • 5a. Required entry fee is not successfully paid/recorded: registration remains pending or is cancelled according
   to configured policy.

Special Requirements
 • Eligibility and capacity checks must occur again before final confirmation.
 • Team registration must use the current valid roster and authorized captain.
 • Tournament registration changes must be auditable.

Technology and Data Variations List
 • Tournament may be solo or team-based.
 • Entry may be free or paid.
 • Tournament format may be knockout, league/round-robin, or another configured format.





    Ali Irtiza Ahmed | S.M.A. Khalil | Page 17



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
```

- [ ] `UC-07`: success scenario, all extensions and special requirements mapped to real screen actions, domain rules, DB writes and passing tests.

### UC-08 — Manage Tournament

```text
UC-08 - Manage Tournament
 Use Case Name                           Manage Tournament
    GameHub - Gaming Cafe & Esports Tournament Scope                                        Management System
 Level                                           User-Goal
 Primary Actor                             Tournament Organizer

Stakeholders and Interests
Tournament Organizer: Wants to create and configure tournaments, control registration, and keep information
current.
Participants: Want accurate rules, schedule, fees, capacity, and registration status.
Manager / Administrator: Wants tournaments to follow GameHub policies and use resources appropriately.

Preconditions
 • The organizer is authenticated and has tournament-management permission.
 • Required games/resources referenced by the tournament are available in the system.

Postconditions
 • Tournament configuration is stored with a defined status such as Draft, Registration Open, Registration Closed,
   Active, Completed, or Cancelled.
 • Changes are available to relevant participants and staff.

Main Success Scenario
 Step                         Actor Action                 System Response
    Organizer requests to create a       System displays tournament 1
    tournament.                         configuration fields.
    Organizer enters name, game,                                                                 System validates required fields and
 2                                   format, dates, capacity, eligibility                                                                      schedule consistency.
    rules, and entry fee.
    Organizer confirms the tournament  System creates the tournament in 3
    details.                              Draft state.
    System changes the tournament
    Organizer reviews the configuration  status to Registration Open and 4
    and opens registration.            makes it visible to eligible
    participants.
    System prevents new registrations                                   Organizer later closes registration at
 5                                                           and preserves the confirmed                                     the appropriate time.
    participant list.

Extensions
 • 2a. Dates, capacity, or format configuration is invalid: the system identifies the problem and asks the organizer
   to correct it.
 • 3a. Organizer edits an existing draft instead of creating a new tournament: the system loads current
   configuration and saves authorized changes.
 • 4a. Organizer attempts to open registration without required information: the system blocks the status change
  and lists missing fields.

    Ali Irtiza Ahmed | S.M.A. Khalil | Page 18



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
 • 5a. Organizer cancels a tournament: the system records the cancellation, prevents further tournament activity,
  and marks affected registrations accordingly.

Special Requirements
 • Only authorized organizers/managers may change tournament configuration or status.
 • Changes affecting already registered participants must be traceable.
 • Tournament dates must not violate configured scheduling constraints.

Technology and Data Variations List
 • Tournament format may be knockout, round-robin/league, or another configured format.
 • Tournament may have individual or team registration.
 • Entry fee, capacity, roster size, and eligibility rules may vary by tournament.





    Ali Irtiza Ahmed | S.M.A. Khalil | Page 19



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
```

- [ ] `UC-08`: success scenario, all extensions and special requirements mapped to real screen actions, domain rules, DB writes and passing tests.

### UC-09 — Conduct Tournament Matches

```text
UC-09 - Conduct Tournament Matches
 Use Case Name                              Conduct Tournament Matches
    GameHub - Gaming Cafe & Esports Tournament Scope                                        Management System
 Level                                           User-Goal
 Primary Actor                             Tournament Organizer

Stakeholders and Interests
Tournament Organizer: Wants correct match scheduling, results, standings/brackets, and tournament
progression.
Participants: Want fair scheduling and accurate published results.
Manager / Administrator: Wants reliable tournament records and efficient resource use.

Preconditions
 • The tournament exists and is ready for competition.
 • Registration is closed or the participant set is otherwise finalized.
 • Required stations/resources are available or schedulable.

Postconditions
 • Match results are recorded and tournament progression/standings are updated.
 • Completed matches cannot be accidentally duplicated without authorized correction.

Main Success Scenario
 Step                         Actor Action                 System Response
    System displays the current                                   Organizer requests the tournament
 1                                                                          participant list, bracket/standings,                               match view.
    and pending matches.
    Organizer schedules or selects the    System checks schedule/resource
 2                                 next match and assigns required      conflicts and stores the match
    resources.                          assignment.
    Organizer starts/authorizes the      System marks the match as In 3
    match.                               Progress.
    Organizer enters the final result     System validates the result against 4
    after the match.                     the tournament format.
    System records the result and marks 5                                 Organizer confirms the result.
    the match Completed.
    System updates bracket progression,
 6                                                         -                                     standings, or the next eligible match
    as appropriate.
    When all required matches are
 7                                                         -                                   complete, system records final
    tournament placement/status.

Extensions
 • 2a. Resource or time conflict exists: the system rejects the conflicting assignment and shows alternatives.
 • 4a. Entered result is invalid or incomplete: the system asks the organizer to correct it.
    Ali Irtiza Ahmed | S.M.A. Khalil | Page 20



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
 • 5a. An authorized correction is required after confirmation: the system records the correction with an audit
   trail and recalculates affected progression/standings.

Special Requirements
 • Only authorized tournament staff may confirm official results.
 • Result changes after confirmation must be auditable.
 • The system must maintain format-consistent progression/standings.

Technology and Data Variations List
 • A result may be score-based, win/loss, best-of series, or another configured format.
 • Match assignment may use specific gaming stations or an external match location record.
 • Progression rules differ for knockout and league/round-robin tournaments.





    Ali Irtiza Ahmed | S.M.A. Khalil | Page 21



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
```

- [ ] `UC-09`: success scenario, all extensions and special requirements mapped to real screen actions, domain rules, DB writes and passing tests.

### UC-10 — Manage Gaming Stations

```text
UC-10 - Manage Gaming Stations
 Use Case Name                           Manage Gaming Stations
    GameHub - Gaming Cafe & Esports Tournament Scope                                        Management System
 Level                                           User-Goal
 Primary Actor                             Manager / Administrator

Stakeholders and Interests
Manager / Administrator: Wants an accurate inventory of stations, specifications, rates, and operational status.
Staff Member: Needs current station information for reservations and sessions.
Customer / Gamer: Depends on accurate availability and station type information.

Preconditions
 • The manager/administrator is authenticated with station-management permission.

Postconditions
 • Station records and their current configuration/status are stored consistently.
 • Updated information is available to reservation, session, maintenance, and reporting functions.

Main Success Scenario
 Step                         Actor Action                 System Response
    Manager requests gaming-station    System displays existing stations 1
    administration.                  and their status.
    Manager chooses to add a new      System displays required station 2
    station.                                     fields.
    Manager enters station identifier,                                                                 System validates the data and
 3                                      type, specifications, rate/category,                                                                     checks identifier uniqueness.
    and initial status.
 4                             Manager confirms the new station.   System creates the station record.
    Manager reviews the updated       System displays the new station and 5
    station list.                                   its current availability/status.

Extensions
 • 2a. Manager selects an existing station instead: the system loads the current record and allows authorized
   updates to specifications, pricing category, or status.
 • 3a. Station identifier already exists or required data is invalid: the system rejects the save and identifies the
   problem.
 • 4a. Manager deactivates/decommissions a station: the system checks for active/future assignments and blocks
   unsafe deactivation until conflicts are resolved.

Special Requirements
 • Station identifiers must be unique.
 • Status changes must immediately affect booking/session availability where relevant.
 • Destructive changes must be restricted to authorized roles and should preserve historical records.

Technology and Data Variations List
 • Station may be PC, console, or another configured gaming device category.
    Ali Irtiza Ahmed | S.M.A. Khalil | Page 22



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
• Rates may be fixed, category-based, or time-based.
• Hardware/specification fields may differ by station type.





    Ali Irtiza Ahmed | S.M.A. Khalil | Page 23



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
```

- [ ] `UC-10`: success scenario, all extensions and special requirements mapped to real screen actions, domain rules, DB writes and passing tests.

### UC-11 — Handle Equipment Maintenance

```text
UC-11 - Handle Equipment Maintenance
 Use Case Name                              Handle Equipment Maintenance
    GameHub - Gaming Cafe & Esports Tournament Scope                                        Management System
 Level                                           User-Goal
 Primary Actor                                        Staff Member

Stakeholders and Interests
Staff Member: Wants faults recorded quickly so unavailable equipment is not assigned to customers.
Manager / Administrator: Wants visibility into downtime, recurring faults, and maintenance history.
Customer / Gamer: Wants only working stations/equipment to appear as available.

Preconditions
 • The affected station/equipment exists in GameHub inventory.
 • The reporting staff member is authenticated.

Postconditions
 • A maintenance issue is recorded with current status.
 • Affected equipment availability reflects the fault status.
 • When resolved, maintenance history and return-to-service information are stored.

Main Success Scenario
 Step                         Actor Action                 System Response
    System displays the selected                                           Staff reports a fault for a
 1                                                              equipment record and maintenance                                    station/equipment item.
    form.
    Staff enters fault description, 2                                                               System validates the report.
    severity, and relevant notes.
    System creates a maintenance
    record and marks affected 3                                         Staff submits the issue.
    equipment unavailable when
    required.
    Authorized staff/manager later      System updates the maintenance
 4                                  records repair progress or            status and records resolution
    resolution.                               details.
    System closes the maintenance issue                                   Authorized user confirms that
 5                                                           and returns the equipment/station                                equipment is ready for service.
    to an appropriate operational status.

Extensions
 • 3a. Equipment already has an open maintenance issue for the same fault: the system warns the user and allows
   updating the existing issue rather than creating a duplicate.
 • 5a. Equipment still fails operational check: the maintenance issue remains open and the equipment stays
   unavailable.
 • 3b. Future reservations are affected by the outage: the system identifies affected bookings for staff
   follow-up/reassignment.

    Ali Irtiza Ahmed | S.M.A. Khalil | Page 24



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
Special Requirements
 • Faulty equipment must not be presented as available for new sessions/reservations.
 • Maintenance history must be retained for reporting and recurring-fault analysis.
 • Only authorized users may close a maintenance issue or return equipment to service.

Technology and Data Variations List
 • Maintenance status may include Reported, In Progress, Resolved, or Out of Service.
 • Issue severity may be Low, Medium, High/Critical, or another configured scale.
 • Resolution may involve repair, component replacement, or decommissioning.





    Ali Irtiza Ahmed | S.M.A. Khalil | Page 25



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
```

- [ ] `UC-11`: success scenario, all extensions and special requirements mapped to real screen actions, domain rules, DB writes and passing tests.

### UC-12 — Generate Management Reports

```text
UC-12 - Generate Management Reports
 Use Case Name                               Generate Management Reports
    GameHub - Gaming Cafe & Esports Tournament Scope                                        Management System
 Level                                           User-Goal
 Primary Actor                             Manager / Administrator

Stakeholders and Interests
Manager / Administrator: Wants timely, accurate information for operational and financial decisions.
Staff Member: May need authorized operational summaries for daily work.
Project/Business Owner: Wants reliable revenue, utilization, membership, and tournament performance
information.

Preconditions
 • The requesting user is authenticated and authorized to view the selected report.
 • Relevant operational data exists in the system.

Postconditions
 • The requested report is generated from current stored data and presented to the authorized user.
 • If export is selected, an exportable report file/representation is produced without changing source data.

Main Success Scenario
 Step                         Actor Action                 System Response
    Manager opens the reporting        System displays available report 1
    feature.                              categories and filter options.
    Manager selects a report type and 2                                                               System validates the criteria.
    enters a date range/filters.
    Manager requests report           System retrieves and aggregates the 3
    generation.                          relevant authorized data.
    System displays the report with
 4                                                         -                                          totals/details appropriate to the
    selected report type.
    Manager optionally chooses         System produces the requested 5
    export/print.                        report output format.

Extensions
 • 2a. Date range or filters are invalid: the system identifies the invalid criteria and asks the manager to correct
   them.
 • 3a. No data matches the criteria: the system displays an empty-result message rather than misleading totals.
 • 5a. Export fails: the on-screen report remains available and the system reports the export problem.

Special Requirements
 • Reports must enforce role-based access to sensitive financial/customer data.
 • Financial and utilization totals must be calculated consistently from stored transactional data.
 • Large reports should complete within a reasonable response time for the project dataset.


    Ali Irtiza Ahmed | S.M.A. Khalil | Page 26



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
Technology and Data Variations List
 • Report type may include revenue, station utilization, reservations, memberships, maintenance, tournament
   participation/results, or other configured summaries.
 • Filters may include date range, station type, tournament, membership plan, or status.
 • Output may be on-screen, printable, or exportable (for example PDF/CSV if implemented).





    Ali Irtiza Ahmed | S.M.A. Khalil | Page 27



    GameHub - Deliverable 3 | CS3004 Software Design & Analysis
```

- [ ] `UC-12`: success scenario, all extensions and special requirements mapped to real screen actions, domain rules, DB writes and passing tests.

## Annex D. Deliverable 4: 12 exact black-box system sequence event contracts

D4 SSDs intentionally show one `:GameHubSystem` black-box lifeline each. Interpret the text as required externally visible event/response ordering; **do not** implement every event as a separate database write. D3 extensions outrank success-only SSD drawings. D5 expands these into application/design interactions.

### UC-01 — SSD event / return sequence

```text
UC-01 / Create Customer Account


    :GameHubSystem
    black-box lifeline


    Customer / Gamer

    requestCreateAccount()
    1


    registrationForm(requiredFields[])



    enterRegistrationInfo(name, email, phone, credentials)
    2


    fieldValidation(validFields)



    submitRegistrationRequest(email, phone)
    3


    identifierCheck(available)



    confirmRegistration(confirmed)
    4


    accountCreated(customerId, accountStatus)




  D3 steps 1-5. Final conﬁrmation includes account creation and registration notice.




i250685  / i250782  / Section D                                                                                                                                                    Page 8 of 28
```

- [ ] `UC-01`: document actual UI event handlers, visible outputs, command/result DTOs and tests for all SSD system events.

### UC-02 — SSD event / return sequence

```text
UC-02 / Reserve Gaming Station


    :GameHubSystem
    black-box lifeline


    Customer / Gamer

    requestStationReservation()
    1


    reservationOptions(criteria, stationTypes[])



    searchAvailableStations(date, start, minutes, type)
    2


    matchingStations(stationIds[])



    selectAvailableStation(stationId)
    3


    stationQuote(details, estimatedChargePKR)



    confirmStationReservation(stationId, date, start, minutes)
    4


    reservationConfirmed(reservationId, status, bookingDetails)




  D3 steps 1-7. Final response includes availability recheck and booking persistence.




i250685  / i250782  / Section D                                                                                                                                                    Page 9 of 28
```

- [ ] `UC-02`: document actual UI event handlers, visible outputs, command/result DTOs and tests for all SSD system events.

### UC-03 — SSD event / return sequence

```text
UC-03 / Conduct Gaming Session


    :GameHubSystem
    black-box lifeline


    Staﬀ Member

    identifyCustomer(customerId, reservationId?)
    1


    customerReservationStationDetails(customer, booking, station)



    confirmStationAssignment(stationId)
    2


    stationCheck(operational, available)



    startGamingSession(customerId, stationId)
    3


    sessionStarted(sessionId, startedAt, stationStatus)



    endGamingSession(sessionId)
    4


    usageSummary(endedAt, minutes, chargePKR)



    confirmSessionCompletion(sessionId)
    5


    sessionClosed(sessionId, stationStatus)




  D3 steps 1-6. Real-world gameplay (step 4) is not a separate system event.




i250685  / i250782  / Section D                                                                                                                                                    Page 10 of 28
```

- [ ] `UC-03`: document actual UI event handlers, visible outputs, command/result DTOs and tests for all SSD system events.

### UC-04 — SSD event / return sequence

```text
UC-04 / Process Payment


    :GameHubSystem
    black-box lifeline


    Staﬀ Member

    selectPayableTransaction(chargeId)
    1


    paymentDetails(duePKR, discountsPKR, methods[])



    enterPaymentDetails(method, paidPKR, reference)
    2


    paymentValidation(valid, balancePKR, changePKR)



    confirmReceiptOfPayment(chargeId, method, paidPKR)
    3


    paymentRecorded(paymentId, paidStatus, receiptNo)




  D3 steps 1-5. Posting payment and issuing receipt are in the ﬁnal return.




i250685  / i250782  / Section D                                                                                                                                                    Page 11 of 28
```

- [ ] `UC-04`: document actual UI event handlers, visible outputs, command/result DTOs and tests for all SSD system events.

### UC-05 — SSD event / return sequence

```text
UC-05 / Subscribe or Renew Membership


    :GameHubSystem
    black-box lifeline


    Customer / Gamer

    requestMembershipPlans()
    1


    availablePlans(planIds[], prices, durations, benefits)



    selectMembershipPlan(planId)
    2


    planDetails(planId, currentMembershipStatus)



    confirmMembershipChoice(planId, mode)
    3


    membershipQuote(feePKR, startDate, expiryDate)



    completeMembershipPayment(paymentReference)
    4


    membershipActivated(membershipId, expiryDate, status)




  D3 steps 1-6. Payment veriﬁcation precedes membership activation.




i250685  / i250782  / Section D                                                                                                                                                    Page 12 of 28
```

- [ ] `UC-05`: document actual UI event handlers, visible outputs, command/result DTOs and tests for all SSD system events.

### UC-06 — SSD event / return sequence

```text
UC-06 / Create or Join Gaming Team


    :GameHubSystem
    black-box lifeline


    Customer / Gamer

    openTeamParticipation()
    1


    teamOptions(create, join)



    chooseCreateTeam(teamName)
    2


    teamNameValidation(isUnique)



    confirmTeamCreation(teamName)
    3


    teamCreated(teamId, captainId)



    inviteTeamMembers(teamId, memberIds[])
    4


    invitationStatus(memberIds[], roster, teamStatus)




  D3 steps 1-5. Join Team is an extension; SSD shows Create Team main path.




i250685  / i250782  / Section D                                                                                                                                                    Page 13 of 28
```

- [ ] `UC-06`: document actual UI event handlers, visible outputs, command/result DTOs and tests for all SSD system events.

### UC-07 — SSD event / return sequence

```text
UC-07 / Register for Tournament


    :GameHubSystem
    black-box lifeline


    Customer / Team Captain

    viewOpenTournaments()
    1


    openTournaments(tournamentIds[])



    selectTournament(tournamentId)
    2


    tournamentDetails(format, schedule, rules, capacity, feePKR)



    selectEntrant(entrantType, entrantId)
    3


    eligibilityCheck(eligible, rosterValid, slotsAvailable)



    confirmTournamentRegistration(tournamentId, entrantId)
    4


    registrationSummary(entryFeePKR, entrant, tournament)



    completeEntryFeeCondition(paymentReference?)
    5


    registrationConfirmed(registrationId, feeStatus, status)




  D3 steps 1-7. Entrant is a customer OR a team.




i250685  / i250782  / Section D                                                                                                                                                    Page 14 of 28
```

- [ ] `UC-07`: document actual UI event handlers, visible outputs, command/result DTOs and tests for all SSD system events.

### UC-08 — SSD event / return sequence

```text
UC-08 / Manage Tournament


    :GameHubSystem
    black-box lifeline


    Tournament Organizer

    requestCreateTournament()
    1


    tournamentConfigurationFields(fields[])


    enterTournamentConfiguration(name, game, format, dates, capacity, rules,
    fee)
    2


    configurationValidation(valid, scheduleConsistent)



    confirmTournamentDetails()
    3


    tournamentCreated(tournamentId, status=Draft)



    openTournamentRegistration(tournamentId)
    4


    registrationOpen(tournamentId, status)



    closeTournamentRegistration(tournamentId)
    5


    registrationClosed(tournamentId, confirmedEntrants[])




  D3 steps 1-5. Opening/closing registration are distinct system events.




i250685  / i250782  / Section D                                                                                                                                                    Page 15 of 28
```

- [ ] `UC-08`: document actual UI event handlers, visible outputs, command/result DTOs and tests for all SSD system events.

### UC-09 — SSD event / return sequence

```text
UC-09 / Conduct Tournament Matches


    :GameHubSystem
    black-box lifeline


    Tournament Organizer

    viewTournamentMatches(tournamentId)
    1


    matchView(participants[], bracket, pendingMatches[])



    scheduleTournamentMatch(matchId, when, resources)
    2


    matchAssignmentSaved(matchId, conflictFree)



    startTournamentMatch(matchId)
    3


    matchStatus(matchId, InProgress)



    enterFinalMatchResult(matchId, scores, winnerId)
    4


    resultValidation(valid, formatConsistent)



    confirmMatchResult(matchId)
    5


    resultRecorded(matchId, standings, tournamentStatus)




  D3 steps 1-7. Final return includes bracket progress and placement updates.




i250685  / i250782  / Section D                                                                                                                                                    Page 16 of 28
```

- [ ] `UC-09`: document actual UI event handlers, visible outputs, command/result DTOs and tests for all SSD system events.

### UC-10 — SSD event / return sequence

```text
UC-10 / Manage Gaming Stations


    :GameHubSystem
    black-box lifeline


    Manager / Administrator

    openGamingStationAdministration()
    1


    stationList(stations[], statuses[])



    chooseAddNewStation()
    2


    newStationFields(fields[])



    enterStationDetails(code, type, specs, ratePKR, status)
    3


    stationValidation(valid, codeUnique)



    confirmNewStation(code)
    4


    stationCreated(stationId, status)



    reviewGamingStationList()
    5


    updatedStationList(stations[], availability[])




  D3 steps 1-5. Add New Station is the selected main success path.




i250685  / i250782  / Section D                                                                                                                                                    Page 17 of 28
```

- [ ] `UC-10`: document actual UI event handlers, visible outputs, command/result DTOs and tests for all SSD system events.

### UC-11 — SSD event / return sequence

```text
UC-11 / Handle Equipment Maintenance


    :GameHubSystem
    black-box lifeline


    Staﬀ Member

    reportEquipmentFault(equipmentId)
    1


    equipmentDetails(equipmentId, stationId, faultForm)



    enterFaultDetails(description, severity, notes)
    2


    faultValidation(valid)



    submitMaintenanceIssue(equipmentId)
    3


    ticketCreated(ticketId, equipmentStatus)



    recordRepairProgress(ticketId, status, resolutionNotes)
    4


    ticketUpdated(ticketId, updatedStatus)



    confirmReadyForService(ticketId)
    5


    maintenanceClosed(ticketId, operationalStatus)




  D3 steps 1-5. Progress/resolution is entered by authorized staﬀ or manager.




i250685  / i250782  / Section D                                                                                                                                                    Page 18 of 28
```

- [ ] `UC-11`: document actual UI event handlers, visible outputs, command/result DTOs and tests for all SSD system events.

### UC-12 — SSD event / return sequence

```text
UC-12 / Generate Management Reports


    :GameHubSystem
    black-box lifeline


    Manager / Administrator

    openReportingFeature()
    1


    reportOptions(types[], filters[])



    chooseReportCriteria(type, dateFrom, dateTo, filters)
    2


    criteriaValidation(valid)



    requestReportGeneration(type, dateFrom, dateTo, filters)
    3


    generatedReport(reportId, totals, detailRows[])



    exportOrPrintReport(reportId, format)
    4


    reportOutput(fileName, format)




  D3 steps 1-5. Final export/print is optional and shown on the success branch.




i250685  / i250782  / Section D                                                                                                                                                    Page 19 of 28



GAMEHUB  / SOFTWARE DESIGN & ANALYSIS
```

- [ ] `UC-12`: document actual UI event handlers, visible outputs, command/result DTOs and tests for all SSD system events.

## Annex E. Deliverable 4 Part 3: academic Java inheritance/polymorphism integration

**Source reconciliation:** The official D4 question requires an `Employee → Faculty, Staff, ResearchAssistant` demonstration. D5 café model **separately** has `Employee → Staff → Manager/TournamentOrganizer` and explicitly says Faculty/ResearchAssistant are not café operations. Preserve both models in **separate packages** (`com.gamehub.personnel` for D4 payroll exercise; `com.gamehub.domain.people` for operational café). Do not count personnel as a 13th user-goal use case.
### Official D4 problem statement

```text
Deliverable #4 - SDA Fall 2026
    Domain Model, System Sequence Diagrams and Code Integration

Overview
Apply this deliverable to your own selected project. Submit the following artifacts for your selected
project:
 • Domain Model
 • System Sequence Diagrams (SSDs) for the main success scenario of each use case
 •  Integration of the Java code described in Part 3

Part 1: Domain Model
Draw the domain model of your project. It must accurately show:
 •  All conceptual classes of your project
 •  Attributes of each class with appropriate data types
 •  Associations among the conceptual classes, with a name for each association
 •  Multiplicity at both ends of every association

Part 2: System Sequence Diagrams (SSDs)
Draw the SSD for the main success scenario of every use case that you defined for your project in
Deliverable #3. The number of SSDs must equal the number of use cases (one SSD per use case).
Each SSD should show the actor, the system as a black box, and the system events with their
parameters and return values.

Part 3: Code Integration
In your selected project, integrate the code for the following problem statement:
Employee has 3 categories such as Faculty, Staff and Research Assistant. These 3 categories are
inherited by employee. Employee has attributes empno, name, address, city, salary, jobtitle.
Following are attributes of categories:
 •  Faculty (tax, medical_allowance, pension, research_allowance); maximum medical allowance is
   115,000 per year
 •  Staff (tax, medical_allowance, pension, bonus); maximum medical allowance is 115,000 and
  bonus is 40,000 per year
 • Research Assistant (research_fund, overtime)
Describe how you manage override and polymorphism in your Java program. As a sample, how to
input 4 employees, 2 staff and 3 Research assistants with their attributes and methods/operations.
Display sample will be as follows:

   Category: Faculty
   Employee name: Umar Ali
   Job Title: Professor
   Salary: 450,000
   Medical Allowance: 95,000
   Gross Salary: 525,000
   Net Salary: 475,000
   Pension: 10,000
   ------------------------------------
   Employee name: Muhammad Ahmad
   ...



Submission Guidelines
 • You may use any software design tool, but AI-generated images will receive zero marks.
 •  Submit only one PDF file containing the images exported from your design tool.
 • No late submissions will be accepted.
 • Empty submissions will not be considered.
 •  Only one submission per group is allowed.
 • Naming convention: GM1RollNo_GM2RollNo_SectionName.pdf
```
### Team-submitted D4 integration and demonstration source

```text
Part 3  / Java Integration

Package gamehub.personnel - compilable in the GameHub Java project.




  EMPLOYEE HIERARCHY

  Employee (abstract) -> Faculty | Staff | ResearchAssistant



Override / dynamic dispatch

 Employee declares abstract getCategory(), grossSalary(), netSalary(), and categoryDetails(). All three
 subclasses override these. An Employee-typed collection invokes the correct subclass behavior at
 runtime.


Required data fields

 Employee: empno, name, address, city, salary, jobtitle. Faculty: tax, medicalAllowance, pension,
 researchAllowance. Staff: tax, medicalAllowance, pension, bonus. ResearchAssistant: researchFund,
 overtime.


Consistent money model

Amounts are annual PKR. BigDecimal avoids binary floating-point rounding. Medical allowance cannot
 exceed PKR 115,000/year for Faculty or Staff. Every Staff bonus is exactly PKR 40,000/year.


Formula: Faculty

 Gross = salary + medicalAllowance + researchAllowance; Net = Gross - tax - pension.


Formula: Staff

 Gross = salary + medicalAllowance + 40,000 bonus; Net = Gross - tax - pension.


Formula: Research Assistant

 Gross = salary + overtime; Net = Gross. Research fund is an earmarked work budget, not personal
 compensation. No tax/pension was specified for this class.


Sample and optional interactive input

By default the demo creates 4 Faculty, 2 Staff, 3 Research Assistants (9 total). java
 gamehub.personnel.GameHubPayrollDemo --input prompts for all nine records.





i250685  / i250782  / Section D                                                                Page 20 of 28



GAMEHUB  / SOFTWARE DESIGN & ANALYSIS

Part 3  / Numeric audit

Documented assumptions are necessary because the provided example does not state its formula.


Faculty sample: Umar Ali

  Base annual salary                                           450,000


  Annual medical allowance                                      95,000


  Annual research allowance                                  0


  Gross = salary + allowances                              545,000


  Annual tax                                                   60,000


  Annual pension                                               10,000


  Net = gross - tax - pension                               475,000



Why the example gross figure is not copied

The provided assignment lists base salary 450,000 and medical allowance 95,000, yet labels

 gross salary 525,000. These do not reconcile under the stated fields without an additional

 20,000 adjustment. This implementation uses explicit formulas rather than fabricating an

 undisclosed deduction.


Integration boundary in GameHub

The module is under gamehub.personnel and can be invoked from GameHub application startup

 or staff administration. It is deliberately isolated from reservation, tournament and payment

 logic: the academic-style Faculty and Research Assistant classifications are mandated by the

 question, not claimed as existing café workflow actors.


Compile and run


  cd java


  javac gamehub/personnel/*.java


  java gamehub.personnel.GameHubPayrollDemo
i250685  / i250782  / Section D                                                                Page 21 of 28

  java gamehub.personnel.GameHubPayrollDemo --input



 PART 3  / EXECUTABLE JAVA SOURCE

 gamehub/personnel/Employee.java

  Complete source | Lines 1-47 of 47



    1       package gamehub.personnel;
    2
    3       import java.math.BigDecimal;
    4       import java.util.Objects;
    5
    6       /** GameHub's demonstrator for the Deliverable 4 employee hierarchy. */
    7       public abstract class Employee {
    8           private final String empno;
    9           private final String name;
   10           private final String address;
   11           private final String city;
   12           private final BigDecimal salary; // annual base salary in PKR
   13           private final String jobtitle;
   14
   15           protected Employee(String empno, String name, String address, String city,
   16                              BigDecimal salary, String jobtitle) {
   17               this.empno = required(empno, "empno");
   18               this.name = required(name, "name");
   19               this.address = required(address, "address");
   20               this.city = required(city, "city");
   21               this.salary = nonNegative(salary, "salary");
   22               this.jobtitle = required(jobtitle, "jobtitle");
   23           }
   24
   25           protected static String required(String value, String field) {
   26               Objects.requireNonNull(value, field);
   27               if (value.isBlank()) throw new IllegalArgumentException(field + " cannot be blank");
   28               return value.trim();
   29           }
   30
   31           protected static BigDecimal nonNegative(BigDecimal value, String field) {
   32               Objects.requireNonNull(value, field);
   33               if (value.signum() < 0) throw new IllegalArgumentException(field + " must be non-negative");
   34               return value;
   35           }
   36
   37           public String getEmpno() { return empno; }
   38           public String getName() { return name; }
   39           public String getAddress() { return address; }
   40           public String getCity() { return city; }
   41           public BigDecimal getSalary() { return salary; }
   42           public String getJobtitle() { return jobtitle; }
   43           public abstract String getCategory();
   44           public abstract BigDecimal grossSalary();
   45           public abstract BigDecimal netSalary();
   46           public abstract String categoryDetails();
   47       }





i250685  / i250782  / Section D                                                                                                                  Page 22 of 28



 PART 3  / EXECUTABLE JAVA SOURCE

 gamehub/personnel/Faculty.java

  Complete source | Lines 1-38 of 38



    1       package gamehub.personnel;
    2
    3       import java.math.BigDecimal;
    4
    5       public final class Faculty extends Employee {
    6           private static final BigDecimal MAX_MEDICAL = new BigDecimal("115000");
    7           private final BigDecimal tax;
    8           private final BigDecimal medicalAllowance;
    9           private final BigDecimal pension;
   10           private final BigDecimal researchAllowance;
   11
   12           public Faculty(String id, String name, String address, String city,
   13                          BigDecimal salary, String title, BigDecimal tax,
   14                          BigDecimal medicalAllowance, BigDecimal pension,
   15                          BigDecimal researchAllowance) {
   16               super(id, name, address, city, salary, title);
   17               this.tax = nonNegative(tax, "tax");
   18               this.medicalAllowance = nonNegative(medicalAllowance, "medical allowance");
   19               this.pension = nonNegative(pension, "pension");
   20               this.researchAllowance = nonNegative(researchAllowance, "research allowance");
   21               if (medicalAllowance.compareTo(MAX_MEDICAL) > 0)
   22                   throw new IllegalArgumentException("Annual medical allowance exceeds PKR 115,000");
   23           }
   24
   25           @Override public String getCategory() { return "Faculty"; }
   26           @Override public BigDecimal grossSalary() {
   27               return getSalary().add(medicalAllowance).add(researchAllowance);
   28           }
   29           @Override public BigDecimal netSalary() {
   30               return grossSalary().subtract(tax).subtract(pension);
   31           }
   32           @Override public String categoryDetails() {
   33               return "Medical Allowance: " + money(medicalAllowance)
   34                    + " | Research Allowance: " + money(researchAllowance)
   35                    + " | Tax: " + money(tax) + " | Pension: " + money(pension);
   36           }
   37           private static String money(BigDecimal value) { return String.format("%,.0f", value); }
   38       }





i250685  / i250782  / Section D                                                                                                                  Page 23 of 28



 PART 3  / EXECUTABLE JAVA SOURCE

 gamehub/personnel/Staff.java

  Complete source | Lines 1-38 of 38



    1       package gamehub.personnel;
    2
    3       import java.math.BigDecimal;
    4
    5       public final class Staff extends Employee {
    6           private static final BigDecimal MAX_MEDICAL = new BigDecimal("115000");
    7           public static final BigDecimal ANNUAL_BONUS = new BigDecimal("40000");
    8           private final BigDecimal tax;
    9           private final BigDecimal medicalAllowance;
   10           private final BigDecimal pension;
   11           private final BigDecimal bonus;
   12
   13           public Staff(String id, String name, String address, String city,
   14                        BigDecimal salary, String title, BigDecimal tax,
   15                        BigDecimal medicalAllowance, BigDecimal pension) {
   16               super(id, name, address, city, salary, title);
   17               this.tax = nonNegative(tax, "tax");
   18               this.medicalAllowance = nonNegative(medicalAllowance, "medical allowance");
   19               this.pension = nonNegative(pension, "pension");
   20               if (medicalAllowance.compareTo(MAX_MEDICAL) > 0)
   21                   throw new IllegalArgumentException("Annual medical allowance exceeds PKR 115,000");
   22               this.bonus = ANNUAL_BONUS;
   23           }
   24
   25           @Override public String getCategory() { return "Staff"; }
   26           @Override public BigDecimal grossSalary() {
   27               return getSalary().add(medicalAllowance).add(bonus);
   28           }
   29           @Override public BigDecimal netSalary() {
   30               return grossSalary().subtract(tax).subtract(pension);
   31           }
   32           @Override public String categoryDetails() {
   33               return "Medical Allowance: " + money(medicalAllowance)
   34                    + " | Bonus: " + money(bonus)
   35                    + " | Tax: " + money(tax) + " | Pension: " + money(pension);
   36           }
   37           private static String money(BigDecimal value) { return String.format("%,.0f", value); }
   38       }





i250685  / i250782  / Section D                                                                                                                  Page 24 of 28



 PART 3  / EXECUTABLE JAVA SOURCE

 gamehub/personnel/ResearchAssistant.java

  Complete source | Lines 1-26 of 26



    1       package gamehub.personnel;
    2
    3       import java.math.BigDecimal;
    4
    5       public final class ResearchAssistant extends Employee {
    6           private final BigDecimal researchFund;
    7           private final BigDecimal overtime; // annual overtime compensation in PKR
    8
    9           public ResearchAssistant(String id, String name, String address, String city,
   10                                    BigDecimal salary, String title,
   11                                    BigDecimal researchFund, BigDecimal overtime) {
   12               super(id, name, address, city, salary, title);
   13               this.researchFund = nonNegative(researchFund, "research fund");
   14               this.overtime = nonNegative(overtime, "overtime");
   15           }
   16
   17           @Override public String getCategory() { return "Research Assistant"; }
   18           @Override public BigDecimal grossSalary() { return getSalary().add(overtime); }
   19           // The research fund is an earmarked budget, not take-home compensation.
   20           @Override public BigDecimal netSalary() { return grossSalary(); }
   21           @Override public String categoryDetails() {
   22               return "Research Fund (not salary): " + money(researchFund)
   23                    + " | Overtime Compensation: " + money(overtime);
   24           }
   25           private static String money(BigDecimal value) { return String.format("%,.0f", value); }
   26       }





i250685  / i250782  / Section D                                                                                                                  Page 25 of 28



 PART 3  / EXECUTABLE JAVA SOURCE

 gamehub/personnel/GameHubPayrollDemo.java

  Complete source | Lines 1-71 of 95



    1       package gamehub.personnel;
    2
    3       import java.math.BigDecimal;
    4       import java.util.ArrayList;
    5       import java.util.List;
    6       import java.util.Scanner;
    7
    8       /** In the GameHub project's personnel package; run with optional --input. */
    9       public final class GameHubPayrollDemo {
   10           private GameHubPayrollDemo() { }
   11           private static BigDecimal pkr(String value) { return new BigDecimal(value); }
   12
   13           public static void main(String[] args) {
   14               List<Employee> employees = args.length > 0 && args[0].equals("--input")
   15                       ? inputNineEmployees(new Scanner(System.in)) : sampleNineEmployees();
   16               for (Employee employee : employees) { // runtime dispatch = polymorphism
   17                   printPayslip(employee);
   18               }
   19           }
   20
   21           private static void printPayslip(Employee employee) {
   22               System.out.println("Category: " + employee.getCategory());
   23               System.out.println("Employee No: " + employee.getEmpno());
   24               System.out.println("Employee name: " + employee.getName());
   25               System.out.println("Job Title: " + employee.getJobtitle());
   26               System.out.printf("Base Salary (PKR/year): %,.0f%n", employee.getSalary());
   27               System.out.printf("Gross Salary (PKR/year): %,.0f%n", employee.grossSalary());
   28               System.out.printf("Net Salary (PKR/year): %,.0f%n", employee.netSalary());
   29               System.out.println(employee.categoryDetails());
   30               System.out.println("----------------------------------------------------");
   31           }
   32
   33           /** Four Faculty, two Staff and three Research Assistants. */
   34           private static List<Employee> sampleNineEmployees() {
   35               return List.of(
   36                 new Faculty("F01", "Umar Ali", "Street 1", "Islamabad", pkr("450000"),
   37                             "Professor", pkr("60000"), pkr("95000"), pkr("10000"), pkr("0")),
   38                 new Faculty("F02", "Muhammad Ahmad", "Street 2", "Islamabad", pkr("550000"),
   39                             "Associate Professor", pkr("65000"), pkr("110000"), pkr("15000"), pkr("50000")),
   40                 new Faculty("F03", "Amna Khan", "Street 3", "Rawalpindi", pkr("480000"),
   41                             "Lecturer", pkr("48000"), pkr("75000"), pkr("12000"), pkr("30000")),
   42                 new Faculty("F04", "Bilal Raza", "Street 4", "Islamabad", pkr("510000"),
   43                             "Assistant Professor", pkr("60000"), pkr("115000"), pkr("14000"), pkr("40000")),
   44                 new Staff("S01", "Hassan Malik", "Street 5", "Islamabad", pkr("330000"),
   45                           "Gaming Floor Supervisor", pkr("30000"), pkr("65000"), pkr("8000")),
   46                 new Staff("S02", "Zara Farooq", "Street 6", "Rawalpindi", pkr("360000"),
   47                           "Tournament Coordinator", pkr("40000"), pkr("80000"), pkr("10000")),
   48                 new ResearchAssistant("R01", "Daniyal Shah", "Street 7", "Islamabad", pkr("290000"),
   49                           "Game Analytics RA", pkr("70000"), pkr("22000")),
   50                 new ResearchAssistant("R02", "Sana Aziz", "Street 8", "Rawalpindi", pkr("310000"),
   51                           "Hardware Evaluation RA", pkr("80000"), pkr("18000")),
   52                 new ResearchAssistant("R03", "Farhan Noor", "Street 9", "Islamabad", pkr("300000"),
   53                           "Research Assistant", pkr("60000"), pkr("15000"))
   54               );
   55           }
   56
   57           private static List<Employee> inputNineEmployees(Scanner in) {
   58               List<Employee> employees = new ArrayList<>();
   59               for (int i = 1; i <= 4; ++i) {
   60                   System.out.println("Faculty #" + i);
   61                   String[] b = readBase(in);
   62                   employees.add(new Faculty(b[0], b[1], b[2], b[3], pkr(b[4]), b[5],
   63                           readMoney(in, "Tax"), readMoney(in, "Medical allowance"),
   64                           readMoney(in, "Pension"), readMoney(in, "Research allowance")));
   65               }
   66               for (int i = 1; i <= 2; ++i) {
   67                   System.out.println("Staff #" + i + " (bonus automatically PKR 40,000/year)");
   68                   String[] b = readBase(in);
   69                   employees.add(new Staff(b[0], b[1], b[2], b[3], pkr(b[4]), b[5],
   70                           readMoney(in, "Tax"), readMoney(in, "Medical allowance"),
   71                           readMoney(in, "Pension")));





i250685  / i250782  / Section D                                                                                                                  Page 26 of 28



 PART 3  / EXECUTABLE JAVA SOURCE

 gamehub/personnel/GameHubPayrollDemo.java

  Complete source | Lines 72-95 of 95



   72               }
   73               for (int i = 1; i <= 3; ++i) {
   74                   System.out.println("Research Assistant #" + i);
   75                   String[] b = readBase(in);
   76                   employees.add(new ResearchAssistant(b[0], b[1], b[2], b[3], pkr(b[4]), b[5],
   77                           readMoney(in, "Research fund"), readMoney(in, "Overtime compensation")));
   78               }
   79               return employees;
   80           }
   81
   82           private static String[] readBase(Scanner in) {
   83               String[] fields = {"Employee number", "Name", "Address", "City", "Annual base salary (PKR)", "Job title"};
   84               String[] values = new String[fields.length];
   85               for (int i = 0; i < fields.length; ++i) {
   86                   System.out.print(fields[i] + ": ");
   87                   values[i] = in.nextLine();
   88               }
   89               return values;
   90           }
   91           private static BigDecimal readMoney(Scanner in, String prompt) {
   92               System.out.print(prompt + " (PKR/year): ");
   93               return pkr(in.nextLine());
   94           }
   95       }





i250685  / i250782  / Section D                                                                                                                  Page 27 of 28



 PART 3  / EXECUTION EVIDENCE

 Sample console output - 9 employees

  Produced by: java gamehub.personnel.GameHubPayrollDemo



  Category: Faculty
  Employee No: F01
  Employee name: Umar Ali
  Job Title: Professor
  Base Salary (PKR/year): 450,000
  Gross Salary (PKR/year): 545,000
  Net Salary (PKR/year): 475,000
  Medical Allowance: 95,000 | Research Allowance: 0 | Tax: 60,000 | Pension: 10,000
  ----------------------------------------------------
  Category: Faculty
  Employee No: F02
  Employee name: Muhammad Ahmad
  Job Title: Associate Professor
  Base Salary (PKR/year): 550,000
  Gross Salary (PKR/year): 710,000
  Net Salary (PKR/year): 630,000
  Medical Allowance: 110,000 | Research Allowance: 50,000 | Tax: 65,000 | Pension: 15,000
  ----------------------------------------------------
  Category: Faculty
  Employee No: F03
  Employee name: Amna Khan
  Job Title: Lecturer
  Base Salary (PKR/year): 480,000
  Gross Salary (PKR/year): 585,000
  Net Salary (PKR/year): 525,000
  Medical Allowance: 75,000 | Research Allowance: 30,000 | Tax: 48,000 | Pension: 12,000
  ----------------------------------------------------
  Category: Faculty
  Employee No: F04
  Employee name: Bilal Raza
  Job Title: Assistant Professor
  Base Salary (PKR/year): 510,000
  Gross Salary (PKR/year): 665,000
  Net Salary (PKR/year): 591,000
  Medical Allowance: 115,000 | Research Allowance: 40,000 | Tax: 60,000 | Pension: 14,000
  ----------------------------------------------------
  Category: Staff
  Employee No: S01
  Employee name: Hassan Malik
  Job Title: Gaming Floor Supervisor
  Base Salary (PKR/year): 330,000
  Gross Salary (PKR/year): 435,000
  Net Salary (PKR/year): 397,000
  Medical Allowance: 65,000 | Bonus: 40,000 | Tax: 30,000 | Pension: 8,000
  ----------------------------------------------------
  Category: Staff
  Employee No: S02
  Employee name: Zara Farooq
  Job Title: Tournament Coordinator
  Base Salary (PKR/year): 360,000
  Gross Salary (PKR/year): 480,000
  Net Salary (PKR/year): 430,000
  Medical Allowance: 80,000 | Bonus: 40,000 | Tax: 40,000 | Pension: 10,000
  ----------------------------------------------------
  Category: Research Assistant
  Employee No: R01
  Employee name: Daniyal Shah
  Job Title: Game Analytics RA
  Base Salary (PKR/year): 290,000
  Gross Salary (PKR/year): 312,000
  Net Salary (PKR/year): 312,000
  Research Fund (not salary): 70,000 | Overtime Compensation: 22,000
  ----------------------------------------------------
  Category: Research Assistant
  Employee No: R02
  Employee name: Sana Aziz
  Job Title: Hardware Evaluation RA
  Base Salary (PKR/year): 310,000
  Gross Salary (PKR/year): 328,000
  Net Salary (PKR/year): 328,000
  Research Fund (not salary): 80,000 | Overtime Compensation: 18,000
  ----------------------------------------------------
  Category: Research Assistant
  Employee No: R03
  Employee name: Farhan Noor
  Job Title: Research Assistant
  Base Salary (PKR/year): 300,000
  Gross Salary (PKR/year): 315,000
  Net Salary (PKR/year): 315,000
  Research Fund (not salary): 60,000 | Overtime Compensation: 15,000
  ----------------------------------------------------





i250685  / i250782  / Section D                                                                                                                  Page 28 of 28
```
**Personnel acceptance checklist:**
- [ ] Abstract `Employee` stores `empno, name, address, city, salary, jobtitle` and exposes dynamic-dispatch contract `getCategory(), grossSalary(), netSalary(), categoryDetails()`.
- [ ] `Faculty` medical allowance capped at **PKR 115,000** yearly, and gross/net formula accounts for base salary, research allowance, tax and pension.
- [ ] `Staff` medical allowance capped at **PKR 115,000** and bonus fixed to **PKR 40,000** yearly; tax and pension accounted for.
- [ ] `ResearchAssistant` research fund tracked separately from wages; overtime contributes to pay.
- [ ] Default demonstration creates **4 Faculty + 2 Staff + 3 ResearchAssistants** with correct printed breakdown; interactive input variant operational.
- [ ] Every subclass overrides declared abstract behavior; actual `Employee` collection demonstrates runtime polymorphism.
- [ ] Unit tests cover caps, formulas, negative-value validation and polymorphic output; personnel code compiles with the final app.
- [ ] `docs/DESIGN_TO_CODE.md` clearly explains why D4 personnel classes are not merged into D5 café roles.

## Annex F. Deliverable 5: design class models, exact named associations and multiplicities

The following text extracts the named D5 associations; do not invent different cardinalities without recording exactly why a DB normalization or audit-history representation differs. The diagram views CD-01/02/03 are **all mandatory**. Preserve the D5 `TournamentRegistration` XOR entrant constraint.

### CD-01 — association/multiplicity contract

```text
(source → target; multiplicities follow connector direction)



    01. GamingCafe — 1 owns / 1..* stations — GamingStation                                                                                                                                                                                    08. GamingSession — 0..1 incurs / 0..1 charge — Charge


    02. GamingCafe — 1 serves / 0..* members — Customer                                                                                                                                                                                      09. Charge — 1 settled by / 0..* payments — Payment


    03. Customer — 1 places / 0..* bookings — Reservation                                                                                                                                                                                       10. Payment — 1 issues / 0..1 receipt — Receipt


    04. GamingStation — 1 assigned / 0..* bookings — Reservation                                                                                                                                                                                11. Customer — 1 holds / 0..* memberships — Membership


    05. Reservation — 0..1 leads to / 0..1 session — GamingSession                                                                                                                                                                              12. MembershipPlan — 1 defines / 0..* subscriptions — Membership


    06. Customer — 1 starts / 0..* sessions — GamingSession                                                                                                                                                                                     13. Membership — 0..1 generates / 0..* charges — Charge


    07. GamingStation — 1 hosts / 0..* sessions — GamingSession                                                                                                                                                                                14. StandardPricingPolicy — realizes — PricingPolicy
```
- [ ] `CD-01`: each named association implemented with actual Java reference(s), identity/ownership rule, FK/uniqueness constraints, test and traceability mapping.

### CD-02 — association/multiplicity contract

```text
(source → target; multiplicities follow connector direction)



    01. Customer — 1 member / 0..* team links — TeamMembership                                                                                                                                                      07. TournamentRegistration — 0..1 fee / 0..1 charge — Charge


    02. GamingTeam — 1 team / 1..* roster records — TeamMembership                                                                                                                                                  08. Tournament — 1 schedule / 0..* matches — TournamentMatch


    03. Customer — 1 captain / 0..* created teams — GamingTeam                                                                                                                                                       09. TournamentMatch — 1 match / 0..1 result — MatchResult


    04. Tournament — 1 tournament / 0..* entrants — TournamentRegistration                                                                                                                                             10. Staff — 0..* organizers / 0..* events — Tournament


    05. Customer — 0..1 solo entrant / 0..* entries — TournamentRegistration                                                                                                                                              11. Staff — 0..* records / 0..* matches — TournamentMatch


    06. GamingTeam — 0..1 team entrant / 0..* entries — TournamentRegistration
```
- [ ] `CD-02`: each named association implemented with actual Java reference(s), identity/ownership rule, FK/uniqueness constraints, test and traceability mapping.

### CD-03 — association/multiplicity contract

```text
(source → target; multiplicities follow connector direction)



    01. Staff — is an Employee — Employee                                                                                                                                                                07. Equipment — 1 generates / 0..* tickets — MaintenanceTicket


    02. Manager — specializes Staff — Staff                                                                                                                                                                08. Staff — 1 reports / 0..* tickets — MaintenanceTicket


    03. TournamentOrganizer — specializes Staff — Staff                                                                                                                                                    09. Staff — 1 supervises / 0..* sessions — GamingSession


    04. GamingCafe — 1 employs / 0..* employees — Employee                                                                                                                                             10. Staff — 1 handles / 0..* payments — Payment


    05. GamingCafe — 1 contains / 1..* stations — GamingStation                                                                                                                                            11. Manager — 1 requests / 0..* reports — ManagementReport


    06. GamingStation — 1 includes / 1..* devices — Equipment                                                                                                                                              12. GamingCafe — 1 retains / 0..* reports — ManagementReport
```
- [ ] `CD-03`: each named association implemented with actual Java reference(s), identity/ownership rule, FK/uniqueness constraints, test and traceability mapping.

### D5 controllers, repository ports, GRASP and patterns

```text
GameHub | Controllers, Interfaces and GRASP

Three-tier design: boundaries delegate system events to thin controllers; domain experts own business rules; repositories own persistence.



  APPLICATION CONTROL / GRASP CONTROLLER                                                DOMAIN POLICIES / INFORMATION EXPERT                                                       PERSISTENCE PORTS / PURE FABRICATION


    «controller»                                                                          «controller»                                                                                         «interface»                                                                                                                                                                        «interface»                                                                        «interface»
    AccountController                                    ReservationController                                                  PricingPolicyuses port                             implements             StandardPricingPolicyuses        uses port                                            CustomerRepository                                    StationRepository



   + registerCustomer(data)                                        + confirmReservation(criteria)                                                                                                                                     + quoteSession(...): Decimal                                     + quoteSession(...): Decimal                                               + existsByEmailOrPhone(...): Boolean                              + findById(id): Entity
    + quoteMembership(...): Decimal                                  + quoteMembership(...): Decimal                                           + save(customer): void                                         + save(entity): void




    uses




    «controller»                                                                          «controller»
    uses                                                                                 uses port                uses port
    SessionController                                    PaymentController



   + startOrEndSession(id)                                        + recordPayment(details)
    «interface»                                                                        «interface»
    «interface»                                                                                                                                                                                                                                                      ReservationRepository                               PaymentRepository                                                                                                                                                                                                                                      implements           CashPaymentProcessor                                                                                                                           PaymentProcessor


    + hasOverlap(station, range): Boolean                              + findById(id): Entity
    + verify(ref, amount): Boolean                                    + verify(ref, amount): Boolean                                             + saveAtomically(reservation): void                                + save(entity): void



    «controller»                                                                          «controller»
    uses                                                                                                                      uses port
    MembershipController                                   TeamController



   + activateMembership(plan)                                      + createOrJoinTeam(data)





    «interface»                                                                        «interface»
    MembershipRepository                                TeamRepository
    Reservation                                      GamingStation

    «controller»                                                                          «controller»
    uses                                uses port                                       saves registration
    RegistrationController                                TournamentController                                                                                                                                                               + findById(id): Entity                                           + findById(id): Entity
    + save(entity): void                                            + save(entity): void
    + overlaps(interval): Boolean                                     + isOperational(): Boolean
    + confirm(): void                                              + canHost(interval): Boolean
   + registerEntrant(tournament)                                    + openCloseRegistration(id)





    «controller»                                                                          «controller»
    uses                                                uses
    MatchController                                         StationController                                                                                                                                                                                                                                                                     «interface»                                                                        «interface»
    TournamentRepository                                 MatchRepository

   + scheduleAndRecordResult(id)                                   + createStation(details)                                                            Tournament                                   TournamentMatch

    + findById(id): Entity                                           + findById(id): Entity
    + save(entity): void                                            + save(entity): void

    + hasSpace(): Boolean                                         + recordResult(score): void
    + openRegistration(): void





    «controller»                                                                          «controller»
    uses                                                                  uses port                                                               uses port
    MaintenanceController                                    ReportController



   + createResolveTicket(id)                                       + generateReport(criteria)

    «interface»                                                                        «interface»
    MaintenanceRepository                                 ReportRepository


    MaintenanceTicket
    + findById(id): Entity                                           + findById(id): Entity
    + save(entity): void                                            + save(entity): void


    + resolve(note): void





    «interface»
    RegistrationRepository




    + save(registration): void
    + findConfirmed(tournamentId): List





  GRASP RESPONSIBILITY ASSIGNMENTS — concrete design decisions


    Controller                                                                                                                                                   Information Expert
    ReservationController receives confirmBooking(...); delegates pricing and overlap checks.                                                                            GamingStation knows operational status; Reservation knows its interval; Charge knows balance.



    Creator                                                                                                                Low Coupling
    GamingCafe creates owned GamingStation; GamingTeam creates roster TeamMembership records.                                                                             Controllers depend on PricingPolicy, PaymentProcessor and Repository interfaces.



    High Cohesion                                                                                                                                Pure Fabrication
    PaymentController orchestrates; Charge calculates remaining balance; repositories persist.                                                                                  Repositories isolate database concerns; ReportExporter isolates file-format output.



    Polymorphism                                                                                                                                           Protected Variations
    StandardPricingPolicy implements PricingPolicy; CashPaymentProcessor implements PaymentProcessor.                                                                        Pricing and payment implementations can change without modifying client controllers.





    GRASP rationale: Controller handles system events, Information Expert keeps validation/state transitions in entities, Creator chooses aggregates for creating owned objects; Low Coupling comes from repository/policy interfaces, High Cohesion keeps controllers orchestration-only, and Pure Fabrication places database and payment adapters outside domain entities.
```
**Non-optional GRASP validation:** Controller, Information Expert, Creator, Low Coupling, High Cohesion, Protected Variations and applicable GoF interfaces/Strategy must be tied to code, not just mentioned. Avoid database logic in JavaFX controllers, and never confuse D5 application controllers with JavaFX event-handler classes.

## Annex G. Deliverable 5: all 12 internal design sequence diagrams (full extracted interactions)


### UC-01 — design-sequence collaborations

```text
GameHub | UC-01 Create Customer Account

Design Sequence Diagram (SD) • expands D4 SSD into boundary / controller / entity / data interactions




    «actor»                              «boundary»                                      «control»                                         «entity»                                      «repository»                                 «service»
  Customer / Gamer                        RegistrationView                          AccountController                          Customer                           CustomerRepository                    PasswordHasher



    requestCreateAccount()



    openRegistration()



    registrationForm(fields)



    display form



    enterRegistrationInfo(name,email,phone,credentials)



    validateFields(data)



    fieldValidation(valid)



    submitRegistrationRequest(email,phone)



    checkIdentifier(email,phone)



    existsByEmailOrPhone(email,phone)



    identifierCheck(available)



    identifier status



    confirmRegistration(confirmed)
  opt [identifier not already registered]

    createCustomer(data)



    hash(credentials)



    passwordHash



    «create» Customer(data,passwordHash)



    save(customer)



    customerId



    accountCreated(customerId,Active)



    registration confirmation





  Solid arrows: calls • dashed arrows: returns • opt: guarded success-path behavior • exceptional paths are documented in D3 use-case extensions.
```
- [ ] `UC-01`: every design-sequence responsibility is implemented or documented in `docs/DECISIONS.md` with equivalent replacement; no absent repository/port interactions.

### UC-02 — design-sequence collaborations

```text
GameHub | UC-02 Reserve Gaming Station

Design Sequence Diagram (SD) • expands D4 SSD into boundary / controller / entity / data interactions




    «actor»                              «boundary»                                      «control»                                         «entity»                                      «repository»                                 «service»
  Customer / Gamer                       ReservationView                         ReservationController                        GamingStation                         ReservationRepository                        PricingPolicy



    requestStationReservation()



    getReservationOptions()



    reservationOptions(criteria)



    display options



    searchAvailableStations(date,start,minutes,type)



    findAvailableStations(criteria)



    findMatchingStations(criteria)



    candidate station IDs



    isOperationalAndAvailable(interval)



    matchingStations



    matchingStations



    selectAvailableStation(stationId)



    quoteStation(stationId,minutes)



    quoteStation(station,minutes)



    estimatedChargePKR



    stationQuote(details,charge)



    confirmStationReservation(stationId,interval)



    confirmBooking(customerId,stationId,interval)



    hasOverlap(stationId,interval)



    no overlap



    isOperationalAndAvailable(interval)



    available



    saveAtomically(new Reservation)
  opt [station remains available]

    reservationId



    reservationConfirmed(id,status,details)



    booking confirmation





  Solid arrows: calls • dashed arrows: returns • opt: guarded success-path behavior • exceptional paths are documented in D3 use-case extensions.
```
- [ ] `UC-02`: every design-sequence responsibility is implemented or documented in `docs/DECISIONS.md` with equivalent replacement; no absent repository/port interactions.

### UC-03 — design-sequence collaborations

```text
GameHub | UC-03 Conduct Gaming Session

Design Sequence Diagram (SD) • expands D4 SSD into boundary / controller / entity / data interactions




    «actor»                              «boundary»                                      «control»                                         «entity»                                      «repository»                                 «service»
    Staff Member                          SessionView                             SessionController                        GamingSession                           StationRepository                          PricingPolicy



    identifyCustomer(customerId,reservationId?)



    findCustomerAndBooking(ids)



    findValidReservationAndStation(ids)



    customer, booking, station



    customerReservationStationDetails



    confirmStationAssignment(stationId)



    validateStationAssignment(stationId)



    findStation(stationId)



    station status



    stationCheck(operational,available)



    startGamingSession(customerId,stationId)
  opt [station operational and unoccupied]

    startSession(ids)



    «create» GamingSession and start()



    markOccupied(stationId)



    station occupied



    sessionStarted(id,start,status)



    endGamingSession(sessionId)



    endSession(sessionId)



    close(now)



    durationMinutes



    quoteStation(station,duration)



    chargePKR



    usageSummary(duration,charge)



    confirmSessionCompletion(sessionId)



    finalizeSession(sessionId)



    markAvailable(stationId)



    station available



    sessionClosed(id,status)





  Solid arrows: calls • dashed arrows: returns • opt: guarded success-path behavior • exceptional paths are documented in D3 use-case extensions.
```
- [ ] `UC-03`: every design-sequence responsibility is implemented or documented in `docs/DECISIONS.md` with equivalent replacement; no absent repository/port interactions.

### UC-04 — design-sequence collaborations

```text
GameHub | UC-04 Process Payment

Design Sequence Diagram (SD) • expands D4 SSD into boundary / controller / entity / data interactions




    «actor»                              «boundary»                                      «control»                                         «entity»                                      «repository»                                 «service»
    Staff Member                        PaymentView                           PaymentController                           Charge                            PaymentRepository                    PaymentProcessor



    selectPayableTransaction(chargeId)



    getOutstandingCharge(chargeId)



    remainingBalance()



    duePKR



    paymentDetails(due,methods)



    enterPaymentDetails(method,amount,reference)



    validatePaymentDetails(data)



    remainingBalance()



    balancePKR



    paymentValidation(valid,balance,change)



    confirmReceiptOfPayment(chargeId,method,paidPKR)



    processPayment(data)



    verify(reference,amount)



    approved



    save(new Payment)
  opt [payment verified successfully]

    paymentId



    markSettled() [if paid in full]



    saveReceipt(paymentId)



    receiptNo



    paymentRecorded(paymentId,status,receiptNo)



    display receipt





  Solid arrows: calls • dashed arrows: returns • opt: guarded success-path behavior • exceptional paths are documented in D3 use-case extensions.
```
- [ ] `UC-04`: every design-sequence responsibility is implemented or documented in `docs/DECISIONS.md` with equivalent replacement; no absent repository/port interactions.

### UC-05 — design-sequence collaborations

```text
GameHub | UC-05 Subscribe or Renew Membership

Design Sequence Diagram (SD) • expands D4 SSD into boundary / controller / entity / data interactions




    «actor»                              «boundary»                                      «control»                                         «entity»                                      «repository»                                 «service»
  Customer / Gamer                     MembershipView                       MembershipController                       Membership                        MembershipRepository                  PaymentProcessor



    requestMembershipPlans()



    listActivePlans()



    findActivePlans()



    planIds,prices,durations



    availablePlans(...)



    selectMembershipPlan(planId)



    getPlanAndCurrentStatus(planId)



    findPlanAndCustomerMembership(planId)



    plan + current membership



    planDetails,currentStatus



    confirmMembershipChoice(planId,mode)



    quoteSubscriptionOrRenewal(planId,mode)



    membershipQuote(fee,start,expiry)



    completeMembershipPayment(paymentReference)



    activatePaidMembership(reference)



    verify(reference,quotedFee)



    payment verified



    «create»/renew membership
  opt [membership payment verified]

    save(membership)



    membershipId



    membershipActivated(id,expiry,status)



    display confirmation





  Solid arrows: calls • dashed arrows: returns • opt: guarded success-path behavior • exceptional paths are documented in D3 use-case extensions.
```
- [ ] `UC-05`: every design-sequence responsibility is implemented or documented in `docs/DECISIONS.md` with equivalent replacement; no absent repository/port interactions.

### UC-06 — design-sequence collaborations

```text
GameHub | UC-06 Create or Join Gaming Team

Design Sequence Diagram (SD) • expands D4 SSD into boundary / controller / entity / data interactions




    «actor»                              «boundary»                                      «control»                                         «entity»                                      «repository»                                 «service»
  Customer / Gamer                       TeamView                              TeamController                        GamingTeam                           TeamRepository                    TeamMembership



    openTeamParticipation()



    getTeamOptions()



    teamOptions(create,join)



    show options



    chooseCreateTeam(teamName)
  alt [create team]

    validateTeamName(teamName)



    isTeamNameUnique(teamName)



    name available



    teamNameValidation(unique)



    confirmTeamCreation(teamName)
  opt [team name is unique]

    createTeam(name,captainId)



    «create» GamingTeam(name,captainId)



    save(team)



    teamId



    teamCreated(teamId,captainId)



    inviteTeamMembers(teamId,memberIds[])



    inviteMembers(teamId,ids)



    «create» pending TeamMembership per member



    saveInvitations(teamId,members)



    pending invitation records



    invitationStatus(roster,status)



    show roster



    chooseJoinExistingTeam(teamId)
  [join existing team]

    requestJoinTeam(teamId,customerId)



    findById(teamId)



    team, roster, status



    «create» TeamMembership(Pending)



    savePendingMembership(teamId,customerId)



    joinRequestId



    joinRequestRecorded(requestId)



    display join-request confirmation





  UC-06 covers both mutually exclusive paths: create a team OR request to join an existing team.




  Solid arrows: calls • dashed arrows: returns • opt: guarded success-path behavior • exceptional paths are documented in D3 use-case extensions.
```
- [ ] `UC-06`: every design-sequence responsibility is implemented or documented in `docs/DECISIONS.md` with equivalent replacement; no absent repository/port interactions.

### UC-07 — design-sequence collaborations

```text
GameHub | UC-07 Register for Tournament

Design Sequence Diagram (SD) • expands D4 SSD into boundary / controller / entity / data interactions




    «actor»                              «boundary»                                      «control»                                         «entity»                                      «repository»                                 «service»                                           «repository»
Customer / Team Captain                    RegistrationView                          RegistrationController                        Tournament                         TournamentRepository                  PaymentProcessor                             RegistrationRepository



    viewOpenTournaments()



    findOpenTournaments()



    findOpen()



    open tournaments



    openTournaments(tournamentIds)



    selectTournament(tournamentId)



    getTournamentDetails(id)



    findById(id)



    format,schedule,capacity,fee



    tournamentDetails(...)



    selectEntrant(type,entrantId)



    validateEntrant(type,entrantId)



    hasSpace() and isRegistrationOpen()



    available



    validateSoloOrTeamRoster(entrantId)



    entrant eligible



    eligibilityCheck(eligible)



    confirmTournamentRegistration(tournamentId,entrantId)
   opt [entrant eligible]

    prepareRegistration(tournamentId,entrantId)



    registrationSummary(fee,entrant)



    completeEntryFeeCondition(paymentReference?)



    confirmRegistrationAndFee(reference?)



    verify(reference,fee) [if required]
   opt [entry fee > 0]

    feeStatus



    save(new TournamentRegistration)



    registrationId



    registrationConfirmed(id,feeStatus)



    show confirmation





   Solid arrows: calls • dashed arrows: returns • opt: guarded success-path behavior • exceptional paths are documented in D3 use-case extensions.
```
- [ ] `UC-07`: every design-sequence responsibility is implemented or documented in `docs/DECISIONS.md` with equivalent replacement; no absent repository/port interactions.

### UC-08 — design-sequence collaborations

```text
GameHub | UC-08 Manage Tournament

Design Sequence Diagram (SD) • expands D4 SSD into boundary / controller / entity / data interactions




    «actor»                              «boundary»                                      «control»                                         «entity»                                      «repository»                                 «service»
 Tournament Organizer                TournamentAdminView                     TournamentController                        Tournament                         TournamentRepository                   RegistrationRepository



    requestCreateTournament()



    loadTournamentForm()



    configurationFields



    display form



    enterTournamentConfiguration(details)



    validateConfiguration(details)



    configurationValidation(valid)



    confirmTournamentDetails()
  opt [configuration valid]

    createTournament(details)



    «create» Tournament(status=Draft)



    save(tournament)



    tournamentId



    tournamentCreated(id,Draft)



    openTournamentRegistration(tournamentId)



    openRegistration(id)



    openRegistration()



    save(tournament)



    registrationOpen(id,status)



    closeTournamentRegistration(tournamentId)



    closeRegistration(id)



    closeRegistration()



    listConfirmedEntrants(id)



    confirmedEntrants[]



    save(tournament)



    registrationClosed(id,entrants)





  Solid arrows: calls • dashed arrows: returns • opt: guarded success-path behavior • exceptional paths are documented in D3 use-case extensions.
```
- [ ] `UC-08`: every design-sequence responsibility is implemented or documented in `docs/DECISIONS.md` with equivalent replacement; no absent repository/port interactions.

### UC-09 — design-sequence collaborations

```text
GameHub | UC-09 Conduct Tournament Matches

Design Sequence Diagram (SD) • expands D4 SSD into boundary / controller / entity / data interactions




    «actor»                              «boundary»                                      «control»                                         «entity»                                      «repository»                                 «service»
 Tournament Organizer                      MatchView                              MatchController                        TournamentMatch                         MatchRepository                       StandingsService



    viewTournamentMatches(tournamentId)



    loadBracketAndMatches(id)



    findMatchesAndBracket(id)



    pendingMatches,bracket



    matchView(participants,bracket)



    scheduleTournamentMatch(matchId,when,resources)



    scheduleMatch(id,when,resources)



    checkScheduleConflicts(when,resources)



    no conflict



    saveMatchSchedule(id,when,resources)
  opt [no schedule or resource conflict]

    matchAssignmentSaved(id)



    startTournamentMatch(matchId)



    startMatch(id)



    start() [InProgress]



    save(match)



    matchStatus(InProgress)



    enterFinalMatchResult(matchId,scores,winner)



    validateSubmittedScore(scores,winner)



    validateScore(scores,winner)



    formatConsistent



    resultValidation(valid)



    confirmMatchResult(matchId)
  opt [result valid]

    recordMatchResult(id)



    finish(result)



    saveMatchAndResult(match,result)



    advanceBracketAndStandings(id)



    standings,status



    resultRecorded(id,standings)



    updated bracket





  Solid arrows: calls • dashed arrows: returns • opt: guarded success-path behavior • exceptional paths are documented in D3 use-case extensions.
```
- [ ] `UC-09`: every design-sequence responsibility is implemented or documented in `docs/DECISIONS.md` with equivalent replacement; no absent repository/port interactions.

### UC-10 — design-sequence collaborations

```text
GameHub | UC-10 Manage Gaming Stations

Design Sequence Diagram (SD) • expands D4 SSD into boundary / controller / entity / data interactions




    «actor»                              «boundary»                                      «control»                                         «entity»                                      «repository»                                 «service»
Manager / Administrator                   StationAdminView                            StationController                          GamingStation                            StationRepository                          StationValidator



    openGamingStationAdministration()



    listStations()



    findAll()



    stations,statuses



    stationList(stations,status)



    chooseAddNewStation()



    getStationFields()



    newStationFields



    enterStationDetails(code,type,specs,rate,status)



    validateStationDetails(details)



    validate(details)



    valid



    isCodeUnique(code)



    unique



    stationValidation(valid,unique)



    confirmNewStation(code)
  opt [station details valid]

    createStation(details)



    «create» GamingStation(details)



    save(station)



    stationId



    stationCreated(id,status)



    reviewGamingStationList()



    getUpdatedStations()



    findAll()



    updated stations



    updatedStationList(stations,availability)





  Solid arrows: calls • dashed arrows: returns • opt: guarded success-path behavior • exceptional paths are documented in D3 use-case extensions.
```
- [ ] `UC-10`: every design-sequence responsibility is implemented or documented in `docs/DECISIONS.md` with equivalent replacement; no absent repository/port interactions.

### UC-11 — design-sequence collaborations

```text
GameHub | UC-11 Handle Equipment Maintenance

Design Sequence Diagram (SD) • expands D4 SSD into boundary / controller / entity / data interactions




    «actor»                              «boundary»                                      «control»                                         «entity»                                      «repository»                                 «service»
    Staff Member                        MaintenanceView                        MaintenanceController                       MaintenanceTicket                      MaintenanceRepository                     Equipment



    reportEquipmentFault(equipmentId)



    findEquipmentForFault(id)



    loadEquipmentStatus(id)



    equipment,station



    equipmentDetails,faultForm



    enterFaultDetails(description,severity,notes)



    validateFaultDetails(details)



    faultValidation(valid)



    submitMaintenanceIssue(equipmentId)



    openMaintenanceTicket(id,details)



    «create» MaintenanceTicket



    markFaulty()



    save(ticket)



    ticketId



    ticketCreated(id,equipmentStatus)



    recordRepairProgress(ticketId,status,notes)



    updateTicket(id,status,notes)



    logProgress(notes,status)



    save(ticket)



    ticketUpdated(id,status)



    confirmReadyForService(ticketId)



    resolveTicket(id)
  opt [repair verified]

    resolve(notes)



    restoreService()



    save(ticket)



    maintenanceClosed(id,operational)



    status updated





  Solid arrows: calls • dashed arrows: returns • opt: guarded success-path behavior • exceptional paths are documented in D3 use-case extensions.
```
- [ ] `UC-11`: every design-sequence responsibility is implemented or documented in `docs/DECISIONS.md` with equivalent replacement; no absent repository/port interactions.

### UC-12 — design-sequence collaborations

```text
GameHub | UC-12 Generate Management Reports

Design Sequence Diagram (SD) • expands D4 SSD into boundary / controller / entity / data interactions




    «actor»                              «boundary»                                      «control»                                         «entity»                                      «repository»                                 «service»
Manager / Administrator                      ReportView                              ReportController                       ManagementReport                         ReportRepository                        ReportExporter



    openReportingFeature()



    getReportOptions()



    reportOptions(types,filters)



    show reporting form



    chooseReportCriteria(type,dates,filters)



    validateReportCriteria(criteria)



    criteriaValidation(valid)



    requestReportGeneration(type,dates,filters)



    generateReport(criteria)



    aggregatePaymentsSessionsReservations(criteria)



    totals,detailRows



    «create» ManagementReport(totals,rows)



    save(report)



    reportId



    generatedReport(reportId,totals,rows)



    exportOrPrintReport(reportId,format)
  opt [export requested]

    exportReport(id,format)



    export(report,format)



    fileName,bytes



    reportOutput(fileName,format)



    file available





  Solid arrows: calls • dashed arrows: returns • opt: guarded success-path behavior • exceptional paths are documented in D3 use-case extensions.
```
- [ ] `UC-12`: every design-sequence responsibility is implemented or documented in `docs/DECISIONS.md` with equivalent replacement; no absent repository/port interactions.

## Annex H. Conflict-resolution and completion rules

1. **Rubric wins for JavaFX:** D2 mentions Swing/JavaFX as options; final rubric is JavaFX mandatory.
2. **D3 wins for alternate flows:** D4 SSDs show main-success only. Implement all D3 extensions even if unshown in D4/D5.
3. **D5 wins for software responsibility assignment:** domain model is conceptual, not permission to couple UI and persistence.
4. **Preserve D4 mandated personnel demonstration separately:** academic employee hierarchy is a submission obligation, not a café permission/role taxonomy; D5 explicitly warns of this distinction.
5. **No phantom real payments:** offline cash/manual external verification; explicitly label no payment gateway.
6. **No fake station occupancy:** a future reservation is time-window unavailability, not a permanently occupied physical station.
7. **Solo/team XOR registration:** exactly one customer or gaming team in every tournament registration; enforce in domain and DB.
8. **All domain timestamps and invoices are durable:** role, cancel, payment, correction, maintenance audit must persist and be testable across restarts.
9. **Every D3 step maps to SSD / D5 message:** where names differ, implement the same observable behavior with adapters/typed DTOs; note the mapping, never silently remove features.
10. **Documentation rubric:** finish instructor-template documentation if that template is available; otherwise produce comprehensive headings and legible diagrams exported from a UML/design tool, not AI-generated drawings.
11. **Individual marking:** record real per-member contributions and demo responsibility, not a fabricated equal division.
12. **Done means verified:** no progress box is checked merely because source code exists. Record actual build/test/UI/DB evidence and known limits.

### Acceptance matrix — actual evidence required

- [ ] D2 scope and exclusions reconciled into all feature modules.
- [ ] All 12 D3 use-case flows **and extensions** run with real persisted data and user-visible outcomes.
- [ ] All 12 D4 SSD event sequences have real JavaFX→application code trace and tests.
- [ ] D4 academic Java hierarchy, 4+2+3 sample, override/polymorphism and payroll bounds compile and pass unit tests.
- [ ] D5 all three CD association/multiplicity views map to source/SQL.
- [ ] D5 all twelve detailed SDs map to application controllers/services/entities/repos.
- [ ] All nine rubric dimensions have direct evidence; JavaFX app launches and data persists.
- [ ] `mvn clean verify` / Maven Wrapper and JavaFX startup are actually executed, not assumed.
- [ ] Real UI end-to-end journeys: café reservation→session→charge→payment; teams→tournament→registration→matches; maintenance→availability; DB→management reports.

**Final Codex command:** Starting with an EMPTY DIRECTORY containing only this file, create the entire JavaFX/Maven/SQLite application from scratch. Execute the entire standalone plan, treating Annexes A–H as source contracts. Do not stop at scaffolding. Implement one vertical use case at a time; compile and test after each. Update `[ ]` only after evidence. Finish by reporting source files, commands/results, each UC status, failures, and how to run the real JavaFX application. If the app wasn't actually launched, say so; never claim success without tests.


**NON-NEGOTIABLE GREENFIELD REMINDER:** You receive **no other files**. If a line mentions an existing file, diagram, source folder, build, or original PDF, treat it as nonexistent and create the required artifact from this Markdown specification. Use only generated project files and real installed dependencies. Continue with implementation without asking for the original deliverables.
