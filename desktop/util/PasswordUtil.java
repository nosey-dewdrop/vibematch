package util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/*
 * Handles password hashing. We never store the real password. Instead we make a
 * random salt for each user, then store SHA-256 of (salt + password). When they
 * log in we hash what they typed with their salt and compare.
 *
 * (SHA-256 isnt the strongest choice out there but with a per user salt its
 * totally fine for a project like this.)
 */
public class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    // make a new random salt, returned as a base64 string
    public static String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // hash the password together with the salt
    public static String hash(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String combined = salt + password;
            byte[] digest = md.digest(combined.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            // sha-256 is always available so this shouldnt happen
            throw new RuntimeException("hashing failed", e);
        }
    }

    // check a typed password against the stored hash
    public static boolean verify(String password, String salt, String expectedHash) {
        String actual = hash(password, salt);
        return actual.equals(expectedHash);
    }

    private PasswordUtil() {
    }
}
