package com.gamehub.application.controller; import com.gamehub.application.service.ReportService; import java.time.Instant; import java.nio.file.Path; import java.util.Map; public final class ReportController {private final ReportService service;public ReportController(ReportService s){service=s;}public Map<String,Long> counts(long actor)throws Exception{return service.counts(actor);}public Map<String,Long> summary(long a,Instant f,Instant t)throws Exception{return service.summary(a,f,t);}public Map<String,Long> summary(long actor, String category, Instant from, Instant to) throws Exception {
    return service.summary(actor, category, from, to);
}
public void export(long a,String c,Instant f,Instant t,Path p)throws Exception{service.export(a,c,f,t,p);}}
