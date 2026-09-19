package com.rpgate.dto;

import java.util.List;
import java.util.Map;
import com.rpgate.dto.MovementDtos.Response;

public final class ReportDtos {
    private ReportDtos() { }
    public record Daily(String date, long total, List<Response> records) { }
    public record CategorySummary(Map<String, Long> counts, long total) { }
    public record DashboardSummary(long total, long today, long in, long out, long personnel) { }
    public record PersonnelHistory(PersonnelDtos.Response personnel, List<Response> records) { }
}
