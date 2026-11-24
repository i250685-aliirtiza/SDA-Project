# Decisions

- Preserve the original specification in `docs/ORIGINAL_SPECIFICATION.md`.
- Store money as integer paisa in SQLite and use `BigDecimal` in the separate payroll exercise.
- Store instants as canonical `Instant.toString()` UTC text and display reservation input in Asia/Karachi.
- Keep legacy compatibility overloads used by existing callers while actorful restricted operations require an authenticated principal.
- Paid membership and tournament registrations create charges and become active or confirmed only after settlement.
- GUI acceptance remains manual evidence and was not claimed during this headless run.

- Session arrival policy allows staff to start a confirmed reservation up to two hours before its scheduled start, rejects expired reservations, and rejects starts more than two hours early. Walk-in sessions require a planned duration and are checked against confirmed reservations.
