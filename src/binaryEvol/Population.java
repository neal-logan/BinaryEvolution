package binaryEvol;

import java.util.HashSet;
import java.util.Random;

public class Population {

    private BinarySolution[] population; //solutions
    private String shortString = "";
    private String longString = "";
    private int fuzzyMatchingSearchRange = 7;
    private int generation = 0;
    private int epochLength = 0;
    private FitnessFunction test = new SimpleFuzzyFitness();
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
        this.population = new BinarySolution[size];
        this.epochLength = epochLength;

        for (int i = 0; i < size; i++) {
            population[i] = this.randomBitSet(shortString.length());

        }

//        System.out.print("Initialized population");
    }

    //Generates a new BitSet with uniform-randomized bits
    public BinarySolution randomBitSet(int length) {

        BinarySolution bitSet = new BinarySolution(length, this);
        for (int i = 0; i < length; i++) {
            bitSet.set(i, random.nextBoolean());
        }
        String bitString = bitSetToString(bitSet);
        System.out.println(bitString);
//        System.out.println("Length: " + bitString.length());
        return bitSet;
    }

//////////////////////////////   ACCESSORS   ///////////////////////////////////           
    public int getSearchRange() {
        return this.fuzzyMatchingSearchRange;
    }

    public BinarySolution[] getPopulation() {
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
////////////////////////////    MUTATION    ///////////////////////////////////
    /*
    bitMutationRate: Chance each bit has to mutate, on [0,1]
     */
    public void applyBitwiseRandomMutation(double bitMutationRate) {
        for (BinarySolution solution : getPopulation()) {
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
        for (BinarySolution solution : getPopulation()) {
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
    private void nPointCrossover(BinarySolution a, BinarySolution b, int numberOfPoints) {
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
    private void uniformCrossover(BinarySolution a, BinarySolution b) {
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
    private void constantBiasUniformCrossover(BinarySolution a, BinarySolution b, double bias) {
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
    private void tournamentReplaceWorstWithCloneOfBest(int poolSize, FitnessFunction test) {
        double[] fitness = this.getFitness(test);

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
        population[worstSolution] = (BinarySolution) population[bestSolution].deepClone();
    }

//////////////////////////////   OPERATION    //////////////////////////////////
    //Shuffles the solution BitSets in the pop[] (population) array
    public void shuffle() {
        int rand;
        for (int i = 0; i < getPopulation().length; i++) {
            rand = random.nextInt(getPopulation().length);
            BinarySolution temp = getPopulation()[i];
            population[i] = getPopulation()[rand];
            population[rand] = temp;
        }
    }

    public int bestSolutionIndex(FitnessFunction fitnessFunction) {
        double highestFitness = Double.NEGATIVE_INFINITY;
        int bestSolutionIndex = -1;
        double[] fitness = this.getFitness(fitnessFunction);
        for (int i = 0; i < population.length; i++) {
            double currentFitness = fitnessFunction.getFitness(population[i]);
            if (currentFitness > highestFitness) {
                highestFitness = currentFitness;
                bestSolutionIndex = i;
            }
        }
        System.out.print(fitness[bestSolutionIndex]);
        return bestSolutionIndex;
    }

    //Uses the default fitness function
    public int bestSolutionIndex() {
        return bestSolutionIndex(this.test);
    }

    public static double sum(double[] values) {
        double sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[i];
        }
        return sum;
    }

    public double[] getFitness(FitnessFunction fitnessFunction) {
        double[] fitness = new double[this.population.length];
        for (int i = 0; i < this.population.length; i++) {
            fitness[i] = fitnessFunction.getFitness(population[i]);
        }
        return fitness;
    }

    public double[] getFitness() {
        double[] fitness = new double[this.population.length];
        for (int i = 0; i < this.population.length; i++) {
            fitness[i] = test.getFitness(population[i]);
        }
        return fitness;
    }

    public double getMeanFitness(FitnessFunction fitnessFunction) {
        return sum(this.getFitness(fitnessFunction)) / population.length;
    }

    public double getMeanFitness() {
        return sum(this.getFitness(test)) / population.length;
    }

    //Runs one generation
    public void runOneGeneration() {

        //Kill/Clone - only way good genes are encouraged
        int numberOfTournaments = population.length / 10;
        int tournamentSize = 7;
        for (int i = 0; i < numberOfTournaments; i++) {
            this.tournamentReplaceWorstWithCloneOfBest(tournamentSize, new SimpleFuzzyFitness());
        }

        //Crossover - mixes things up
        this.shuffle(); //Necessary to ensure random partners
        double crossoverRate = 0.1;
        int crossoverPoints = 8;
        for (int i = 0; i < population.length - 1; i += 2) {
            if (random.nextDouble() < crossoverRate) {
                this.nPointCrossover(population[i], population[i + 1], crossoverPoints);
            }
        }

        //Mutate - only way new genes are introduced
        this.applyVariableLengthMutation(0.05, 80);

        //Iterate
        generation++;
    }

//////////////////////////////   DISPLAY   ////////////////////////////////////
    //GraphViz?
    //TODO: find some data visualization options
    public void display() {

    }

    public void textDisplay() {

        BinarySolution bestSolution = population[this.bestSolutionIndex()];
        String best = bestSolution.getSolutionAsString();
        System.out.println("Generation: " + this.generation
                + "\t MeanFitness: " + this.getMeanFitness(test) + "\t Highest fitness: " + test.getFitness(bestSolution) + " Feasible? " + bestSolution.isFeasible());
        System.out.println("Best solution: " + best);

    }

    public static String bitSetToString(BinarySolution bitSet) {
        String s = "";
        for (int i = 0; i < bitSet.length(); i++) {
            if (bitSet.get(i)) {
                s += "1";
            } else {
                s += "0";
            }
        }
        return s;
    }

}
