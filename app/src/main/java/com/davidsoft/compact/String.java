package com.davidsoft.compact;

public final class String {

    /**
     * Returns a string whose value is the concatenation of this
     * string repeated {@code count} times.
     * <p>
     * If this string is empty or count is zero then the empty
     * string is returned.
     *
     * @param   count number of times to repeat
     *
     * @return  A string composed of this string repeated
     *          {@code count} times or the empty string if this
     *          string is empty or count is zero
     *
     * @throws  IllegalArgumentException if the {@code count} is
     *          negative.
     */
    public static java.lang.String repeat(java.lang.String src, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count is negative: " + count);
        }
        if (count == 1) {
            return src;
        }
        if (src.length() == 0 || count == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(src.length() * count);
        for (int i = 0; i < count; i++) {
            builder.append(src);
        }
        return builder.toString();
    }
}
