package com.gamehub.application;
import com.gamehub.infrastructure.Database;
import com.gamehub.application.service.*;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
/** Compatibility facade; production controllers directly use focused services. */
public final class OperationsService {
    private final StationService station;
    private final ReservationService reservation;
    private final SessionService session;
    private final PaymentService payment;
    private final MembershipService membership;
    private final TeamService team;
    private final TournamentService tournament;
    private final TournamentRegistrationService tournamentRegistration;
    private final MatchService match;
    private final MaintenanceService maintenance;
    private final ReportService report;
    private final CustomerReadService customerRead;
    public OperationsService(Database db) {
        station = new StationService(db);
        reservation = new ReservationService(db);
        session = new SessionService(db);
        payment = new PaymentService(db);
        membership = new MembershipService(db);
        team = new TeamService(db);
        tournament = new TournamentService(db);
        tournamentRegistration = new TournamentRegistrationService(db);
        match = new MatchService(db);
        maintenance = new MaintenanceService(db);
        report = new ReportService(db);
        customerRead = new CustomerReadService(db);
    }
    public long addStation(long actor,String code,String type,String specs,long hourlyPaisa) throws SQLException{ return station.addStation(actor, code, type, specs, hourlyPaisa); }
    public void updateStation(long actor,long id,String type,String specs,long rate) throws SQLException{ station.updateStation(actor, id, type, specs, rate); }
    public void deactivateStation(long actor,long id) throws SQLException{ station.deactivateStation(actor, id); }
    public List<String> stations() throws SQLException{ return station.stations(); }
    public List<String> stationRecords() throws SQLException{ return station.stationRecords(); }
    public List<String> stationRecords(Instant start,Instant end) throws SQLException{ return station.stationRecords(start, end); }
    public List<String> availableStations(Instant start,Instant end,String type)throws SQLException{ return station.availableStations(start, end, type); }
    public long reserve(long userId,long stationId,Instant start,Instant end)throws SQLException{ return reservation.reserve(userId, stationId, start, end); }
    public List<String> eligibleReservationRecords()throws SQLException{ return reservation.eligibleReservationRecords(); }
    public List<String> activeSessionRecords()throws SQLException{ return session.activeSessionRecords(); }
    public List<String> customerRecords()throws SQLException{ return customerRead.customerRecords(); }
    public List<String> walkInCustomerRecords()throws SQLException{ return session.walkInCustomerRecords(); }
    public List<String> availableStationRecords()throws SQLException{ return station.availableStationRecords(); }
    public List<String> availableStationRecords(Instant start,Instant end)throws SQLException{ return station.availableStationRecords(start, end); }
    public long startWalkInSession(long staffId,long customerId,long stationId)throws SQLException{ return session.startWalkInSession(staffId, customerId, stationId); }
    public long startWalkInSession(long staffId,long customerId,long stationId,int plannedMinutes)throws SQLException{ return session.startWalkInSession(staffId, customerId, stationId, plannedMinutes); }
    public String sessionBill(long sessionId)throws SQLException{ return session.sessionBill(sessionId); }
    public void extendSession(long staffId,long sessionId,int minutes)throws SQLException{ session.extendSession(staffId, sessionId, minutes); }
    public List<String> reservations(long userId)throws SQLException{ return reservation.reservations(userId); }
    public void cancelReservation(long userId,long id)throws SQLException{ reservation.cancelReservation(userId, id); }
    public long startSession(long staffId,long reservationId)throws SQLException{ return session.startSession(staffId, reservationId); }
    public long finishSessionCalculated(long actorId,long sessionId)throws SQLException{ return session.finishSessionCalculated(actorId, sessionId); }
    public long finishSession(long actorId,long sessionId,long charge)throws SQLException{ return session.finishSession(actorId, sessionId, charge); }
    public long payChargeAsStaff(long staffId,long chargeId,long amount,String method,String reference)throws SQLException{ return payment.payChargeAsStaff(staffId, chargeId, amount, method, reference); }
    public long payCharge(long userId,long chargeId,long amount,String method,String reference)throws SQLException{ return payment.payCharge(userId, chargeId, amount, method, reference); }
    public long settleSession(long userId,long sessionId,long amount,String method,String reference)throws SQLException{ return payment.settleSession(userId, sessionId, amount, method, reference); }
    public long createTeam(long captainId,String name)throws SQLException{ return team.createTeam(captainId, name); }
    public void requestJoin(long teamId,long userId)throws SQLException{ team.requestJoin(teamId, userId); }
    public void decideJoin(long captainId,long teamId,long userId,boolean approve)throws SQLException{ team.decideJoin(captainId, teamId, userId, approve); }
    public List<String> teamRequests(long captainId)throws SQLException{ return team.teamRequests(captainId); }
    public List<String> teams()throws SQLException{ return team.teams(); }
    public List<String> captainTeams(long captainId)throws SQLException{ return team.captainTeams(captainId); }
    public List<String> tournaments()throws SQLException{ return tournament.tournaments(); }
    public List<String> matchParticipants(long matchId)throws SQLException{ return match.matchParticipants(matchId); }
    public List<String> matches(long tournamentId)throws SQLException{ return match.matches(tournamentId); }
    public List<String> registrations(long tournamentId)throws SQLException{ return tournamentRegistration.registrations(tournamentId); }
    public List<String> outstandingCharges()throws SQLException{ return payment.outstandingCharges(); }
    public List<String> charges(long userId)throws SQLException{ return payment.charges(userId); }
    public List<String> membershipPlans()throws SQLException{ return membership.membershipPlans(); }
    public long subscribeOrRenew(long userId,String plan)throws SQLException{ return membership.subscribeOrRenew(userId, plan); }
    public long subscribeOrRenew(long userId,String plan,int days,long feePaisa)throws SQLException{ return membership.subscribeOrRenew(userId, plan, days, feePaisa); }
    public String membership(long userId)throws SQLException{ return membership.membership(userId); }
    public void configureTournament(long organizerId,long tournamentId,String game,Instant opens,Instant closes,Instant schedule,String eligibility)throws SQLException{ tournament.configureTournament(organizerId, tournamentId, game, opens, closes, schedule, eligibility); }
    public void scheduleMatch(long organizerId,long matchId,long stationId,Instant scheduledAt)throws SQLException{ match.scheduleMatch(organizerId, matchId, stationId, scheduledAt); }
    public void correctMatchResult(long organizerId,long matchId,int scoreA,int scoreB,long winner,String reason)throws SQLException{ match.correctMatchResult(organizerId, matchId, scoreA, scoreB, winner, reason); }
    public long createTournament(long organizerId,String name,String format,String entrantType,int capacity,long fee)throws SQLException{ return tournament.createTournament(organizerId, name, format, entrantType, capacity, fee); }
    public long createTournament(long organizerId,String name,String format,long fee)throws SQLException{ return tournament.createTournament(organizerId, name, format, fee); }
    public void changeTournamentStatus(long organizerId,long id,String next)throws SQLException{ tournament.changeTournamentStatus(organizerId, id, next); }
    public long registerTournament(long tournamentId,long userId)throws SQLException{ return tournamentRegistration.registerTournament(tournamentId, userId); }
    public long registerTournamentTeam(long tournamentId,long captainId,long teamId)throws SQLException{ return tournamentRegistration.registerTournamentTeam(tournamentId, captainId, teamId); }
    public long openMaintenance(long staffId,long stationId,String severity,String description)throws SQLException{ return maintenance.openMaintenance(staffId, stationId, severity, description); }
    public List<String> maintenanceTickets() throws SQLException{ return maintenance.maintenanceTickets(); }
    public void updateMaintenance(long staffId,long ticketId,String status,String notes,boolean readiness)throws SQLException{ maintenance.updateMaintenance(staffId, ticketId, status, notes, readiness); }
    public Map<String,Long> reportCounts()throws SQLException{ return report.reportCounts(); }
    public long reportCount(String table)throws SQLException{ return report.reportCount(table); }
    public List<Long> generateMatches(long organizerId,long tournamentId)throws SQLException{ return match.generateMatches(organizerId, tournamentId); }
    public void startMatch(long organizerId,long matchId)throws SQLException{ match.startMatch(organizerId, matchId); }
    public void recordMatchResult(long organizerId,long matchId,int scoreA,int scoreB,long winner)throws SQLException{ match.recordMatchResult(organizerId, matchId, scoreA, scoreB, winner); }
    public List<String> standings(long tournamentId)throws SQLException{ return match.standings(tournamentId); }
    public void exportReportCsv(long actorId,String category,Instant from,Instant to,java.nio.file.Path target)throws SQLException,java.io.IOException{ report.exportReportCsv(actorId, category, from, to, target); }
    public Map<String,Long> reportSummary(long actorId,Instant from,Instant to)throws SQLException{ return report.reportSummary(actorId, from, to); }
    public Map<String,Long> reportSummary(Instant from,Instant to)throws SQLException{ return report.reportSummary(from, to); }
}
