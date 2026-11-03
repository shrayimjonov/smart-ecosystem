package uz.uzkassa.cts.security;

import org.apache.commons.codec.digest.Md5Crypt;

import java.nio.charset.StandardCharsets;
import java.util.Random;


public final class Md5CryptUtils {

    public static String encodePassword(String rawPassword) {
        // Generate a random salt (8 characters) or use a fixed one if needed
        String salt = generateRandomSalt();

        // Encode the password using MD5-Crypt
        String encodedPassword = Md5Crypt.md5Crypt(rawPassword.getBytes(StandardCharsets.UTF_8), salt);

        return encodedPassword;  // This will return the encoded password in the format "$1$salt$hash"
    }

    // Generate a random salt for the password
    private static String generateRandomSalt() {
        Random random = new Random();
        StringBuilder salt = new StringBuilder();

        // Append $1$ to the salt to indicate MD5-Crypt
        salt.append("$1$");

        // Generate random 8-character salt
        for (int i = 0; i < 8; i++) {
            salt.append((char) (random.nextInt(26) + 'a'));  // Append random lowercase letters
        }

        salt.append("$");  // MD5-Crypt salts should end with a $
        return salt.toString();
    }

}
