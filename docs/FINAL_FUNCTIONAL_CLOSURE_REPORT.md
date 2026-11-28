# GameHub Final Functional Closure Report

## 1. Result

Baseline commit: `683ae5e0bcdf9b6218a8f341b679370c881fc2a0`

Final branch: `gamehub-final-functional`

Functional implementation commit: `ebedd08ee81c63fde8145a0155a2ab5637988eeb`

Documentation tip after closure report update: see the final branch commit reported with the verification result.

The closure branch implements the primary audited defects and adds regression coverage. The Maven verification suite passes with 34 tests. Four new tests cover match allocation conflicts, tournament cancellation propagation, maintenance duplicate and affected reservations, and registration auditing.

The JavaFX application starts through `mvnw.cmd javafx:run` with an isolated database and remains running without a startup exception. The process was stopped manually after launch observation. No reproducible JavaFX click automation or TestFX suite is present, so GUI interaction evidence remains pending.

**IMPLEMENTED — GUI VERIFICATION PENDING**

## 2. Changes by audit defect

| Defect | Resolution | Main files and methods | Automated evidence |
|---|---|---|---|
| F01 Match scheduling conflicts | Added `duration_minutes` to match persistence and transactional checks against other scheduled matches, reservations, and active sessions. Conflicting assignments return an explanatory error. | `V1__init.sql`, `Database`, `MatchRepository.scheduleConflict`, `MatchService.scheduleMatch` | `FunctionalClosureRegressionTest.matchSchedulingRejectsTheSameStationAndTime` |
| F02 Tournament cancellation | Cancellation now runs in one transaction. It cancels pending and confirmed registrations, cancels pending tournament charges, cancels scheduled or in-progress matches, clears their station allocation, and writes an audit event. Paid payment rows remain historical. | `TournamentService.changeTournamentStatus`, `TournamentRegistrationRepository.cancelForTournament`, `PaymentRepository.cancelTournamentCharges`, `TournamentRepository.cancelMatches` | `FunctionalClosureRegressionTest.tournamentCancellationCancelsRegistrationsAndPendingCharges` |
| F03 Duplicate maintenance faults | An unresolved ticket with the same station and normalized description is rejected with the existing ticket number. Distinct descriptions remain valid. | `MaintenanceRepository.existingOpen`, `MaintenanceService.openMaintenance` | `FunctionalClosureRegressionTest.maintenanceReportsAffectedReservationsAndRejectsDuplicateFaults` |
| F04 Affected reservations | Future confirmed reservations on a station reported for maintenance are marked `AFFECTED`. Staff can view them and cancel them from the Maintenance screen. A service-level reassignment path validates replacement availability and conflicts. | `ReservationRepository.markAffected`, `affectedReservations`, `cancelAffected`, `reassignAffected`, `MaintenanceService`, `MaintenanceController`, `GameHubApp.maintenancePage` | `FunctionalClosureRegressionTest.maintenanceReportsAffectedReservationsAndRejectsDuplicateFaults` |
| F05 Registration auditing | Individual and team registration creation write actor, entity, operation, and timestamp audit rows. Full-payment confirmation writes a registration payment-confirmed audit row. | `TournamentRegistrationService`, `PaymentService`, `TournamentRegistrationRepository.registrationForCharge` | `FunctionalClosureRegressionTest.registrationAndPaymentCreateAuditEvents` |
| F06 Match result correction | Existing correction authorization, score validation, revision increment, audit event, and dependent-round safety guard remain enforced. A winner change is rejected after a later bracket round exists instead of silently corrupting the bracket. | `MatchService.correctMatchResult`, `MatchRepository.correctMatchResultQuery2`, `AuditRepository` | Existing match correction regression paths plus full suite |
| F07 Read authorization | Customer directory reads require staff or manager identity. Customer reservation reads validate the supplied customer account. Report counts, report summaries, and report exports require a manager actor. Actorless report summary calls now fail. | `CustomerReadService`, `AccountService`, `AccountController`, `ReservationService`, `ReportService`, `OperationsService`, `ReportController` | Existing authorization tests plus updated report callers |
| F08 Session-end maintenance | Staff finishing a session can select “Report an equipment fault” and enter a description. The session is completed normally, then the maintenance workflow is opened for the session station. | `SessionRepository.sessionStation`, `SessionService.sessionStation`, `SessionController`, `GameHubApp.sessionPage` | Compile and service suite. GUI interaction pending |
| F09 Receipts | Added authorized receipt-history retrieval and a Receipt history section on the Payments screen. Payment and receipt rows remain immutable history. | `PaymentRepository.receipts`, `PaymentService.receipts`, `PaymentController`, `GameHubApp.paymentPage` | Existing payment and export tests, compile verification |
| F10 Team roster constraints | Team approval checks for an active or pending tournament registration and rejects roster changes while the roster is locked. | `TournamentRegistrationRepository.registeredTeam`, `TeamService.decideJoin`, `TournamentRegistrationService.registerTournamentTeam` | Existing team/tournament tests and full suite |
| F11 Remaining gaps | Reconciled source documentation and retained the existing architecture. No unrelated UI redesign or database replacement was introduced. | Updated closure documentation | Full test and database checks |

