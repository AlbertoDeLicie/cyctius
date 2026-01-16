package com.cyctius.entity;

import com.cyctius.core.model.intervals.Interval;
import com.cyctius.enums.WorkoutVisibility;
import com.cyctius.util.IntervalListConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

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
    @Column(name = "visibility", nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkoutVisibility visibility;
    @Convert(converter = IntervalListConverter.class)
    @Column(name = "intervals_json", nullable = false, columnDefinition = "TEXT")
    private List<Interval> intervals;
    @Column(name = "is_soft_deleted", nullable = false)
    private Boolean isSoftDeleted;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "metadata_id", referencedColumnName = "workout_id")
    private WorkoutMetadata metadata;
}
