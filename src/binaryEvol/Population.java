package binaryEvol;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Random;

public class Population {

    private BitSet[] population; //solutions
    private String shortString = "";
    private String longString = "";
    private int fuzzyMatchingSearchRange = 10;
    private int generation = 0;
    private int epochLength = 0;
    Random random = new Random(System.currentTimeMillis());

///////////////////////////   INITIALIZATION   ///////////////////////////////
    public Population(String a, String b, int size, int epochLength) {
        if (a.length() > b.length()) {
            longString = a;
            shortString = b;
        } else {
            longString = b;
            shortString = a;
        }
        this.population = new BitSet[size];
        this.epochLength = epochLength;

        for (int i = 0; i < size; i++) {
            population[i] = this.randomBitSet(shortString.length());
        }
        System.out.print("Initialized population");
    }

    //Generates a new BitSet with uniform-randomized bits
    public BitSet randomBitSet(int length) {
        BitSet bitSet = new BitSet(length);
        for (int i = 0; i < length; i++) {
            bitSet.set(i, random.nextBoolean());
        }
        return bitSet;
    }

//////////////////////////////   ACCESSORS   ///////////////////////////////////           
    public String getSolutionAsString(BitSet solution) {
        String sequence = "";
        for (int i = 0; i < solution.length(); i++) {
            if (solution.get(i)) {
                sequence += getShortString().charAt(i) + "";
            }
        }
        return sequence;
    }

    public String getSolutionAsString(int solutionIndex) {
        return getSolutionAsString(this.population[solutionIndex]);
    }

    public int getSearchRange() {
        return this.fuzzyMatchingSearchRange;
    }

    public BitSet[] getPopulation() {
        return population;
    }

    public String getShortString() {
        return shortString;
    }

    public String getLongString() {
        return longString;
    }

    public int getGeneration() {
        return generation;
    }

    public int getEpochLength() {
        return epochLength;
    }

////////////////////////////////  FITNESS   ////////////////////////////////////    
    /*
        Returns true iff the specified solution sequence is a valid subsequence
        in the longString.
     */
    //TODO: Optimize
    public boolean isFeasible(String solutionSequence) {

        int solSeqIter = 0;
        int longStringIterator = 0;

        while (solSeqIter < solutionSequence.length()
                && longStringIterator < getLongString().length()) {
            char currentChar = solutionSequence.charAt(solSeqIter);
            boolean foundMatch = false;
            while (longStringIterator < getLongString().length()
                    && !foundMatch) {
                if (currentChar == getLongString().charAt(longStringIterator)) {
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

////////////////////////////    MUTATION    ///////////////////////////////////
    /*
    bitMutationRate: Chance each bit has to mutate, on [0,1]
     */
    public void applyBitwiseRandomMutation(double bitMutationRate) {
        for (BitSet solution : getPopulation()) {
            for (int i = 0; i < solution.length(); i++) {
                if (random.nextDouble() < bitMutationRate) {
                    solution.flip(i);
                }
            }
        }
    }

    public void applyUniformMutation(double portionOfPopulationToMutate, int numberOfMutationsPerSolution) {

    }


    /*
        Mutates between 1 and maxFlippedBits in sequence
        bitMutationRate refers to the overall expected bit-wise mutation rate, 
        taking the expected number of additional flipped bits into account
     */
    public void applyVariableLengthMutation(double bitMutationRate, int maxFlippedBits) {
        bitMutationRate /= ((1 + maxFlippedBits) / 2.0); //Correcting for higher rate of flipped bits
        for (BitSet solution : getPopulation()) {
            for (int i = 0; i < solution.length(); i++) {
                if (random.nextDouble() < (bitMutationRate)) {
                    int bits = random.nextInt(maxFlippedBits) + 1;
                    solution.flip(i, i + bits);
                }
            }
        }
    }

//////////////////////////   CROSSOVER METHODS   ///////////////////////////////
    //Best to use an even number of points to avoid endpoint bias
    //Crosses over in place
    private void singleNPointCrossover(BitSet a, BitSet b, int numberOfPoints) {
        HashSet<Integer> crossoverPoints = new HashSet<>();
        while (crossoverPoints.size() < numberOfPoints) {
            crossoverPoints.add(random.nextInt(a.length()));
        }

        boolean swapping = false;
        for (int i = 0; i < a.length(); i++) {
            //Determine whether a and b are swapping bits
            if (crossoverPoints.contains(i)) {
                if (swapping == false) {
                    swapping = true;
                } else {
                    swapping = false;
                }
            }
            //Swap bits, if currently appropriate
            if (swapping) {
                boolean temp = a.get(i);
                a.set(i, b.get(i));
                b.set(i, temp);
            }
        }
    }

    //Randomly swaps bits between a and b
    private void singleUniformCrossover(BitSet a, BitSet b) {
        for (int i = 0; i < a.length(); i++) {
            if (random.nextBoolean()) {
                boolean temp = a.get(i);
                a.set(i, b.get(i));
                b.set(i, temp);
            }
        }
    }

    //Keeps a certain portion of genetic material, i.e., 70-30 splits
    //Faster to use bias between 0 and 0.5, but will work
    private void singleConstantBiasUniformCrossover(BitSet a, BitSet b, double bias) {
        for (int i = 0; i < a.length(); i++) {
            if (random.nextDouble() < bias) {
                boolean temp = a.get(i);
                a.set(i, b.get(i));
                b.set(i, temp);
            }
        }
    }

//////////////////////////////   OPERATION    //////////////////////////////////
    //Shuffles the solution BitSets in the pop[] (population) array
    public void shuffle() {
        int rand;
        for (int i = 0; i < getPopulation().length; i++) {
            rand = random.nextInt(getPopulation().length);
            BitSet temp = getPopulation()[i];
            population[i] = getPopulation()[rand];
            population[rand] = temp;
        }
    }

    //Runs one generation
    public void runOneGeneration() {
        //Shuffle
        this.shuffle();

        //Calculate fitness information
        double[] fitness = new double[population.length];
        for (int i = 0; i < fitness.length; i++) {
            Assessment assessment = new Assessment(this, i);
            fitness[i] = assessment.getEscalatingPowerFuzzyFitness(0.5);
        }

        double totalFitness = 0;
        for (int i = 0; i < fitness.length; i++) {
            totalFitness += fitness[i];
        }

        //Kill/Clone
        
        
        
        
        //Crossover
        
        
        
        
        //Mutate
        generation++;
    }

//////////////////////////////   DISPLAY   ////////////////////////////////////
    
    //GraphViz?
    //TODO: find some data visualization options
    public void display() {

    }

    
    public void textDisplay() {
        
    }
    
}
