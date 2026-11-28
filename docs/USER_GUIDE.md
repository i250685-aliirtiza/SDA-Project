# GameHub User Guide

## Start

Run `mvnw.cmd javafx:run` on Windows. The first launch offers manager setup. Set `GAMEHUB_DB_PATH` when an isolated or alternate SQLite database is required.

## Core workflows

Customers use Reservations, Membership, Teams, and Events. Staff use Customers, Sessions, Payments, and Maintenance. Organizers use Tournaments and Matches. Managers also use Stations and Reports.

When a station enters maintenance, future confirmed reservations are shown as affected. Authorized staff can review the affected reservation list and cancel or reassign through the service workflow. Payments includes receipt history for the signed-in account.

The full closure evidence and limitations are in `docs/FINAL_FUNCTIONAL_CLOSURE_REPORT.md`.
