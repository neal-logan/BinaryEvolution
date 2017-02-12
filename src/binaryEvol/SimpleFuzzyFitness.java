package binaryEvol;

/**
 *
 * @author neal
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
