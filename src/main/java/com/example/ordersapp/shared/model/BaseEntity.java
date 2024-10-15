package com.example.ordersapp.shared.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    protected Long id;

    @CreationTimestamp(source = SourceType.DB)
    @Column(name = "created_at", updatable = false, nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    protected Instant createdAt;

    @UpdateTimestamp
    @Column(name = "modified_at")
    protected Instant modifiedAt;

    @Version
    @ColumnDefault("0")
    protected Integer version;

}
