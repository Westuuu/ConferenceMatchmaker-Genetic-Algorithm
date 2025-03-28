package ConferenceMatchmaker;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Matchmaker class provides methods to generate and optimize participant recommendations
 * for a conference using a genetic algorithm. The algorithm finds the best matches for participants
 * based on their desired attributes.
 */
public class Matchmaker {
    private static final int RECOMMENDATION_COUNT = 5;

    /**
     * Initializes a population of potential participant recommendations for the genetic algorithm.
     * Each recommendation is a list of participants (excluding the target participant).
     *
     * @param participants the list of all participants in the conference
     * @param populationSize the size of the population to generate
     * @param targetedParticipant the participant for whom recommendations are being generated
     * @return a list of participant recommendation lists forming the initial population
     */
    private static List<List<Participant>> initializePopulation(List<Participant> participants, int populationSize, Participant targetedParticipant) {
        return Stream.generate(() -> {
                    List<Participant> shuffledParticipants = new ArrayList<>(participants);
                    Collections.shuffle(shuffledParticipants);

                    return shuffledParticipants.stream()
                            .filter(p -> !p.equals(targetedParticipant))
                            .limit(RECOMMENDATION_COUNT)
                            .collect(Collectors.toCollection(ArrayList::new));
                })
                .limit(populationSize)
                .collect(Collectors.toList());
    }

    /**
     * Selects a parent list from the population using a fitness-based selection method known as
     * tournament selection. Two random individuals are selected, and the one with the higher fitness
     * score is chosen.
     *
     * @param population the current population of participant recommendation lists
     * @param fitnesses the list of fitness scores corresponding to the population
     * @return a selected parent list of participants from the population
     */
    private static List<Participant> selectParent(List<List<Participant>> population, List<Integer> fitnesses) {
        Random random = new Random();
        int index1 = random.nextInt(population.size());
        int index2;
        do {
            index2 = random.nextInt(population.size());
        } while (index1 == index2);

        return fitnesses.get(index1) > fitnesses.get(index2) ? population.get(index1) : population.get(index2);
    }

    /**
     * Performs crossover between two parent lists to produce a child list. The child inherits a portion
     * of its attributes from each parent, with a crossover point determining how the inheritance is split.
     *
     * @param parent1 the first parent list of participants
     * @param parent2 the second parent list of participants
     * @return a child list of participants formed by combining elements from both parents
     */
    private static List<Participant> crossover(List<Participant> parent1, List<Participant> parent2) {
        Random random = new Random();
        int crossoverPoint = random.nextInt(RECOMMENDATION_COUNT);

        Set<Participant> childSet = parent1.stream()
                .limit(crossoverPoint)
                .collect(Collectors.toCollection(HashSet::new));

        parent2.stream()
                .filter(p -> childSet.size() < RECOMMENDATION_COUNT)
                .forEach(childSet::add);

        return new ArrayList<>(childSet);
    }

    /**
     * Mutates an individual recommendation list by randomly replacing one participant within a list of recommendations with another.
     * Mutation occurs with a certain probability defined by the mutation rate.
     *
     * @param individual the individual list of participants to mutate
     * @param participants the list of all participants available for selection
     * @param targetParticipant the participant for whom recommendations are being made
     * @param mutationRate the probability of mutation occurring (between 0 and 1)
     */
    private static void mutate(List<Participant> individual, List<Participant> participants, Participant targetParticipant, double mutationRate) {
        Random random = new Random();
        if (random.nextDouble() < mutationRate) {
            int mutationPoint = random.nextInt(RECOMMENDATION_COUNT);
            Participant newParticipant;
            do {
                newParticipant = participants.get(random.nextInt(participants.size()));
            } while (individual.contains(newParticipant) || newParticipant.equals(targetParticipant));
            individual.set(mutationPoint, newParticipant);
        }
    }

