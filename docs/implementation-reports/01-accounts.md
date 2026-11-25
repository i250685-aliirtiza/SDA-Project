# Implementation 01 — Accounts and Authentication

## A. Implementation identity

- Implementation number: 01
- Starting commit SHA: `e0a848dadc3c8ac15286be9e7eb74dd318285466` (latest `origin/main` at start)
- Final implementation commit SHA: recorded after the implementation commit
- Branch: `main`
- Remote push status: recorded after the implementation commit and push attempt

## B. Requirement coverage

| Original requirement | Implemented behavior | Source | Verification | Status |
|---|---|---|---|---|
| UC-01 main success, D3 steps 1–5 | JavaFX registration collects full name, email, phone, password and confirmation, then calls the production controller and service | `GameHubApp.registrationForm`, `AccountController.register`, `AccountService.register`, `UserService.registerCustomer` | `AccountManagementRegressionTest`, full Maven verify | PASS |
| UC-01 extension 2a | Required fields, email format, phone format, password length and confirmation are validated before insert. Errors are shown in the persistent workspace feedback region | `UserService.normalizeEmail`, `normalizePhone`, `validateCredentials`, `GameHubApp.run` | `AccountManagementRegressionTest`, source inspection | PASS |
| UC-01 extension 3a | Email and phone are normalized and checked before insertion. SQLite unique constraints remain the final integrity boundary | `UserService.registerCustomer`, `UserRepository.customerIdentifierExists`, `V1__init.sql` | Duplicate email and formatted-phone regression test | PASS |
| UC-01 extension 4a | Cancel closes the dialog before the controller is invoked | `GameHubApp.registrationForm` | Source inspection. GUI click-through unavailable in this environment | BLOCKED |
| UC-01 special requirements | PBKDF2-HMAC-SHA256 with random salt is stored. Password fields use JavaFX `PasswordField` | `UserService.hash`, `derive`, `GameHubApp.registrationForm`, `managerSetup`, `provisioningForm` | Authentication and persistence tests, source inspection | PASS |
| First-launch manager setup | Bootstrap is accepted only when `users` is empty and is transactional | `GameHubApp.managerSetup`, `UserService.bootstrap` | `UserServiceTest`, isolated database startup | PASS |
| Customer authentication | Login is normalized, inactive users are excluded, wrong passwords return no user | `UserService.authenticate`, `UserRepository.passwordHash`, `findActive` | `UserServiceTest`, `AccountManagementRegressionTest` | PASS |
| Manager, staff and organizer authentication | Manager provisioning creates only STAFF or ORGANIZER roles, with hashed credentials | `UserService.provision`, `AccountController.provision` | `AccountManagementRegressionTest` | PASS |
| Manager-authorized provisioning through GUI | Manager-only “Provision staff or organizer” control opens a visible form and invokes the production controller | `GameHubApp.registrationTab`, `provisioningForm` | Production code compiled. GUI click-through unavailable | BLOCKED |
| Persistent customer identity and authorized list | Customer records are read from SQLite on screen entry and refresh, with customer-only navigation restrictions | `CustomerRepository.records`, `GameHubApp.registrationTab`, `showWorkspace` | `ChainedAcceptanceTest`, source inspection | PASS |
| Transactional account creation and audit | User row, customer row and registration audit row commit together. Failures roll back | `UserService.registerCustomer`, `audit` | `AccountManagementRegressionTest` | PASS |
| Logout and account switching | Sign out clears `current` and returns to login | `GameHubApp.showWorkspace` logout handler | Source inspection. GUI click-through unavailable | BLOCKED |

## C. Acceptance results

The service and repository checks below use isolated temporary SQLite files. GUI statuses are marked BLOCKED where the JavaFX window could not be interacted with in this environment.

