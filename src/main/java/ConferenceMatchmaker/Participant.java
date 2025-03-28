package ConferenceMatchmaker;

import java.util.Set;

/**
 * The {@code Participant} class represents a participant in the conference matchmaking system.
 * Each participant has a unique ID, a set of possessed attributes, and a set of desired attributes.
 * Participants are stored in a static list for easy access.
 */
public class Participant {
    private final int id;
    private final Set<String> possessedAttributes;
    private final Set<String> desiredAttributes;


    /**
     * Constructs a new {@code Participant} with the specified ID, possessed attributes, and desired attributes.
     * The participant is automatically added to the static list of all participants.
     *
     * @param id                the unique identifier of the participant.
     * @param possessedAttributes a set of attributes that the participant possesses.
     * @param desiredAttributes a set of attributes that the participant is looking for in others.
     */
    public Participant(int id, Set<String> possessedAttributes, Set<String> desiredAttributes) {
        this.id = id;
        this.possessedAttributes = possessedAttributes;
        this.desiredAttributes = desiredAttributes;
    }

    /**
     * Returns a string representation of the {@code Participant} object.
     *
     * @return a string containing the participant's ID, possessed attributes, and desired attributes.
     */
    @Override
    public String toString() {
        return "Participant{" +
                "id=" + id +
                ", possessedAttributes=" + possessedAttributes +
                ", desiredAttributes=" + desiredAttributes +
                '}';
    }

    /**
     *
     * @return the set of attributes the participant possesses.
     */
    public Set<String> getPossessedAttributes() {
        return possessedAttributes;
    }

    /**
     *
     * @return the set of attributes the participant is looking for.
     */
    public Set<String> getDesiredAttributes() {
        return desiredAttributes;
    }

    /**
     *
     * @return the ID of the participant.
     */
    public int getId() {
        return id;
    }

}
