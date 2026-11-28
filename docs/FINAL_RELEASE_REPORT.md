# GameHub Final Production Release Report

## Executive summary

The release candidate branch `gamehub-final-release` was created from the latest `origin/gamehub-final-functional` tip `e1850a23906011460339de7708bfb6d115f5c162`. The branch preserves the existing history and main backup. The production application compiles and the current regression suite passes 36 tests.

The release work includes the functional closure changes, maintenance reassignment UI, bidirectional station allocation checks, atomic session fault completion, registration lifecycle audit events, CSS palette refinement, release documentation, and a manual GUI acceptance script.

Release promotion is gated. Desktop interaction automation is unavailable in this environment. A successful JavaFX process launch was observed, but no visible control actions, screenshots, or interaction log were produced. Additional legacy read APIs and full match-result recalculation remain mandatory review items.

## Branch and commit

- Release branch: `gamehub-final-release`
- Starting functional baseline: `e1850a23906011460339de7708bfb6d115f5c162`
- Release commit: recorded after the final verification commit
- Main branch: not modified
- Release tag: not created

## Functional changes

- Match scheduling checks overlap with matches, reservations, and active sessions.
- Reservation creation checks scheduled tournament matches.
- Maintenance exposes affected reservations, replacement-station selection, reassignment, and cancellation.
- Session-end fault reporting completes charge, session, maintenance, station, and audit changes in one transaction.
- Tournament cancellation propagates to registrations, pending charges, and scheduled match allocations.
- Registration creation, payment confirmation, and cancellation write audit records.
- Customer reservation reads have an explicit actor-target authorization API.
- Team rosters are locked while registered in a tournament.
- Receipt history is available in the Payments screen.

## UI changes

- Added visible maintenance reassignment controls.
- Refined the existing stylesheet palette toward violet and cyan accents while preserving layout and event wiring.
- Added release and manual acceptance instructions.
- No UI framework or business-layer rewrite was introduced.

## Automated verification

Command:

```text
cmd /c mvnw.cmd clean verify
```

Result from the release baseline: **36 tests passed, 0 failed, 0 errors, 0 skipped**.

The added tests cover match allocation conflicts, tournament cancellation, maintenance duplicates and affected reservations, registration audit events, cross-customer reservation denial, and atomic session-end fault completion.

## Database verification

An isolated database was initialized through the JavaFX launch path. SQLite JDBC returned `PRAGMA integrity_check => ok`. `PRAGMA foreign_key_check` returned no rows. No production database was used.

## GUI verification

`cmd /c mvnw.cmd javafx:run` reached the JavaFX run phase with an isolated database and no startup exception. TestFX or another desktop interaction mechanism is not available in the environment. All screen interaction remains unverified. The exact manual script is `docs/MANUAL_ACCEPTANCE_SCRIPT.md`.

## UC-01 through UC-12 matrix

| Use case | Implemented | Service tested | GUI verified | Fully compliant |
|---|---|---|---|---|
| UC-01 Accounts | Yes | Yes | No | Pending GUI |
| UC-02 Reservations | Yes | Yes | No | Pending GUI |
| UC-03 Sessions | Yes | Yes | No | Pending GUI |
| UC-04 Payments | Yes | Yes | No | Pending GUI |
| UC-05 Membership | Yes | Yes | No | Pending GUI |
| UC-06 Teams | Yes | Yes | No | Pending GUI |
| UC-07 Registration | Yes | Yes | No | Pending GUI |
| UC-08 Tournament management | Yes | Yes | No | Pending GUI and lifecycle audit review |
| UC-09 Matches | Partial | Yes | No | Result-correction recalculation review pending |
| UC-10 Stations | Yes | Yes | No | Pending GUI |
| UC-11 Maintenance | Yes | Yes | No | Pending GUI |
| UC-12 Reports | Yes | Yes | No | Pending GUI and read-authorization sweep |

## Deliverables and architecture

Deliverables 2-5 remain represented by the JavaFX presentation layer, application services and controllers, JDBC repositories, SQLite schema, domain pricing strategy, transaction context, audit repository, and personnel polymorphism hierarchy. The architecture remains Presentation → Application → Infrastructure. No framework replacement or unrelated refactoring was performed.

## Rubric estimate

| Category | Maximum | Estimated range |
|---|---:|---:|
| User interface | 10 | 5-7 |
| Business logic | 15 | 10-13 |
| OOP | 15 | 10-13 |
| Database | 10 | 9-10 |
| Architecture and integration | 10 | 7-9 |
| Use cases | 10 | 6-8 |
| Design-to-code consistency | 10 | 6-8 |
| GRASP and GoF | 10 | 6-8 |
| Documentation | 10 | 7-9 |
| **Total** | **100** | **66-85** |

This is an engineering estimate, not an instructor grade.

## Remaining limitations

- GUI acceptance has not been executed through desktop controls.
- Several legacy operational read methods still lack actor-aware authorization, including session, outstanding-charge, maintenance-ticket, match-detail, and some station-inventory reads.
- Match correction safely rejects winner changes after downstream rounds rather than rebuilding already-played dependent matches.
- Main promotion and `v1.0.0` tagging remain intentionally gated by these conditions.

## Build and launch

```text
cmd /c mvnw.cmd clean verify
cmd /c mvnw.cmd javafx:run
```

Use `GAMEHUB_DB_PATH` to isolate the database.

## Submission readiness verdict

**BLOCKED — MANDATORY FUNCTIONAL DEFECTS REMAIN**
