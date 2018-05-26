package com.cafateria.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cafateria.R;


public class ProfileImageCropper extends Activity implements View.OnClickListener {

    CustomImageVIew view;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image_cropper);
        view = findViewById(R.id.view);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            continueCrop();
        } else {
            askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 11);
        }

    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            } else {
                Log.d("image", "askForPermission: " + permission);
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("IMAGE", "onRequestPermissionsResult: " + requestCode);

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("IMAGE", "onRequestPermissionsResult: request");

                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

                    continueCrop();
                }
                break;
        }
    }

    private void continueCrop() {
        String path = getIntent().getExtras().getString("filepath");
        Bitmap bm = BitmapFactory.decodeFile(path);
        view.setImageBitmap(bm);

        ImageView rotate = (ImageView) findViewById(R.id.rotate);
        rotate.setOnClickListener(this);

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rotate:
                view.setRotation(view.getRotation() - 90);
                break;
            case R.id.save:
                ImageView circle = (ImageView) findViewById(R.id.circle);
                //circle.buildDrawingCache();
                //Bitmap bm = circle.getDrawingCache(); //Bitmap.createBitmap(view.getDrawingCache(),0,0,150,150);

                RelativeLayout relativelayoutwholeactivity = (RelativeLayout) findViewById(R.id.relativelayoutwholeactivity);
                relativelayoutwholeactivity.setDrawingCacheEnabled(true);
                relativelayoutwholeactivity.buildDrawingCache(true);
                Bitmap squred_img = Bitmap.createBitmap(relativelayoutwholeactivity.getDrawingCache(), (int) circle.getX(), (int) circle.getY(), circle.getWidth(), circle.getHeight());
                Bitmap bm = getCircularBitmap(squred_img);
                view.setImageBitmap(bm);
                relativelayoutwholeactivity.setDrawingCacheEnabled(false);

                Intent intent = this.getIntent();
                intent.putExtra("img", bm);
                this.setResult(RESULT_OK, intent);
                finish();

                break;
        }
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