| ID | Expected behavior | Actual observed behavior | Testing method | Status |
|---|---|---|---|---|
| A01 | Fresh launch initializes a new database and shows login/setup controls | JavaFX Maven goal reached the application launch phase and created the isolated database file | `GAMEHUB_DB_PATH=%TEMP%\\gamehub-acceptance-isolated.db mvnw.cmd javafx:run` | PASS |
| A02 | Initial manager can be created | `UserService.bootstrap` creates and persists a manager in an empty DB | JUnit isolated DB | PASS |
| A03 | Manager can sign in | Manager authentication succeeds with the stored password | `UserServiceTest` | PASS |
| A04 | Manager creates staff account through GUI | Production provisioning form and controller are present, but no visible click-through was possible | JavaFX launch plus source inspection | BLOCKED |
| A05 | Manager creates organizer account through GUI | Production provisioning form and controller are present, but no visible click-through was possible | JavaFX launch plus source inspection | BLOCKED |
| A06 | Customer registration succeeds through GUI | Production registration path is wired and backend registration passes | `AccountManagementRegressionTest` plus source inspection | BLOCKED |
| A07 | New customer appears immediately in authorized list | Repository reads current SQLite state and refresh button reloads records | `ChainedAcceptanceTest` plus source inspection | PASS |
| A08 | Customer can sign in after logout | Customer authentication and persisted identity pass | `CustomerAccountBossTest` | PASS |
| A09 | Customer sees restricted navigation | Workspace only adds staff, manager and organizer pages for those roles | Source inspection | PASS |
| A10 | Staff can sign in | Provisioned STAFF credentials authenticate | `AccountManagementRegressionTest` | PASS |
| A11 | Staff sees customer records | STAFF receives the customer/accounts page backed by `CustomerRepository` | Source inspection and chained integration test | PASS |
| A12 | Organizer can sign in | Provisioned ORGANIZER credentials authenticate | `AccountManagementRegressionTest` | PASS |
| A13 | Organizer sees organizer navigation and restricted actions | Organizer workspace exposes tournament management and match controls | Source inspection | PASS |
| A14 | Incorrect password is rejected | Authentication returns `null` and the GUI reports failure | `UserServiceTest`, `AccountManagementRegressionTest` | PASS |
| A15 | Duplicate email and phone are rejected | Normalized duplicate identifiers are rejected and no extra customer row is committed | `AccountManagementRegressionTest` | PASS |
| A16 | Malformed registration is rejected | Email, phone, name and password validation throw before transaction work | `AccountManagementRegressionTest` and source inspection | PASS |
| A17 | Mismatched passwords are rejected | Confirmation mismatch throws before insert | Source inspection. GUI click-through unavailable | PASS |
| A18 | Cancelled registration creates no account | Dialog cancellation does not call the controller | Source inspection. GUI click-through unavailable | BLOCKED |
| A19 | Accounts survive restart | Customer and manager authentication succeed after reopening SQLite | `CustomerAccountBossTest`, `ChainedAcceptanceTest` | PASS |
| A20 | Records remain correct after restart | SQLite user/customer/audit rows remain available after reopen | `ChainedAcceptanceTest`, isolated DB tests | PASS |

## D. GUI-to-database integration

- Registration button: `GameHubApp.registrationForm` → `AccountController.register` → `AccountService.register` → `UserService.registerCustomer` → `UserRepository.insertUser`, `insertCustomer` and the audit insert on the shared `Database.connection()`.
- Authentication button: `GameHubApp.showLogin` → `UserService.authenticate` → `UserRepository.passwordHash` and `findActive`.
- First manager setup: `GameHubApp.managerSetup` → `UserService.bootstrap` → `UserRepository.hasUsers` and `insertUser`.
- Manager provisioning: `GameHubApp.provisioningForm` → `AccountController.provision` → `UserService.provision` → `UserRepository.usernameExists` and `insertUser`.
- Customer listing: `GameHubApp.registrationTab` refresh handler → `AccountController.customers` → `AccountService.customers` → `CustomerRepository.records`.
- Persistence is SQLite through `Database`, with foreign keys enabled and schema from `V1__init.sql`.

## E. Changes

- `src/main/java/com/gamehub/application/UserService.java` — normalized validation, PBKDF2 hashing, transactional bootstrap/registration/provisioning, authentication and audit writes.
- `src/main/java/com/gamehub/infrastructure/UserRepository.java` — duplicate identifier and username lookup queries.
- `src/main/java/com/gamehub/application/service/AccountService.java` — forwards the registration confirmation to the production service.
- `src/main/java/com/gamehub/application/controller/AccountController.java` — exposes manager-authorized provisioning to the GUI.
- `src/main/java/com/gamehub/GameHubApp.java` — masked manager setup, manager provisioning form and customer-account controls.
- `src/test/java/com/gamehub/AccountManagementRegressionTest.java` — isolated SQLite regression coverage for duplicate normalization, audit persistence, authentication and role provisioning.
- `docs/implementation-reports/01-accounts.md` — this report.

The pre-existing user modifications to `implementation.md` and the untracked `complete_gamehub_refactor.py` were preserved and were not included in the implementation commit.

## F. Test evidence

- `./mvnw.cmd -Dtest=AccountManagementRegressionTest test` — BUILD SUCCESS, 2 tests, 0 failures, 0 errors, 0 skipped.
- `./mvnw.cmd clean verify` — BUILD SUCCESS, 21 tests, 0 failures, 0 errors, 0 skipped. The Maven wrapper output was redirected while running to completion because the desktop command timeout is 30 seconds.
- `./mvnw.cmd javafx:run` with an isolated `GAMEHUB_DB_PATH` — JavaFX Maven goal reached the application process and initialized the isolated SQLite file. No GUI interaction harness or visible window was available, so click-through scenarios are reported as BLOCKED.

## G. Remaining defects

- TestFX or an equivalent JavaFX interaction harness is not configured in this environment. A01 launch and backend integration were exercised, but visible click-through for A02, A04–A06 and A18 remains unverified.
- The report does not claim that all GameHub use cases are complete. This implementation stops at account and authentication foundation work.

## H. Delivery verdict

INCOMPLETE
