package cody.ecommerce.cody_app.util;


import java.security.SecureRandom;

public class IdUtil {
    private static final int DEFAULT_LENGTH = 4;
    private static final int CHARACTERS_LENGTH = 62;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateId() {
        return generateId(DEFAULT_LENGTH);
    }

    public static String generateId(int length) {
        StringBuilder id = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            id.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS_LENGTH)));
        }
        id.append(TimeZoneUtil.getCurrentDateTime());
        return id.toString();
    }
}