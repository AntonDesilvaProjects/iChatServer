package com.ichat.server;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class UserColor {
    private static List<String> SOFT_COLORS = Arrays.asList(
                "#EEFFFF",
                "#EEEEFF",
                "#DDEEEE",
                "#DDDDEE",
                "#DDFFFF",
                "#DDDDFF",
                "#CCDDDD",
                "#CCCCDD",
                "#CCEEEE",
                "#CCCCEE",
                "#CCFFFF",
                "#CCCCFF",
                "#EEEEEE",
                "#FFEEFF",
                "#EEFFEE",
                "#EEDDEE",
                "#DDEEDD",
                "#FFDDFF",
                "#DDFFDD",
                "#DDCCDD",
                "#CCDDCC",
                "#EECCEE",
                "#CCEECC",
                "#FFCCFF",
                "#CCFFCC",
                "#DDDDDD",
                "#FFFFEE",
                "#FFEEEE",
                "#EEEEDD",
                "#EEDDDD",
                "#FFFFDD",
                "#FFDDDD",
                "#DDDDCC",
                "#DDCCCC",
                "#EEEECC",
                "#EECCCC",
                "#FFFFCC",
                "#FFCCCC");
    //make start point random so we get random color every time!
    private static int currentColor = new Random().nextInt(SOFT_COLORS.size());
    public static String getNextAvailableColor() {
        return SOFT_COLORS.get(currentColor++ % SOFT_COLORS.size());
    }
}
