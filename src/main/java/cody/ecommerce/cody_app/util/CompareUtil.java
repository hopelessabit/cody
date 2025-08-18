package cody.ecommerce.cody_app.util;

public class CompareUtil {

    /**
     * Returns the first non-null value using Objects.requireNonNullElse.
     *
     * @param <T>            The type of the values.
     * @param value          The primary value to check.
     * @param fallbackValue  The fallback value if the primary is null.
     * @return The non-null value.
     */
    public static <T> T compare(T value, T fallbackValue) {
        if (value != null)
            return value;
        else return fallbackValue;
    }
}
