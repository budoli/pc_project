package kd.mgproj.domain.policy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kd.mgproj.common.enums.PolicyCategory;
import kd.mgproj.domain.policy.entity.PoliciesEntity;

public interface PolicyRepository extends JpaRepository<PoliciesEntity, Long> {
	Optional<PoliciesEntity> findByExternalId(String s);

	@Query("""
		    select distinct p
		    from PoliciesEntity p
		    left join PolicyConditionsEntity c on c.policy = p
		    where (:keyword is null or :keyword = ''
		        or lower(p.title) like lower(concat('%', :keyword, '%'))
		        or lower(p.description) like lower(concat('%', :keyword, '%'))
		        or lower(p.organization) like lower(concat('%', :keyword, '%'))
		    )
		    and (:region is null or :region = ''
		        or p.region = :region
		        or c.region = :region
		    )
		    and (:category is null or p.category = :category)
		    and (:age is null or (
		        (c.minAge is null or c.minAge <= :age)
		        and (c.maxAge is null or c.maxAge >= :age)
		    ))
		    and (:jobCondition is null or c.jobCondition = :jobCondition)
		    order by p.createdAt desc
		""")
	List<PoliciesEntity> searchPolicies(
		@Param("keyword") String keyword,
		@Param("region") String region,
		@Param("category") PolicyCategory category,
		@Param("age") Integer age,
		@Param("jobCondition") String jobCondition
	);
}
