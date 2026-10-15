package kd.mgproj.domain.policy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import kd.mgproj.common.entity.BaseEntity;
import kd.mgproj.domain.user.entity.UsersEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "favorite_policies", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "policy_id"}))
public class FavoritePolicyEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UsersEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private PoliciesEntity policy;

    public static FavoritePolicyEntity create(UsersEntity user, PoliciesEntity policy) {
        FavoritePolicyEntity favorite = new FavoritePolicyEntity();
        favorite.user = user;
        favorite.policy = policy;
        return favorite;
    }
}
