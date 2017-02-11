package binaryEvol;

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
    -Higher complexity, probably don't want to use these every time
    -Roughly O(n^2) with searchRange; 5-7 should be reasonable
    -Much more intensive than cardinality fitness
    
    -searchRange is the distance from initial iterator positions to search
    calculated as the sum of the distances from each iterator
     */
    public Assessment(Population population, String solution, int searchRange) {
        this.population = population;
        this.searchRange = searchRange;
        this.solution = solution;

        //Feasibility check
        if (population.isFeasible(solution)) {
            matches = solution.length();
            longStringSkips = longString.length() - solution.length();
            feasible = true;
            
        //If not feasible, do fuzzy assessment
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
                                longStringSearch >= 0 && !match;
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
                            longStringIterator += searchRange;
                            longStringSkips += searchRange;
                            solutionIterator++;
                            solutionSkips++;
                        } else {
                            longStringIterator++;
                            longStringSkips++;
                            solutionIterator += searchRange;
                            solutionSkips += searchRange;
                        }
                    }
                }
            }

            //Skip any remaining characters in both lists
            this.solutionSkips += (solution.length() - solutionIterator);
            this.longStringSkips += (longString.length() - longStringIterator);
        }

    }

    public double getSimpleFitness() {
        if (feasible) {
            return solution.length();
        } else {
            return matches - solutionSkips;
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
    
    
    
}
