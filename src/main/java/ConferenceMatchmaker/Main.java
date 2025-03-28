package ConferenceMatchmaker;

import static ConferenceMatchmaker.Matchmaker.runGA;

public class Main {
    private static final int DEFAULT_GENERATIONS = 500;
    private static final int DEFAULT_POPULATION_SIZE = 40;
    private static final double DEFAULT_MUTATION_RATE = 0.02;
    private static final String DEFAULT_DATA_FILE_PATH = "data1.txt";
    private static final String DEFAULT_RESULTS_FILE_PATH = "results.txt";

    public static void main(String[] args) {

        ArgumentParser argumentParser = new ArgumentParser(args);

        int generations = argumentParser.getInt("--generations", DEFAULT_GENERATIONS);
        int populationSize = argumentParser.getInt("--populationSize", DEFAULT_POPULATION_SIZE);
        double mutationRate = argumentParser.getDouble("--mutationRate", DEFAULT_MUTATION_RATE);
        String dataFilePath = argumentParser.getString("--dataFilePath", DEFAULT_DATA_FILE_PATH);
        String resultsFilePath = argumentParser.getString("--resultsFilePath", DEFAULT_RESULTS_FILE_PATH);

        if (argumentParser.contains("--help") || argumentParser.contains("-h")) {
            System.out.println("Usage: java -jar ConferenceMatchmaker.jar [options]");
            System.out.println("Options:");
            System.out.println("  --generations <int>       The number of generations to run the genetic algorithm (default: 500)");
            System.out.println("  --populationSize <int>    The size of the population for each generation (default: 40)");
            System.out.println("  --mutationRate <double>   The probability of mutation for each gene in the population (default: 0.02)");
            System.out.println("  --dataFilePath <string>   The path to the file containing participant data (default: data.txt)");
            System.out.println("  --resultsFilePath <string> The path to the file where results will be written (default: results.txt)");
            System.exit(0);
        }

        System.out.println("Generations: " + generations);
        System.out.println("Population Size: " + populationSize);
        System.out.println("Mutation Rate: " + mutationRate);
        System.out.println("Data File Path: " + dataFilePath);
        System.out.println("Results File Path: " + resultsFilePath);

        runGA(dataFilePath, generations, populationSize, mutationRate, resultsFilePath);
    }
}
