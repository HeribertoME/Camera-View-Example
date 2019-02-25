package com.hmelizarraraz.cameraview;

import android.support.annotation.Nullable;

public class ResultHolder {

    private static byte[] image;
    private static long timeToCallback;

    public static void setImage(@Nullable byte[] image) {
        ResultHolder.image = image;
    }

    @Nullable
    public static byte[] getImage() {
        return image;
    }


    public static void setTimeToCallback(long timeToCallback) {
        ResultHolder.timeToCallback = timeToCallback;
    }

    public static long getTimeToCallback() {
        return timeToCallback;
    }

    public static void dispose() {
        setImage(null);
        setTimeToCallback(0);
    }
}
