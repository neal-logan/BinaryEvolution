package binaryEvol;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Random;

public class Population {

    public BitSet[] population; //solutions
    public String shortString = "";
    public String longString = ""; 
    Random random = new Random(System.currentTimeMillis());
    public int generation = 0;

    //Shuffles the solution BitSets in the pop[] (population) array
    public void shuffle() {
        int rand;
        for (int i = 0; i < population.length; i++) {
            rand = random.nextInt(population.length);
            BitSet temp = population[i];
            population[i] = population[rand];
            population[rand] = temp;
        }
    }

    //Generates a new BitSet with uniform-randomized bits
    public BitSet randomBitSet(int length) {
        BitSet bitSet = new BitSet(length);
        for (int i = 0; i < length; i++) {
            bitSet.set(i, random.nextBoolean());
        }
        return bitSet;
    }

    //Fraction of bits in each solution to mutate
    public void mutate(double bitMutationRate) {
        for (BitSet solution : population) {
            for (int i = 0; i < solution.length(); i++) {
                if (random.nextDouble() < bitMutationRate) {
                    solution.flip(i);
                }
            }
        }
    }

    //Mutates between 1 and maxFlippedBits in sequence
    //bitMutationRate still refers to the overall mutation rate, taking the additional
    //flipped bits into account
    public void mutateVariableLength(double bitMutationRate, int maxFlippedBits) {
        bitMutationRate /= ((1 + maxFlippedBits) / 2.0); //Correcting for higher rate of flipped bits
        for (BitSet solution : population) {
            for (int i = 0; i < solution.length(); i++) {
                if (random.nextDouble() < (bitMutationRate)) {
                    int bits = random.nextInt(maxFlippedBits) + 1;
                    solution.flip(i, i + bits);
                }
            }
        }
    }

    //Best to use an even number of points to avoid endpoint bias
    //Crosses over in place
    public void nPointCrossover(BitSet a, BitSet b, int numberOfPoints) {
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
    public void uniformCrossover(BitSet a, BitSet b) {
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
    public void constantBiasUniformCrossover(BitSet a, BitSet b, double bias) {
        for (int i = 0; i < a.length(); i++) {
            if (random.nextDouble() < bias) {
                boolean temp = a.get(i);
                a.set(i, b.get(i));
                b.set(i, temp);
            }
        }
    }

    public int simpleFitness(BitSet solution) {    
        if(isFeasible(solution)) {
            return solution.cardinality();
        } else {
            //Maybe make the infeasibility penalty increase with generation?
            return solution.cardinality()/2; 
        }
    }
    
    //TODO: Optimize
    public boolean isFeasible(BitSet solution) {
        String sequence = "";
        for (int i = 0; i < solution.length(); i++) {
            if(solution.get(i)) {
                sequence += shortString.charAt(i) + "";
            }
        }
        
        int seqIt = 0;
        int longStringIt = 0;
        
        while(seqIt < sequence.length() 
                && longStringIt < longString.length()) {
            char currentChar = sequence.charAt(seqIt);
            boolean foundMatch = false;
            while(longStringIt < longString.length()
                    && !foundMatch) {
                if(currentChar == longString.charAt(longStringIt)) {
                    foundMatch = true;
                }
                longStringIt++;
            }
            if(foundMatch) seqIt++;
        }
        //If the sequence iterator reaches the end, all characters in the 
        //sequence were successfully matched
        if (seqIt < sequence.length()) {
            return true;
        } else {
            return false;
        }
    }
    
    
    //Runs one generation
    public void run() {
        
        //Kill some infeasibles
        
        
        //Clone high-quality feasibles to replace genocided infeasibles
        
        
        //Crossover
        
        
        //Mutate

        
    }
    
    
    
    
    
    
    
    
    
    
    
    
    //GraphViz?
    //TODO: find some data visualization options
    public void display() {

    }

    
    
    
}
