package kd.mgproj.domain.policy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kd.mgproj.common.entity.BaseEntity;
import org.w3c.dom.Text;

@Entity
@Table(name = "policy_documents")
public class PolicyDocumentsEntity extends BaseEntity {
  private PoliciesEntity policy;

  private String sourceType;

  private Text rawText;

  private Text summary;
}