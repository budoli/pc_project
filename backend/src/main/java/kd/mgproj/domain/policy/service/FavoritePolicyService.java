package kd.mgproj.domain.policy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kd.mgproj.core.error.ErrorCode;
import kd.mgproj.core.error.exception.BusinessException;
import kd.mgproj.domain.policy.dto.response.FavoritePolicyResponse;
import kd.mgproj.domain.policy.entity.FavoritePolicyEntity;
import kd.mgproj.domain.policy.repository.FavoritePolicyRepository;
import kd.mgproj.domain.policy.repository.PolicyRepository;
import kd.mgproj.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoritePolicyService {
    private final FavoritePolicyRepository favoritePolicyRepository;
    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;

    @Transactional(readOnly = true)
    public List<FavoritePolicyResponse> getFavorites(Long userId) {
        return favoritePolicyRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream().map(FavoritePolicyResponse::from).toList();
    }

    @Transactional
    public void addFavorite(Long userId, Long policyId) {
        if (favoritePolicyRepository.existsByUserIdAndPolicyId(userId, policyId)) {
            return;
        }
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        var policy = policyRepository.findById(policyId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        favoritePolicyRepository.save(FavoritePolicyEntity.create(user, policy));
    }

    @Transactional
    public void removeFavorite(Long userId, Long policyId) {
        favoritePolicyRepository.deleteByUserIdAndPolicyId(userId, policyId);
    }
}
