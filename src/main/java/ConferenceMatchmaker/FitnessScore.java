package ConferenceMatchmaker;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FitnessScore {

    /**
     * Calculates the fitness score of a list of participant suggestions based on how well they match
     * the desired attributes. The fitness score is the sum of the number of matching attributes for
     * each suggestion.
     *
     * @param suggestions the list of participant suggestions to evaluate
     * @param desiredAttributes the set of desired attributes that the target participant seeks
     * @return the total fitness score representing the sum of matching attributes
     */
    public static int fitness(List<Participant> suggestions, Set<String> desiredAttributes) {
        return suggestions.stream()
                .mapToInt(suggestion -> {
                    Set<String> possessedAttributes = new HashSet<>(suggestion.getPossessedAttributes());
                    possessedAttributes.retainAll(desiredAttributes);
                    return possessedAttributes.size();
                })
                .sum();
    }

    /**
     * Calculates the fitness score for a list of recommendations in relation to a target participant's
     * desired attributes.
     *
     * @param recommendations the list of participant recommendations to evaluate
     * @param targetParticipant the target participant for whom recommendations are being made
     * @return the fitness score indicating how well the recommendations match the target's desires
     */
    private static int calculateFitnessForList(List<Participant> recommendations, Participant targetParticipant) {
        return fitness(recommendations, new HashSet<>(targetParticipant.getDesiredAttributes()));
    }

    /**
     * Calculates the global satisfaction percentage for all participants based on their recommendations.

     * The global satisfaction is determined by summing up the total satisfaction scores of all participants
     * and dividing by the maximum possible satisfaction for all participants. The maximum satisfaction for
     * each participant is computed as the product of the number of desired attributes and the number of
     * recommendations for that participant.
     *
     * @param participants       a list of {@link Participant} objects representing all the participants in the matchmaking process.
     * @param recommendationsMap a map where each {@link Participant} is associated with a list of their recommended participants.
     * @return the global satisfaction percentage as a double.
     */
    public static double calculateGlobalSatisfaction(List<Participant> participants, Map<Participant, List<Participant>> recommendationsMap) {
        int totalSatisfactionAllParticipants = 0;
        int maxSatisfactionAllParticipants = 0;

        for (Participant participant : participants) {
            List<Participant> bestRecommendations = recommendationsMap.get(participant);

            int totalSatisfaction = calculateFitnessForList(bestRecommendations, participant);
            int maxSatisfaction = participant.getDesiredAttributes().size() * bestRecommendations.size();

            totalSatisfactionAllParticipants += totalSatisfaction;
            maxSatisfactionAllParticipants += maxSatisfaction;
        }

        return (double) totalSatisfactionAllParticipants / maxSatisfactionAllParticipants * 100;
    }

    /**
     * Calculates the satisfaction percentage for a given participant based on their best recommendations.
     * The satisfaction percentage is calculated as the ratio of the total satisfaction score
     * (determined by the fitness of the recommended participants for the given participant)
     * to the maximum possible satisfaction score.
     *
     * @param participant        the {@link Participant} for whom the satisfaction percentage is being calculated.
     * @param bestRecommendations a list of {@link Participant} objects representing the best recommendations for the participant.
     * @return the satisfaction percentage as a double.
     */
    public static double calculateSatisfactionPercentage(Participant participant, List<Participant> bestRecommendations) {
        int totalSatisfaction = calculateFitnessForList(bestRecommendations, participant);
        int maxSatisfaction = participant.getDesiredAttributes().size() * bestRecommendations.size();

        return (double) totalSatisfaction / maxSatisfaction * 100;
    }

}
