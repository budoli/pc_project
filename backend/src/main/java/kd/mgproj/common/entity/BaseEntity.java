package kd.mgproj.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public abstract class BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(updatable = true)
  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
