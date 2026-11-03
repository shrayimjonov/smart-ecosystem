package uz.uzkassa.smartpossupply.utils;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Component
public class OtpEncryptionUtils {

    private static final String OTP_PREFIX = "MDR:";
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private final SecretKey secretKey;

    public OtpEncryptionUtils(ApplicationProperties applicationProperties) throws Exception {
        byte[] keyBytes = MessageDigest.getInstance("SHA-256")
                .digest(applicationProperties.getOtpConfig().getSecretKey().getBytes());
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    public String encrypt(String otp) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH, iv));
            byte[] encrypted = cipher.doFinal((OTP_PREFIX + otp).getBytes());

            byte[] combined = new byte[IV_LENGTH + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
            System.arraycopy(encrypted, 0, combined, IV_LENGTH, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("OTP encryption failed", e);
        }
    }

    public boolean matches(String rawOtp, String encryptedOtp) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedOtp);
            byte[] iv = Arrays.copyOfRange(combined, 0, IV_LENGTH);
            byte[] cipherText = Arrays.copyOfRange(combined, IV_LENGTH, combined.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH, iv));
            String decrypted = new String(cipher.doFinal(cipherText));

            return decrypted.equals(OTP_PREFIX + rawOtp);
        } catch (Exception e) {
            return false;
        }
    }
}