package binaryEvol;

/**
 *
 * @author neal
 * An interface for wrapper classes for fitness functions
 * 
 */
public interface FitnessFunction {
    
    public abstract double getFitness(BinarySolution solution);
    
}
