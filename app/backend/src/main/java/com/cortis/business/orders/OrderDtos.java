package com.cortis.business.orders;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class OrderDtos {
    private OrderDtos() {}

    public enum Status { NEW, IN_PROGRESS, READY, SHIPPED, CANCELLED }
    public enum Scope { all, thisMonth, overdue }

    public record LineCreate(
            @Size(max = 100) String itemCode,
            @NotBlank @Size(max = 500) String description,
            @NotNull @DecimalMin("0.001") @Digits(integer = 9, fraction = 3) BigDecimal quantity,
            @NotBlank @Pattern(regexp = "[A-Za-z]{1,8}") String unit) {}

    public record Create(
            @NotBlank @Size(max = 200) String customer,
            @NotNull LocalDate deliveryDate,
            @Size(max = 2000) String note,
            @NotEmpty @Size(max = 100) List<@NotNull @Valid LineCreate> lines) {}

    public record Summary(
            UUID id, String orderCode, String customer, LocalDate orderDate,
            LocalDate deliveryDate, Status status, int itemCount, String note) {}

    public record Line(UUID id, int position, String itemCode, String description,
                       BigDecimal quantity, String unit) {}

    public record Detail(Summary order, List<Line> lines) {}

    public record Overview(long thisMonth, long overdue, long inProgress) {}

    public record StatusChange(@NotNull Status status) {}
}
