package com.contextgenesis.perplexy.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
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
    public static final int REQUEST_WRITE_STORAGE = 112;
    static Bitmap bitmap;

    public static void shareImageWhatsapp(final Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                dialogBuilder.setMessage("The write external storage permission is required to save the screenshot of the question. " +
                        "Kindly grant the permission when requested to use the share functionality")
                        .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        REQUEST_WRITE_STORAGE);
                            }
                        });
                dialogBuilder.show();
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Toast.makeText(activity, "Preparing for Share", Toast.LENGTH_LONG).show();
            shareImage(activity);
        }
    }

    private static File takeScreenshot(Activity activity) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

        // create bitmap screen capture
        View v1 = activity.getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);
        File imageFile = new File(Environment.getExternalStorageDirectory(), "/" + now + ".jpg");
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
