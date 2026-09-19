package com.rpgate.dto;

import java.util.List;

public final class BackupDtos {
    private BackupDtos() { }
    public record Backup(String version, String exportedAt, List<PersonnelDtos.Response> personnel, List<MovementDtos.Response> movements) { }
    public record RestoreResult(int personnelImported, int movementsImported) { }
}
