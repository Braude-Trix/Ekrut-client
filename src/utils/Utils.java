package utils;

/**
 * Util class for general and common functions
 */
public final class Utils {
    private Utils() {
    }

    public static boolean isBlank(CharSequence cs) {
        if (cs == null || cs.length() == 0) {
            return true;
        }
        return cs.chars().allMatch(Character::isWhitespace);
    }
}
