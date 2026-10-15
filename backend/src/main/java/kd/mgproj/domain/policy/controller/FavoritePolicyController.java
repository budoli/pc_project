package kd.mgproj.domain.policy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kd.mgproj.core.security.UserDetailsImpl;
import kd.mgproj.domain.policy.dto.response.FavoritePolicyResponse;
import kd.mgproj.domain.policy.service.FavoritePolicyService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/policies/favorites")
public class FavoritePolicyController {
    private final FavoritePolicyService favoritePolicyService;

    @GetMapping
    public ResponseEntity<List<FavoritePolicyResponse>> getFavorites(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(favoritePolicyService.getFavorites(userDetails.getUserId()));
    }

    @PostMapping("/{policyId}")
    public ResponseEntity<Void> addFavorite(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long policyId
    ) {
        favoritePolicyService.addFavorite(userDetails.getUserId(), policyId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{policyId}")
    public ResponseEntity<Void> removeFavorite(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long policyId
    ) {
        favoritePolicyService.removeFavorite(userDetails.getUserId(), policyId);
        return ResponseEntity.noContent().build();
    }
}
