package ConferenceMatchmaker;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * The {@code ResultsWriter} class is responsible for writing the matchmaking results to a file.
 * It allows writing individual participant recommendations as well as the overall global satisfaction percentage.
 */
public class ResultsWriter implements AutoCloseable {
    private final String filePath;
    private BufferedWriter writer;

    /**
     * Constructs a {@code ResultsWriter} for writing results to the specified file.
     * If the file exists, it will be overwritten.
     *
     * @param filePath the path to the file where results will be written.
     * @throws IOException if an I/O error occurs while opening the file.
     */
    public ResultsWriter(String filePath) throws IOException {
        this.filePath = filePath;
        this.writer = new BufferedWriter(new FileWriter(filePath, false));
    }

    /**
     * Initializes a {@code ResultsWriter} instance that writes results to a file.
     *
     * <p>This constructor creates a {@link BufferedWriter} to write results to the file specified by
     * the {@code filePath}. The default file path is set to "results.txt", and the file is overwritten
     * if it already exists.
     *
     * @throws IOException if an I/O error occurs when creating or accessing the file
     */
    public ResultsWriter() throws IOException {
        this.filePath = "results.txt";
        this.writer = new BufferedWriter(new FileWriter(filePath, false));
    }

    /**
     * Writes the global satisfaction percentage and detailed participant recommendations to the output.
     *
     * <p>This method writes the global satisfaction percentage followed by the recommendation details
     * for each participant, including the participant's ID, a list of recommended participants' IDs,
     * and the calculated satisfaction percentage. The results are both printed to the console and
     * written to the appropriate output file using helper methods.
     *
     * @param globalSatisfactionPercentage  the overall satisfaction percentage across all participants
     * @param participants                  the list of all participants in the conference
     * @param recommendationsMap            a map containing each participant and their recommended participants
     * @throws IOException if an I/O error occurs while writing results to the output
     */
    public void writeResults(double globalSatisfactionPercentage,
                                     List<Participant> participants,
                                     Map<Participant, List<Participant>> recommendationsMap) throws IOException {
        writeGlobalSatisfaction(globalSatisfactionPercentage);

        writeHeaders("Participant Id", "Recommendations", "Satisfaction Percentage");

        for (Participant participant : participants) {
            List<Participant> bestRecommendations = recommendationsMap.get(participant);
            double satisfactionPercentage = FitnessScore.calculateSatisfactionPercentage(participant, bestRecommendations);

            List<Integer> recommendedIds = bestRecommendations.stream()
                    .map(Participant::getId)
                    .distinct()
                    .collect(Collectors.toList());

            writeParticipantResult(participant.getId(), recommendedIds, satisfactionPercentage);
        }
    }

    /**
     * Writes the best recommendations for a target participant to the console.
     *
     * <p>This method displays the target participant's ID, their satisfaction percentage, and the
     * list of recommended participant IDs in a formatted output.
     *
     * @param bestRecommendations the list of participants that are the best recommendations for the target participant
     * @param targetParticipant the participant for whom the recommendations are generated
     */
    public static void writeResultsToConsole(List<Participant> bestRecommendations, Participant targetParticipant) {
        List<Integer> recommendedIds = bestRecommendations.stream()
                .map(Participant::getId)
                .distinct()
                .collect(Collectors.toList());

        System.out.printf("Participant ID: %d - Total Satisfaction: %.2f%% - Recommended IDs: %s\n",
                targetParticipant.getId(), FitnessScore.calculateSatisfactionPercentage(targetParticipant, bestRecommendations), recommendedIds);
    }

    /**
     * Writes a matchmaking result for a specific participant.
     * The participant's ID, the list of recommended participant IDs, and their satisfaction percentage are written to the file.
     *
     * @param participantId          the ID of the participant.
     * @param recommendedIds         a list of recommended participant IDs.
     * @param satisfactionPercentage the satisfaction percentage for the participant's recommendations.
     */
    private void writeParticipantResult(int participantId, List<Integer> recommendedIds, double satisfactionPercentage) {
        try {
            String line = String.format("%d\t%s\t%.2f%%\n",
                    participantId,
                    recommendedIds.stream().map(String::valueOf).collect(Collectors.joining(",")),
                    satisfactionPercentage);
            writer.write(line);
        } catch (IOException e) {
            System.err.println("Error writing participant result to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Writes the global satisfaction percentage for all participants.
     *
     * @param globalSatisfactionPercentage the overall satisfaction percentage for the matchmaking results.
     */
    private void writeGlobalSatisfaction(double globalSatisfactionPercentage) {
        try {
            writer.write(String.format("Global Satisfaction: %.2f%%\n", globalSatisfactionPercentage));
        } catch (IOException e) {
            System.err.println("Error writing global satisfaction to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Writes the column headers for the matchmaking results.
     *
     * @param headers the column headers to write (e.g., "participantid", "recommended participants", "satisfactionpercentage").
     * @throws IOException if an I/O error occurs while writing the headers.
     */
    private void writeHeaders(String... headers) {
        try {
            String headerLine = String.join("\t", headers) + "\n";
            writer.write(headerLine);
        } catch (IOException e) {
            System.err.println("Error writing headers to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Closes the file writer to ensure that all data is properly saved.
     */
    @Override
    public void close() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                System.err.println("Error closing the file writer: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
