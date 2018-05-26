package com.cafateria.handler;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.cafateria.helper.Callback;
import com.cafateria.helper.ImageDownloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created  on 18/02/2018.
 * *  *
 */
public class Images {
    private static final String TAG = "IMAGES";

    public static void set(final Context c, final ImageView imageView, String online_path, final String locale_path) {
        //check if on locale
        Log.e(TAG, "locale path:" + locale_path);
        if (new File(locale_path).exists()) {
            Log.e(TAG, "file exist");
            imageView.setImageDrawable(Drawable.createFromPath(locale_path));
        } else {
            Log.e(TAG, "file not exist");
            //check if online


            ImageDownloader.download(online_path, locale_path, new Callback() {
                @Override
                public void onComplete(boolean is) {
                    if (is) {
                        Log.e(TAG, "download image fininshed");
                        if (new File(locale_path).exists()) {
                            Log.e(TAG, "path exist after download : " + locale_path);
                            imageView.setImageDrawable(Drawable.createFromPath(locale_path));
                        } else {
                            Log.e(TAG, "IMAGE DOWNLOADED FROM ONLINE BUT NOT FOUND ON LOCALE STORAGE");
                        }
                    } else {
                        Log.e(TAG, "ERROR WHILE DOWNLOADING IMAGE FROM ONLINE");
                    }
                }
            });

        }
    }

    public static String getPathFromUri(Activity activity, Uri uri) {
        // just some safety built in
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    public static void update(final Context context, final Bitmap bm, final ImageView image, String online_path, final String locale_path, final Callback callback) {

    }

    public static void saveBmpToLocalDev(Bitmap bitmap, String local_path, Callback callback) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(local_path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
            Log.e("save done:", local_path);
            callback.onComplete(true);
        } catch (Exception e) {
            callback.onComplete(false);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap bitmapFromImageView(ImageView iv) {
        iv.setDrawingCacheEnabled(true);
        return iv.getDrawingCache();

    }

    public static Bitmap bitmapFromPath(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        return Bitmap.createScaledBitmap(realImage, width, height, filter);
    }
}
