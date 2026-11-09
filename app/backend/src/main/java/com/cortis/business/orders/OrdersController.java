package com.cortis.business.orders;

import com.cortis.auth.security.CortisUserPrincipal;
import com.cortis.business.orders.OrderDtos.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrdersController {
    private final OrdersService orders;

    public OrdersController(OrdersService orders) { this.orders = orders; }

    @GetMapping
    @PreAuthorize("hasAuthority('CORTIS_ORDERS_READ')")
    public List<Summary> list(@RequestParam(defaultValue = "") String q,
                              @RequestParam(defaultValue = "thisMonth") Scope scope,
                              @RequestParam(defaultValue = "50") int limit,
                              @RequestParam(defaultValue = "0") int offset) {
        return orders.list(q, scope, limit, offset);
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('CORTIS_ORDERS_READ')")
    public Overview summary() { return orders.overview(); }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CORTIS_ORDERS_READ')")
    public Detail detail(@PathVariable UUID id) { return orders.detail(id); }

    @PostMapping
    @PreAuthorize("hasAuthority('CORTIS_ORDERS_WRITE')")
    public ResponseEntity<Summary> create(@Valid @RequestBody Create request,
                                           @AuthenticationPrincipal CortisUserPrincipal principal) {
        Summary created = orders.create(request, principal.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/api/v1/orders/" + created.id()))
                .body(created);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('CORTIS_ORDERS_WRITE')")
    public Summary changeStatus(@PathVariable UUID id,
                                @Valid @RequestBody StatusChange request,
                                @AuthenticationPrincipal CortisUserPrincipal principal) {
        return orders.changeStatus(id, request.status(), principal.getUserId());
    }
}
