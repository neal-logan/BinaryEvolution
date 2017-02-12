/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
