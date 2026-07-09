package kd.mgproj.domain.policy.entity;

import jakarta.persistence.*;
import kd.mgproj.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;

@Entity
@Getter @NoArgsConstructor @AllArgsConstructor
@Table(name = "policy_embeddings")
public class PolicyEmbeddingsEntity extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "policy_document_id", nullable = false)
  private PolicyDocumentsEntity policyDocuments;

  private String vectorId;

  private int chunkIndex;

  @Column(columnDefinition = "TEXT")
  private String chunkText;

  private String embeddingModel;
}


