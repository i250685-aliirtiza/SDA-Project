# Design to code

The application now has explicit application-layer controller counterparts under `com.gamehub.application.controller`. These are used as orchestration boundaries from the JavaFX shell for reservations, sessions and reports, and expose the corresponding validated operations for the other use cases.

| D5 responsibility | Implementation |
|---|---|
| AccountController | `application/controller/AccountController` validates credentials and delegates account registration/authentication |
| ReservationController | Uses `ReservationInterval` and delegates reservation ownership/cancellation |
| SessionController | Delegates reserved and walk-in sessions, extension and completion |
| PaymentController | Validates staff settlement input and delegates payment |
| MembershipController | Validates plan selection and delegates catalog-backed membership |
| TeamController | Delegates team creation, requests and captain decisions |
| RegistrationController | Supports solo and team entrant registration |
| TournamentController | Creates/configures drafts and enforces lifecycle orchestration |
| MatchController | Generates, schedules, starts, records and corrects matches |
| StationController | Creates, edits and deactivates stations |
| MaintenanceController | Reports and transitions maintenance tickets |
| ReportController | Performs authorized summaries and CSV export |

`ReservationInterval`, `ChargeBalance`, `PricingPolicy` and `StandardPricingPolicy` are meaningful domain/value objects. `ReportRepository` owns report SQL and `Database` owns migration/connection setup. `OperationsService` still contains legacy orchestration and SQL for several workflows, so the decomposition is incremental rather than complete repository extraction.
JavaFX integration evidence

`GameHubApp` initializes all twelve controllers. Mutation and refresh handlers route through corresponding controller fields. The JavaFX shell contains no direct `OperationsService` calls.
