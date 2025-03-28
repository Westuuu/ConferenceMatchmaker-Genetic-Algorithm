package ConferenceMatchmaker;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code ArgumentParser} class provides a way to parse command-line arguments.
 * It takes an array of arguments (in key-value pairs) and stores them in a map.
 * Provides utility methods to retrieve integer, double, or string values from the parsed arguments.
 */
public class ArgumentParser {
    private final Map<String, String> argMap = new HashMap<>();

    /**
     * Constructs an {@code ArgumentParser} object and parses the given command-line arguments.
     * The arguments can be either key-value pairs (e.g., "--key value") or standalone flags (e.g., "--help").
     *
     * <p>For key-value pairs, the key is expected to be a string starting with "--" or "-",
     * followed by a value that does not start with "--" or "-". The key and value are then stored
     * in a map for later retrieval. For standalone flags (such as "--help"), the key is stored with
     * a {@code null} value to indicate that it is a flag.
     *
     * <p>This constructor assumes that the arguments array contains valid arguments in the correct format.
     * If a key is followed by another key (e.g., two flags in succession), the first key is treated as a flag.
     *
     * @param args an array of command-line arguments, where each argument may be either a flag or a key-value pair.
     */
    public ArgumentParser(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String key = args[i];

            if (key.startsWith("--") || key.startsWith("-")) {
                if (i + 1 < args.length && !args[i + 1].startsWith("--") && !args[i + 1].startsWith("-")) {
                    String value = args[i + 1];
                    argMap.put(key, value);
                    i++;
                } else {
                    argMap.put(key, null);
                }
            }
        }
    }

    /**
     * Checks whether the specified key (e.g., flag) is present in the arguments.
     *
     * @param key the argument key to check for (e.g., "--help").
     * @return {@code true} if the key is present, {@code false} otherwise.
     */
    public boolean contains(String key) {
        return argMap.containsKey(key);
    }

    /**
     * Retrieves the integer value associated with the specified key.
     * If the key is not found or the value cannot be parsed as an integer, the default value is returned.
     *
     * @param key          the argument key to look for.
     * @param defaultValue the default value to return if the key is not found or the value is invalid.
     * @return the integer value associated with the key, or the default value if not found or invalid.
     */
    public int getInt(String key, int defaultValue) {
        if (argMap.containsKey(key)) {
            try {
                return Integer.parseInt(argMap.get(key));
            } catch (NumberFormatException e) {
                System.err.println("Invalid integer for " + key + ". Using default: " + defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * Retrieves the double value associated with the specified key.
     * If the key is not found or the value cannot be parsed as a double, the default value is returned.
     *
     * @param key          the argument key to look for.
     * @param defaultValue the default value to return if the key is not found or the value is invalid.
     * @return the double value associated with the key, or the default value if not found or invalid.
     */
    public double getDouble(String key, double defaultValue) {
        if (argMap.containsKey(key)) {
            try {
                return Double.parseDouble(argMap.get(key));
            } catch (NumberFormatException e) {
                System.err.println("Invalid double for " + key + ". Using default: " + defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * Retrieves the string value associated with the specified key.
     * If the key is not found, the default value is returned.
     *
     * @param key          the argument key to look for.
     * @param defaultValue the default value to return if the key is not found.
     * @return the string value associated with the key, or the default value if not found.
     */
    public String getString(String key, String defaultValue) {
        return argMap.getOrDefault(key, defaultValue);
    }
}
