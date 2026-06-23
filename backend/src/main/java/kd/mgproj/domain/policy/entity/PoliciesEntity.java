package kd.mgproj.domain.policy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kd.mgproj.common.entity.BaseEntity;
import kd.mgproj.common.enums.PolicyCategory;
import kd.mgproj.common.enums.PolicyStatus;
import org.w3c.dom.Text;

import java.util.Date;

@Entity
@Table(name = "policies")
public class PoliciesEntity extends BaseEntity {
  private String title;

  private Text description;

  private String organization;

  private Date applyStartDate;

  private Date applyEndDate;

  private String region;

  private PolicyCategory category;

  private String sourceUrl;

  private PolicyStatus status;
}