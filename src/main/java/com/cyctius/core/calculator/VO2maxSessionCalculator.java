package com.cyctius.core.calculator;

import com.cyctius.core.parameters.VO2maxSessionParameters;
import com.cyctius.core.profile.AthleteVO2MaxProfile;
import com.cyctius.core.session.VO2maxSession;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

/**
 * Математическая модель планирования VO2max тренировок на основе W' Balance.
 *
 * 1. Основные понятия:
 *    - CP (Critical Power): Мощность, которую атлет может поддерживать длительное время (в данной модели CP ≈ FTP).
 *    - W' (W-prime): Анаэробный резерв энергии (в Джоулях), доступный для работы выше CP.
 *    - W' Balance: Текущий остаток анаэробного резерва.
 *
 * 2. Расчет профиля атлета:
 *    - W' рассчитывается на основе рекордов времени до отказа (TTE) на мощностях 106% и 120% FTP.
 *    - Формула: W' = (P_target - FTP) * TTE_seconds.
 *
 * 3. Динамика W' Balance:
 *    - Расход (Работа P > CP): W'bal уменьшается линейно: ΔW' = (P_work - CP) * t_work.
 *    - Восстановление (Отдых P < CP): W'bal восстанавливается экспоненциально (модель Skiba, 2012):
 *      W'bal(t) = W'max - (W'max - W'start_rec) * e^(-t / τ)
 *      где τ (тау) — константа скорости восстановления: τ = 546 * e^(-0.01 * (CP - P_rest)) + 316.
 *
 * 4. Интерпретация Score (1.0 - 10.0):
 *    - Score определяет целевой уровень истощения W' к концу последнего интервала:
 *      Target_W'bal = W'max * ((Score - 1) / 10.0).
 *    - Score 10.0 соответствует полному истощению (W'bal ≈ 0).
 *    - Score 1.0 соответствует отсутствию истощения (W'bal ≈ W'max).
 *
 * 5. Алгоритм оптимизации (Penalty Function):
 *    - Алгоритм ищет оптимальное количество повторений (N), время работы (t_work) и мощность (p_work).
 *    - Целевая функция минимизирует общую "стоимость" (Cost), которая складывается из:
 *      a) Ошибки достижения целевого W'bal.
 *      b) Отклонения времени работы от базового значения для выбранного типа интервалов.
 *      c) Отклонения мощности от базового значения.
 *    - Веса штрафов: Cost = Error_W' * 1.0 + Deviation_Time * 0.3 + Deviation_Power * 0.3.
 *    - Это обеспечивает "линейность" и предсказуемость: параметры меняются плавно, сохраняя структуру типа.
 */
@Slf4j
@Component
public class VO2maxSessionCalculator implements SessionCalculator<AthleteVO2MaxProfile, VO2maxSessionParameters, VO2maxSession> {

    @Override
    public VO2maxSession calculate(AthleteVO2MaxProfile athlete, VO2maxSessionParameters parameters) {
        if (parameters == null || !parameters.isValid()) {
            throw new IllegalArgumentException("Invalid session parameters");
        }
        double targetWBal = athlete.calculateWPrime() * (1 - (parameters.getScore() / 10.0));
        double restPower = athlete.getFtp() * 0.4;
        double tau = athlete.calculateTau(restPower);

        Config config = getConfig(parameters.getIntervalType());
        
        BestSetup bestSetup = findBestSetup(athlete, parameters, targetWBal, restPower, tau, config);

        if (bestSetup == null) {
            return null;
        }

        return VO2maxSession.builder()
                .workIntensity(bestSetup.pWork / athlete.getFtp())
                .restIntensity(restPower / athlete.getFtp())
                .workDuration((int) bestSetup.tWork)
                .restDuration((int) bestSetup.tRest)
                .totalDuration(parameters.getDurationMinutes() * 60)
                .repeats(bestSetup.n)
                .score(parameters.getScore())
                .build();
    }

