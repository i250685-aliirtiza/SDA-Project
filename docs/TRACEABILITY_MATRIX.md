# Traceability Matrix

| Use case | Production entry points | Persistence | Automated evidence |
|---|---|---|---|
| UC-01 | `AccountController`, `UserService` | `users`, `customers`, `audit_events` | Account and persistence tests |
| UC-02 | `ReservationController`, `ReservationService` | `reservations`, `gaming_stations` | Reservation tests |
| UC-03 | `SessionController`, `SessionService`, maintenance integration | `sessions`, `charges`, `maintenance` | Session and chained tests |
| UC-04 | `PaymentController`, `PaymentService` | `payments`, `receipts`, `charges` | Payment tests |
| UC-05 | `MembershipController`, `MembershipService` | `membership_plans`, `memberships`, `charges` | Membership tests |
| UC-06 | `TeamController`, `TeamService` | `teams`, `team_members`, `team_join_requests` | Team tests |
| UC-07 | `RegistrationController`, `TournamentRegistrationService` | `registrations`, `charges`, `audit_events` | Registration and closure tests |
| UC-08 | `TournamentController`, `TournamentService` | `tournaments`, `registrations`, `charges`, `tournament_matches` | Tournament and closure tests |
| UC-09 | `MatchController`, `MatchService` | `tournament_matches`, `match_results`, `audit_events` | Workflow and closure tests |
| UC-10 | `StationController`, `StationService` | `gaming_stations`, `audit_events` | Authorization and operations tests |
| UC-11 | `MaintenanceController`, `MaintenanceService` | `maintenance`, `reservations`, `gaming_stations`, `audit_events` | Workflow and closure tests |
| UC-12 | `ReportController`, `ReportService` | Source transaction tables | Report export and authorization tests |

GUI interaction remains pending and is tracked in the closure report.
