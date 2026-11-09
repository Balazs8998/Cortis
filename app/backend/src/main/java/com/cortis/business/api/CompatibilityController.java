package com.cortis.business.api;

import com.cortis.auth.security.CortisUserPrincipal;
import com.cortis.business.model.BusinessDtos.*;
import com.cortis.business.service.CompatibilityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/compatibility/tool-holder")
public class CompatibilityController {
    private final CompatibilityService service;
    public CompatibilityController(CompatibilityService service) { this.service = service; }

    @GetMapping
    @PreAuthorize("hasAuthority('CORTIS_COMPATIBILITY_READ')")
    public CompatibilityView check(@RequestParam UUID toolId, @RequestParam UUID holderId) {
        return service.check(toolId, holderId);
    }
    @PostMapping("/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CORTIS_COMPATIBILITY_REVIEW')")
    public CompatibilityView review(@RequestBody @Valid CompatibilityReview body,
                                    @AuthenticationPrincipal CortisUserPrincipal principal) {
        return service.review(body, principal.getUserId());
    }
}
