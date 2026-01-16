package com.cyctius.entity;

import org.hibernate.annotations.UuidGenerator;

import com.cyctius.core.enums.WorkoutType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "workout_metadata", schema = "cyctius_db_schema")
public class WorkoutMetadata extends Auditable {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private String id;
    @Column(name = "workout_id", nullable = false)
    private String workoutId;
    @Column(name = "estimated_tss", nullable = false)
    private Integer estimatedTss;
    @Column(name = "training_type", nullable = false)
    private WorkoutType trainingType;
    @Column(name = "difficulty", nullable = false)
    private Double difficulty; // 1.0 - 10.0 scale
    @Column(name = "intensity_factor", nullable = false)
    private Double intensityFactor;
    @Column(name = "average_intensity", nullable = true)
    private Integer averageIntensity;
    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds;
}
