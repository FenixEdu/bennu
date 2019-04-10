package org.fenixedu.bennu.io.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Utility to generate Cryptographically Secure URL-Safe Random Identifiers.
 *
 * Using large enough lengths (typically larger that 16 characters), these identifiers are suitable for use as unique object IDs
 * (effectively replacing database-generated identifiers) and as nounces/secret values.
 *
 * The methods return an identifier with the request length, consisting of equiprobably-distributed alphanumeric characters.
 */
public class IdGenerator {

    /**
     * Returns a randomly generated identifier with the default length for a secure id.
     *
     * @return A randomly generated identifier
     */
    public static String generateSecureId() {
        return generateSecureId(32);
    }

    private static final Pattern REMOVE_PATTERN = Pattern.compile("[-_]");

    /**
     * Returns a randomly generated identifier with the provided length.
     *
     * @param length
     *         The length of the identifier to return
     * @return A randomly generated identifier
     * @throws IllegalArgumentException
     *         If <code>length</code> is not a positive integer
     */
    public static String generateSecureId(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Invalid length: " + length);
        }
        SecureRandom random = new SecureRandom();
        // Request some extra bytes to account for '-' and '_' characters
        byte[] bytes = new byte[length + 5];
        while (true) {
            random.nextBytes(bytes);

            // Remove all invalid characters
            String str = REMOVE_PATTERN.matcher(Base64.getUrlEncoder().encodeToString(bytes)).replaceAll("");

            // If we have the necessary length, trim and return it
            if (str.length() >= length) {
                return str.substring(0, length);
            }
        }
    }

}