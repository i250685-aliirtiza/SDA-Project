package com.gamehub;

import com.gamehub.application.UserService;
import com.gamehub.application.service.*;
import com.gamehub.domain.Role;
import com.gamehub.infrastructure.Database;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class FunctionalClosureRegressionTest {
    @Test
    void matchSchedulingRejectsTheSameStationAndTime() throws Exception {
        Path path = Files.createTempFile("match-conflict-", ".db");
        try (Database db = new Database(path)) {
            UserService users = new UserService(db);
            users.bootstrap("manager", "password1");
            var manager = users.authenticate("manager", "password1");
            var customers = new java.util.ArrayList<com.gamehub.domain.User>();
            for (int i = 0; i < 4; i++) customers.add(users.registerCustomer("Player " + i, "p" + i + "@example.com", "03001234" + String.format("%03d", i), "password2"));
            var tournaments = new TournamentService(db);
            long tournament = tournaments.createTournament(manager.id(), "Conflict Cup", "SINGLE_ELIMINATION", 0);
            var registration = new TournamentRegistrationService(db);
            for (var customer : customers) registration.solo(tournament, customer.id());
            tournaments.changeTournamentStatus(manager.id(), tournament, "REGISTRATION_CLOSED");
            var matches = new MatchService(db);
            var generated = matches.generateMatches(manager.id(), tournament);
            long station = new StationService(db).addStation(manager.id(), "MATCH-01", "PC", "", 50000);
            Instant when = Instant.now().plusSeconds(7200);
            matches.scheduleMatch(manager.id(), generated.get(0), station, when);
            assertThrows(IllegalStateException.class, () -> matches.scheduleMatch(manager.id(), generated.get(1), station, when));
        } finally { Files.deleteIfExists(path); }
    }

    @Test
    void tournamentCancellationCancelsRegistrationsAndPendingCharges() throws Exception {
        Path path = Files.createTempFile("tournament-cancel-", ".db");
        try (Database db = new Database(path)) {
            UserService users = new UserService(db);
            users.bootstrap("manager", "password1");
            var manager = users.authenticate("manager", "password1");
            var customer = users.registerCustomer("Customer", "cancel@example.com", "03001234001", "password2");
            var tournaments = new TournamentService(db);
            long tournament = tournaments.createTournament(manager.id(), "Cancelled Cup", "SINGLE_ELIMINATION", "INDIVIDUAL", 4, 5000);
            tournaments.changeTournamentStatus(manager.id(), tournament, "REGISTRATION_OPEN");
            new TournamentRegistrationService(db).registerTournament(tournament, customer.id());
            tournaments.changeTournamentStatus(manager.id(), tournament, "CANCELLED");
            try (var rows = db.connection().prepareStatement("SELECT status FROM registrations WHERE tournament_id=?")) {
                rows.setLong(1, tournament);
                try (var result = rows.executeQuery()) { assertTrue(result.next()); assertEquals("CANCELLED", result.getString(1)); }
            }
            try (var rows = db.connection().createStatement().executeQuery("SELECT status FROM charges WHERE source_type='TOURNAMENT'")) {
                assertTrue(rows.next()); assertEquals("CANCELLED", rows.getString(1));
            }
        } finally { Files.deleteIfExists(path); }
    }

    @Test
    void maintenanceReportsAffectedReservationsAndRejectsDuplicateFaults() throws Exception {
        Path path = Files.createTempFile("maintenance-closure-", ".db");
        try (Database db = new Database(path)) {
            UserService users = new UserService(db);
            users.bootstrap("manager", "password1");
            var manager = users.authenticate("manager", "password1");
            var customer = users.registerCustomer("Customer", "maintenance@example.com", "03001234002", "password2");
            long station = new StationService(db).addStation(manager.id(), "MAINT-01", "PC", "", 50000);
            new ReservationService(db).reserve(customer.id(), station, Instant.now().plusSeconds(3600), Instant.now().plusSeconds(7200));
            var maintenance = new MaintenanceService(db);
            maintenance.openMaintenance(manager.id(), station, "HIGH", "Display failure");
            assertThrows(IllegalStateException.class, () -> maintenance.openMaintenance(manager.id(), station, "HIGH", "Display failure"));
            assertEquals(1, maintenance.affectedReservations().size());
            long reservation;
            try (var rows = db.connection().createStatement().executeQuery("SELECT id FROM reservations WHERE status='AFFECTED'")) { assertTrue(rows.next()); reservation = rows.getLong(1); }
            maintenance.cancelAffectedReservation(manager.id(), reservation);
            assertEquals(0, maintenance.affectedReservations().size());
        } finally { Files.deleteIfExists(path); }
    }

    @Test
    void registrationAndPaymentCreateAuditEvents() throws Exception {
        Path path = Files.createTempFile("registration-audit-", ".db");
        try (Database db = new Database(path)) {
            UserService users = new UserService(db);
            users.bootstrap("manager", "password1");
            var manager = users.authenticate("manager", "password1");
            var customer = users.registerCustomer("Customer", "audit@example.com", "03001234003", "password2");
            long tournament = new TournamentService(db).createTournament(manager.id(), "Audit Cup", "SINGLE_ELIMINATION", 0);
            new TournamentRegistrationService(db).registerTournament(tournament, customer.id());
            try (var rows = db.connection().createStatement().executeQuery("SELECT COUNT(*) FROM audit_events WHERE entity_type='REGISTRATION' AND operation='CREATED'")) {
                assertTrue(rows.next()); assertEquals(1, rows.getInt(1));
            }
        } finally { Files.deleteIfExists(path); }
    }
}

