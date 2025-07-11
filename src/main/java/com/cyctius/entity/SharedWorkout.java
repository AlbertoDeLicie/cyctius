package com.cyctius.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "shared_workouts", schema = "cyctius_db_schema")
public class SharedWorkout extends Auditable {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private String id;
    private String workoutId;
    private Integer lifeTimeS;

    public boolean isExpired() {
        if (lifeTimeS == null || lifeTimeS <= 0) {
            return true; // No expiration if lifetime is not set or zero
        }

        long currentTime = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        long creationTime = getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        return (currentTime - creationTime) > (lifeTimeS * 1000L);
    }
}
