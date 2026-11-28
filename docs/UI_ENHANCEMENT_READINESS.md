# UI Enhancement Readiness

## Scope

This inventory records the current JavaFX presentation layer before any visual enhancement. No CSS or layout redesign is included in this branch.

## Presentation architecture

`GameHubApp.java` is a pure-JavaFX composition root and screen router. It creates the stage, authentication flow, role-filtered sidebar, workspace scroll pane, feedback bar, dialogs, forms, lists, and refresh timers. There is no FXML. Screen methods are `overview`, `customerPage`, `reservationPage`, `sessionPage`, `paymentPage`, `membershipPage`, `teamPage`, `eventPage`, `tournamentPage`, `matchPage`, `stationPage`, `maintenancePage`, and `reportPage`.

Shared construction helpers are `page`, `section`, `toolbar`, `columns`, `formField`, `field`, `date`, `choices`, `records`, `fill`, `action`, `secondary`, `formDialog`, `confirm`, `status`, `scroll`, and `spacer`.

## Existing CSS classes

The stylesheet defines classes for the root, authentication layout and cover, branding, sidebar and navigation, header and date, pages and sections, stat cards and metrics, hero content, buttons and secondary states, forms and controls, dialogs, feedback success/error states, record lists and record state labels, empty states, profile/avatar, checkout panel, scroll bars, and compact hero behavior.

The class inventory includes `.auth-cover`, `.auth-cover-content`, `.auth-layout`, `.auth-panel`, `.auth-title`, `.avatar`, `.brand-word`, `.button`, `.check-box`, `.checkout-panel`, `.combo-box`, `.combo-box-popup`, `.cover-copy`, `.cover-title`, `.dashboard-heading`, `.date-picker`, `.date-picker-popup`, `.dialog-form`, `.dialog-pane`, `.feedback`, `.feedback-bar`, `.feedback-error`, `.feedback-success`, `.field-caption`, `.header-group`, `.header-title`, `.hero`, `.hero-action`, `.hero-content`, `.hero-copy`, `.hero-title`, `.inline-result`, `.label`, `.list-empty`, `.metric-caption`, `.metric-value`, `.muted`, `.nav-button`, `.nav-items`, `.nav-section`, `.page`, `.profile-name`, `.record-id`, `.record-list`, `.record-state`, `.record-text`, `.record-title`, `.root`, `.scroll-bar`, `.scroll-pane`, `.secondary`, `.section-title`, `.shortcut`, `.shortcut-copy`, `.shortcut-title`, `.sidebar`, `.sign-out`, `.stat-card`, `.state-inactive`, `.state-positive`, `.state-waiting`, `.state-working`, `.text-area`, `.text-field`, and `.top-header`.

## Inline styling and generated classes

No `setStyle(...)` calls were found in `GameHubApp.java`. Styling is applied through stylesheet classes. Java code adds state classes such as `nav-active`, `feedback-error`, `feedback-success`, and the status classes selected by `statusClass`.

## Screen layouts and controls

- Authentication uses a two-column cover and form panel with manager setup and customer registration dialogs.
- Overview uses a role-specific hero, metric cards, and shortcut cards.
- Customers, reservations, sessions, payments, teams, events, tournaments, matches, stations, maintenance, and reports use scrollable sections with toolbars and list views.
- Forms use captions, text fields, password fields, text areas, date pickers, combo boxes, check boxes, and shared modal dialogs.
- Tables are represented by styled `ListView<String>` record rows. They are not columnar JavaFX tables.
- Empty, success, error, and state feedback are represented by placeholder labels and feedback-bar classes.
- The sidebar is role-filtered and grouped into workspace sections. Sign-out and profile controls are shared.

## Styling limitations

- Record rows are formatted strings parsed for IDs and statuses. A richer table layout would require JavaFX layout and controller changes.
- The large `GameHubApp` class combines navigation, layout, event wiring, display formatting, and refresh scheduling. Splitting it would be architectural work outside a CSS-only pass.
- Responsive behavior is strongest for the hero area. Dense toolbars, lists, and multi-column forms would require layout review for narrow windows.
- Receipt history, affected-reservation reassignment, match scheduling, and report output currently use generic list rows. More detailed presentation would require new layout nodes.
- Inline state classes are reusable, but spacing and typography tokens are not centralized as CSS variables.

## CSS-only candidates

Color palette, typography, font weights, focus rings, button states, navigation active states, list-row spacing, section borders, status colors, dialog surface styling, empty-state presentation, scrollbars, and compact hero styling can be improved through CSS.

## Layout changes requiring JavaFX work

Columnar receipts, explicit reservation reassignment forms, richer match schedule grids, sortable report tables, customer profile editing, and multi-step tournament progression views require JavaFX layout and event changes. They should be treated as functional presentation work and tested through the existing controllers and services.

## Readiness conclusion

The current presentation layer is suitable for a CSS-only visual pass after manual GUI acceptance is completed. Functional controls and service wiring should remain stable during that pass.

