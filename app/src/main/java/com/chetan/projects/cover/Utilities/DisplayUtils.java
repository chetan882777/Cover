package com.chetan.projects.cover.Utilities;

public class DisplayUtils {
    private static int displayWidth;
    private static int displayHeight;

    public static void setDisplayResolution(int width, int height){
        displayHeight = height;
        displayWidth = width;
    }

    public static int getDisplayWidth() {
        return displayWidth;
    }

    public static int getDisplayHeight() {
        return displayHeight;
    }
}
