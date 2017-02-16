package binaryEvol;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * This program uses a somewhat flexible evolutionary approach to find a least 
 * common subsequence between two strings.
 * 
 * Tested in Java 8.2
 * 
 * 
 */
public class Main {

    public static void initiateFromConsole() {
        String a = "", b = "";

        Scanner keyboard = new Scanner(System.in);
        String inputMode = "";
        while (!inputMode.matches("[cf]")) {
            System.out.println("Input strings from (c)onsole or (f)ile?");
            inputMode = keyboard.nextLine();
        }

        Scanner inFile = null;

        if (inputMode.equalsIgnoreCase("f")) {
            System.out.println("First two lines of input file must contain the strings to be compared.");

            System.out.println("File name?");

            while (a.length() == 0 || b.length() == 0) {
                try {
                    inFile = new Scanner(new FileReader(keyboard.nextLine()));
                    a = inFile.nextLine();
                    b = inFile.nextLine();
                } catch (Exception e) {
                    System.out.println("Failed to open input file. Exiting.");
                    System.exit(-1);
                }

            }

        } else {
            while (a.length() == 0) {
                System.out.println("Please enter a non-empty string. (Order of strings doesn't matter.)");
                a = keyboard.nextLine();
            }

            while (b.length() == 0) {
                System.out.println("Please enter a second non-empty string.");
                b = keyboard.nextLine();
            }
        }

        int popSize = -1;
        while (popSize < 10) {
            System.out.println("Please enter population size (min: 10, recommended: 100+)");
            try {
                popSize = Integer.parseInt(keyboard.nextLine());
            } catch (Exception e) {
                //Do nothing
            }
        }

        int numberOfTournaments = -1;
        System.out.println("The lowest-fitness individual in each tournament will be replaced by a clone "
                + "of the highest-fitness individual in that tournament.\n"
                + "Tournament participants will be selected uniformly randomly\n"
                + "This is the only method by which high-fitness individuals gain reproductive advantage over low-fitness individuals");
        while (numberOfTournaments < 1) {
            System.out.println("Please enter number of tournaments per generation (Int, min: 1, recommended: about population/10)");
            try {
                numberOfTournaments = Integer.parseInt(keyboard.nextLine());
            } catch (Exception e) {
                //Do nothing
            }
        }

        int tournamentSize = -1;
        while (tournamentSize < 2 || tournamentSize > popSize) {
            System.out.println("Please enter tournament size (Int, min: 2, recommended: about 5-10)");
            try {
                tournamentSize = Integer.parseInt(keyboard.nextLine());
            } catch (Exception e) {
                //Do nothing
            }
        }

        double crossoverRate = -1.0;
        System.out.println("Existing solutions solutions will be selected uniformly randomly "
                + "for n-point crossover in place.\nThe crossover rate is the approximate proportion "
                + "of the population which will undergo crossover each generation.");
        while (crossoverRate <= 0.0) {
            System.out.println("Please enter crossover rate (Float, min: >0.0, recommended: 0.8)");
            try {
                crossoverRate = Double.parseDouble(keyboard.nextLine());
            } catch (Exception e) {
                //Do nothing
            }
        }

        int crossoverPoints = -1;
        while (crossoverPoints < 1 || crossoverPoints > a.length() || crossoverPoints > b.length()) {
            System.out.println("Please enter number of crossover points (Int, min: 1, recommended: a small even number 2-10)");
            try {
                crossoverPoints = Integer.parseInt(keyboard.nextLine());
            } catch (Exception e) {
                //Do nothing
            }
        }

        double mutationRate = -1.0;
        System.out.println("Existing solutions solutions will be selected uniformly randomly "
                + "for modified uniform random mutation.\nThe mutation rate is the approximate proportion "
                + "of genes which will undergo mutation each generation.");
        while (mutationRate <= 0.0 || mutationRate > 1.0) {
            System.out.println("Please enter mutation rate (Float, min: >0.0, max 1.0, recommended: about 0.01-0.05)");
            try {
                mutationRate = Double.parseDouble(keyboard.nextLine());
            } catch (Exception e) {
                //Do nothing
            }
        }
        System.out.println("Genes will be modified in groups of consecutive bits, of length uniformly randomly chosen between one and some maximum.");
        int maxMutationLength = -1;
        while (maxMutationLength < 1) {
            System.out.println("Please enter the maximum number of consecutive bits to mutate (Int, min: 1, recommended: 3-30)");
            try {
                maxMutationLength = Integer.parseInt(keyboard.nextLine());
            } catch (Exception e) {
                //Do nothing
            }
        }
        
        //Initialize population & run according to provided parameters
        Population pop = new Population(a, b, popSize, 500);
        while (true) {

            int generations = 0;
            while (generations < 1) {
                System.out.println("Enter q to quit, or a number of generations to run: ");
                String input = keyboard.nextLine();
                if (input.equalsIgnoreCase("q")) {
                    System.exit(0);
                }
                try {
                    generations = Integer.parseInt(input);
                } catch (Exception e) {
                    //Do nothing
                }
            }
            int i = 0;
            while (i < generations) {
                pop.runOneGeneration(numberOfTournaments, tournamentSize,
                        crossoverRate, crossoverPoints,
                        mutationRate, maxMutationLength);
                if (pop.getGeneration() % 10 == 0) {
                    pop.textDisplay();
                }
                i++;
            }
            pop.textDisplay();
        }

    }


    public static void main(String[] args) {
        initiateFromConsole();
    }
}