## 3. Database migration

`tournament_matches.duration_minutes` was added with a default of 60 and a positive-value check. `Database.migrate()` adds the column to existing databases with `ensureColumn`, preserving existing rows. Existing match rows therefore remain schedulable with the documented one-hour default. No existing data is deleted.

Maintenance-affected reservations use the existing `reservations.status` text column with the value `AFFECTED`, so no destructive schema rewrite is required. Existing statuses and historical rows remain available.

## 4. Automated verification

Command executed from the clean branch worktree:

```text
cmd /c mvnw.cmd clean verify
```

Result: **BUILD SUCCESS**

| Metric | Result |
|---|---:|
| Production source files compiled | 59 |
| Test source files compiled | 21 |
| Tests executed | 34 |
| Passed | 34 |
| Failed | 0 |
| Errors | 0 |
| Skipped | 0 |

The new `FunctionalClosureRegressionTest` uses isolated temporary databases. Existing tests were retained and pass. Existing report-count callers were made actor-aware so the authorization change is exercised without weakening the security boundary.

## 5. Database and startup verification

The application was launched with `GAMEHUB_DB_PATH=C:\Users\Dell\AppData\Local\Temp\gamehub-final-runtime.db` using:

```text
cmd /c mvnw.cmd javafx:run
```

The JavaFX Maven goal reached its run phase and produced no startup exception during the observation period. The isolated database was created and checked with SQLite JDBC.

- `PRAGMA integrity_check` returned `ok`.
- `PRAGMA foreign_key_check` returned no rows.
- The migration-created schema contained the expected domain tables and `schema_migrations`.

No production or personal database was used for destructive testing.

## 6. GUI verification evidence

Source-level UI wiring was updated for affected reservations, session-end fault reporting, and receipt history. Controllers route to services and repositories. Error text from service exceptions reaches the shared JavaFX feedback handler.

Manual click-through remains pending for authentication, role navigation, every screen, all alternative flows, receipt retrieval, cancellation propagation, reassignment, and restart through the visible UI. The launch test proves process startup and database initialization only. It does not certify GUI interaction.

## 7. UC-01 through UC-12 compliance matrix

