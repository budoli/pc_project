package kd.mgproj.domain.policy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kd.mgproj.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
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


