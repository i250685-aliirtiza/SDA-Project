# GameHub test report

## Verification on 27 November 2026

| Check | Result |
| --- | --- |
| Windows build with JDK 21 | `mvnw.cmd -o test-compile` passed. |
| Automated tests | 26 passed using the JUnit Jupiter engine directly. Coverage includes reservation interval boundaries, station rate changes during an active session, and attempts to record a result before a match starts. |
| Maven Verify | `mvnw.cmd clean verify` reached the test phase. The default cache lacked Surefire dependencies and the environment denied writes to that cache. |
| Existing database copy | Opened twice after the additive session-rate migration. The original row counts were preserved. SQLite integrity and foreign-key checks passed. |

## JavaFX workflow checks

The application ran with a separate SQLite database. Automation used the actual JavaFX buttons, forms, selections, and dialogs.

| Use case | Verified scenario |
| --- | --- |
| UC-01 Create Customer Account | Created the manager and staff accounts, checked invalid login feedback, created customers, and confirmed the customer list refreshed immediately. |
| UC-02 Reserve Gaming Station | Found a station, changed the search interval, confirmed a new reservation, cancelled it, and verified the cancellation after restart. |
| UC-03 Conduct Gaming Session | Started, extended, and ended a walk-in. Checked a reserved customer in and ended that session. |
| UC-04 Process Payment | Recorded staff payments and checked the receipt confirmation. |
| UC-05 Subscribe or Renew Membership | Requested a paid plan, settled the charge as staff, and confirmed active status as the customer. |
| UC-06 Create or Join Gaming Team | Created two teams and exercised both approval and rejection of join requests. |
| UC-07 Register for Tournament | Registered two customers for a paid event, settled both fees, and verified their entries became confirmed. |
| UC-08 Manage Tournament | Created a draft and opened and closed registration. |
| UC-09 Conduct Tournament Matches | Generated a two-entrant bracket, verified result entry was disabled before start, recorded a result, and confirmed tournament completion. |
| UC-10 Manage Gaming Stations | Created a station via the manager screen and selected it in downstream workflows. |
| UC-11 Handle Equipment Maintenance | Reported an issue, progressed the ticket, and resolved it with a readiness check. |
| UC-12 Generate Management Reports | Generated a report as manager and verified the staff navigation excluded reports. |

## UI redesign verification

- Recompiled with JDK 21 and reran all 26 tests through the JUnit Jupiter engine. All passed.
- Reran the JavaFX scenarios across all 12 use-case areas and checked that the customer directory finds a player by phone and shows an empty state for unmatched searches.
- Inspected rendered sign-in, overview, reservations, and payment screens at standard and compact window sizes. Also inspected the player dashboard and customer directory at the standard size.

Full keyboard and assistive-technology coverage, remaining alternate flows, and final submission artifacts still need review.

## Bug-run verification

- All 30 JUnit tests passed after recompiling with JDK 21. The JUnit engine was run directly using the cached dependencies.
- Regression coverage includes four- and eight-player knockout brackets, odd brackets with byes, round-robin completion, exact minute billing, duplicate session check-in, and stations with multiple maintenance tickets.
- The JavaFX run passed across all 12 use-case areas with an isolated database. It also checked that switching charges clears the previous payment reference and that a match state change clears a draft score.
- An isolated copy of the existing database reopened twice and passed row-count, integrity, and foreign-key checks.