| Use case | Functional status | Evidence | GUI status |
|---|---|---|---|
| UC-01 Create Customer Account | Implemented with validation, duplicate checks, hashed credentials, assisted staff registration, and audit | Account and persistence tests | Pending |
| UC-02 Reserve Gaming Station | Implemented with interval overlap checks, customer conflict checks, cancellation, and affected-reservation state | Reservation regression tests | Pending |
| UC-03 Conduct Gaming Session | Implemented for reserved and walk-in sessions, extensions, billing, station state, and session-end maintenance action | Session and chained tests | Pending |
| UC-04 Process Payment | Implemented with staff authorization, partial balance, idempotency, receipts, and receipt history | Payment and export tests | Pending |
| UC-05 Subscribe or Renew Membership | Implemented with catalog plans, unpaid state, settlement activation, renewal extension, and persistence | Membership workflow tests | Pending |
| UC-06 Create or Join Gaming Team | Implemented with captain approvals, rejection, capacity, and registration roster locking | Team and tournament tests | Pending |
| UC-07 Register for Tournament | Implemented with solo/team eligibility, capacity, duplicate prevention, fee state, and registration audit | Registration and closure tests | Pending |
| UC-08 Manage Tournament | Implemented with draft configuration, lifecycle authorization, transactional cancellation propagation, and audit | Tournament workflow and closure tests | Pending |
| UC-09 Conduct Tournament Matches | Implemented with generation, byes, round robin, conflict-aware scheduling, start, results, progression, and safe correction guard | Workflow and closure tests | Pending |
| UC-10 Manage Gaming Stations | Implemented with manager authorization, unique codes, safe deactivation, rates, and persistence | Authorization and operations tests | Pending |
| UC-11 Handle Equipment Maintenance | Implemented with duplicate detection, affected reservations, multiple-ticket blocking, readiness resolution, and history | Workflow and closure tests | Pending |
| UC-12 Generate Management Reports | Implemented with manager authorization, categories, date filtering, CSV export, and protected report counts | Report export and authorization tests | Pending |

## 8. Deliverable 2-5 consistency

Deliverable 2 scope remains represented by the JavaFX desktop application, SQLite persistence, station operations, reservations, sessions, payments, memberships, teams, tournaments, matches, maintenance, and reports.

Deliverable 3 extensions are now represented for match conflicts, cancellation propagation, maintenance duplicate detection, affected reservations, session-end faults, registration audit, and roster locking. Paid payment rows remain historical on cancellation. The implementation does not claim refunds or external notifications.

Deliverable 4 system responses are routed through JavaFX feedback and service exceptions. The separate `Employee`/`Faculty`/`Staff`/`ResearchAssistant` hierarchy remains present and compiled. GUI interaction evidence is pending.

Deliverable 5 responsibilities remain mapped to controllers, focused services, repositories, `Database`, `TransactionContext`, `PricingPolicy`, and value objects. New collaborations are implemented through the existing repository and service boundaries without introducing a new framework or replacing the architecture.

## 9. Updated rubric estimate

The estimate below is not an instructor grade. Automated functional evidence improved after the closure work. GUI and documentation deductions remain until manual interaction evidence is attached.

| Category | Maximum | Estimated range |
|---|---:|---:|
| User interface | 10 | 6-8 |
| Business logic | 15 | 11-14 |
| OOP in business logic | 15 | 10-13 |
| Database | 10 | 9-10 |
| Architecture and integration | 10 | 8-9 |
| Use cases | 10 | 7-9 |
| Design-to-code consistency | 10 | 7-9 |
| GRASP and GoF | 10 | 6-8 |
| Documentation | 10 | 7-9 |
| **Total** | **100** | **71-89** |

## 10. Outstanding limitations

- GUI interactions, visible error feedback, resize behavior, and restart through the UI still require manual or TestFX verification.
- Maintenance reassignment is available through the application service. The visible UI currently exposes the cancellation follow-up path.
- The existing application still contains a legacy `OperationsService` façade for compatibility. Sensitive report calls were made actor-aware, while broader operational read methods should be included in a future authorization sweep if the instructor requires every read façade to carry an actor identity.
- Match duration is represented with a one-hour default because the original model did not define a separate match-duration field. The decision is documented by the migration and schedule conflict implementation.

## 11. CSS-only enhancement readiness

The branch remains ready for a CSS-only visual enhancement after GUI verification. The current JavaFX controls, event handlers, services, repositories, and schema do not require aesthetic changes to use the new functionality. Any future layout change for reassignment selection, detailed receipt rows, or richer match scheduling should be reviewed as a functional UI change rather than treated as stylesheet-only work.

## 12. Final verdict

**IMPLEMENTED — GUI VERIFICATION PENDING**



