package com.gamehub.application.controller;

import com.gamehub.application.service.ReservationService;

import java.time.Instant;
import java.util.List;

public final class ReservationController {
    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    public long reserve(long customer, long station, Instant start, Instant end) throws Exception {
        return service.reserve(customer, station, start, end);
    }

    public List<String> available(Instant start, Instant end, String stationType) throws Exception {
        return service.available(start, end, stationType);
    }

    public List<String> available(Instant start, Instant end) throws Exception {
        return available(start, end, null);
    }

    public List<String> stationTypes() throws Exception {
        return service.stationTypes();
    }

    public List<String> mine(long customer) throws Exception {
        return service.mine(customer);
    }

    public List<String> forCustomer(long actor, long customer) throws Exception { return service.reservationsFor(actor, customer); }

    public void cancel(long customer, long reservation) throws Exception {
        service.cancel(customer, reservation);
    }
}

