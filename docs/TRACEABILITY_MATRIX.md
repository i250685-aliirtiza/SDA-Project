# GameHub requirements traceability

Status reflects the implementation and verification evidence available in this repository. A passing service test does not count as GUI verification.

| Use case | Actor and preconditions | UI workflow | Persistence and service integration | Automated evidence | Status |
|---|---|---|---|---|---|
| UC-01 Create Customer Account | Customer, valid contact details and password | Accounts page opens a validated registration dialog | `UserService.registerCustomer`, `users`, `customers` | `CustomerAccountBossTest`, `UserServiceTest` | Partial, GUI not visually exercised |
| UC-02 Reserve Gaming Station | Customer, active station and non-overlapping interval | Reservation page searches live availability and selects a station | `availableStations`, `reserve`, `cancelReservation`, `reservations` | `ReservationSliceTest` | Partial, GUI not visually exercised |
| UC-03 Conduct Gaming Session | Staff, confirmed reservation | Sessions page loads customer reservations, starts and finishes a session | `startSession`, `finishSession`, `sessions`, `charges` | `SessionSliceTest`, `OperationsServiceTest` | Partial, GUI not visually exercised |
| UC-04 Process Payment | Staff and payable charge | Payments page loads outstanding charges and creates payment/receipt | `charges`, `payCharge`, `payments`, `receipts` | `PaymentSliceTest` | Partial, GUI not visually exercised |
| UC-05 Subscribe or Renew Membership | Customer account | Membership form selects plan and displays resulting membership | `subscribeOrRenew`, `membership`, `memberships` | `MembershipSliceTest` | Partial, GUI not visually exercised |
| UC-06 Create or Join Gaming Team | Customer, team or captain permissions | Teams page creates teams, submits requests, and approves members | `createTeam`, `requestJoin`, `decideJoin`, `teams` | `TeamSliceTest` | Partial, GUI not visually exercised |
| UC-07 Register for Tournament | Eligible customer or captain, open tournament | Registration page loads tournament records and registers selected entrant | `tournaments`, `registerTournament`, `registrations` | `TournamentRegistrationSliceTest` | Partial, GUI not visually exercised |
| UC-08 Manage Tournament | Organizer or manager | Tournament management page creates tournaments and changes lifecycle status | `createTournament`, `changeTournamentStatus`, `tournaments` | No dedicated UI test | Partial |
| UC-09 Conduct Tournament Matches | Organizer, eligible registrations and active tournament | Matches page selects tournaments, generates brackets, starts matches, records results | `generateMatches`, `matches`, `startMatch`, `recordMatchResult` | No dedicated UI test | Partial |
| UC-10 Manage Gaming Stations | Manager or administrator | Stations page adds, lists, and deactivates stations | `addStation`, `stations`, `deactivateStation`, `gaming_stations` | `OperationsServiceTest` | Partial, GUI not visually exercised |
| UC-11 Handle Equipment Maintenance | Staff or manager | Maintenance page reports, progresses, and resolves tickets | `openMaintenance`, `updateMaintenance`, `maintenance` | No dedicated UI test | Partial |
| UC-12 Generate Management Reports | Manager or administrator | Reports page refreshes live database counts | `reportCounts`, report tables | No dedicated UI test | Partial |

## Verification boundary

`mvnw.cmd clean verify` and `mvnw.cmd test` pass. The JavaFX process reaches the run phase. This environment does not expose a controllable desktop surface, so screen-by-screen click-through, visual layout inspection, and chained GUI acceptance remain unverified. The application is therefore not marked production-ready or fully verified in this matrix.
