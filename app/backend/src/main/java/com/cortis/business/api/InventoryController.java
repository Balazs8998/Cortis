package com.cortis.business.api;

import com.cortis.auth.security.CortisUserPrincipal;
import com.cortis.business.model.CatalogKind;
import com.cortis.business.model.BusinessDtos.*;
import com.cortis.business.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    private final InventoryService service;
    public InventoryController(InventoryService service) { this.service = service; }

    @GetMapping("/{kind}")
    @PreAuthorize("hasAuthority('CORTIS_INVENTORY_READ')")
    public List<InventoryView> list(@PathVariable String kind,
                                    @RequestParam(defaultValue = "30") int limit,
                                    @RequestParam(defaultValue = "0") int offset) {
        return service.list(CatalogKind.parse(kind), limit, offset);
    }
    @GetMapping("/{kind}/{id}")
    @PreAuthorize("hasAuthority('CORTIS_INVENTORY_READ')")
    public InventoryView get(@PathVariable String kind, @PathVariable UUID id) {
        return service.get(CatalogKind.parse(kind), id);
    }
    @PostMapping("/{kind}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CORTIS_INVENTORY_WRITE')")
    public InventoryView create(@PathVariable String kind, @RequestBody @Valid InventoryCreate body,
                                @AuthenticationPrincipal CortisUserPrincipal principal) {
        return service.create(CatalogKind.parse(kind), body, principal.getUserId());
    }
    @PostMapping("/{kind}/{id}/adjust")
    @PreAuthorize("hasAuthority('CORTIS_INVENTORY_WRITE')")
    public InventoryView adjust(@PathVariable String kind, @PathVariable UUID id,
                                @RequestBody @Valid StockAdjustment body,
                                @AuthenticationPrincipal CortisUserPrincipal principal) {
        return service.adjust(CatalogKind.parse(kind), id, body, principal.getUserId());
    }
    @GetMapping("/{kind}/{id}/movements")
    @PreAuthorize("hasAuthority('CORTIS_INVENTORY_READ')")
    public List<MovementView> movements(@PathVariable String kind, @PathVariable UUID id,
                                        @RequestParam(defaultValue = "30") int limit) {
        return service.history(CatalogKind.parse(kind), id, limit);
    }
}
