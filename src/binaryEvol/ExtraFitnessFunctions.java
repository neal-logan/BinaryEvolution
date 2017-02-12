//package binaryEvol;
//
///**
// *
// * @author neal
// */
//public class ExtraFitnessFunctions {
//    
//    public double getPowerFuzzyFitness(double power) {
//        if (isFeasible()) {
//            return Math.pow(this.cardinality(), power);
//        } else {
//            return Math.pow(getMatches(), power) - Math.pow(getSolutionSkips(), power);
//        }
//    }
//
//    public double getEscalatingPowerFuzzyFitness(double power) {
//        power = power * (1.0 + (double) population.getGeneration() / population.getEpochLength());
//        if (isFeasible()) {
//            return Math.pow(this.cardinality(), power);
//        } else {
//            return Math.pow(getMatches(), power) - Math.pow(getSolutionSkips(), power);
//        }
//    }
//
//    //Character mismatch penalty increases steadily with generation
//    public double getFitnessEscalatingPenalties() {
//        if (isFeasible()) {
//            return this.cardinality();
//        } else {
//            return getMatches() - (getSolutionSkips() * (1.0 + population.getGeneration()) / population.getEpochLength());
//        }
//    }
//
//    public double getEscalatingPowerMildFeasibilityCardinalityFitness(double power) {
//        power = power * (1.0 + (double) population.getGeneration() / population.getEpochLength());
//        if (isFeasible()) {
//            return Math.pow(this.cardinality(), power);
//        } else {
//            return Math.pow(getMatches(), power) - Math.pow(getSolutionSkips(), power);
//        }
//    }
//
//    public int getHarshFeasibilityCardinalityFitness() {
//        if (isFeasible()) {
//            return this.cardinality();
//        } else {
//            return 0;
//        }
//    }
//}
