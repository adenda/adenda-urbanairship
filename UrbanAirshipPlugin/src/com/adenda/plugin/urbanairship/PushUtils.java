package com.adenda.plugin.urbanairship;

import android.graphics.Color;

import com.urbanairship.push.PushMessage;

public class PushUtils {

    /**
     * Parses a color from the PushMessage.
     *
     * @param message The push message.
     * @param extraName The extra's name.
     * @param defaultValue The default value.
     * @return The parsed color if the extra exists, otherwise the default value.
     */
    public static int parseColorExtra(PushMessage message, String extraName, int defaultValue) {
        if (message == null) {
            return defaultValue;
        }

        String color = message.getPushBundle().getString(extraName);
        if (color == null) {
            return defaultValue;
        }

        return Color.parseColor(color);
    }

    /**
     * Parses a boolean from the PushMessage.
     *
     * @param message The push message.
     * @param extraName The extra's name.
     * @param defaultValue The default value.
     * @return The parsed boolean if the extra exists, otherwise the default value.
     */
    public static boolean parseBooleanExtra(PushMessage message, String extraName, boolean defaultValue) {
        if (message == null) {
            return defaultValue;
        }

        String boolString = message.getPushBundle().getString(extraName);
        if (boolString == null) {
            return defaultValue;
        }

        return Boolean.parseBoolean(boolString);
    }

}
