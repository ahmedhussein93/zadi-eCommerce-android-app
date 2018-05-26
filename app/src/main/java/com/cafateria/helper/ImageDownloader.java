package com.cafateria.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.cafateria.handler.Images;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created  on 31/07/2017.
 */
public class ImageDownloader {
    static boolean isDownloadCom = true;

    public static void download(final String url, final String pathToLocalePic, final Callback callback) {
        new DownloaderAsyncTask(pathToLocalePic, url, callback).execute();
    }

    private static class DownloaderAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private static final String TAG = "IMAGE_DOWNLOADER";
        String local_path;
        String url;
        Callback callback;

        public DownloaderAsyncTask(String local_path, String url, Callback callback) {
            this.local_path = local_path;
            this.url = url;
            this.callback = callback;
            isDownloadCom = false;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            isDownloadCom = true;
            //startCounter();

            try {
                return downloadBitmap(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.e("ImageDownloader", "onPostExecute");
            if (!isCancelled() && bitmap != null) {
                Images.saveBmpToLocalDev(bitmap, local_path, callback);
                callback.onComplete(true);
            } else
                callback.onComplete(false);
        }


        private Bitmap downloadBitmap(String url) {
            Log.e(TAG, "downloadBitmap:" + url);
            HttpURLConnection urlConnection = null;
            try {
                URL uri = new URL(url);
                urlConnection = (HttpURLConnection) uri.openConnection();
                int statusCode = urlConnection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    Log.e(TAG, "IMAGE DOWNLAODED:" + url);
                    return bitmap;
                }
            } catch (Exception e) {
                urlConnection.disconnect();
                Log.w("ImageDownloader", "Error downloading image from " + url);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }
}
