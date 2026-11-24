# GameHub completion checklist

Evidence is current repository evidence. GUI click-through was not run in this headless session.

| Area | Evidence | Status |
|---|---|---|
| Build and tests | `./mvnw.cmd clean verify`, 12 JUnit tests | Pending final gate |
| Accounts and roles | `UserService`, `AuthorizationRegressionTest` | Verified |
| Reservations and sessions | `OperationsService`, reservation/session tests | Verified |
| Payments and membership | `MembershipPaymentRegressionTest`, idempotent payment path | Verified |
| Teams | transactional join and captain policy | Verified |
| Tournaments | typed formats, paid registration, bracket advancement | Partially verified |
| Maintenance and reports | state machine and date-filtered manager report | Verified by build |
| GUI | JavaFX source entry points and status region | Manual verification pending |
| D4 personnel | `com.gamehub.personnel` hierarchy and demo | Verified by build |
