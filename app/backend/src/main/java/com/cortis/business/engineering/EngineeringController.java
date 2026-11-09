package com.cortis.business.engineering;

import com.cortis.auth.security.CortisUserPrincipal;
import com.cortis.business.engineering.EngineeringDtos.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/engineering")
public class EngineeringController {
    private final EngineeringService service;
    public EngineeringController(EngineeringService service) { this.service = service; }

    @GetMapping("/assets/{kind}/{code}")
    @PreAuthorize("hasAuthority('CORTIS_ENGINEERING_READ') and hasAuthority('CORTIS_INVENTORY_READ')")
    public Asset asset(@PathVariable String kind, @PathVariable String code) {
        return service.asset(kind, code);
    }

    @GetMapping("/features/{kind}/{code}")
    @PreAuthorize("hasAuthority('CORTIS_ENGINEERING_READ') and hasAuthority('CORTIS_INVENTORY_READ')")
    public List<Feature> features(@PathVariable String kind, @PathVariable String code) {
        return service.features(kind, code);
    }

    @PutMapping("/features/{kind}/{code}")
    @PreAuthorize("hasAuthority('CORTIS_ENGINEERING_WRITE')")
    public List<Feature> saveFeature(@PathVariable String kind, @PathVariable String code,
                                     @RequestBody @Valid FeatureUpdate body,
                                     @AuthenticationPrincipal CortisUserPrincipal principal) {
        return service.saveFeature(kind, code, body, principal.getUsername());
    }

    @GetMapping("/machines/{machineCode}/stations")
    @PreAuthorize("hasAuthority('CORTIS_ENGINEERING_READ') and hasAuthority('CORTIS_INVENTORY_READ')")
    public List<Station> stations(@PathVariable String machineCode) {
        return service.stations(machineCode);
    }

    @PostMapping("/machines/{machineCode}/stations")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CORTIS_ENGINEERING_WRITE')")
    public Station createStation(@PathVariable String machineCode, @RequestBody @Valid StationCreate body,
                                 @AuthenticationPrincipal CortisUserPrincipal principal) {
        return service.createStation(machineCode, body, principal.getUserId());
    }

    @GetMapping("/stations/{stationId}/mounts")
    @PreAuthorize("hasAuthority('CORTIS_ENGINEERING_READ') and hasAuthority('CORTIS_INVENTORY_READ')")
    public List<Mount> mounts(@PathVariable UUID stationId) { return service.mounts(stationId); }

    @PostMapping("/stations/{stationId}/mounts")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CORTIS_ENGINEERING_REVIEW')")
    public Mount approveMount(@PathVariable UUID stationId, @RequestBody @Valid MountCreate body,
                              @AuthenticationPrincipal CortisUserPrincipal principal) {
        return service.approveMount(stationId, body, principal.getUserId());
    }

    @GetMapping("/plan")
    @PreAuthorize("hasAuthority('CORTIS_ENGINEERING_READ') and hasAuthority('CORTIS_INVENTORY_READ')")
    public Plan plan(@RequestParam String toolCode, @RequestParam String machineCode) {
        return service.plan(toolCode, machineCode);
    }
}
