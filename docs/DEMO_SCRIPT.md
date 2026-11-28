# GameHub Demonstration Script

1. Start with `cmd /c mvnw.cmd javafx:run` and an isolated `GAMEHUB_DB_PATH`.
2. Bootstrap a manager, provision staff and organizer accounts, and create a customer.
3. Add a station, reserve it, start the reserved session, finish it, and settle the generated charge.
4. Open Payments and verify the receipt history entry.
5. Create a tournament, register entrants, generate matches, and try scheduling two matches on the same station and time. The second scheduling attempt must display a conflict.
6. Report maintenance on a reserved station. Review the affected reservation and cancel or reassign it through the authorized workflow.
7. Cancel a tournament and verify registrations and pending charges are cancelled while historical payment rows remain.
8. Reopen the database and run SQLite integrity and foreign-key checks.

Automated evidence is recorded in `docs/FINAL_FUNCTIONAL_CLOSURE_REPORT.md`. Visible GUI interaction still requires manual execution or a GUI test harness.
