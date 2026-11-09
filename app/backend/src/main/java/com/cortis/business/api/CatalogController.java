package com.cortis.business.api;

import com.cortis.auth.security.CortisUserPrincipal;
import com.cortis.business.model.CatalogKind;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.cortis.business.model.BusinessDtos.*;
import com.cortis.business.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/catalog")
public class CatalogController {
    private final CatalogService service;
    public CatalogController(CatalogService service) { this.service = service; }

    @GetMapping("/{kind}")
    @PreAuthorize("hasAuthority('CORTIS_CATALOG_READ')")
    public List<MasterView> list(@PathVariable String kind,
                                 @RequestParam(defaultValue = "") String q,
                                 @RequestParam(defaultValue = "30") int limit,
                                 @RequestParam(defaultValue = "0") int offset) {
        return service.list(CatalogKind.parse(kind), q, limit, offset);
    }
    @GetMapping("/{kind}/types")
    @PreAuthorize("hasAuthority('CORTIS_CATALOG_READ')")
    public List<TypeView> types(@PathVariable String kind) { return service.types(CatalogKind.parse(kind)); }

    @GetMapping("/{kind}/{id}")
    @PreAuthorize("hasAuthority('CORTIS_CATALOG_READ')")
    public MasterView get(@PathVariable String kind, @PathVariable UUID id) {
        return service.get(CatalogKind.parse(kind), id);
    }
    @PostMapping("/{kind}/types")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CORTIS_CATALOG_WRITE')")
    public TypeView createType(@PathVariable String kind, @RequestBody @Valid TypeCreate request,
                                   @AuthenticationPrincipal CortisUserPrincipal principal) {
        return service.createType(CatalogKind.parse(kind), request, principal.getUsername());
    }
    @PostMapping("/{kind}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CORTIS_CATALOG_WRITE')")
    public MasterView create(@PathVariable String kind, @RequestBody @Valid MasterCreate request,
                             @AuthenticationPrincipal CortisUserPrincipal principal) {
        return service.create(CatalogKind.parse(kind), request, principal.getUsername());
    }
}
