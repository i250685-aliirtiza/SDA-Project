# Traceability matrix

| Use case | Main code | Persistence | Automated evidence |
|---|---|---|---|
| UC-01 Accounts | `UserService.registerCustomer`, `bootstrap`, `provision` | `users`, `customers` | `UserServiceTest`, `CustomerAccountBossTest` |
| UC-02 Reservation | `OperationsService.reserve`, `availableStations` | `reservations`, `gaming_stations` | `ReservationSliceTest` |
| UC-03 Session | `startSession`, `finishSessionCalculated` | `sessions`, `charges` | `SessionSliceTest`, `OperationsServiceTest` |
| UC-04 Payment | `payChargeAsStaff`, `payCharge` | `payments`, `receipts` | `PaymentSliceTest`, `MembershipPaymentRegressionTest` |
| UC-05 Membership | plan overload and settlement activation | `membership_plans`, `memberships`, `charges` | `MembershipSliceTest`, `MembershipPaymentRegressionTest` |
| UC-06 Teams | `createTeam`, `requestJoin`, `decideJoin` | `teams`, `team_members`, `team_join_requests` | `TeamSliceTest` |
| UC-07 Registration | `registerTournament` | `registrations`, `charges` | `TournamentRegistrationSliceTest` |
| UC-08 Tournament management | `createTournament`, `changeTournamentStatus` | `tournaments` | `TournamentRegistrationSliceTest` |
| UC-09 Matches | `generateMatches`, `recordMatchResult`, `standings` | `tournament_matches`, `match_results` | build verification |
| UC-10 Stations | `addStation`, `updateStation`, `deactivateStation` | `gaming_stations` | `AuthorizationTest`, `OperationsServiceTest` |
| UC-11 Maintenance | `openMaintenance`, `updateMaintenance` | `maintenance` | build verification |
| UC-12 Reports | authorized `reportSummary` | persisted entities | build verification |