    private BestSetup findBestSetup(AthleteVO2MaxProfile athlete, VO2maxSessionParameters parameters, 
                                    double targetWBal, double restPower, double tau, Config config) {
        BestSetup best = null;
        double minCost = Double.MAX_VALUE;
        int totalDurationSec = parameters.getDurationMinutes() * 60;

        for (int n : getRepeatRange(config, totalDurationSec)) {
            for (int tWork : getWorkDurationRange(config, totalDurationSec)) {
                if (isTotalWorkExceed(totalDurationSec, n, tWork)) break;

                if (n <= 1) continue;

                int tRest = calculateRestDuration(totalDurationSec, n, tWork);
                if (isRestDurationTooShort(totalDurationSec, n, tWork)) continue;

                for (double pFactor = config.pMin; pFactor <= config.pMax; pFactor += 0.01) {
                    double pWork = athlete.getFtp() * pFactor;
                    double finalWBal = simulateFast(athlete, n, tWork, pWork, tRest, tau);

                    if (isSimulationFailed(finalWBal, targetWBal)) continue;
   
                    double wError = Math.abs(finalWBal - targetWBal) / athlete.calculateWPrime();
                    double tDeviation = Math.abs(tWork - config.tBase) / (double) config.tBase;
                    double pDeviation = Math.abs(pFactor - config.pBase) / config.pBase;

                    // Penalty function for linearity
                    double cost = wError * 1.0 + tDeviation * 0.3 + pDeviation * 0.3;

                    if (cost < minCost) {
                        minCost = cost;
                        best = new BestSetup(n, tWork, pWork, tRest);
                    }
                }
            }
        }
        return best;
    }

    private List<Integer> getRepeatRange(Config config, int totalDurationSec) {
        int nIdeal = (int) (totalDurationSec / (config.tBase * 2.0));
        int nMin = Math.max(2, (int) (nIdeal * 0.5));
        int nMax = Math.max(nMin + 1, (int) (nIdeal * 2.0));

        return IntStream.rangeClosed(nMin, nMax).boxed().collect(Collectors.toList());
    }

    private List<Integer> getWorkDurationRange(Config config, int totalDurationSec) {
        return IntStream.rangeClosed(config.tMin, config.tMax).boxed().collect(Collectors.toList());
    }

    private Boolean isTotalWorkExceed(int totalDurationSec, int n, int tWork) {
        return n * tWork >= totalDurationSec;
    }

    private Integer calculateRestDuration(int totalDurationSec, int n, int tWork) {
        return (totalDurationSec - n * tWork) / (n - 1);
    }

    private Boolean isRestDurationTooShort(int totalDurationSec, int n, int tWork) {
        return calculateRestDuration(totalDurationSec, n, tWork) < 30;
    }

    private Boolean isSimulationFailed(double finalWBal, double targetWBal) {
        return finalWBal < targetWBal;
    }
    
    private double simulateFast(AthleteVO2MaxProfile athlete, int n, int tWork, double pWork, double tRest, double tau) {
        double wBal = athlete.calculateWPrime();
        double wMax = athlete.calculateWPrime();
        double cp = athlete.getFtp();

        for (int i = 0; i < n; i++) {
            wBal -= (pWork - cp) * tWork;
            if (i < n - 1) {
                wBal = wMax - (wMax - wBal) * Math.exp(-tRest / tau);
            }
        }
        return wBal;
    }

    private Config getConfig(VO2maxSessionParameters.VO2maxIntervalType type) {
        switch (type) {
            case SHORT:
                return new Config(30, 60, 40, 1.01, 1.25, 1.20, 5);
            case CLASSIC:
                return new Config(120, 300, 180, 1.01, 1.15, 1.12, 10);
            case LONG:
                return new Config(300, 600, 480, 1.01, 1.08, 1.06, 20);
            default:
                throw new IllegalArgumentException("Unknown interval type");
        }
    }

    @Data
    @Builder
    private static class Config {
        final int tMin;
        final int tMax;
        final int tBase;
        final double pMin;
        final double pMax;
        final double pBase;
        final int tStep;

        Config(int tMin, int tMax, int tBase, double pMin, double pMax, double pBase, int tStep) {
            this.tMin = tMin;
            this.tMax = tMax;
            this.tBase = tBase;
            this.pMin = pMin;
            this.pMax = pMax;
            this.pBase = pBase;
            this.tStep = tStep;
        }
    }

    private static class BestSetup {
        final int n;
        final int tWork;
        final double pWork;
        final double tRest;

        BestSetup(int n, int tWork, double pWork, double tRest) {
            this.n = n;
            this.tWork = tWork;
            this.pWork = pWork;
            this.tRest = tRest;
        }
    }
}
