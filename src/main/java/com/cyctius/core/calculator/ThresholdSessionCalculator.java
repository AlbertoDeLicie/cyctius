package com.cyctius.core.calculator;

import com.cyctius.core.parameters.ThresholdSessionParameters;
import com.cyctius.core.profile.ThresholdAthleteProfile;
import com.cyctius.core.session.ThresholdSession;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Математическая модель планирования Threshold и Sweet Spot сессий на основе кривой Power-Duration (PD).
 *
 * 1. Философия модели:
 *    В отличие от VO2max (где лимитирует анаэробная емкость W'), в Threshold сессиях лимитирует
 *    способность поддерживать высокую долю от максимальной мощности на заданном интервале времени.
 *
 * 2. Использование PD-кривой (Power-Duration):
 *    Кривая PD представляет собой "All-out" возможности атлета. Для любой длительности T
 *    мы можем найти максимальную мощность P_max, которую атлет способен выдать.
 *
 * 3. Расчет интенсивности (Интенсивность как производная от времени и Score):
 *    - База: Суммарное время работы (TiZ) определяется из общего времени сессии и коэффициента отдыха.
 *    - Лимит: Находим P_max для суммарного TiZ по PD-кривой профиля.
 *    - Запас (Intensity Factor): Score определяет, какой процент от P_max мы используем.
 *      P_target = P_max * (0.80 + (Score - 1) * (0.20 / 9)).
 *      Score 10.0 соответствует 100% от лимита (реальный All-out для этого времени).
 *      Score 1.0 соответствует 80% от лимита (очень легкая работа).
 *
 * 4. Баланс работы и отдыха (Rest-to-Work Ratio):
 *    - Время отдыха (t_rest) зависит от Score:
 *      На Score 10.0: t_rest = 0.25 * t_work (соотношение 1:4).
 *      На Score 1.0: t_rest = 1.0 * t_work (соотношение 1:1).
 *    - Это делает сессии с высоким Score более плотными и тяжелыми.
 *
 * 5. Структура сессии:
 *    - Количество повторений (N) всегда >= 2.
 *    - При высоком Score (>= 8.5) сессия делится на 2 больших блока для максимизации непрерывности.
 *    - При низком Score работа дробится на более мелкие интервалы для облегчения выполнения.
 *
 * 6. Зонирование:
 *    - Sweet Spot: Интенсивность ограничивается сверху ~95% FTP.
 *    - Threshold: Интенсивность удерживается в диапазоне 95% - 115% FTP (в зависимости от PD-кривой).
 */
@Slf4j
@Component
public class ThresholdSessionCalculator implements SessionCalculator<ThresholdAthleteProfile, ThresholdSessionParameters, ThresholdSession> {

    @Override
    public ThresholdSession calculate(ThresholdAthleteProfile athlete, ThresholdSessionParameters parameters) {
        if (parameters == null || !parameters.isValid()) {
            throw new IllegalArgumentException("Invalid session parameters");
        }

        double score = parameters.getScore();
        double totalSessionSec = parameters.getDurationMinutes() * 60.0;

        // 1. Коэффициент отдыха (Rest-to-Work Ratio)
        // Score 10.0 -> 0.25 (1:4), Score 1.0 -> 1.0 (1:1)
        double restRatio = 1.0 - (score - 1.0) * (0.75 / 9.0);

        // 2. Суммарное время работы (TiZ)
        // totalSessionSec = totalWorkSec + (totalWorkSec * restRatio)
        double totalWorkSec = totalSessionSec / (1.0 + restRatio);

        // 3. Теоретический предел мощности для этого времени по PD-кривой
        double pMaxPercent = athlete.getPowerAtDuration(totalWorkSec);

        // 4. Целевая интенсивность (% от лимита)
        // Score 10.0 -> 100% от pMax, Score 1.0 -> 80% от pMax
        double intensityFactor = 0.80 + (score - 1.0) * (0.20 / 9.0);
        double targetPowerPercent = pMaxPercent * intensityFactor;

        // 5. Корректировка по зонам
        if (parameters.getIntervalType() == ThresholdSessionParameters.ThresholdIntervalType.SWEET_SPOT) {
            targetPowerPercent = Math.max(85.0, Math.min(targetPowerPercent, 95.0));
        } else {
            // Для Threshold держимся в диапазоне 95-115%
            targetPowerPercent = Math.max(95.0, Math.min(targetPowerPercent, 115.0));
        }

        // 6. Количество повторений
        int repeats;
        if (score >= 8.5) {
            repeats = 2;
        } else if (score >= 6.0) {
            repeats = 3;
        } else if (score >= 4.0) {
            repeats = 4;
        } else {
            repeats = (int) Math.max(2, Math.round(10.0 - score));
        }

        double workDurationPerInterval = totalWorkSec / repeats;
        double restDurationPerInterval = workDurationPerInterval * restRatio;

        return ThresholdSession.builder()
                .workIntensity(targetPowerPercent / 100.0)
                .restIntensity(0.5)
                .workDuration((int) Math.round(workDurationPerInterval))
                .restDuration((int) Math.round(restDurationPerInterval))
                .totalDuration((int) Math.round(totalSessionSec))
                .repeats(repeats)
                .score(score)
                .totalTimeInZone(totalWorkSec)
                .build();
    }
}
