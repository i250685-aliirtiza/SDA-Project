# GameHub Test Report

## Closure branch

- Baseline: `683ae5e0bcdf9b6218a8f341b679370c881fc2a0`
- Branch: `gamehub-final-functional`
- Verification command: `cmd /c mvnw.cmd clean verify`
- Result: BUILD SUCCESS
- Tests: 36 passed, 0 failed, 0 errors, 0 skipped

The six added closure tests cover match scheduling conflicts, tournament cancellation propagation, maintenance duplicate and affected reservations, and registration audit events. All tests use isolated temporary SQLite databases. Existing tests remain passing.

## Database verification

The application was launched with an isolated `GAMEHUB_DB_PATH`. SQLite JDBC reported `PRAGMA integrity_check => ok`. `PRAGMA foreign_key_check` returned no rows.

## JavaFX verification

`cmd /c mvnw.cmd javafx:run` reached the JavaFX run phase with the isolated database and no startup exception during observation. Manual control interaction and restart through the visible UI remain pending. No GUI test framework is present in the repository.

