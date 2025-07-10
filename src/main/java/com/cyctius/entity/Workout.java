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

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "workouts", schema = "cyctius_db_schema")
public class Workout extends Auditable {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private String id;
    @Column(name = "author_id", updatable = false, nullable = false)
    private String authorId;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "intervals_json", nullable = false, columnDefinition = "TEXT")
    private String intervalsJson;
    @Column(name = "is_soft_deleted", nullable = false)
    private Boolean isSoftDeleted;
}
