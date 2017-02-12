package binaryEvol;

import java.util.BitSet;

/**
 *
 * @author neal
 */
public class Assessment {

    private String solution;
    private String longString;
    private int solutionSkips = 0;
    private int longStringSkips = 0;
    private int matches = 0;
    private int searchRange = 0;
    private Population population;
    private boolean feasible = false;

    /*
    -Roughly O(n^2) with searchRange when infeasible
    -searchRange is the distance from initial iterator positions to search
    calculated as the sum of the distances from each iterator
    -search range of 3-12 should be fine
    -search range must be at least 2
     */
    //TODO: Add option to skip feasibility check?
    public Assessment(Population population, int solutionIndex) {
        this.longString = population.getLongString();
        this.population = population;
        this.searchRange = population.getSearchRange();
        this.solution = population.getSolutionAsString(solutionIndex);

        //Feasibility check
        if (population.isFeasible(solution)) {
            matches = solution.length();
            longStringSkips = longString.length() - solution.length();
            feasible = true;

            //If not feasible, do fuzzy assessment          
            //-Higher complexity, probably don't want to use these every time
            //-Much more intensive than cardinality fitness
        } else {
            double sequenceLengthRatio = solution.length() / (double) longString.length();
            int solutionIterator = 0, longStringIterator = 0;
            while (solutionIterator < solution.length()
                    && longStringIterator < longString.length()) {

                //If match, iterate
                if (solution.charAt(solutionIterator)
                        == longString.charAt(longStringIterator)) {
                    solutionIterator++;
                    longStringIterator++;
                    matches++;
                    //If no match without search, search!    
                } else {
                    //Search:
                    boolean match = false;
                    //Increase search range to max range
                    for (int currentRange = 1; currentRange <= searchRange && !match; currentRange++) {
                        //Search diagonally at current range, unless a match has been found
                        for (int solutionSearch = 0, longStringSearch = currentRange;
                                longStringSearch >= 0 && !match
                                && solutionIterator + solutionSearch < solution.length()
                                && longStringIterator + longStringSearch < longString.length();
                                solutionSearch++, longStringSearch--) {
                            //If a match is found, iterate, add to matches, and break
                            if (solution.charAt(solutionIterator + solutionSearch)
                                    == longString.charAt(longStringIterator + longStringSearch)) {
                                match = true;
                                matches++;
                                solutionIterator += solutionSearch;
                                solutionSkips += solutionSearch;
                                longStringIterator += longStringSearch;
                                longStringSkips += longStringSearch;
                                break;
                            }
                        }
                    }

                    //Searching complete
                    //If match not found after search:
                    if (!match) {
                        //Iterate; skip more of the sequence which has been skipped less relative to overall lengths
                        double skipRatio = solutionSkips / (double) longStringSkips;
                        if (skipRatio > sequenceLengthRatio) {
                            longStringIterator += searchRange - 1;
                            longStringSkips += searchRange - 1;
                            solutionIterator++;
                            solutionSkips++;
                        } else {
                            longStringIterator+= searchRange/2;
                            longStringSkips+= searchRange/2;
                            solutionIterator += 1 + searchRange/2;
                            solutionSkips += 1 + searchRange/2;
                        }
                    }
                }
            }

            //Skip any remaining characters in both lists
            this.solutionSkips += (solution.length() - solutionIterator);
            this.longStringSkips += (longString.length() - longStringIterator);
        }

    }

    public double getSimpleFuzzyFitness() {
        if (feasible) {
            return solution.length();
        } else {
            return matches - solutionSkips;
        }
    }

    public double getPowerFuzzyFitness(double power) {
        if(feasible) {
            return Math.pow(solution.length(), power);
        } else {
            return Math.pow(matches, power) - Math.pow(solutionSkips, power);
        }
    }
    
    public double getEscalatingPowerFuzzyFitness(double power) {
        power = power * (1.0 + (double) population.getGeneration() / population.getEpochLength());
        if(feasible) {
            return Math.pow(solution.length(), power);
        } else {
            return Math.pow(matches, power) - Math.pow(solutionSkips, power);
        }
    }
    
    
    //Character mismatch penalty increases steadily with generation
    public double getFitnessEscalatingPenalties() {
        if (feasible) {
            return solution.length();
        } else {
            return matches - (solutionSkips * (1.0 + population.getGeneration()) / population.getEpochLength());
        }
    }
    
    public double getEscalatingPowerMildFeasibilityCardinalityFitness(double power) {
        power = power * (1.0 + (double) population.getGeneration() / population.getEpochLength());
        if(feasible) {
            return Math.pow(this.solution.length(), power);
        } else {
            return Math.pow(matches, power) - Math.pow(solutionSkips, power);
        }
    }
    
    public int getHarshFeasibilityCardinalityFitness() {
        if (feasible) {
            return solution.length();
        } else {
            return 0;
        }
    }    
 
    
    
    
}
