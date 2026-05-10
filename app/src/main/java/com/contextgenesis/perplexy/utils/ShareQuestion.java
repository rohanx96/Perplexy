package com.contextgenesis.perplexy.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.contextgenesis.perplexy.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


/**
 * Created by rish on 12/3/16.
 */

public class ShareQuestion {
    static Bitmap bitmap;

    public static void shareImageWhatsapp(final Activity activity) {
        Toast.makeText(activity, "Preparing for Share", Toast.LENGTH_LONG).show();
        shareImage(activity);
    }

    private static File takeScreenshot(Activity activity) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        String mPath = activity.getExternalCacheDir() + "/" + now + ".jpg";

        // create bitmap screen capture
        View v1 = activity.getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);
        File imageFile = new File(activity.getExternalCacheDir(), now + ".jpg");
        if (!imageFile.exists()) {
            Log.i("Sharing", "creating file");
            try {
                imageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e("Sharing", mPath);
        try {
            FileOutputStream outputStream = new FileOutputStream(imageFile, true);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
//            openScreenshot(activity, imageFile);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return imageFile;
    }

    private static void openScreenshot(Activity activity, File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        activity.startActivity(intent);
    }

    private static void shareImage(Activity activity) {
        File imageFile = takeScreenshot(activity);
        Uri uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider",imageFile);
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setDataAndType(uri, "image/*");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Perplexed! Help me answer this question! " + "https://play.google.com/store/apps/details?id=" + activity.getPackageName());
        whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
        activity.startActivity(Intent.createChooser(whatsappIntent, "Share image using"));
        try {
            activity.startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "Whatsapp has not been installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void shareIt(Activity activity, String question) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Subject test");
        i.putExtra(Intent.EXTRA_TEXT, question+"\n For more such questions download https://play.google.com/store/apps/details?id=" +activity.getPackageName());
        activity.startActivity(Intent.createChooser(i, "Share via"));
    }
}
