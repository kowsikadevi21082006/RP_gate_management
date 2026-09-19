package com.rpgate.dto;

import com.rpgate.model.Category;
import com.rpgate.model.MovementStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public final class MovementDtos {
    private MovementDtos() { }
    public record PersonRef(Long personnelId, String armyNo, String cardNo, String rank, @NotBlank String name, String mobileNo, String unit) { }
    public record Request(@NotNull Category category, @NotNull @NotBlank String movementAt, PersonRef person, String movementType, String remarks, Map<String, String> details) { }
    public record Response(long movementId, Category category, String movementType, String movementAt, String createdAt, String updatedAt, MovementStatus status, PersonRef person, Map<String, String> details, String remarks) { }
    public record Filter(String search, Category category, String movementType, String from, String to, MovementStatus status, Long personnelId) { }
}
