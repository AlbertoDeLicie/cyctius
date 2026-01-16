package com.cyctius.core.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.apache.commons.math3.analysis.interpolation.AkimaSplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Профиль атлета для Threshold калькулятора.
 * Хранит FTP, TTE и точки кривой Power-Duration (PD).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ThresholdAthleteProfile {
    private Integer ftp;
    private Double tteAtFtp; // TTE на 100% FTP в секундах
    private List<PdPoint> pdPoints; // Точки кривой [Power %, Time (s)]

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PdPoint {
        private Double powerPercent;
        private Double timeSeconds;
    }

    /**
     * Находит максимальную мощность (в % от FTP) для заданной длительности (в секундах).
     * Использует Akima Spline интерполяцию в логарифмическом пространстве времени: P = f(log(T)).
     * Сплайны обеспечивают более плавную кривую по сравнению с линейной интерполяцией.
     */
    public Double getPowerAtDuration(double durationSeconds) {
        if (durationSeconds <= 0) return 200.0;

        List<PdPoint> points = getValidSortedPoints();
        
        if (points.size() < 2) {
            double refTte = (tteAtFtp != null && tteAtFtp > 0) ? tteAtFtp : 2400.0;
            return 100.0 * Math.pow(durationSeconds / refTte, -0.07);
        }

        double logTarget = Math.log(durationSeconds);
        
        double[] x = new double[points.size()];
        double[] y = new double[points.size()];
        
        for (int i = 0; i < points.size(); i++) {
            x[i] = Math.log(points.get(i).getTimeSeconds());
            y[i] = points.get(i).getPowerPercent();
        }

        try {
            if (points.size() >= 5) {
                // Akima spline - стандарт для гладких PD кривых в спорте
                AkimaSplineInterpolator interpolator = new AkimaSplineInterpolator();
                PolynomialSplineFunction function = interpolator.interpolate(x, y);
                
                // Проверка на границы (экстраполяция)
                if (logTarget < x[0]) return extrapolateLinear(logTarget, x[0], y[0], x[1], y[1]);
                if (logTarget > x[x.length - 1]) return extrapolateLinear(logTarget, x[x.length - 2], y[y.length - 2], x[x.length - 1], y[y.length - 1]);
                
                return function.value(logTarget);
            } else {
                // Если точек мало (2-4), используем линейную интерполяцию с ручной экстраполяцией
                if (logTarget < x[0]) return extrapolateLinear(logTarget, x[0], y[0], x[1], y[1]);
                if (logTarget > x[x.length - 1]) return extrapolateLinear(logTarget, x[x.length - 2], y[y.length - 2], x[x.length - 1], y[y.length - 1]);
                
                LinearInterpolator interpolator = new LinearInterpolator();
                return interpolator.interpolate(x, y).value(logTarget);
            }
        } catch (Exception e) {
            // Fallback на ручную линейную логику
            return fallbackLinear(logTarget, points);
        }
    }

    private double extrapolateLinear(double x, double x1, double y1, double x2, double y2) {
        if (Math.abs(x2 - x1) < 1e-9) return y1;
        return y1 + (x - x1) * (y2 - y1) / (x2 - x1);
    }

    private double fallbackLinear(double logTarget, List<PdPoint> points) {
        double[] x = new double[points.size()];
        double[] y = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            x[i] = Math.log(points.get(i).getTimeSeconds());
            y[i] = points.get(i).getPowerPercent();
        }
        
        if (logTarget < x[0]) return extrapolateLinear(logTarget, x[0], y[0], x[1], y[1]);
        if (logTarget > x[x.length - 1]) return extrapolateLinear(logTarget, x[x.length - 2], y[y.length - 2], x[x.length - 1], y[y.length - 1]);

        for (int i = 1; i < x.length; i++) {
            if (x[i] >= logTarget) {
                return extrapolateLinear(logTarget, x[i-1], y[i-1], x[i], y[i]);
            }
        }
        return y[y.length - 1];
    }

    private List<PdPoint> getValidSortedPoints() {
        return pdPoints.stream()
                .filter(p -> p != null && p.getPowerPercent() != null && p.getTimeSeconds() != null && p.getTimeSeconds() > 0)
                .sorted(Comparator.comparingDouble(PdPoint::getTimeSeconds))
                .collect(Collectors.toList());
    }
}
