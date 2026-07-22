package kd.mgproj.domain.policy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import kd.mgproj.common.entity.BaseEntity;
import kd.mgproj.domain.policy.dto.YouthPolicyItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "policy_documents")
public class PolicyDocumentsEntity extends BaseEntity {

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "policy_id", nullable = false, unique = true)
	private PoliciesEntity policy;

	private String sourceType;

	@Column(columnDefinition = "TEXT")
	private String rawText;

	@Column(columnDefinition = "TEXT")
	private String summary;

	public static PolicyDocumentsEntity from(
		PoliciesEntity policy,
		YouthPolicyItem item
	) {
		PolicyDocumentsEntity document = new PolicyDocumentsEntity();

		document.policy = policy;
		document.sourceType = "YOUTH_CENTER";
		document.rawText = buildRawText(item);
		document.summary = item.plcyExplnCn();

		return document;
	}

	private static String buildRawText(YouthPolicyItem item) {
		return """
			[정책명]
			%s
			
			[키워드]
			%s
			
			[정책 설명]
			%s
			
			[정책 분야]
			%s > %s
			
			[지원 내용]
			%s
			
			[신청 기간]
			%s
			
			[사업 기간]
			%s ~ %s
			%s
			
			[신청 방법]
			%s
			
			[심사 방법]
			%s
			
			[제출 서류]
			%s
			
			[기타 사항]
			%s
			
			[신청 URL]
			%s
			
			[참고 URL]
			%s
			%s
			
			[지원 연령]
			%s세 ~ %s세
			
			[지역 코드]
			%s
			
			[소득 조건]
			%s
			
			[추가 신청 자격]
			%s
			
			[참여 제한 대상]
			%s
			
			[주관 기관]
			%s
			
			[운영 기관]
			%s
			""".formatted(
			nvl(item.plcyNm()),
			nvl(item.plcyKywdNm()),
			nvl(item.plcyExplnCn()),
			nvl(item.lclsfNm()),
			nvl(item.mclsfNm()),
			nvl(item.plcySprtCn()),
			nvl(item.aplyYmd()),
			nvl(item.bizPrdBgngYmd()),
			nvl(item.bizPrdEndYmd()),
			nvl(item.bizPrdEtcCn()),
			nvl(item.plcyAplyMthdCn()),
			nvl(item.srngMthdCn()),
			nvl(item.sbmsnDcmntCn()),
			nvl(item.etcMttrCn()),
			nvl(item.aplyUrlAddr()),
			nvl(item.refUrlAddr1()),
			nvl(item.refUrlAddr2()),
			nvl(item.sprtTrgtMinAge()),
			nvl(item.sprtTrgtMaxAge()),
			nvl(item.zipCd()),
			nvl(item.earnEtcCn()),
			nvl(item.addAplyQlfcCndCn()),
			nvl(item.ptcpPrpTrgtCn()),
			nvl(item.sprvsnInstCdNm()),
			nvl(item.operInstCdNm())
		);
	}

	private static String nvl(String value) {
		return value == null || value.isBlank() ? "-" : value.trim();
	}

	public void updateFrom(YouthPolicyItem item) {
		this.rawText = buildRawText(item);
		this.summary = item.plcyExplnCn();
	}
}