# Manual JavaFX Acceptance Script

Use a fresh temporary SQLite database. Start with `GAMEHUB_DB_PATH=<temporary-path> mvnw.cmd javafx:run`.

## Roles and setup

1. Bootstrap a manager account.
2. Sign in as manager and create one staff account and one organizer account.
3. Sign out and create two customer accounts from the customer registration dialog.
4. Record the visible success and validation feedback for blank fields, invalid passwords, duplicate email, and invalid login.

## UC-10 and UC-02

5. Sign in as manager, open Stations, create an active PC station, refresh, edit its rate, and verify the row.
6. Sign in as customer, open Reservations, search a future interval, select the station, confirm the reservation, refresh, and verify the reservation row.
7. Try a conflicting interval and verify a visible conflict error. Cancel the original reservation and verify the status refresh.

## UC-03 and UC-04

8. Sign in as staff, open Sessions, start the reservation, extend it, and finish it without a fault.
9. Open Payments, select the generated charge, record cash payment, refresh, and verify Receipt history.
10. Repeat with a walk-in session and verify the station and charge state.
11. Finish a session with Report an equipment fault selected. Verify the station becomes unavailable, the maintenance ticket appears, and the charge remains persisted.

## UC-05, UC-06, and UC-07

12. Sign in as customer, request a free membership and verify active status.
13. Request a paid membership, sign in as staff, settle it, and verify activation after refresh and restart.
14. Create a team, submit a join request from another customer, sign in as captain, approve it, and verify the roster.
15. Create individual and team tournaments as organizer, register both entrant types, settle paid entry charges, and verify confirmed entries.

## UC-08 and UC-09

16. Configure a tournament, open and close registration, generate matches, and schedule a match.
17. Attempt to schedule another match on the same station and overlapping time. Verify a visible conflict error.
18. Start a match, record a result, inspect standings, correct a completed result with a reason, and verify revision/audit state.
19. Cancel a tournament and verify registrations, pending charges, scheduled matches, and station allocations update while settled payment history remains.

## UC-11

20. Report a maintenance fault and verify future reservations become visible under Affected reservations.
21. Select an affected reservation, choose an eligible replacement station, confirm Reassign, refresh, and verify the new station.
22. Try the same fault description again and verify the duplicate-ticket warning. Progress and resolve all tickets with notes and readiness confirmation.

## UC-12 and persistence

23. Generate each report category as manager, apply date ranges, export CSV, and verify report totals.
24. Attempt manager-only screens and actions as customer, staff, and organizer. Verify navigation and visible authorization errors.
25. Close and restart the application. Verify accounts, reservations, sessions, payments, receipts, memberships, teams, tournaments, matches, maintenance, affected reservations, and reports remain correct.
26. Run `PRAGMA integrity_check` and `PRAGMA foreign_key_check` against the isolated database.

Record each step as PASS, FAIL, or BLOCKED with exact visible text and database evidence. A successful process launch alone does not count as GUI verification.

