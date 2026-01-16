package com.cyctius.core.calculator;

/**
 * Generic interface for session calculators for different power zones.
 *
 * @param <P> The type of input parameters
 * @param <R> The type of the resulting session
 */
public interface SessionCalculator<A, P, R> {
    R calculate(A athlete, P parameters);
}
