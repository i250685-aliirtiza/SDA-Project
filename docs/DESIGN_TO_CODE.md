# Design to Code

The closure branch preserves the presentation → application → infrastructure architecture.

- JavaFX `GameHubApp` routes through application controllers.
- Focused services enforce business rules and transactions.
- JDBC repositories own SQL and prepared statements.
- `Database` owns migrations and connection setup.
- `TransactionContext` owns commit, rollback, and auto-commit restoration.
- `PricingPolicy` remains the strategy abstraction for billing.

Closure additions map to existing responsibilities. `MatchRepository.scheduleConflict` protects station allocation. `TournamentRepository.cancelMatches`, `TournamentRegistrationRepository.cancelForTournament`, and `PaymentRepository.cancelTournamentCharges` support transactional cancellation. `MaintenanceService` coordinates affected reservations. Receipt retrieval is owned by `PaymentRepository` and `PaymentService`. Registration and payment state changes use the shared audit repository.

The legacy `OperationsService` façade remains for compatibility. Sensitive report calls now require a manager actor. The branch does not introduce a new framework or replace SQLite or JavaFX.
