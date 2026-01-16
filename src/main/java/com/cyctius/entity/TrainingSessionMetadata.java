package com.cyctius.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.EqualsAndHashCode;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Entity
@Table(name = "training_session_metadata", schema = "cyctius_db_schema")
public class TrainingSessionMetadata extends Auditable {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private String id;
    @Column(name = "training_session_id", nullable = false)
    private String trainingSessionId;
    @Column(name = "tss", nullable = false)
    private Integer tss;
    @Column(name = "intensity_factor", nullable = false)
    private Double intensityFactor;
    @Column(name = "normalized_power", nullable = true)
    private Integer normalizedPower;
    @Column(name = "average_power", nullable = true)
    private Integer averagePower;
    @Column(name = "average_heart_rate", nullable = true)
    private Integer averageHeartRate;
    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds;
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;
    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;
}
