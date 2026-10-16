package kd.mgproj.domain.policy.dto.response;

import kd.mgproj.domain.policy.entity.FavoritePolicyEntity;

public record FavoritePolicyResponse(Long id, String title) {
    public static FavoritePolicyResponse from(FavoritePolicyEntity favorite) {
        return new FavoritePolicyResponse(favorite.getPolicy().getId(), favorite.getPolicy().getTitle());
    }
}