    /**
     * Runs a genetic algorithm to find the best set of recommendations for a target participant.
     *
     * <p>The algorithm evolves a population of recommendations over several generations using
     * selection, crossover, and mutation to optimize participant matches. After each participant's
     * recommendations are generated, the best solution is printed to the console.
     *
     * @param participants the list of all participants in the conference
     * @param targetParticipant the participant for whom recommendations are being generated
     * @param generations the number of generations the algorithm should run for
     * @param popSize the size of the population to maintain during the evolution process
     * @param mutationRate the probability of mutation during the evolution process
     * @return a list of the best recommended participants for the target participant after the genetic algorithm finishes
     */
    private static List<Participant> geneticAlgorithm(List<Participant> participants, Participant targetParticipant, int generations, int popSize, double mutationRate) {
        List<List<Participant>> population = initializePopulation(participants, popSize, targetParticipant);
        AtomicReference<Integer> bestFitnessScore = new AtomicReference<>(Integer.MIN_VALUE);
        AtomicReference<List<Participant>> bestSolution = new AtomicReference<>(null);

        for (int generation = 0; generation < generations; generation++) {
            List<Integer> fitnesses = population.stream()
                    .map(individual -> {
                        int fitnessScore = FitnessScore.fitness(individual, new HashSet<>(targetParticipant.getDesiredAttributes()));
                        bestFitnessScore.updateAndGet(currentBest -> {
                            if (fitnessScore > currentBest) {
                                bestSolution.set(new ArrayList<>(individual));
                                return fitnessScore;
                            }
                            return currentBest;
                        });
                        return fitnessScore;
                    })
                    .collect(Collectors.toList());

            List<List<Participant>> newPopulation = new ArrayList<>();
            newPopulation.add(bestSolution.get());

            List<List<Participant>> finalPopulation = population;
            newPopulation.addAll(
                    Stream.generate(() -> {
                                List<Participant> parent1 = selectParent(finalPopulation, fitnesses);
                                List<Participant> parent2 = selectParent(finalPopulation, fitnesses);
                                List<Participant> child = crossover(parent1, parent2);
                                mutate(child, participants, targetParticipant, mutationRate);
                                return child;
                            })
                            .limit(popSize - 1)
                            .toList()
            );

            population = newPopulation;
        }

        List<Participant> bestRecommendations = bestSolution.get();

        ResultsWriter.writeResultsToConsole(bestRecommendations, targetParticipant);

        return bestSolution.get();
    }

    /**
     * Generates recommendations for each participant using a genetic algorithm.
     * For each participant in the provided list, the method invokes a genetic algorithm
     * to determine a list of recommended participants based on the specified parameters.
     * The results are stored in a map where each participant is associated with their
     * corresponding list of recommended participants.
     *
     * @param participants      a list of {@link Participant} objects representing all participants.
     * @param GENERATIONS       the number of generations to run the genetic algorithm.
     * @param POPULATION_SIZE   the size of the population used in the genetic algorithm.
     * @param MUTATION_RATE     the mutation rate for the genetic algorithm, affecting the diversity of the population.
     * @return a map where each {@link Participant} is associated with a list of recommended {@link Participant} objects.
     */
    public static Map<Participant, List<Participant>> generateRecommendationList(List<Participant> participants,
                                                                               int GENERATIONS,
                                                                               int POPULATION_SIZE,
                                                                               double MUTATION_RATE) {
        Map<Participant, List<Participant>> recommendationsMap = new HashMap<>();

        for (Participant participant : participants) {
            List<Participant> bestRecommendations = geneticAlgorithm(participants, participant, GENERATIONS, POPULATION_SIZE, MUTATION_RATE);
            recommendationsMap.put(participant, bestRecommendations);
        }

        return recommendationsMap;
    }

    /**
     * Runs the genetic algorithm for all participants in the conference, evaluating and writing
     * the satisfaction percentage for each participant as well as the global satisfaction to a file.
     *
     * @param filepath the file path to the list of participants
     * @param resultFilePath the file path to write the results
     * @param GENERATIONS the number of generations to run for each participant
     * @param POPULATION_SIZE the size of the population for each run
     * @param MUTATION_RATE the mutation rate to use during the genetic algorithm
     */
    public static void runGA(String filepath, int GENERATIONS, int POPULATION_SIZE, double MUTATION_RATE, String resultFilePath) {
        FileReader fileReader = new FileReader();
        List<Participant> participants = fileReader.getParticipants(filepath);

        Map<Participant, List<Participant>> recommendationsMap = generateRecommendationList(participants, GENERATIONS, POPULATION_SIZE, MUTATION_RATE);

        double globalSatisfactionPercentage = FitnessScore.calculateGlobalSatisfaction(participants, recommendationsMap);

        try (ResultsWriter resultsWriter = new ResultsWriter(resultFilePath)) {
            resultsWriter.writeResults(globalSatisfactionPercentage, participants, recommendationsMap);
        } catch (IOException e) {
            System.err.println("Error writing results: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.printf("Global Satisfaction: %.2f%%\n", globalSatisfactionPercentage);
    }
}
