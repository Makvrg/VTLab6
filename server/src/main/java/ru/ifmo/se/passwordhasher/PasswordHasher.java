package ru.ifmo.se.passwordhasher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordHasher {

    private static final String PEPPER = "D_9f34Ixm$xV&";
    private static final int HASHING_ITERATIONS = 100;

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt)
            throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] pepperBytes = PEPPER.getBytes();
        byte[] passwordBytes = password.getBytes();
        byte[] saltBytes = Base64.getDecoder().decode(salt);
        byte[] combined = new byte[pepperBytes.length + passwordBytes.length + saltBytes.length];
        System.arraycopy(pepperBytes, 0, combined, 0, pepperBytes.length);
        System.arraycopy(passwordBytes, 0, combined, pepperBytes.length, passwordBytes.length);
        System.arraycopy(
                saltBytes, 0,
                combined, pepperBytes.length + passwordBytes.length,
                saltBytes.length
        );
        byte[] hash = md.digest(combined);
        for (int i = 0; i < HASHING_ITERATIONS - 1; i++) {
            hash = md.digest(hash);
        }
        return Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(String enteredRawPassword,
                                         String salt,
                                         String storedHash)
            throws NoSuchAlgorithmException {
        String computedHash = hashPassword(enteredRawPassword, salt);
        return computedHash.equals(storedHash);
    }
}