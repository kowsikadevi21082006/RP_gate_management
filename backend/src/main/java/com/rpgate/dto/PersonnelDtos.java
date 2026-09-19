package com.rpgate.dto;

import jakarta.validation.constraints.NotBlank;

public final class PersonnelDtos {
    private PersonnelDtos() { }
    public record Request(String armyNo, String cardNo, String rank, @NotBlank String name, String mobileNo, String unit) { }
    public record Response(long personnelId, String armyNo, String cardNo, String rank, String name, String mobileNo, String unit, String status, String createdAt, String updatedAt) { }
}
