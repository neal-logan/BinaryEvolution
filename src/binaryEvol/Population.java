package binaryEvol;

import java.util.HashSet;
import java.util.Random;

public class Population {

    private BitSet[] population; //solutions
    private double[] fitness;
    private String shortString = "";
    private String longString = "";
    private int fuzzyMatchingSearchRange = 7;
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
        this.fitness = new double[population.length];
        this.epochLength = epochLength;

        for (int i = 0; i < size; i++) {
            population[i] = this.randomBitSet(shortString.length());
            
        }
        this.runAssessments();
        
//        System.out.print("Initialized population");
    }

    //Generates a new BitSet with uniform-randomized bits
    public BitSet randomBitSet(int length) {
        
        BitSet bitSet = new BitSet(length);
        for (int i = 0; i < length; i++) {
            bitSet.set(i, random.nextBoolean());
        }
        String bitString = bitSetToString(bitSet);
        System.out.println(bitString);
//        System.out.println("Length: " + bitString.length());
        return bitSet;
    }

//////////////////////////////   ACCESSORS   ///////////////////////////////////           
    public String getSolutionAsString(int solutionIndex) {
        
        String sequence = "";
        for (int i = 0; i < population[solutionIndex].length(); i++) {
            if (population[solutionIndex].get(i)) {
                sequence += shortString.charAt(i) + "";
            }
        }
        return sequence;
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
                    solution.flip(i, Math.min(i + bits, solution.length()));
                }
            }
        }
    }

//////////////////////////   CROSSOVER METHODS   ///////////////////////////////
    //Best to use an even number of points to avoid endpoint bias
    //Crosses over in place
    private void nPointCrossover(BitSet a, BitSet b, int numberOfPoints) {
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
    private void uniformCrossover(BitSet a, BitSet b) {
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
    private void constantBiasUniformCrossover(BitSet a, BitSet b, double bias) {
        for (int i = 0; i < a.length(); i++) {
            if (random.nextDouble() < bias) {
                boolean temp = a.get(i);
                a.set(i, b.get(i));
                b.set(i, temp);
            }
        }
    }

//////////////////////////////   CLONING   /////////////////////////////////////
    //Takes a random set of solutions
    //Kills worst and replaces it with a clone of the best
    private void tournamentReplaceWorstWithCloneOfBest(int poolSize) {
        //Select combatants
        HashSet<Integer> pool = new HashSet<>();
        while (pool.size() < poolSize) {
            pool.add(random.nextInt(population.length));
        }
        //Identify worst and best
        int worstSolution = -1;
        double lowestFitness = Double.POSITIVE_INFINITY;
        int bestSolution = -1;
        double highestFitness = Double.NEGATIVE_INFINITY;
        for (int combatant : pool) {
            if (fitness[combatant] < lowestFitness) {
                lowestFitness = fitness[combatant];
                worstSolution = combatant;
            }
            if (fitness[combatant] > highestFitness) {
                highestFitness = fitness[combatant];
                bestSolution = combatant;
            }
        }
        //Replace worst with clone of best
        population[worstSolution] = (BitSet)population[bestSolution].deepClone();
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

    public void runAssessments() {
        for (int i = 0; i < fitness.length; i++) {
            Assessment assessment = new Assessment(this, i);
            fitness[i] = assessment.getSimpleFuzzyFitness();
        }

    }
    
    public int bestSolutionIndex() {
        double highestFitness = Double.NEGATIVE_INFINITY;
        int bestSolution = -1;
        for(int i = 0; i < population.length; i++) {
            if(fitness[i] > highestFitness) {
                highestFitness = fitness[i];
                bestSolution = i;
            }
        }
        return bestSolution;
    }
    
    public double getTotalFitness() {
        double totalFitness = 0;
        for (int i = 0; i < fitness.length; i++) {
            totalFitness += fitness[i];
        }
        return totalFitness;
    }
    
    public double getMeanFitness() {
        return this.getTotalFitness()/population.length;
    }

    //Runs one generation
    public void runOneGeneration() {
        
        //Kill/Clone - only way good genes are encouraged
        int numberOfTournaments = population.length / 20;
        int tournamentSize = 7;
        for(int i = 0; i < numberOfTournaments; i++) {
            this.tournamentReplaceWorstWithCloneOfBest(tournamentSize);
        }
        
        //Crossover - mixes things up
        this.shuffle(); //Necessary to ensure random partners
        double crossoverRate = 0.3;
        int crossoverPoints = 2;
        for (int i = 0; i < population.length - 1; i += 2) {
            if(random.nextDouble() < crossoverRate) {
                this.nPointCrossover(population[i], population[i + 1], crossoverPoints);
            }
        }

        //Mutate - only way new genes are introduced
        this.applyVariableLengthMutation(0.01, 3);

        //Update Assessments
        this.runAssessments();
        
        //Iterate
        generation++;
    }

//////////////////////////////   DISPLAY   ////////////////////////////////////
    //GraphViz?
    //TODO: find some data visualization options
    public void display() {

    }

    public void textDisplay() {
        String best = this.getSolutionAsString(this.bestSolutionIndex());
        System.out.println("Generation: " + this.generation
        + "\t MeanFitness: " + this.getMeanFitness() + "\t Highest fitness: " + best.length() + " Feasible? " + this.isFeasible(best));
        System.out.println("Best solution: " + best);
        
        
    }

    public static String bitSetToString(BitSet bitSet) {
        String s = "";
        for(int i = 0; i < bitSet.length(); i++) {
            if(bitSet.get(i)) {
                s += "1";
            } else {
                s += "0";
            }
        }
        return s;
    }
    
}
