package com.contextgenesis.perplexy.utils;

import android.content.Context;

import java.io.File;

/**
 * Created by rish on 8/3/16.
 */
public class BasePath {

    public static String getBasePath(Context context) {
        File dir = new File(context.getFilesDir(), "perplexy");

        if (!dir.exists())
            dir.mkdirs();

        return dir.getAbsolutePath();
    }

    public static String getBasePathShare(Context context) {
        File dir = new File(context.getFilesDir(), "perplexy");

        if (!dir.exists())
            dir.mkdirs();

        return dir.getAbsolutePath();
    }

}
