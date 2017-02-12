package binaryEvol;

/**
 *
 * @author neal
 */
public class BinarySolution {

    //Always Valid
    private boolean[] bits;
    private Population population;

    //Flagged Validity 
    private boolean assessmentValid = false;
    private int solutionSkips = 0;
    private int longStringSkips = 0;
    private int matches = 0;
    private boolean feasible = false;

    public BinarySolution(int length, Population population) {

        this.population = population;
        bits = new boolean[length];
        for (int i = 0; i < length; i++) {
            bits[i] = false;
        }
        this.updateAssessment();
    }

    public boolean get(int index) {
        this.updateAssessment();
        return bits[index];
    }

    public void set(int index, boolean value) {
        if (bits[index] != value) {
            bits[index] = value;
            assessmentValid = false;
        }
    }

    public void flip(int index) {
        if (bits[index]) {
            bits[index] = false;
        } else {
            bits[index] = true;
        }
        assessmentValid = false;
    }

    //Start inclusive, end exclusive
    public void flip(int start, int end) {
        for (int i = start; i < end; i++) {
            flip(i);
        }
    }

    public int cardinality() {
        this.updateAssessment();
        int cardinality = 0;
        for (int i = 0; i < bits.length; i++) {
            if (bits[i]) {
                cardinality++;
            }
        }
        return cardinality;
    }

    public int length() {
        return bits.length;
    }

    public int getSolutionSkips() {
        this.updateAssessment();
        return solutionSkips;
    }

    public int getLongStringSkips() {
        this.updateAssessment();
        return longStringSkips;
    }

    public int getMatches() {
        this.updateAssessment();
        return matches;
    }

    public BinarySolution deepClone() {
        BinarySolution bitSet = new BinarySolution(bits.length, this.population);
        for (int i = 0; i < bits.length; i++) {
            bitSet.set(i, bits[i]);
        }
        return bitSet;
    }

    public String getSolutionAsString() {
        String sequence = "";
        for (int i = 0; i < bits.length; i++) {
            if (bits[i]) {
                sequence += population.getShortString().charAt(i) + "";
            }
        }
        return sequence;
    }

    /*
        Returns true iff the specified solution sequence is a valid subsequence
        in the longString.
     */
    //TODO: Optimize
    public boolean isFeasible() {
        String solutionSequence = this.getSolutionAsString();
        int solSeqIter = 0;
        int longStringIterator = 0;

        while (solSeqIter < solutionSequence.length()
                && longStringIterator < population.getLongString().length()) {
            char currentChar = solutionSequence.charAt(solSeqIter);
            boolean foundMatch = false;
            while (longStringIterator < population.getLongString().length()
                    && !foundMatch) {
                if (currentChar == population.getLongString().charAt(longStringIterator)) {
                    foundMatch = true;
                }
                longStringIterator++;
            }
            if (foundMatch) {
                solSeqIter++;
            }
        }
        //If the sequence iterator reaches the end, all characters in the 
        //sequence were successfully matched
        if (solSeqIter < solutionSequence.length()) {
            return true;
        } else {
            return false;
        }
    }


    /*
    -Roughly O(n^2) with searchRange when infeasible
    -searchRange is the distance from initial iterator positions to search
    calculated as the sum of the distances from each iterator
    -search range of 3-12 should be fine
    -search range must be at least 2
     */
    //TODO: Add option to skip feasibility check?
    public void updateAssessment() {
        //If nothing has changed, don't redo assessment
        if (assessmentValid) {
            return;
        }

        String solution = this.getSolutionAsString();

        String longString = population.getLongString();
        int searchRange = population.getSearchRange();

        //Feasibility check
        if (this.isFeasible()) {
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
                        double skipRatio = getSolutionSkips() / (double) getLongStringSkips();
                        if (skipRatio > sequenceLengthRatio) {
                            longStringIterator += searchRange - 1;
                            longStringSkips += searchRange - 1;
                            solutionIterator++;
                            solutionSkips++;
                        } else {
                            longStringIterator += searchRange / 2;
                            longStringSkips += searchRange / 2;
                            solutionIterator += 1 + searchRange / 2;
                            solutionSkips += 1 + searchRange / 2;
                        }
                    }
                }
            }

            //Skip any remaining characters in both lists
            this.solutionSkips += (solution.length() - solutionIterator);
            this.longStringSkips += (longString.length() - longStringIterator);
        }
        assessmentValid = true;
    }

}
