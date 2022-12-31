package utils;

/**
 * Util class for general and common functions
 */
public final class Utils {
    public static int TIME_OUT_TIME_IN_MINUTES = 10;
    private Utils() {
    }

    public static boolean isBlank(CharSequence cs) {
        if (cs == null || cs.length() == 0) {
            return true;
        }
        return cs.chars().allMatch(Character::isWhitespace);
    }
}
