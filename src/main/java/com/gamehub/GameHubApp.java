package com.gamehub;

import com.gamehub.application.UserService;
import com.gamehub.application.controller.*;
import com.gamehub.application.service.*;
import com.gamehub.domain.Role;
import com.gamehub.domain.User;
import com.gamehub.infrastructure.CustomerRepository;
import com.gamehub.infrastructure.Database;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public final class GameHubApp extends Application {
    private static final ZoneId ZONE = ZoneId.of("Asia/Karachi");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("EEE, d MMM", Locale.ENGLISH);
    private static final Pattern RECORD_ID = Pattern.compile("^#(\\d+)(?:\\s|\\||$)");
    private static final Pattern REQUEST_MEMBER = Pattern.compile(" \\| member #(\\d+) \\(");
    private static final Pattern TIMESTAMP = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?Z");
    private static final DateTimeFormatter RECORD_DATE = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale.ENGLISH).withZone(ZONE);
    private static final Set<String> POSITIVE_STATES = Set.of("AVAILABLE", "CONFIRMED", "PAID", "COMPLETED", "RESOLVED", "ACCEPTED", "REGISTRATION_OPEN");
    private static final Set<String> WORKING_STATES = Set.of("SCHEDULED", "ACTIVE", "IN_USE", "IN_PROGRESS", "OCCUPIED", "REGISTRATION_CLOSED");
    private static final Set<String> WAITING_STATES = Set.of("PENDING", "UNPAID", "PARTIALLY_PAID", "MAINTENANCE", "REPORTED");
    private static final Set<String> INACTIVE_STATES = Set.of("CANCELLED", "DRAFT", "EXPIRED", "INACTIVE", "REJECTED", "OUT_OF_SERVICE");

    private enum Screen {
        HOME("Overview", "Overview", "WORKSPACE", ""),
        ACCOUNTS("Customers", "Customer accounts", "FRONT DESK", "Register and find players."),
        RESERVATIONS("Reservations", "Reservations", "PLAY", "Find a station and manage bookings."),
        SESSIONS("Sessions", "Gaming sessions", "FRONT DESK", "Check in and manage play time."),
        PAYMENTS("Payments", "Payments & receipts", "FRONT DESK", "Settle charges and issue receipts."),
        MEMBERSHIP("Membership", "Membership", "PLAY", "View plans and manage your membership."),
        TEAMS("Teams", "Gaming teams", "COMPETITION", "Find teams and manage join requests."),
        EVENTS("Tournaments", "Tournaments", "COMPETITION", "Browse events and track your entries."),
        TOURNAMENTS("Tournaments", "Manage tournaments", "COMPETITION", "Configure events and registration."),
        MATCHES("Matches", "Tournament matches", "COMPETITION", "Manage brackets and enter scores."),
        STATIONS("Stations", "Gaming stations", "OPERATIONS", "Manage equipment and hourly rates."),
        MAINTENANCE("Maintenance", "Maintenance", "OPERATIONS", "Report faults and track repairs."),
        REPORTS("Reports", "Management reports", "OPERATIONS", "Review operations and export results.");

        final String nav, title, group, description;

        Screen(String nav, String title, String group, String description) {
            this.nav = nav;
            this.title = title;
            this.group = group;
            this.description = description;
        }
    }

    @FunctionalInterface
    private interface Work { void run() throws Exception; }

    @FunctionalInterface
    private interface Records { List<String> get() throws Exception; }

    @FunctionalInterface
    private interface Count { long read() throws Exception; }

    private record DashboardMetric(Screen screen, String title, Count read) {}

    private record ReservationSlot(Instant start, Instant end, String stationType) {}

    private Database db;
    private UserService users;
    private AccountController accounts;
    private ReservationController reservations;
    private SessionController sessions;
    private PaymentController payments;
    private MembershipController memberships;
    private TeamController teams;
    private RegistrationController registrations;
    private TournamentController tournaments;
    private MatchController matches;
    private StationController stations;
    private MaintenanceController maintenance;
    private ReportController reports;
    private Stage stage;
    private User current;
    private Label feedback;
    private ScrollPane workspace;
    private Label screenTitle;
    private Label screenGroup;
    private javafx.animation.Timeline deskRefresh;
    private final Map<Screen, Button> navigation = new EnumMap<>(Screen.class);

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("GameHub");
        try {
            db = new Database();
            users = new UserService(db);
            accounts = new AccountController(users, new CustomerRepository(db.connection()));
            reservations = new ReservationController(new ReservationService(db));
            sessions = new SessionController(new SessionService(db));
            payments = new PaymentController(new PaymentService(db));
            memberships = new MembershipController(new MembershipService(db));
            teams = new TeamController(new TeamService(db));
            registrations = new RegistrationController(new TournamentRegistrationService(db));
            tournaments = new TournamentController(new TournamentService(db));
            matches = new MatchController(new MatchService(db));
            stations = new StationController(new StationService(db));
            maintenance = new MaintenanceController(new MaintenanceService(db));
            reports = new ReportController(new ReportService(db));
            showLogin();
        } catch (Exception failure) {
            Alert alert = new Alert(Alert.AlertType.ERROR, message(failure));
            alert.setHeaderText("GameHub could not start");
            style(alert.getDialogPane());
            alert.showAndWait();
        }
    }

    private void showLogin() throws Exception {
        stopDeskRefresh();
        current = null;
        workspace = null;
        navigation.clear();
        TextField login = field("Username or email");
        PasswordField password = new PasswordField();
        password.setPromptText("Your password");
        feedback = label("", "feedback");
        feedback.setWrapText(true);
        feedback.managedProperty().bind(feedback.visibleProperty());
        feedback.setVisible(false);

        Button signIn = action("Sign in", "", () -> {
            User found = accounts.authenticate(login.getText(), password.getText());
            if (found == null) {
                password.clear();
                password.requestFocus();
                throw new IllegalArgumentException("Check your username or email and password.");
            }
            current = found;
            showWorkspace();
        });
        signIn.setDefaultButton(true);
        signIn.setMaxWidth(Double.MAX_VALUE);
        login.setOnAction(event -> password.requestFocus());
        password.setOnAction(event -> signIn.fire());

        Button register = secondary("Create customer account", "", () -> registrationForm(login, null));
        register.setMaxWidth(Double.MAX_VALUE);
        VBox form = new VBox(18, new VBox(7, label("Sign in", "auth-title"),
                wrap("Use your GameHub account to continue.", "muted")),
                formField("Username or email", login), formField("Password", password), signIn, register, feedback);
        form.setMaxWidth(390);
        form.setMaxHeight(Region.USE_PREF_SIZE);
        if (users.needsManagerSetup()) {
            Button setup = secondary("Set up GameHub", "", () -> managerSetup());
            setup.setMaxWidth(Double.MAX_VALUE);
            form.getChildren().add(setup);
        }

        VBox coverCopy = new VBox(10, wrap("Your gaming floor.", "cover-title"),
                wrap("Book a station, run sessions and compete in tournaments.", "cover-copy"));
        BorderPane coverContent = new BorderPane();
        coverContent.getStyleClass().add("auth-cover-content");
        coverContent.setTop(brand());
        coverContent.setBottom(coverCopy);
        StackPane art = new StackPane(WorkspaceVisuals.floor(), coverContent);
        art.getStyleClass().add("auth-cover");
        HBox.setHgrow(art, Priority.ALWAYS);
        StackPane panel = new StackPane(form);
        panel.getStyleClass().add("auth-panel");
        panel.setPrefWidth(455);
        panel.setMinWidth(335);
        HBox layout = new HBox(art, panel);
        layout.getStyleClass().add("auth-layout");
        stage.setMinWidth(790);
        stage.setMinHeight(575);
        setScene(layout, 1100, 680);
        login.requestFocus();
    }

    private void registrationForm(TextField login, Work refresh) {
        TextField name = field("Full name"), email = field("name@example.com"), phone = field("+92");
        PasswordField password = new PasswordField(), confirmation = new PasswordField();
        password.setPromptText("At least 8 characters");
        formDialog("Create customer account", "Create account", () -> {
            accounts.register(name.getText(), email.getText(), phone.getText(), password.getText(), confirmation.getText());
            if (login != null) login.setText(email.getText().trim());
            status(login == null ? "Customer account created." : "Account created. Sign in with your email.", false);
            if (refresh != null) load(refresh);
        }, formField("Full name", name), formField("Email", email), formField("Phone", phone),
                formField("Password", password), formField("Confirm password", confirmation));
    }

    private void managerSetup() {
        TextField username = field("Choose a username");
        PasswordField password = new PasswordField();
        password.setPromptText("At least 8 characters");
        formDialog("Set up GameHub", "Create manager", () -> {
            users.bootstrap(username.getText(), password.getText());
            showLogin();
            status("Manager account created. You can now sign in.", false);
        }, formField("Manager username", username), formField("Password", password));
    }

    private void showWorkspace() {
        boolean customer = current.role() == Role.CUSTOMER;
        boolean manager = current.role() == Role.MANAGER;
        boolean staff = manager || current.role() == Role.STAFF;
        boolean organizer = manager || current.role() == Role.ORGANIZER;

        List<Screen> screens = new ArrayList<>();
        screens.add(Screen.HOME);
        if (customer) screens.addAll(List.of(Screen.RESERVATIONS, Screen.MEMBERSHIP, Screen.TEAMS, Screen.EVENTS));
        if (staff) screens.addAll(List.of(Screen.ACCOUNTS, Screen.SESSIONS, Screen.PAYMENTS));
        if (organizer) screens.addAll(List.of(Screen.TOURNAMENTS, Screen.MATCHES));
        if (manager) screens.add(Screen.STATIONS);
        if (staff) screens.add(Screen.MAINTENANCE);
        if (manager) screens.add(Screen.REPORTS);

        VBox nav = new VBox(5);
        nav.getStyleClass().add("nav-items");
        String group = "";
        for (Screen screen : screens) {
            if (!screen.group.equals(group)) {
                group = screen.group;
                if (screen != Screen.HOME) nav.getChildren().add(label(group, "nav-section"));
            }
            Button button = new Button(screen.nav);
            button.getStyleClass().add("nav-button");
            button.setGraphic(WorkspaceVisuals.icon(screen.name()));
            button.setGraphicTextGap(11);
            button.setMaxWidth(Double.MAX_VALUE);
            button.setOnAction(event -> open(screen));
            navigation.put(screen, button);
            nav.getChildren().add(button);
        }

        ScrollPane navScroll = scroll(nav);
        navScroll.getStyleClass().add("nav-scroll");
        navScroll.setMinHeight(110);
        VBox.setVgrow(navScroll, Priority.ALWAYS);

        Button signOut = secondary("Sign out", "", this::showLogin);
        signOut.setGraphic(WorkspaceVisuals.icon("logout"));
        signOut.getStyleClass().add("sign-out");
        signOut.setMaxWidth(Double.MAX_VALUE);
        Label userName = label(current.username(), "profile-name");
        userName.setMaxWidth(140);
        userName.setTooltip(new Tooltip(current.username()));
        VBox profile = new VBox(3, userName, label(pretty(current.role().name()), "muted"));
        Label initials = label(current.username().substring(0, 1).toUpperCase(Locale.ROOT), "avatar");
        HBox account = new HBox(10, initials, profile);
        account.setAlignment(Pos.CENTER_LEFT);
        account.getStyleClass().add("profile");
        VBox sidebar = new VBox(15, brand(), navScroll, account, signOut);
        sidebar.getStyleClass().add("sidebar");

        screenTitle = label("", "header-title");
        screenTitle.setFocusTraversable(true);
        screenGroup = label("", "header-group");
        Label day = label(LocalDate.now(ZONE).format(DATE), "top-date");
        day.setGraphic(WorkspaceVisuals.icon("calendar"));
        HBox header = new HBox(16, new VBox(3, screenGroup, screenTitle), spacer(), day);
        header.getStyleClass().add("top-header");
        header.setAlignment(Pos.CENTER_LEFT);

        workspace = scroll(new Region());
        workspace.getStyleClass().add("workspace-scroll");
        VBox.setVgrow(workspace, Priority.ALWAYS);
        feedback = label("", "feedback");
        feedback.setWrapText(true);
        feedback.setMaxWidth(Double.MAX_VALUE);
        feedback.managedProperty().bind(feedback.visibleProperty());
        feedback.setVisible(false);

        StackPane feedbackBar = new StackPane(feedback);
        feedbackBar.getStyleClass().add("feedback-bar");
        feedbackBar.visibleProperty().bind(feedback.visibleProperty());
        feedbackBar.managedProperty().bind(feedbackBar.visibleProperty());
        VBox main = new VBox(header, workspace, feedbackBar);
        BorderPane root = new BorderPane(main, null, null, null, sidebar);
        stage.setMinWidth(860);
        stage.setMinHeight(620);
        setScene(root, 1250, 805);
        open(Screen.HOME);
    }

    private void open(Screen screen) {
        stopDeskRefresh();
        navigation.forEach((item, button) -> button.getStyleClass().remove("nav-active"));
        navigation.get(screen).getStyleClass().add("nav-active");
        screenTitle.setText(screen.title);
        screenGroup.setText(screen.group);
        feedback.setVisible(false);
        Pane content = switch (screen) {
            case HOME -> overview();
            case ACCOUNTS -> customerPage();
            case RESERVATIONS -> reservationPage();
            case SESSIONS -> sessionPage();
            case PAYMENTS -> paymentPage();
            case MEMBERSHIP -> membershipPage();
            case TEAMS -> teamPage();
            case EVENTS -> eventPage();
            case TOURNAMENTS -> tournamentPage();
            case MATCHES -> matchPage();
            case STATIONS -> stationPage();
            case MAINTENANCE -> maintenancePage();
            case REPORTS -> reportPage();
        };
        workspace.setContent(content);
        workspace.setVvalue(0);
        screenTitle.requestFocus();
    }

    private Pane overview() {
        Screen primary = switch (current.role()) {
            case CUSTOMER -> Screen.RESERVATIONS;
            case ORGANIZER -> Screen.TOURNAMENTS;
            case STAFF, MANAGER -> Screen.SESSIONS;
        };
        String headline = switch (current.role()) {
            case CUSTOMER -> "Find your next game.";
            case STAFF -> "Run the front desk.";
            case MANAGER -> "Run the gaming floor.";
            case ORGANIZER -> "Run the competition.";
        };
        String description = switch (current.role()) {
            case CUSTOMER -> "Reserve a station and keep track of your bookings, teams and tournament entries.";
            case MANAGER -> "Keep stations ready, check ongoing sessions and follow up on payments.";
            case STAFF -> "Check players in, manage active sessions and record payments.";
            case ORGANIZER -> "Manage registration, build brackets and record match results.";
        };
        String primaryAction = switch (primary) {
            case RESERVATIONS -> "Book a station";
            case SESSIONS -> "Open session desk";
            default -> "Manage tournaments";
        };
        Button go = action(primaryAction, "", () -> open(primary));
        go.setGraphic(WorkspaceVisuals.icon("arrow"));
        go.setContentDisplay(ContentDisplay.RIGHT);
        go.getStyleClass().add("hero-action");
        VBox copy = new VBox(12, wrap(headline, "hero-title"), wrap(description, "hero-copy"), go);
        copy.getStyleClass().add("hero-content");
        copy.setMaxWidth(510);
        StackPane hero = new StackPane(WorkspaceVisuals.floor(), copy);
        hero.getStyleClass().add("hero");
        StackPane.setAlignment(copy, Pos.CENTER_LEFT);
        Rectangle outline = new Rectangle();
        outline.setArcWidth(25);
        outline.setArcHeight(25);
        outline.widthProperty().bind(hero.widthProperty());
        outline.heightProperty().bind(hero.heightProperty());
        hero.setClip(outline);
        hero.widthProperty().addListener((observable, oldWidth, width) ->
                hero.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("compact"), width.doubleValue() < 735));

        VBox page = page(hero);
        List<DashboardMetric> metrics = switch (current.role()) {
            case CUSTOMER -> List.of(
                    new DashboardMetric(Screen.RESERVATIONS, "Confirmed reservations",
                            () -> reservations.mine(current.id()).stream().filter(row -> row.endsWith("[CONFIRMED]")).count()),
                    new DashboardMetric(Screen.EVENTS, "Open tournaments", () -> registrations.open().size()),
                    new DashboardMetric(Screen.EVENTS, "My tournament entries", () -> registrations.mine(current.id()).size()));
            case STAFF -> List.of(
                    new DashboardMetric(Screen.SESSIONS, "Active sessions", () -> sessions.active().size()),
                    new DashboardMetric(Screen.SESSIONS, "Ready for check-in", () -> sessions.reservations().size()),
                    new DashboardMetric(Screen.PAYMENTS, "Outstanding charges", () -> payments.outstanding().size()));
            case MANAGER -> List.of(
                    new DashboardMetric(Screen.STATIONS, "Active stations", () -> stations.inventory().size()),
                    new DashboardMetric(Screen.SESSIONS, "Active sessions", () -> sessions.active().size()),
                    new DashboardMetric(Screen.PAYMENTS, "Outstanding charges", () -> payments.outstanding().size()));
            case ORGANIZER -> List.of(
                    new DashboardMetric(Screen.TOURNAMENTS, "My tournaments", () -> tournaments.owned(current.id()).size()),
                    new DashboardMetric(Screen.TOURNAMENTS, "Registration open", () -> tournaments.owned(current.id()).stream()
                            .filter(row -> Set.of("OPEN", "REGISTRATION_OPEN").contains(tournamentStatus(row))).count()),
                    new DashboardMetric(Screen.MATCHES, "In competition", () -> tournaments.owned(current.id()).stream()
                            .filter(row -> "ACTIVE".equals(tournamentStatus(row))).count()));
        };

        HBox cards = new HBox(12);
        List<Work> metricUpdates = new ArrayList<>();
        for (DashboardMetric metric : metrics) {
            Label value = label("—", "metric-value");
            Label metricTitle = label(metric.title(), "metric-caption");
            HBox metricHeader = new HBox(9, WorkspaceVisuals.icon(metric.screen().name()), metricTitle);
            metricHeader.setAlignment(Pos.CENTER_LEFT);
            VBox layout = new VBox(7, metricHeader, value);
            layout.setMouseTransparent(true);
            Button card = action("", "", () -> open(metric.screen()));
            card.accessibleTextProperty().bind(Bindings.concat(metric.title(), ": ", value.textProperty()));
            card.setGraphic(layout);
            card.getStyleClass().add("stat-card");
            card.setMinWidth(0);
            card.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(card, Priority.ALWAYS);
            cards.getChildren().add(card);
            metricUpdates.add(() -> value.setText(Long.toString(metric.read().read())));
        }
        page.getChildren().addAll(cards, label("Quick access", "dashboard-heading"));
        TilePane shortcuts = new TilePane(12, 12);
        shortcuts.setPrefColumns(3);
        shortcuts.prefTileWidthProperty().bind(Bindings.createDoubleBinding(() -> {
            double width = shortcuts.getWidth();
            if (width < 1) return 220.0;
            int columns = width >= 735 ? 3 : 2;
            return Math.floor((width - (columns - 1) * shortcuts.getHgap()) / columns);
        }, shortcuts.widthProperty()));
        navigation.keySet().stream().filter(screen -> screen != Screen.HOME && screen != primary).forEach(screen -> {
            Button button = action("", "", () -> open(screen));
            button.setAccessibleText(screen.title);
            button.setAccessibleHelp(screen.description);
            Label title = label(screen.title, "shortcut-title");
            Label details = label(screen.description, "shortcut-copy");
            VBox words = new VBox(4, title, details);
            words.setMinWidth(0);
            HBox graphic = new HBox(10, WorkspaceVisuals.icon(screen.name()), words);
            graphic.setAlignment(Pos.TOP_LEFT);
            graphic.setMouseTransparent(true);
            graphic.prefWidthProperty().bind(button.widthProperty().subtract(30));
            HBox.setHgrow(words, Priority.ALWAYS);
            button.setGraphic(graphic);
            button.getStyleClass().add("shortcut");
            button.setMinWidth(0);
            button.setMaxWidth(Double.MAX_VALUE);
            shortcuts.getChildren().add(button);
        });
        page.getChildren().add(shortcuts);
        refreshDesk(() -> {
            for (Work update : metricUpdates) update.run();
        });
        return page;
    }

    private static HBox brand() {
        HBox brand = new HBox(10, WorkspaceVisuals.mark(), label("GameHub", "brand-word"));
        brand.setAlignment(Pos.CENTER_LEFT);
        return brand;
    }

    private Pane customerPage() {
        ListView<String> rows = records("No customers found.");
        rows.setPrefHeight(385);
        TextField search = field("Name, email or phone");
        List<String> customers = new ArrayList<>();
        Work applyFilter = () -> {
            String query = search.getText().strip().toLowerCase(Locale.ROOT);
            fill(rows, () -> customers.stream().filter(row -> row.toLowerCase(Locale.ROOT).contains(query)).toList());
            rows.setPlaceholder(wrap(query.isEmpty() ? "No customers found."
                    : "No customers match this search.", "list-empty"));
        };
        Work refresh = () -> {
            List<String> latest = accounts.customers(current.id());
            customers.clear();
            customers.addAll(latest);
            applyFilter.run();
        };
        search.textProperty().addListener((observable, previous, next) -> load(applyFilter));
        Button register = new Button("Add customer");
        register.setOnAction(event -> registrationForm(null, refresh));
        FlowPane controls = toolbar(formField("Search customers", search), register, secondary("Refresh", "", refresh));
        if (current.role() == Role.MANAGER) {
            controls.getChildren().add(secondary("Add staff or organizer", "", this::provisionAccount));
        }
        refreshDesk(refresh);
        return page(section("Customer directory", null, controls, rows));
    }

    private void provisionAccount() {
        TextField username = field("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("At least 8 characters");
        ComboBox<String> role = choices("STAFF", "ORGANIZER");
        role.getSelectionModel().selectFirst();
        formDialog("Add an operational account", "Create account", () -> {
            accounts.provision(current, username.getText(), password.getText(), Role.valueOf(role.getValue()));
            status("Operational account created.", false);
        }, formField("Username", username), formField("Temporary password", password), formField("Role", role));
    }

    private Pane reservationPage() {
        LocalDate tomorrow = LocalDate.now(ZONE).plusDays(1);
        DatePicker from = date(tomorrow), to = date(tomorrow);
        TextField start = field("HH:mm"), end = field("HH:mm");
        start.setText("10:00");
        end.setText("12:00");
        String allTypes = "All station types";
        ComboBox<String> type = choices(allTypes);
        load(() -> type.getItems().addAll(reservations.stationTypes()));
        type.getSelectionModel().selectFirst();
        ListView<String> available = records("Choose a date and time to find stations.");
        ListView<String> mine = records("You have no reservations.");
        Work refresh = () -> fill(mine, () -> reservations.mine(current.id()));
        var searchedSlot = new javafx.beans.property.SimpleObjectProperty<ReservationSlot>();
        Work search = () -> {
            searchedSlot.set(null);
            available.getItems().clear();
            Instant begins = time(from, start);
            Instant ends = time(to, end);
            if (!begins.isAfter(Instant.now())) throw new IllegalArgumentException("Choose a start time in the future.");
            if (!begins.isBefore(ends)) throw new IllegalArgumentException("End time must be after start time.");
            var slot = new ReservationSlot(begins, ends, type.getSelectionModel().getSelectedIndex() == 0
                    ? null : type.getValue());
            fill(available, () -> reservations.available(slot.start(), slot.end(), slot.stationType()));
            searchedSlot.set(slot);
            if (available.getItems().isEmpty()) {
                String empty = reservations.stationTypes().isEmpty()
                        ? "No gaming stations have been set up. Ask the front desk."
                        : "No stations match. Try a different time or station type.";
                available.setPlaceholder(label(empty, "muted"));
            }
        };
        for (var value : List.<javafx.beans.value.ObservableValue<?>>of(
                from.valueProperty(), to.valueProperty(), start.textProperty(), end.textProperty(), type.valueProperty())) {
            value.addListener((observable, previous, next) -> {
                searchedSlot.set(null);
                available.getItems().clear();
                available.setPlaceholder(label("Search again to apply these changes.", "muted"));
            });
        }
        Button book = action("Confirm reservation", "", () -> {
            ReservationSlot slot = searchedSlot.get();
            long reservation = reservations.reserve(current.id(), selectedId(available), slot.start(), slot.end());
            status("Reservation #" + reservation + " confirmed.", false);
            load(refresh);
            load(search);
        });
        book.disableProperty().bind(unselected(available).or(searchedSlot.isNull()));
        Button cancel = secondary("Cancel reservation", "", () -> confirm("Cancel this reservation?",
                "Cancel reservation", "Reservation cancelled.", () -> {
                    reservations.cancel(current.id(), selectedId(mine));
                    load(refresh);
                    if (searchedSlot.get() != null) load(search);
                }));
        cancel.disableProperty().bind(unselected(mine).or(notSelectedStatus(mine, "[CONFIRMED]")));
        VBox page = page(section("Book a station", "All dates and times are in Pakistan Standard Time.",
                        toolbar(formField("Start date", from), formField("Start time", start),
                                formField("End date", to), formField("End time", end), formField("Station type", type)),
                        toolbar(action("Find stations", "", search), book), available),
                section("My reservations", null, mine, toolbar(secondary("Refresh", "", refresh), cancel)));
        load(refresh);
        return page;
    }

    private Pane sessionPage() {
        ListView<String> active = records("No sessions are running.");
        ListView<String> bookings = records("No reservations are eligible for check-in.");
        ListView<String> customers = records("No customer accounts found.");
        ListView<String> available = records("No stations available for this duration.");
        TextField planned = field("Minutes");
        planned.setText("60");
        TextField extension = field("Minutes");
        Label bill = wrap("", "inline-result");
        bill.managedProperty().bind(bill.visibleProperty());
        bill.setVisible(false);
        Work refreshActive = () -> fill(active, sessions::active);
        Work refreshBookings = () -> fill(bookings, sessions::reservations);
        Work refreshCustomers = () -> fill(customers, sessions::customers);
        Work refreshStations = () -> {
            int minutes;
            try {
                minutes = integer(planned, "Planned duration", 1, 720);
            } catch (IllegalArgumentException invalidDuration) {
                available.getItems().clear();
                available.setPlaceholder(label("Enter a duration from 1 to 720 minutes.", "muted"));
                return;
            }
            fill(available, () -> sessions.stations(minutes));
            if (available.getItems().isEmpty()) {
                String empty = stations.inventory().isEmpty()
                        ? "No active stations. A manager can add one under Stations."
                        : "No stations are free for this duration. Try a shorter duration or check station inventory.";
                available.setPlaceholder(label(empty, "muted"));
            }
        };
        Work refreshWalkIn = () -> {
            refreshCustomers.run();
            refreshStations.run();
        };
        Work refreshSessions = () -> {
            refreshActive.run();
            refreshBookings.run();
            refreshWalkIn.run();
        };
        planned.textProperty().addListener((observable, previous, next) -> load(refreshStations));
        Button startWalk = action("Start walk-in", "Walk-in session started.", () -> {
            sessions.startWalkIn(current.id(), selectedId(customers), selectedId(available),
                    integer(planned, "Planned duration", 1, 720));
            load(refreshSessions);
        });
        startWalk.disableProperty().bind(unselected(customers).or(unselected(available)));
        Button startBooking = action("Start reservation", "Reserved session started.", () -> {
            sessions.startReserved(current.id(), selectedId(bookings));
            load(refreshSessions);
        });
        startBooking.disableProperty().bind(unselected(bookings));
        Button extend = secondary("Extend session", "Session extended.", () -> {
            sessions.extend(current.id(), selectedId(active), integer(extension, "Extension", 1, 720));
            extension.clear();
            load(refreshSessions);
        });
        extend.disableProperty().bind(unselected(active));
        Button finish = action("Finish session", "", () -> {
            long id = selectedId(active);
            CheckBox fault = new CheckBox("Report an equipment fault");
            TextField description = field("Describe the fault");
            formDialog("Finish session", "Finish session", () -> {
                sessions.finish(current.id(), id);
                if (fault.isSelected()) {
                    maintenance.report(current.id(), sessions.sessionStation(id), "MEDIUM", description.getText());
                }
                load(() -> {
                    bill.setText("Session #" + id + " · " + sessions.bill(id));
                    bill.setVisible(true);
                });
                load(refreshSessions);
            }, fault, formField("Fault description", description));
        });
        finish.disableProperty().bind(unselected(active));
        VBox page = page(section("Active sessions", "Extend play time or finish a session to generate its charge.",
                        active, toolbar(formField("Extension in minutes", extension), extend, finish,
                                secondary("Refresh", "", refreshActive)), bill),
                section("Reservation check-in", null, bookings, toolbar(startBooking, secondary("Refresh", "", refreshBookings))),
                section("Walk-in session", "Set the duration, check availability and select a customer and station.",
                        toolbar(formField("Planned minutes", planned), secondary("Check availability", "", refreshWalkIn), startWalk),
                        columns(formField("Customer", customers), formField("Station", available))));
        refreshDesk(refreshSessions);
        return page;
    }

    private void refreshDesk(Work refresh) {
        load(refresh);
        deskRefresh = new javafx.animation.Timeline(new javafx.animation.KeyFrame(
                javafx.util.Duration.seconds(10), event -> load(refresh)));
        deskRefresh.setCycleCount(javafx.animation.Animation.INDEFINITE);
        deskRefresh.play();
    }

    private void stopDeskRefresh() {
        if (deskRefresh != null) {
            deskRefresh.stop();
            deskRefresh = null;
        }
    }

    private Pane paymentPage() {
        ListView<String> rows = records("No outstanding charges.");
        ListView<String> receipts = records("No receipts recorded for this account.");
        Work refresh = () -> fill(rows, payments::outstanding);
        Work refreshReceipts = () -> fill(receipts, () -> payments.receipts(current.id()));
        TextField amount = field("0.00"), reference = field("Reference number");
        ComboBox<String> method = choices("CASH", "MANUAL_EXTERNAL");
        method.getSelectionModel().select("CASH");
        Label receipt = label("", "inline-result");
        receipt.managedProperty().bind(receipt.visibleProperty());
        receipt.setVisible(false);
        Button settle = action("Record payment", "", () -> {
            long id = payments.settle(current.id(), selectedId(rows), money(amount), method.getValue(), reference.getText());
            receipt.setText("Payment recorded · Receipt GH-" + id);
            receipt.setVisible(true);
            amount.clear();
            reference.clear();
            load(refresh);
            load(refreshReceipts);
        });
        settle.disableProperty().bind(unselected(rows));
        rows.getSelectionModel().selectedItemProperty().addListener((observable, previous, selected) -> {
            amount.clear();
            reference.clear();
            method.getSelectionModel().select("CASH");
            if (selected == null) return;
            var balance = Pattern.compile(" \\| Due PKR (\\d+(?:\\.\\d+)?)$").matcher(selected);
            if (balance.find()) amount.setText(balance.group(1));
        });
        rows.setPrefHeight(319);
        load(refresh);
        load(refreshReceipts);
        VBox ledger = section("Outstanding charges", "Choose the charge you want to settle.", rows,
                toolbar(secondary("Refresh", "", refresh)));
        VBox checkout = section("Payment details", "Cash or externally processed payments.",
                formField("Amount (PKR)", amount), formField("Payment method", method),
                formField("Reference", reference), settle, receipt);
        checkout.getStyleClass().add("checkout-panel");
        checkout.setPrefWidth(286);
        checkout.setMaxWidth(320);
        HBox row = new HBox(16, ledger, checkout);
        row.setAlignment(Pos.TOP_LEFT);
        ledger.setMinWidth(0);
        HBox.setHgrow(ledger, Priority.ALWAYS);
        return page(row, section("Receipt history", "Previously issued receipts for the signed-in account.", receipts,
                toolbar(secondary("Refresh", "", refreshReceipts))));
    }

    private Pane membershipPage() {
        ListView<String> plans = records("No membership plans are available.");
        Label currentPlan = wrap("", "inline-result");
        Work refresh = () -> {
            fill(plans, memberships::plans);
            String plan = memberships.status(current.id());
            currentPlan.setText("NONE".equals(plan) ? "You have no active membership." : displayRecord(plan));
        };
        Button subscribe = action("Request membership", "", () -> confirm("Request the selected plan?", "Request plan",
                "", () -> {
                    String selection = plans.getSelectionModel().getSelectedItem();
                    String[] plan = selection.split(" \\| ", 3);
                    memberships.subscribe(current.id(), plan[0]);
                    status("PKR 0.0".equals(plan[1]) ? "Membership updated."
                            : "Membership requested. Pay at the front desk to activate it.", false);
                    load(refresh);
                }));
        subscribe.disableProperty().bind(unselected(plans));
        load(refresh);
        return page(section("Current membership", null, currentPlan),
                section("Membership plans", "Select a plan to subscribe or renew. Paid plans activate after payment.",
                        plans, toolbar(subscribe, secondary("Refresh", "", refresh))));
    }

    private Pane teamPage() {
        ListView<String> club = records("No teams have been created.");
        ListView<String> requests = records("There are no pending join requests for your teams.");
        Work refresh = () -> fill(club, teams::teams);
        Work refreshRequests = () -> fill(requests, () -> teams.requests(current.id()));
        TextField name = field("Team name");
        Button create = action("Create team", "Team created.", () -> {
            teams.create(current.id(), name.getText());
            name.clear();
            load(refresh);
        });
        Button join = secondary("Request to join", "Join request submitted.",
                () -> teams.request(selectedId(club), current.id()));
        join.disableProperty().bind(unselected(club));
        Button approve = action("Approve", "Join request approved.", () -> decideRequest(requests, true, refreshRequests));
        Button reject = secondary("Decline", "Join request declined.", () -> decideRequest(requests, false, refreshRequests));
        approve.disableProperty().bind(unselected(requests));
        reject.disableProperty().bind(unselected(requests));
        load(refresh);
        load(refreshRequests);
        return page(section("Team directory", "Create a team or ask to join an existing roster.",
                        toolbar(formField("New team name", name), create), club,
                        toolbar(join, secondary("Refresh", "", refresh))),
                section("Captain inbox", "Review requests to join teams you captain.", requests,
                        toolbar(approve, reject, secondary("Refresh", "", refreshRequests))));
    }

    private void decideRequest(ListView<String> requests, boolean accept, Work refresh) throws Exception {
        String row = requests.getSelectionModel().getSelectedItem();
        long team = selectedId(requests), member = -1;
        var matcher = REQUEST_MEMBER.matcher(row);
        while (matcher.find()) member = Long.parseLong(matcher.group(1));
        if (member <= 0) throw new IllegalArgumentException("Could not read the selected request.");
        teams.decide(current.id(), team, member, accept);
        load(refresh);
    }

    private Pane eventPage() {
        ListView<String> events = records("No tournaments are accepting registrations.");
        ListView<String> captainTeams = records("You do not captain any teams.");
        ListView<String> entries = records("You have no tournament entries.");
        Work refresh = () -> {
            fill(events, registrations::open);
            fill(captainTeams, () -> registrations.teams(current.id()));
            fill(entries, () -> registrations.mine(current.id()));
        };
        Button solo = action("Register individually", "Registration submitted.", () -> {
            registrations.registerSolo(selectedId(events), current.id());
            load(refresh);
        });
        Button team = action("Register selected team", "Team registration submitted.", () -> {
            registrations.registerTeam(selectedId(events), current.id(), selectedId(captainTeams));
            load(refresh);
        });
        solo.disableProperty().bind(unselected(events).or(notSelectedStatus(events, " | INDIVIDUAL | ")));
        team.disableProperty().bind(unselected(events).or(notSelectedStatus(events, " | TEAM | ")).or(unselected(captainTeams)));
        load(refresh);
        return page(section("Open tournaments", "Select an event to enter individually or with an eligible team.",
                        events, toolbar(solo, secondary("Refresh", "", refresh))),
                section("My captain teams", null, captainTeams, toolbar(team)),
                section("My tournament entries", "Pending entries require payment at the front desk.", entries));
    }

    private Pane tournamentPage() {
        ListView<String> rows = records("You have no tournaments yet.");
        Work refresh = () -> fill(rows, () -> tournaments.owned(current.id()));
        Button create = action("New tournament", "", () -> createTournament(refresh));
        Button configure = secondary("Configure draft", "", () -> configureTournament(selectedId(rows), refresh));
        configure.disableProperty().bind(unselected(rows).or(notSelectedStatus(rows, " | DRAFT | ")));
        ComboBox<String> status = choices();
        status.setPromptText("Next status");
        Button change = action("Update status", "Tournament status updated.", () -> {
            tournaments.transition(current.id(), selectedId(rows), status.getValue());
            load(refresh);
        });
        change.disableProperty().bind(unselected(rows).or(status.valueProperty().isNull()));
        rows.getSelectionModel().selectedItemProperty().addListener((observable, oldRow, row) -> {
            List<String> next = List.of();
            if (row != null) {
                next = switch (tournamentStatus(row)) {
                    case "DRAFT" -> List.of("REGISTRATION_OPEN", "CANCELLED");
                    case "OPEN", "REGISTRATION_OPEN" -> List.of("REGISTRATION_CLOSED", "CANCELLED");
                    case "REGISTRATION_CLOSED" -> List.of("ACTIVE", "CANCELLED");
                    case "ACTIVE" -> List.of("COMPLETED", "CANCELLED");
                    default -> List.of();
                };
            }
            status.getSelectionModel().clearSelection();
            status.setValue(null);
            status.getItems().setAll(next);
        });
        load(refresh);
        return page(section("Your tournaments", "Create an event, then manage registration and competition.",
                toolbar(create, configure, secondary("Refresh", "", refresh)), rows,
                toolbar(formField("Next status", status), change)));
    }

    private void createTournament(Work refresh) {
        TextField name = field("Tournament name"), fee = field("0.00"), capacity = field("32");
        fee.setText("0.00");
        capacity.setText("32");
        ComboBox<String> format = choices("SINGLE_ELIMINATION", "ROUND_ROBIN");
        ComboBox<String> entrant = choices("INDIVIDUAL", "TEAM");
        format.getSelectionModel().selectFirst();
        entrant.getSelectionModel().selectFirst();
        formDialog("New tournament", "Create draft", () -> {
            tournaments.create(current.id(), name.getText(), format.getValue(), entrant.getValue(),
                    integer(capacity, "Capacity", 1, Integer.MAX_VALUE), money(fee));
            status("Tournament draft created.", false);
            load(refresh);
        }, formField("Name", name), formField("Format", format), formField("Entrants", entrant),
                formField("Capacity", capacity), formField("Entry fee (PKR)", fee));
    }

    private void configureTournament(long id, Work refresh) throws Exception {
        var details = tournaments.details(current.id(), id);
        TextField game = field("Game"), eligibility = field("Eligibility requirements");
        game.setText(details.game());
        eligibility.setText(details.eligibility());
        LocalDateTime now = LocalDateTime.now(ZONE).withSecond(0).withNano(0);
        DatePicker openDate = date(now.toLocalDate()), closeDate = date(now.plusDays(7).toLocalDate());
        DatePicker startDate = date(now.plusDays(8).toLocalDate());
        TextField openTime = field("HH:mm"), closeTime = field("HH:mm"), startTime = field("HH:mm");
        setDateTime(openDate, openTime, details.opens(), now);
        setDateTime(closeDate, closeTime, details.closes(), now.plusDays(7));
        setDateTime(startDate, startTime, details.scheduled(), now.plusDays(8));
        formDialog("Configure draft · Pakistan time", "Save details", () -> {
            Instant opens = time(openDate, openTime), closes = time(closeDate, closeTime), starts = time(startDate, startTime);
            if (!opens.isBefore(closes) || starts.isBefore(closes)) {
                throw new IllegalArgumentException("Registration must open before it closes, and the tournament must start afterward.");
            }
            tournaments.configure(current.id(), id, game.getText(), opens, closes, starts, eligibility.getText());
            status("Tournament details saved.", false);
            load(refresh);
        }, formField("Game", game), columns(formField("Registration opens", openDate), formField("Time", openTime)),
                columns(formField("Registration closes", closeDate), formField("Time", closeTime)),
                columns(formField("Tournament starts", startDate), formField("Time", startTime)),
                formField("Eligibility", eligibility));
    }

    private Pane matchPage() {
        ListView<String> events = records("You have no tournaments yet.");
        ListView<String> rows = records("Select a tournament to view its matches.");
        ComboBox<String> winner = choices();
        winner.setPromptText("Choose a participant");
        TextField a = field("0"), b = field("0");
        VBox scoreA = formField("Score A", a), scoreB = formField("Score B", b);
        Work refreshMatches = () -> {
            if (events.getSelectionModel().isEmpty()) {
                rows.getItems().clear();
                return;
            }
            fill(rows, () -> matches.matches(selectedId(events)));
            rows.setPlaceholder(label("No matches generated for this tournament.", "muted"));
        };
        Work refreshEvents = () -> {
            fill(events, () -> tournaments.owned(current.id()));
            refreshMatches.run();
        };
        events.getSelectionModel().selectedItemProperty().addListener((observable, oldRow, row) -> load(refreshMatches));
        rows.getSelectionModel().selectedItemProperty().addListener((observable, previous, row) -> {
            a.clear();
            b.clear();
            a.setAccessibleHelp(null);
            b.setAccessibleHelp(null);
            a.setPromptText("0");
            b.setPromptText("0");
            winner.getSelectionModel().clearSelection();
            winner.setValue(null);
            winner.getItems().clear();
            if (row != null) load(() -> {
                var participants = matches.participants(selectedId(rows));
                winner.getItems().setAll(participants);
                if (participants.size() == 2) {
                    a.setPromptText(participants.get(0).split(" \\| ", 2)[1]);
                    b.setPromptText(participants.get(1).split(" \\| ", 2)[1]);
                    a.setAccessibleHelp("Score for " + a.getPromptText());
                    b.setAccessibleHelp("Score for " + b.getPromptText());
                }
            });
        });
        Button generate = action("Generate bracket", "Matches generated.", () -> {
            matches.generate(current.id(), selectedId(events));
            load(refreshMatches);
        });
        generate.disableProperty().bind(unselected(events).or(javafx.beans.binding.Bindings.createBooleanBinding(() -> {
            String row = events.getSelectionModel().getSelectedItem();
            return row == null || !Set.of("REGISTRATION_CLOSED", "ACTIVE").contains(tournamentStatus(row))
                    || !rows.getItems().isEmpty();
        }, events.getSelectionModel().selectedItemProperty(), rows.getItems())));
        Button schedule = secondary("Schedule match", "", () -> scheduleMatch(selectedId(rows), refreshMatches));
        Button start = action("Start match", "Match started.", () -> {
            matches.start(current.id(), selectedId(rows));
            load(refreshMatches);
        });
        Button result = action("Record result", "Match result recorded.", () -> {
            matches.result(current.id(), selectedId(rows), integer(a, "Score A", 0, Integer.MAX_VALUE),
                    integer(b, "Score B", 0, Integer.MAX_VALUE), id(winner.getValue()));
            load(refreshEvents);
        });
        var eventUnavailable = javafx.beans.binding.Bindings.createBooleanBinding(() -> {
            String event = events.getSelectionModel().getSelectedItem();
            return event == null || !Set.of("REGISTRATION_CLOSED", "ACTIVE").contains(tournamentStatus(event));
        }, events.getSelectionModel().selectedItemProperty());
        var notScheduled = notSelectedStatus(rows, " | SCHEDULED | ");
        schedule.disableProperty().bind(notScheduled.or(eventUnavailable));
        start.disableProperty().bind(notScheduled.or(eventUnavailable)
                .or(javafx.beans.binding.Bindings.size(winner.getItems()).lessThan(2)));
        result.disableProperty().bind(notSelectedStatus(rows, " | IN_PROGRESS | ").or(eventUnavailable)
                .or(winner.valueProperty().isNull()).or(javafx.beans.binding.Bindings.size(winner.getItems()).lessThan(2)));
        load(refreshEvents);
        return page(section("Tournament bracket", "Close registration before generating a bracket.", events,
                        toolbar(generate, secondary("Refresh", "", refreshEvents))),
                section("Matches", "Start the selected match before recording scores and the winner.",
                        rows, toolbar(schedule, start), toolbar(scoreA, scoreB,
                                formField("Winner", winner), result)));
    }

    private void scheduleMatch(long id, Work refresh) throws Exception {
        ComboBox<String> station = choices();
        station.getItems().setAll(stations.records());
        DatePicker day = date(LocalDate.now(ZONE));
        TextField clock = field("HH:mm");
        formDialog("Schedule match · Pakistan time", "Save schedule", () -> {
            Instant when = time(day, clock);
            if (!when.isAfter(Instant.now())) throw new IllegalArgumentException("Choose a time in the future.");
            matches.schedule(current.id(), id, id(station.getValue()), when);
            status("Match scheduled.", false);
            load(refresh);
        }, formField("Station", station), columns(formField("Date", day), formField("Time", clock)));
    }

    private Pane stationPage() {
        ListView<String> rows = records("No active gaming stations.");
        Work refresh = () -> fill(rows, stations::inventory);
        Button add = action("Add station", "", () -> stationForm(null, refresh));
        Button edit = secondary("Edit station", "", () -> stationForm(stations.details(selectedId(rows)), refresh));
        Button deactivate = secondary("Deactivate station", "", () -> confirm("Deactivate this station?",
                "Deactivate", "Station deactivated.", () -> {
                    stations.deactivate(current.id(), selectedId(rows));
                    load(refresh);
                }));
        edit.disableProperty().bind(unselected(rows));
        deactivate.disableProperty().bind(unselected(rows));
        load(refresh);
        return page(section("Station inventory", "Manage equipment, specifications and hourly rates.",
                toolbar(add, edit, deactivate, secondary("Refresh", "", refresh)), rows));
    }

    private void stationForm(StationService.StationDetails details, Work refresh) {
        boolean create = details == null;
        TextField code = field("e.g. PC-01"), type = field("e.g. PC or PlayStation"), rate = field("0.00");
        TextArea specs = new TextArea();
        specs.setPromptText("Hardware or equipment details");
        specs.setWrapText(true);
        specs.setPrefRowCount(3);
        if (!create) {
            code.setText(details.code());
            code.setEditable(false);
            type.setText(details.type());
            specs.setText(details.specs());
            rate.setText(BigDecimal.valueOf(details.rate(), 2).toPlainString());
        }
        formDialog(create ? "Add station" : "Edit " + details.code(), "Save station", () -> {
            long hourlyRate = money(rate);
            if (create) stations.create(current.id(), code.getText(), type.getText(), specs.getText(), hourlyRate);
            else stations.update(current.id(), details.id(), type.getText(), specs.getText(), hourlyRate);
            status(create ? "Station added." : "Station updated.", false);
            load(refresh);
        }, formField("Station code", code), formField("Type", type), formField("Specifications", specs),
                formField("Hourly rate (PKR)", rate));
    }

    private Pane maintenancePage() {
        ListView<String> equipment = records("No active stations.");
        ListView<String> tickets = records("No maintenance tickets.");
        ListView<String> affected = records("No affected reservations.");
        Work refresh = () -> {
            fill(equipment, stations::inventory);
            fill(tickets, maintenance::tickets);
            fill(affected, maintenance::affectedReservations);
        };
        Button report = action("Report issue", "", () -> {
            long station = selectedId(equipment);
            ComboBox<String> severity = choices("LOW", "MEDIUM", "HIGH", "CRITICAL");
            severity.getSelectionModel().select("MEDIUM");
            TextField description = field("Describe what needs attention");
            formDialog("Report maintenance", "Create ticket", () -> {
                maintenance.report(current.id(), station, severity.getValue(), description.getText());
                status("Maintenance ticket created.", false);
                load(refresh);
            }, formField("Severity", severity), formField("Description", description));
        });
        report.disableProperty().bind(unselected(equipment));
        Button progress = secondary("Mark in progress", "Ticket marked in progress.", () -> {
            maintenance.update(current.id(), selectedId(tickets), "IN_PROGRESS", "", false);
            load(refresh);
        });
        progress.disableProperty().bind(notSelectedStatus(tickets, " | REPORTED | "));
        Button resolve = action("Resolve ticket", "", () -> {
            long ticket = selectedId(tickets);
            TextField notes = field("What was fixed?");
            CheckBox ready = new CheckBox("Equipment has been checked and is ready");
            formDialog("Resolve maintenance ticket", "Resolve ticket", () -> {
                maintenance.update(current.id(), ticket, "RESOLVED", notes.getText(), ready.isSelected());
                status("Maintenance ticket resolved.", false);
                load(refresh);
            }, formField("Resolution notes", notes), ready);
        });
        resolve.disableProperty().bind(notSelectedStatus(tickets, " | IN_PROGRESS | "));
        Button cancelAffected = secondary("Cancel affected reservation", "Reservation cancelled.", () -> {
            maintenance.cancelAffected(current.id(), selectedId(affected));
            load(refresh);
        });
        cancelAffected.disableProperty().bind(unselected(affected));
        load(refresh);
        return page(section("Maintenance tickets", "Track equipment issues from report through readiness check.",
                        tickets, toolbar(progress, resolve, secondary("Refresh", "", refresh))),
                section("Report an equipment issue", "Select a station to open a maintenance ticket.",
                        equipment, toolbar(report)),
                section("Affected reservations", "Review future bookings affected by an equipment outage.",
                        affected, toolbar(cancelAffected)));
    }

    private Pane reportPage() {
        LocalDate today = LocalDate.now(ZONE);
        DatePicker from = date(today.minusDays(30)), to = date(today);
        ComboBox<String> category = choices("Summary", "Revenue", "Reservations", "Memberships",
                "Tournament participation", "Maintenance", "Station utilization");
        category.getSelectionModel().selectFirst();
        ListView<String> rows = records("Choose a date range and generate a report.");
        Supplier<Instant[]> range = () -> {
            LocalDate first = readDate(from), last = readDate(to);
            if (last.isBefore(first)) throw new IllegalArgumentException("End date must be on or after the start date.");
            return new Instant[]{first.atStartOfDay(ZONE).toInstant(), last.plusDays(1).atStartOfDay(ZONE).toInstant()};
        };
        Work refresh = () -> {
            Instant[] dates = range.get();
            var metrics = reports.summary(current.id(), category.getValue(), dates[0], dates[1]);
            rows.getItems().setAll(metrics.entrySet().stream().map(entry -> pretty(entry.getKey()) + "  ·  "
                    + (entry.getKey().endsWith("_paisa") ? "PKR " + BigDecimal.valueOf(entry.getValue(), 2) : entry.getValue())).toList());
        };
        Button export = secondary("Export CSV", "", () -> {
            Instant[] dates = range.get();
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Export GameHub report");
            chooser.setInitialFileName("gamehub-report.csv");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
            var file = chooser.showSaveDialog(stage);
            if (file == null) return;
            reports.export(current.id(), category.getValue(), dates[0], dates[1], file.toPath());
            status("CSV exported to " + file.getName() + ".", false);
        });
        load(refresh);
        return page(section("Report builder", "Choose a report and an inclusive date range in Pakistan Standard Time.",
                toolbar(formField("From", from), formField("Through", to), formField("Report", category),
                        action("Generate", "", refresh), export), rows));
    }

    private static VBox page(Node... children) {
        VBox page = new VBox(18, children);
        page.getStyleClass().add("page");
        return page;
    }

    private static VBox section(String title, String help, Node... items) {
        VBox panel = new VBox(14);
        panel.getStyleClass().add("panel");
        VBox heading = new VBox(4, label(title, "section-title"));
        if (help != null) heading.getChildren().add(wrap(help, "muted"));
        panel.getChildren().add(heading);
        panel.getChildren().addAll(items);
        return panel;
    }

    private static FlowPane toolbar(Node... children) {
        FlowPane row = new FlowPane(10, 10, children);
        row.setRowValignment(javafx.geometry.VPos.BOTTOM);
        return row;
    }

    private static HBox columns(Region... columns) {
        HBox row = new HBox(12, columns);
        for (Region col : columns) {
            col.setMaxWidth(Double.MAX_VALUE);
            col.setMinWidth(0);
            HBox.setHgrow(col, Priority.ALWAYS);
        }
        return row;
    }

    private static VBox formField(String title, Region input) {
        Label caption = label(title, "field-caption");
        caption.setLabelFor(input);
        input.setMaxWidth(Double.MAX_VALUE);
        return new VBox(6, caption, input);
    }

    private static TextField field(String hint) {
        TextField field = new TextField();
        field.setPromptText(hint);
        field.setPrefColumnCount(hint.equals("HH:mm") ? 6 : 14);
        return field;
    }

    private static DatePicker date(LocalDate day) {
        DatePicker picker = new DatePicker(day);
        picker.setPrefWidth(148);
        picker.setPromptText("yyyy-mm-dd");
        picker.setConverter(new StringConverter<>() {
            @Override public String toString(LocalDate value) {
                return value == null ? "" : value.toString();
            }

            @Override public LocalDate fromString(String value) {
                return value == null || value.isBlank() ? null : LocalDate.parse(value.trim());
            }
        });
        picker.setEditable(false);
        return picker;
    }

    private static LocalDate readDate(DatePicker picker) {
        if (picker.getValue() == null) throw new IllegalArgumentException("Select a date.");
        return picker.getValue();
    }

    private static Instant time(DatePicker day, TextField clock) {
        try {
            return readDate(day).atTime(LocalTime.parse(clock.getText().trim(), DateTimeFormatter.ofPattern("HH:mm")
                    .withResolverStyle(java.time.format.ResolverStyle.STRICT))).atZone(ZONE).toInstant();
        } catch (java.time.format.DateTimeParseException failure) {
            throw new IllegalArgumentException("Enter a valid 24-hour time in HH:mm format.");
        }
    }

    private static void setDateTime(DatePicker day, TextField clock, Instant saved, LocalDateTime fallback) {
        LocalDateTime value = saved == null ? fallback : LocalDateTime.ofInstant(saved, ZONE);
        day.setValue(value.toLocalDate());
        clock.setText(value.format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    private static ComboBox<String> choices(String... options) {
        ComboBox<String> box = new ComboBox<>();
        box.getItems().setAll(options);
        box.setCellFactory(list -> choiceCell());
        box.setButtonCell(choiceCell());
        return box;
    }

    private static ListCell<String> choiceCell() {
        return new ListCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.matches("[A-Z]+(?:_[A-Z]+)*") ? pretty(item) : displayRecord(item));
            }
        };
    }

    private static ListView<String> records(String empty) {
        ListView<String> list = new ListView<>();
        list.getStyleClass().add("record-list");
        list.setPlaceholder(wrap(empty, "list-empty"));
        list.setPrefHeight(184);
        list.setCellFactory(view -> new ListCell<>() {
            @Override protected void updateItem(String item, boolean isEmpty) {
                super.updateItem(item, isEmpty);
                setText(null);
                setGraphic(null);
                setAccessibleText(null);
                if (isEmpty || item == null) return;
                var match = RECORD_ID.matcher(item);
                boolean hasId = match.find();
                String value = hasId ? item.substring(match.end(1)).replaceFirst("^[\\s|]+", "") : item;
                setAccessibleText(displayRecord(item));
                String[] parts = value.split(" \\| ");
                VBox details = new VBox(4, wrap(displayRecord(parts[0]), "record-title"));
                details.setMinWidth(0);
                List<String> metadata = new ArrayList<>();
                FlowPane badges = new FlowPane(5, 4);
                for (int index = 1; index < parts.length; index++) {
                    String state = parts[index].replaceFirst("^\\[([A-Z_]+)]$", "$1");
                    String style = statusClass(state);
                    if (style.isEmpty()) metadata.add(parts[index]);
                    else badges.getChildren().add(label(pretty(state), "record-state", style));
                }
                if (!metadata.isEmpty()) details.getChildren().add(wrap(displayRecord(String.join(" | ", metadata)), "record-text"));
                if (!badges.getChildren().isEmpty()) details.getChildren().add(badges);
                HBox row = new HBox(11);
                row.setAlignment(Pos.CENTER_LEFT);
                row.prefWidthProperty().bind(widthProperty().subtract(29));
                row.setMinWidth(0);
                if (hasId) {
                    Label id = label("#" + match.group(1), "record-id");
                    id.setMinWidth(Region.USE_PREF_SIZE);
                    row.getChildren().add(id);
                }
                HBox.setHgrow(details, Priority.ALWAYS);
                row.getChildren().add(details);
                setGraphic(row);
            }
        });
        return list;
    }

    private static String statusClass(String state) {
        if (POSITIVE_STATES.contains(state)) return "state-positive";
        if (WORKING_STATES.contains(state)) return "state-working";
        if (WAITING_STATES.contains(state)) return "state-waiting";
        if (INACTIVE_STATES.contains(state)) return "state-inactive";
        return "";
    }

    private static String displayRecord(String value) {
        return TIMESTAMP.matcher(value).replaceAll(match -> RECORD_DATE.format(Instant.parse(match.group())))
                .replace(" | ", "  ·  ").replace(" -> ", "  →  ");
    }

    private static String pretty(String value) {
        String name = value.replace('_', ' ').toLowerCase(Locale.ROOT);
        return name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
    }

    private static String tournamentStatus(String value) {
        var matcher = Pattern.compile(" \\| ([A-Z_]+) \\| PKR ").matcher(value);
        String status = "";
        while (matcher.find()) status = matcher.group(1);
        return status;
    }

    private static void fill(ListView<String> list, Records source) throws Exception {
        String selected = list.getSelectionModel().getSelectedItem();
        List<String> rows = source.get();
        if (list.getItems().equals(rows)) return;
        list.getSelectionModel().clearSelection();
        list.getItems().setAll(rows);
        if (selected == null) return;
        if (rows.contains(selected)) {
            list.getSelectionModel().select(selected);
        } else {
            var oldId = RECORD_ID.matcher(selected);
            if (oldId.find()) {
                String value = oldId.group(1);
                rows.stream().filter(row -> {
                    var rowId = RECORD_ID.matcher(row);
                    return rowId.find() && rowId.group(1).equals(value);
                }).findFirst().ifPresent(list.getSelectionModel()::select);
            }
        }
    }

    private static BooleanBinding unselected(ListView<String> list) {
        return list.getSelectionModel().selectedItemProperty().isNull();
    }

    private static BooleanBinding notSelectedStatus(ListView<String> list, String status) {
        return javafx.beans.binding.Bindings.createBooleanBinding(() -> {
            String row = list.getSelectionModel().getSelectedItem();
            return row == null || !row.contains(status);
        }, list.getSelectionModel().selectedItemProperty());
    }

    private static long selectedId(ListView<String> list) {
        return id(list.getSelectionModel().getSelectedItem());
    }

    private static long id(String row) {
        if (row != null) {
            var match = RECORD_ID.matcher(row);
            if (match.find()) return Long.parseLong(match.group(1));
        }
        throw new IllegalArgumentException("Select a record first.");
    }

    private static int integer(TextField field, String name, int min, int max) {
        try {
            int value = Integer.parseInt(field.getText().trim());
            if (value >= min && value <= max) return value;
        } catch (NumberFormatException ignored) {
            // The same message applies to invalid numbers and out-of-range values.
        }
        if (max == Integer.MAX_VALUE) throw new IllegalArgumentException(name + " must be a whole number of at least " + min + ".");
        throw new IllegalArgumentException(name + " must be between " + min + " and " + max + ".");
    }

    private static long money(TextField field) {
        try {
            String value = field.getText().trim();
            if (!value.matches("\\d+(?:\\.\\d{1,2})?")) throw new NumberFormatException();
            return new BigDecimal(value).movePointRight(2).longValueExact();
        } catch (NumberFormatException | ArithmeticException failure) {
            throw new IllegalArgumentException("Enter a PKR amount with up to two decimal places, for example 600.00.");
        }
    }

    private Button action(String title, String success, Work work) {
        Button button = new Button(title);
        button.setOnAction(event -> perform(work, success));
        return button;
    }

    private Button secondary(String title, String success, Work work) {
        Button button = action(title, success, work);
        button.getStyleClass().add("secondary");
        return button;
    }

    private void load(Work work) {
        try {
            work.run();
        } catch (Exception failure) {
            status(message(failure), true);
        }
    }

    private void perform(Work work, String success) {
        feedback.setVisible(false);
        try {
            work.run();
            if (!success.isEmpty() && !feedback.isVisible()) status(success, false);
        } catch (Exception failure) {
            status(message(failure), true);
        }
    }

    private void status(String message, boolean error) {
        feedback.getStyleClass().removeAll("feedback-error", "feedback-success");
        feedback.getStyleClass().add(error ? "feedback-error" : "feedback-success");
        feedback.setText(message);
        feedback.setVisible(true);
    }

    private void formDialog(String title, String save, Work work, Node... fields) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        ButtonType submit = new ButtonType(save, ButtonBar.ButtonData.OK_DONE);
        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().addAll(submit, ButtonType.CANCEL);
        pane.lookupButton(ButtonType.CANCEL).getStyleClass().add("secondary");
        Label error = wrap("", "feedback", "feedback-error");
        error.managedProperty().bind(error.visibleProperty());
        error.setVisible(false);
        VBox content = new VBox(12, fields);
        content.getChildren().add(error);
        content.getStyleClass().add("dialog-form");
        pane.setContent(content);
        pane.setPrefWidth(475);
        style(pane);
        pane.lookupButton(submit).addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            try {
                work.run();
            } catch (Exception failure) {
                error.setText(message(failure));
                error.setVisible(true);
                event.consume();
            }
        });
        dialog.showAndWait();
    }

    private void confirm(String title, String verb, String success, Work work) {
        ButtonType proceed = new ButtonType(verb, ButtonBar.ButtonData.OK_DONE);
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, "", proceed, ButtonType.CANCEL);
        dialog.initOwner(stage);
        dialog.setTitle("GameHub");
        dialog.setHeaderText(title);
        style(dialog.getDialogPane());
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).getStyleClass().add("secondary");
        if (dialog.showAndWait().orElse(ButtonType.CANCEL) == proceed) perform(work, success);
    }

    private static String message(Exception failure) {
        String detail = failure.getMessage();
        if (failure instanceof java.sql.SQLException) {
            if (detail != null && detail.contains("gaming_stations.code")) return "That station code is already in use.";
            if (detail != null && detail.contains("teams.name")) return "That team name is already in use.";
            if (detail != null && detail.contains("SQLITE_BUSY")) return "GameHub is busy. Try again in a moment.";
            java.util.logging.Logger.getLogger(GameHubApp.class.getName()).log(java.util.logging.Level.WARNING,
                    "GameHub database operation failed", failure);
            return "The database operation could not be completed.";
        }
        return detail == null || detail.isBlank() ? "GameHub could not complete that action." : detail;
    }

    private static Label label(String text, String... classes) {
        Label label = new Label(text);
        label.getStyleClass().addAll(classes);
        return label;
    }

    private static Label wrap(String text, String... classes) {
        Label label = label(text, classes);
        label.setWrapText(true);
        return label;
    }

    private static ScrollPane scroll(Node child) {
        ScrollPane pane = new ScrollPane(child);
        pane.setFitToWidth(true);
        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return pane;
    }

    private static Region spacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private void setScene(Pane root, int width, int height) {
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(stylesheet());
        boolean first = !stage.isShowing();
        stage.setScene(scene);
        if (first) {
            stage.show();
        } else if (!stage.isMaximized()) {
            stage.sizeToScene();
            stage.centerOnScreen();
        }
    }

    private void style(DialogPane pane) {
        pane.getStylesheets().add(stylesheet());
    }

    private String stylesheet() {
        return Objects.requireNonNull(getClass().getResource("/gamehub.css")).toExternalForm();
    }

    @Override
    public void stop() throws Exception {
        stopDeskRefresh();
        if (db != null) db.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
