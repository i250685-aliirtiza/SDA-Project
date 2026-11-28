package com.gamehub;

import com.gamehub.application.UserService;
import com.gamehub.application.service.ReservationService;
import com.gamehub.application.service.SessionService;
import com.gamehub.application.service.StationService;
import com.gamehub.infrastructure.Database;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationAvailabilityRegressionTest {
    @TempDir Path temp;

    @Test
    void stationTypeAndIntervalMatchTheBookingPolicy() throws Exception {
        try (var db = new Database(temp.resolve("availability.db"))) {
            var users = new UserService(db);
            users.bootstrap("manager", "password1");
            var manager = users.authenticate("manager", "password1");
            var first = users.registerCustomer("First Player", "first@example.com", "03001234567", "password2");
            var second = users.registerCustomer("Second Player", "second@example.com", "03001234568", "password2");
            var stations = new StationService(db);
            var reservations = new ReservationService(db);
            var sessions = new SessionService(db);
            long pc = stations.addStation(manager.id(), "PC-01", "PC", "", 60000);
            stations.addStation(manager.id(), "PS-01", "PlayStation", "", 60000);
            assertEquals(List.of("PC", "PlayStation"), reservations.stationTypes());

            Instant start = Instant.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(900);
            Instant end = start.plusSeconds(3600);
            assertEquals(1, reservations.available(start, end, "PC").size());
            long reservation = reservations.reserve(first.id(), pc, start, end);
            assertTrue(reservations.available(start, end, "PC").isEmpty());
            assertEquals(1, reservations.available(start.minusSeconds(30), start, "PC").size());
            assertTrue(reservations.available(start.minusSeconds(30), start.plusNanos(1), "PC").isEmpty());
            assertThrows(IllegalStateException.class,
                    () -> reservations.reserve(second.id(), pc, start.minusSeconds(30), start.plusNanos(1)));
            assertEquals(1, reservations.available(end, end.plusSeconds(60), "PC").size());
            assertTrue(reservations.reserve(second.id(), pc, end, end.plusNanos(1)) > 0);
            assertEquals(1, reservations.available(start, end, "PlayStation").size());

            long session = sessions.startSession(manager.id(), reservation);
            sessions.finishSessionCalculated(manager.id(), session);

            assertTrue(reservations.available(start, end, "PC").isEmpty());
            assertThrows(IllegalStateException.class, () -> reservations.reserve(second.id(), pc, start, end));
            assertEquals(1, reservations.available(end.plusNanos(1), end.plusSeconds(60), "PC").size());
        }
    }
}
