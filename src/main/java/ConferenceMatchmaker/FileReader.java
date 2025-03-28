package ConferenceMatchmaker;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The FileReader class provides methods for reading participant data from a file.
 * It parses each line of the file to create Participant objects, which contain
 * the participant's ID, possessed attributes, and desired attributes.
 */
public class FileReader {
    private static final Logger LOGGER = Logger.getLogger(FileReader.class.getName());
    private static List<Participant> participants;

    /**
     * Reads the participants from the specified file path. Each line in the file should
     * contain a participant's information formatted as:
     * <pre>
     * ID [tab or space] possessedAttributes [tab or space] desiredAttributes
     * </pre>
     * where possessedAttributes and desiredAttributes are comma-separated lists.
     *
     * @param path the path to the file containing participant data
     * @return a list of Participant objects parsed from the file
     */
    public static List<Participant> readParticipants(String path) {
        List<Participant> participantList = new ArrayList<>();

        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            lines.forEach(line -> {
                try {
                    String[] parts = line.split("[\\t ]+");
                    if (parts.length < 3) {
                        throw new IllegalArgumentException("Invalid data format: Expected at least 3 fields (ID, possessedAttributes, desiredAttributes).");
                    }

                    int id;
                    try {
                        id = Integer.parseInt(parts[0].trim());
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid ID format: " + parts[0] + " is not a valid integer.");
                    }

                    String[] possessedAttributes = parts[1].trim().split(",");
                    String[] desiredAttributes = parts[2].trim().split(",");

                    if (possessedAttributes.length == 0 || desiredAttributes.length == 0) {
                        throw new IllegalArgumentException("Attributes cannot be empty.");
                    }

                    participantList.add(new Participant(id, Set.of(possessedAttributes), Set.of(desiredAttributes)));

                } catch (IllegalArgumentException e) {
                    LOGGER.log(Level.WARNING, "Skipping line due to data error: {0}. Reason: {1}", new Object[]{line, e.getMessage()});
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to read participants from file: {0}", e.getMessage());
            System.exit(1);
        }
        
        participants = participantList;
        return participants;
    }

    /**
     * Gets the list of participants by reading from the specified file path.
     * It calls the readParticipants method to parse the file and load the participants.
     *
     * @param filePath the path to the file containing participant data
     * @return a list of Participant objects parsed from the file
     */
    public List<Participant> getParticipants(String filePath) {
        participants = readParticipants(filePath);
        return participants;
    }
}
