package kd.mgproj.domain.policy.entity;

import jakarta.persistence.*;
import kd.mgproj.common.entity.BaseEntity;
import kd.mgproj.common.enums.PolicyCategory;
import kd.mgproj.common.enums.PolicyStatus;
import kd.mgproj.domain.policy.dto.YouthPolicyItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "policies")
public class PoliciesEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String externalId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String organization;

    private LocalDate applyStartDate;

    private LocalDate applyEndDate;
    @Column(columnDefinition = "TEXT")
    private String region;

    @Enumerated(EnumType.STRING)
    private PolicyCategory category;

    @Column(columnDefinition = "TEXT")
    private String sourceUrl;

    @Enumerated(EnumType.STRING)
    private PolicyStatus status;

    @Column(columnDefinition = "TEXT")
    private String rawData;

    public static PoliciesEntity fromYouthPolicy(
            YouthPolicyItem item,
            String rawData
    ) {
        PoliciesEntity policy = new PoliciesEntity();

        policy.externalId = item.plcyNo();
        policy.title = item.plcyNm();
        policy.description = item.plcyExplnCn();
        policy.organization = item.sprvsnInstCdNm();
        policy.applyStartDate = parseDate(item.bizPrdBgngYmd());
        policy.applyEndDate = parseDate(item.bizPrdEndYmd());
        policy.region = item.zipCd();
        policy.category = PolicyCategory.from(item.lclsfNm());
        policy.sourceUrl = item.aplyUrlAddr();
        policy.status = PolicyStatus.OPEN;
        policy.rawData = rawData;

        return policy;
    }

    public PoliciesEntity updateFromYouthPolicy(
            YouthPolicyItem item,
            String rawData
    ) {
        this.title = item.plcyNm();
        this.description = item.plcyExplnCn();
        this.organization = item.sprvsnInstCdNm();
        this.applyStartDate = parseDate(item.bizPrdBgngYmd());
        this.applyEndDate = parseDate(item.bizPrdEndYmd());
        this.region = item.zipCd();
        this.category = PolicyCategory.from(item.lclsfNm());
        this.sourceUrl = item.aplyUrlAddr();
        this.rawData = rawData;

        return this;
    }


    private static LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String trimmed = value.trim();

        if (trimmed.length() != 8) {
            return null;
        }

        return LocalDate.parse(trimmed, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}