package binaryEvol;

/**
 *
 * @author neal
 * If a solution is feasible, uses cardinality of solution to determine fitness
 * Else uses fast fuzzy subsequence matching to help determine fitness
 */
public class SimpleFuzzyFitness implements FitnessFunction {

    @Override
    public double getFitness(BinarySolution solution) {
        if (solution.isFeasible()) {
            return solution.cardinality();
        } else {
            return solution.getMatches() - solution.getSolutionSkips();
        }
    }

}
