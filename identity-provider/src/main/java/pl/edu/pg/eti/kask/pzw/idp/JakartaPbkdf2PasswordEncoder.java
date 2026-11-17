package pl.edu.pg.eti.kask.pzw.idp;

import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Password hashes in the format of Jakarta Security {@code Pbkdf2PasswordHash}, used by the PZW application when it
 * stores users: {@code algorithm:iterations:base64(salt):base64(hash)}. Hashes created by the application can be
 * verified here, so both share one user database.
 */
public class JakartaPbkdf2PasswordEncoder implements PasswordEncoder {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    private static final int ITERATIONS = 2048;

    private static final int SALT_SIZE_BYTES = 32;

    private static final int KEY_SIZE_BYTES = 32;

    private final SecureRandom random = new SecureRandom();

    @Override
    public String encode(CharSequence rawPassword) {
        byte[] salt = new byte[SALT_SIZE_BYTES];
        random.nextBytes(salt);
        byte[] hash = hash(ALGORITHM, rawPassword, salt, ITERATIONS, KEY_SIZE_BYTES);
        return String.join(":", ALGORITHM, String.valueOf(ITERATIONS),
                Base64.getEncoder().encodeToString(salt), Base64.getEncoder().encodeToString(hash));
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null) {
            return false;
        }
        String[] parts = encodedPassword.split(":");
        if (parts.length != 4) {
            return false;
        }
        try {
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            byte[] actual = hash(parts[0], rawPassword, salt, Integer.parseInt(parts[1]), expected.length);
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] hash(String algorithm, CharSequence password, byte[] salt, int iterations, int keySize) {
        PBEKeySpec spec = new PBEKeySpec(password.toString().toCharArray(), salt, iterations, keySize * 8);
        try {
            return SecretKeyFactory.getInstance(algorithm).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalArgumentException("Cannot hash with " + algorithm, e);
        } finally {
            spec.clearPassword();
        }
    }

}
