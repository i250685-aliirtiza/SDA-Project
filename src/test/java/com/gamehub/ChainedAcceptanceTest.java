package com.gamehub;

import com.gamehub.application.OperationsService;
import com.gamehub.application.UserService;
import com.gamehub.domain.Role;
import com.gamehub.infrastructure.Database;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ChainedAcceptanceTest {
  @Test
  void persistedDemoChainCoversCoreWorkflows() throws Exception {
    Path path = Files.createTempFile("gamehub-chain-", ".db");
    try (Database db = new Database(path)) {
      UserService users = new UserService(db);
      users.bootstrap("manager", "password1");
      var manager = users.authenticate("manager", "password1");
      var staff = users.provision("manager", "password1", "staff", "password2", Role.STAFF);
      var organizer = users.provision("manager", "password1", "organizer", "password3", Role.ORGANIZER);
      var alice = users.registerCustomer("Alice", "alice@example.com", "03001234567", "password4");
      var bob = users.registerCustomer("Bob", "bob@example.com", "03001234568", "password5");
      OperationsService ops = new OperationsService(db);

      long station = ops.addStation(manager.id(), "DEMO-01", "PC", "RTX", 60000);
      Instant start = Instant.now().plusSeconds(3600);
      long reservation = ops.reserve(alice.id(), station, start, start.plusSeconds(3600));
      long session = ops.startSession(staff.id(), reservation);
      ops.finishSession(manager.id(), session, 60000);
      long sessionCharge; try (var q = db.connection().prepareStatement("SELECT charge_id FROM sessions WHERE id=?")) { q.setLong(1, session); try (var r = q.executeQuery()) { assertTrue(r.next()); sessionCharge = r.getLong(1); } }
      ops.payChargeAsStaff(staff.id(), sessionCharge, 60000, "CASH", "chain-session");

      long membershipCharge = ops.subscribeOrRenew(alice.id(), "GOLD");
      ops.payChargeAsStaff(staff.id(), membershipCharge, 5000, "CASH", "chain-membership");
      assertTrue(ops.membership(alice.id()).startsWith("GOLD"));

      long team = ops.createTeam(alice.id(), "Demo Team");
      ops.requestJoin(team, bob.id());
      ops.decideJoin(alice.id(), team, bob.id(), true);

      long tournament = ops.createTournament(organizer.id(), "Demo Cup", "SINGLE_ELIMINATION", 0);
      long first = ops.registerTournament(tournament, alice.id());
      long second = ops.registerTournament(tournament, bob.id());
      assertTrue(first > 0 && second > 0);
      ops.changeTournamentStatus(organizer.id(), tournament, "REGISTRATION_CLOSED");
      ops.changeTournamentStatus(organizer.id(), tournament, "ACTIVE");
      var matches = ops.generateMatches(organizer.id(), tournament);
      assertEquals(1, matches.size());
      ops.startMatch(organizer.id(), matches.get(0));
      ops.recordMatchResult(organizer.id(), matches.get(0), 2, 1, first);

      long ticket = ops.openMaintenance(staff.id(), station, "MEDIUM", "Demo inspection");
      ops.updateMaintenance(staff.id(), ticket, "IN_PROGRESS", "Technician assigned", false);
      ops.updateMaintenance(staff.id(), ticket, "RESOLVED", "Ready after inspection", true);
      assertTrue(ops.reportSummary(manager.id(), start.minusSeconds(3600), Instant.now().plusSeconds(60)).containsKey("revenue_paisa"));
    }
    try (Database reopened = new Database(path)) {
      OperationsService ops = new OperationsService(reopened);
      assertTrue(ops.reportCount("users") >= 5);
      assertTrue(ops.reportCount("payments") >= 2);
      assertTrue(ops.reportCount("tournaments") >= 1);
    } finally {
      Files.deleteIfExists(path);
    }
  }
}



