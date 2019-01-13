package com.sevencats.photomaker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.graphics.drawable.Animatable2Compat;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sevencats.photomaker.utils.BitmapUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class Launcher extends AppCompatActivity {

    // ------ LOAD PHOTO FILTER LIBRARY
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private Button getPhoto;
    private int PICK_IMAGE_REQUEST = 1;
    private ImageView anim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        anim = findViewById(R.id.launcher_img);
        getPhoto = findViewById(R.id.getPhoto);

        //------ CHECK SELF PERMISSION -------
        if (ContextCompat.checkSelfPermission(Launcher.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Launcher.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(Launcher.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        anim.setImageResource(R.drawable.logo_anim);
        Drawable d = anim.getDrawable();
        final Handler handler = new Handler();
        if (d instanceof AnimatedVectorDrawableCompat) {
            final AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) d;
            avd.start();
            avd.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                @Override
                public void onAnimationEnd(Drawable drawable) {
                    super.onAnimationEnd(drawable);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            avd.start();
                        }
                    }, 3000);
                }
            });
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (d instanceof AnimatedVectorDrawable) {
                final AnimatedVectorDrawable avd = (AnimatedVectorDrawable) d;
                avd.start();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    avd.registerAnimationCallback(new Animatable2.AnimationCallback() {
                        @Override
                        public void onAnimationEnd(Drawable drawable) {
                            super.onAnimationEnd(drawable);
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    avd.start();
                                }
                            }, 3000);
                        }
                    });
                }
            }
        }

        getPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = Launcher.this.getContentResolver().query(imageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Intent intent = new Intent(Launcher.this, Home.class);
            intent.putExtra("path", picturePath);
            startActivity(intent);
            finish();
        }
    }
}


