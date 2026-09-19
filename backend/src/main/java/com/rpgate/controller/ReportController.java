package com.rpgate.controller;

import com.rpgate.dto.MovementDtos;
import com.rpgate.dto.ReportDtos;
import com.rpgate.model.Category;
import com.rpgate.service.MovementService;
import com.rpgate.service.PersonnelService;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final MovementService movements; private final PersonnelService personnel;
    public ReportController(MovementService movements, PersonnelService personnel) { this.movements=movements; this.personnel=personnel; }
    @GetMapping("/daily") public ReportDtos.Daily daily(@RequestParam String date) { var rows=movements.list(new MovementDtos.Filter(null,null,null,date,date+"T23:59:59.999Z",null,null)); return new ReportDtos.Daily(date,rows.size(),rows); }
    @GetMapping("/category-summary") public ReportDtos.CategorySummary category(@RequestParam(required=false) String from,@RequestParam(required=false) String to) { var rows=movements.list(new MovementDtos.Filter(null,null,null,from,to,null,null)); Map<String,Long> counts=new LinkedHashMap<>(); rows.forEach(r->counts.merge(r.category().value(),1L,Long::sum)); return new ReportDtos.CategorySummary(counts,rows.size()); }
    @GetMapping("/dashboard") public ReportDtos.DashboardSummary dashboard() { var rows=movements.list(new MovementDtos.Filter(null,null,null,null,null,null,null)); String today=java.time.LocalDate.now().toString(); long in=rows.stream().filter(r->r.movementType()!=null && r.movementType().toUpperCase().endsWith("IN")).count(); long out=rows.stream().filter(r->r.movementType()!=null && r.movementType().toUpperCase().endsWith("OUT")).count(); long todayCount=rows.stream().filter(r->r.createdAt()!=null && r.createdAt().startsWith(today)).count(); return new ReportDtos.DashboardSummary(rows.size(),todayCount,in,out,personnel.list(null).size()); }
    @GetMapping("/personnel/{id}") public ReportDtos.PersonnelHistory history(@PathVariable long id) { var person=personnel.getOrThrow(id); var rows=movements.list(new MovementDtos.Filter(null,null,null,null,null,null,id)); return new ReportDtos.PersonnelHistory(person,rows); }
}
