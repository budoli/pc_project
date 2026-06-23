package kd.mgproj.domain.policy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kd.mgproj.common.entity.BaseEntity;
import org.w3c.dom.Text;

@Entity
@Table(name = "policy_embeddings")
public class PolicyEmbeddingsEntity extends BaseEntity {
  private PolicyDocumentsEntity policyDocuments;

  private String vectorId;

  private int chunkIndex;

  private Text chunkText;

  private String embeddingModel;
}


