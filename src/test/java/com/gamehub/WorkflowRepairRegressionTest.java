package com.gamehub;

import com.gamehub.application.UserService;
import com.gamehub.application.service.*;
import com.gamehub.infrastructure.Database;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkflowRepairRegressionTest {
    @Test
    void freeMembershipActivatesAndPaidMembershipRenewsAfterFullPayment() throws Exception {
        var path = Files.createTempFile("gamehub-renewal-", ".db");
        try (var db = new Database(path)) {
            var users = new UserService(db);
            var customer = users.registerCustomer("player", "password1");
            assertTrue(users.needsManagerSetup());
            users.bootstrap("manager", "password1");
            assertFalse(users.needsManagerSetup());
            long manager = users.authenticate("manager", "password1").id();
            var membership = new MembershipService(db);
            var payments = new PaymentService(db);

            membership.subscribe(customer.id(), "BASIC");
            assertTrue(membership.status(customer.id()).startsWith("BASIC"));
            long charge = membership.subscribe(customer.id(), "GOLD");
            assertThrows(IllegalStateException.class, () -> membership.subscribe(customer.id(), "GOLD"));
            payments.settle(manager, charge, 2500, "CASH", "half-payment");
            assertTrue(payments.outstanding().getFirst().contains("PKR 25.00"));
            assertTrue(membership.status(customer.id()).startsWith("BASIC"));

            payments.settle(manager, charge, 2500, "CASH", "remaining-payment");
            String first = membership.status(customer.id());
            assertTrue(first.startsWith("GOLD"));
            long renewal = membership.subscribe(customer.id(), "GOLD");
            payments.settle(manager, renewal, 5000, "CASH", "renewal-payment");
            assertNotEquals(first, membership.status(customer.id()));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    void paidTeamEntriesReserveCapacityAndBecomeConfirmedOnSettlement() throws Exception {
        var path = Files.createTempFile("gamehub-team-payment-", ".db");
        try (var db = new Database(path)) {
            var users = new UserService(db);
            users.bootstrap("manager", "password1");
            long manager = users.authenticate("manager", "password1").id();
            var teams = new TeamService(db);
            var tournaments = new TournamentService(db);
            var registration = new TournamentRegistrationService(db);
            long captain = users.registerCustomer("captain", "password1").id();
            long player = users.registerCustomer("player", "password1").id();
            long team = teams.create(captain, "Night Shift");
            teams.request(team, player);
            assertThrows(IllegalStateException.class, () -> teams.request(team, player));
            teams.decide(captain, team, player, false);
            teams.request(team, player);
            teams.decide(captain, team, player, true);

            long otherCaptain = users.registerCustomer("second-captain", "password1").id();
            long otherPlayer = users.registerCustomer("second-player", "password1").id();
            long otherTeam = teams.create(otherCaptain, "Day Shift");
            teams.request(otherTeam, otherPlayer);
            teams.decide(otherCaptain, otherTeam, otherPlayer, true);

            long tournament = tournaments.create(manager, "Team Cup", "SINGLE_ELIMINATION", "TEAM", 1, 25000);
            tournaments.transition(manager, tournament, "REGISTRATION_OPEN");
            assertThrows(IllegalStateException.class, () -> registration.solo(tournament, captain));
            registration.team(tournament, captain, team);
            assertTrue(registration.myRegistrations(captain).getFirst().contains("PENDING"));
            assertThrows(IllegalStateException.class, () -> registration.team(tournament, otherCaptain, otherTeam));

            var payments = new PaymentService(db);
            long charge = Long.parseLong(payments.outstanding().getFirst().substring(1).split(" ")[0]);
            payments.settle(manager, charge, 25000, "CASH", "team-entry");
            assertTrue(registration.myRegistrations(captain).getFirst().contains("CONFIRMED"));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    void oddEntrantsAdvanceThroughByes() throws Exception {
        knockoutFinishesAfterEveryRound(5);
    }

    @Test
    void evenBracketsDoNotFinishAfterTheFirstSemifinal() throws Exception {
        knockoutFinishesAfterEveryRound(4);
        knockoutFinishesAfterEveryRound(8);
    }

    @Test
    void maintenanceDoesNotReactivateRetiredStationsOrIgnoreOpenTickets() throws Exception {
        var path = Files.createTempFile("gamehub-maintenance-", ".db");
        try (var db = new Database(path)) {
            var users = new UserService(db);
            users.bootstrap("manager", "password1");
            long manager = users.authenticate("manager", "password1").id();
            var stations = new StationService(db);
            var maintenance = new MaintenanceService(db);
            long retired = stations.addStation(manager, "RETIRED", "PC", "", 60000);
            stations.deactivateStation(manager, retired);
            assertThrows(IllegalArgumentException.class, () -> maintenance.report(manager, retired, "LOW", "Old issue"));
            assertTrue(maintenance.tickets().isEmpty());

            long working = stations.addStation(manager, "ACTIVE", "PC", "", 60000);
            long first = maintenance.report(manager, working, "LOW", "Controller issue");
            long second = maintenance.report(manager, working, "HIGH", "Power issue");
            maintenance.update(manager, first, "IN_PROGRESS", "", false);
            assertThrows(IllegalArgumentException.class, () -> maintenance.update(manager, first, "RESOLVED", "", false));
            maintenance.update(manager, first, "RESOLVED", "Controller replaced", true);
            assertTrue(stations.stationRecords().isEmpty());
            maintenance.update(manager, second, "IN_PROGRESS", "", false);
            maintenance.update(manager, second, "RESOLVED", "Power supply replaced", true);
            assertEquals(1, stations.stationRecords().size());
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    void leagueCompletesOnlyAfterItsFinalResult() throws Exception {
        var path = Files.createTempFile("gamehub-league-", ".db");
        try (var db = new Database(path)) {
            var users = new UserService(db);
            users.bootstrap("manager", "password1");
            long manager = users.authenticate("manager", "password1").id();
            var tournaments = new TournamentService(db);
            var registrations = new TournamentRegistrationService(db);
            var matches = new MatchService(db);
            long tournament = tournaments.create(manager, "League", " round_robin ", " individual ", 3, 0);
            tournaments.transition(manager, tournament, "REGISTRATION_OPEN");
            for (int index = 0; index < 3; index++) {
                long player = users.registerCustomer("entrant" + index, "password1").id();
                registrations.solo(tournament, player);
            }
            tournaments.transition(manager, tournament, "REGISTRATION_CLOSED");
            tournaments.transition(manager, tournament, "ACTIVE");
            assertThrows(IllegalStateException.class, () -> tournaments.transition(manager, tournament, "COMPLETED"));
            var fixtures = matches.generate(manager, tournament);
            assertEquals(3, fixtures.size());
            for (long fixture : fixtures) {
                matches.start(manager, fixture);
                String firstParticipant = matches.participants(fixture).getFirst();
                long winner = Long.parseLong(firstParticipant.substring(1).split(" ")[0]);
                matches.result(manager, fixture, 2, 1, winner);
                if (fixture != fixtures.getLast()) {
                    assertThrows(IllegalStateException.class,
                            () -> tournaments.transition(manager, tournament, "COMPLETED"));
                }
            }
            assertTrue(tournaments.owned(manager).getFirst().contains(" | COMPLETED | "));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    private void knockoutFinishesAfterEveryRound(int entrants) throws Exception {
        var path = Files.createTempFile("gamehub-bracket-", ".db");
        try (var db = new Database(path)) {
            var users = new UserService(db);
            users.bootstrap("manager", "password1");
            long manager = users.authenticate("manager", "password1").id();
            var tournaments = new TournamentService(db);
            var registration = new TournamentRegistrationService(db);
            var matches = new MatchService(db);
            long tournament = tournaments.create(manager, "Open Cup", "SINGLE_ELIMINATION", "INDIVIDUAL", entrants, 0);
            tournaments.configure(manager, tournament, "Gaming", Instant.now().minusSeconds(60),
                    Instant.now().plusSeconds(60), Instant.now().plus(2, ChronoUnit.MINUTES), "");
            tournaments.transition(manager, tournament, "REGISTRATION_OPEN");
            for (int player = 0; player < entrants; player++) {
                long id = users.registerCustomer("player" + player, "password1").id();
                registration.solo(tournament, id);
            }
            tournaments.transition(manager, tournament, "REGISTRATION_CLOSED");
            List<Long> roundOne = matches.generate(manager, tournament);
            assertEquals((entrants + 1) / 2, roundOne.size());
            if (entrants % 2 != 0) {
                assertThrows(IllegalStateException.class, () -> matches.start(manager, roundOne.getLast()));
            }
            int played = 0;
            for (int round = 1; round <= 3; round++) {
                String prefix = " | Round " + round + " /";
                for (String row : matches.matches(tournament).stream()
                        .filter(row -> row.contains(prefix) && row.contains(" | SCHEDULED | ")).toList()) {
                    long match = Long.parseLong(row.substring(1).split(" ")[0]);
                    String participant = matches.participants(match).getFirst();
                    long winner = Long.parseLong(participant.substring(1).split(" ")[0]);
                    assertThrows(IllegalStateException.class, () -> matches.result(manager, match, 2, 1, winner));
                    matches.start(manager, match);
                    matches.result(manager, match, 2, 1, winner);
                    played++;
                    if (played < entrants - 1) {
                        assertFalse(tournaments.owned(manager).getFirst().contains(" | COMPLETED | "));
                    }
                }
            }
            assertEquals(entrants - 1, played);
            assertTrue(tournaments.owned(manager).getFirst().contains(" | COMPLETED | "));
        } finally {
            Files.deleteIfExists(path);
        }
    }
}
