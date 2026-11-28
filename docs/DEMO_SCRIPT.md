# Instructor demo script

1. Start with `./mvnw.cmd javafx:run` using a fresh `GAMEHUB_DB_PATH`.
2. Choose first-time manager setup and create a local manager.
3. Sign in as manager, create a station, and inspect the dashboard.
4. Register a customer, sign in as that customer, search a future Karachi-local interval, select a station result, and confirm a reservation.
5. Sign in as staff, start the selected reservation, finish it with calculated billing, and settle the outstanding charge.
6. As customer, select a persisted membership plan. As staff, settle the generated membership charge and refresh status.
7. Create a team, request membership from another customer, and approve the pending request as captain.
8. Create a typed tournament, register entrants, settle paid entry charges, close registration, generate matches, start matches, and record higher-score winners.
9. Report and resolve maintenance from the station and ticket selectors. Refresh the manager report with a date range.
10. Restart the application and verify reservations, receipts, membership, tournament and maintenance state persisted.

JavaFX control automation exercised these use-case areas. Use this script to rehearse the demo and check keyboard navigation, layout at different window sizes, and the remaining alternate flows.
