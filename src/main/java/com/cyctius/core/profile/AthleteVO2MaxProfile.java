package com.cyctius.core.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AthleteVO2MaxProfile {
    private Integer ftp;
    private Double tte120Min; // Time to exhaustion at 120% FTP in minutes
    private Double tte106Min; // Time to exhaustion at 106% FTP in minutes

    /**
     * Calculates W' (anaerobic capacity) in Joules based on the FTP and TTE values.
     * Uses the simplified model: W' = (P_target - FTP) * TTE
     */
    public Double calculateWPrime() {
        double p120 = ftp * 1.20;
        double p106 = ftp * 1.06;
        double t1 = tte120Min * 60;
        double t2 = tte106Min * 60;

        double wPrime1 = (p120 - ftp) * t1;
        double wPrime2 = (p106 - ftp) * t2;

        return Math.max(wPrime1, wPrime2);
    }

    public Double calculateTau(double restPower) {
        // Skiba et al. 2012 recovery time constant formula
        return 546 * Math.exp(-0.01 * (ftp - restPower)) + 316;
    }
}
