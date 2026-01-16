package com.cyctius.core.service.impl;

import com.cyctius.core.enums.WorkoutType;
import com.cyctius.core.model.WorkoutModel;
import com.cyctius.core.model.intervals.SingleInterval;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class WorkoutTypeClassifierServiceImplTest {

    private WorkoutTypeClassifierServiceImpl classifier;

    private WorkoutPowerUtilsImpl workoutPowerUtils;

    @BeforeEach
    void setUp() {
        workoutPowerUtils = new WorkoutPowerUtilsImpl();
        classifier = new WorkoutTypeClassifierServiceImpl(workoutPowerUtils);
    }

    @Test
    void classifyWorkout_WhenWorkoutIsNull_ShouldReturnRecovery() {
        assertEquals(WorkoutType.RECOVERY, classifier.classifyWorkout(null));
    }

    @Test
    void classifyWorkout_EnduranceRideWithShortSprints_ShouldBeEndurance() {
        // 2 часа (7200с) в Z2, 30с в Z7. Всего 7230с.
        // Z7: 30/7230 = 0.4% (Порог Z7 = 5%).
        // Z2: 7200/7230 = 99.5% (Порог Z2 = 60%).
        SingleInterval endurance = new SingleInterval(65, 90, true, 7200); // Z2
        SingleInterval sprint = new SingleInterval(160, 110, true, 30);    // Z7
        WorkoutModel workout = WorkoutModel.builder().intervals(List.of(endurance, sprint)).build();

        assertEquals(WorkoutType.ENDURANCE, classifier.classifyWorkout(workout));
    }

    @Test
    void classifyWorkout_EnduranceRideWithManySprints_ShouldBeNeuromuscular() {
        // 1 час (3600с). 4 минуты (240с) в Z7.
        // Z7: 240 / 3600 = 6.6% (Порог Z7 = 5%).
        SingleInterval endurance = new SingleInterval(65, 90, true, 3360);
        SingleInterval sprints = new SingleInterval(160, 110, true, 240);
        WorkoutModel workout = WorkoutModel.builder().intervals(List.of(endurance, sprints)).build();

        assertEquals(WorkoutType.NEUROMUSCULAR, classifier.classifyWorkout(workout));
    }

    @Test
    void classifyWorkout_IntervalSession_ShouldBeThreshold() {
        // 1 час: 20м в Z4 (1200с), 40м в Z1 (2400с). Всего 3600с.
        // Z4: 1200 / 3600 = 33.3% (Порог Z4 = 20%).
        SingleInterval work = new SingleInterval(100, 90, true, 1200);
        SingleInterval recovery = new SingleInterval(50, 90, true, 2400);
        WorkoutModel workout = WorkoutModel.builder().intervals(List.of(work, recovery)).build();

        assertEquals(WorkoutType.THRESHOLD, classifier.classifyWorkout(workout));
    }

    @Test
    void classifyWorkout_ShortIntervalSession_BelowThreshold_ShouldBeRecovery() {
        // 1 час: 5м в Z4 (300с), 55м в Z1 (3300с).
        // Z4: 300 / 3600 = 8.3% (Порог Z4 = 20%).
        SingleInterval work = new SingleInterval(100, 90, true, 300);
        SingleInterval recovery = new SingleInterval(50, 90, true, 3300);
        WorkoutModel workout = WorkoutModel.builder().intervals(List.of(work, recovery)).build();

        assertEquals(WorkoutType.RECOVERY, workoutPowerUtils.powerZoneByIntensity(100).name().contains("THRESHOLD") 
            ? classifier.classifyWorkout(workout) : WorkoutType.RECOVERY);
        // Доп. проверка: 8.3% недостаточно для Z4, Z3, Z2. Остается Recovery.
        assertEquals(WorkoutType.RECOVERY, classifier.classifyWorkout(workout));
    }

    @Test
    void classifyWorkout_MixedHighIntensity_ShouldBeVO2Max() {
        // 1000с в Z5 (120%), 1000с в Z1 (50%). Всего 2000с.
        // Z5: 1000 / 2000 = 50% (Порог Z5 = 10%).
        SingleInterval interval = new SingleInterval(120, 90, true, 1000);
        SingleInterval interval2 = new SingleInterval(50, 90, true, 1000);
        WorkoutModel workout = WorkoutModel.builder().intervals(List.of(interval, interval2)).build();
                
        assertEquals(WorkoutType.VO2MAX, classifier.classifyWorkout(workout));
    }

    @Test
    void classifyWorkout_MostlyZ1SomeZ2_ShouldBeRecovery() {
        // Z2: 0%, Z1: 100%. Порог Z2 = 60%.
        SingleInterval interval = new SingleInterval(50, 90, true, 1000);
        SingleInterval interval2 = new SingleInterval(45, 90, true, 1000);
        SingleInterval interval3 = new SingleInterval(100, 90, true, 10);
        WorkoutModel workout = WorkoutModel.builder().intervals(List.of(interval, interval2, interval3)).build();
                
        assertEquals(WorkoutType.RECOVERY, classifier.classifyWorkout(workout));
    }

    @Test
    void classifyWorkout_MostlyZ1SomeZ2SomeZ4_ShouldBeThreshold() {
        // 1000с Z1, 1000с Z2, 1000с Z4. Всего 3000с.
        // Z4: 1000 / 3000 = 33.3% (Порог Z4 = 20%).
        SingleInterval z1 = new SingleInterval(50, 90, true, 1000);
        SingleInterval z2 = new SingleInterval(75, 90, true, 1000);
        SingleInterval z4 = new SingleInterval(105, 90, true, 1000);
        WorkoutModel workout = WorkoutModel.builder().intervals(List.of(z1, z2, z4)).build();
        
        assertEquals(WorkoutType.THRESHOLD, classifier.classifyWorkout(workout));
    }
}
