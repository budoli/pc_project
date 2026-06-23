package kd.mgproj.domain.policy.entity;

import jakarta.persistence.*;
import kd.mgproj.common.entity.BaseEntity;
import org.w3c.dom.Text;

@Entity
@Table(name = "policy_documents")
public class PolicyDocumentsEntity extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "policy_id", nullable = false)
  private PoliciesEntity policy;

  private String sourceType;

  @Column(columnDefinition = "TEXT")
  private String rawText;

  @Column(columnDefinition = "TEXT")
  private String summary;
}