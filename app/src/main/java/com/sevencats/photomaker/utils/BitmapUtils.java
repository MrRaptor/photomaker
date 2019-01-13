package com.sevencats.photomaker.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class BitmapUtils {

    public static int flipRotate = 0;

    public static Bitmap overlay(Context context, Bitmap bmp1, int overlayDrawableResourceId, int alpha, boolean isFlip, boolean flipFlag) {

        Bitmap dst;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), overlayDrawableResourceId, options);
        options.inMutable = true;
        options.inJustDecodeBounds = false;
        Bitmap bmp2 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), overlayDrawableResourceId, options), bmp1.getWidth(), bmp1.getHeight(), false);
        dst = bmp2;

        if (flipRotate == 0 || flipFlag) {
            if (isFlip) {
                Matrix m = new Matrix();
                m.preScale(-1, 1);
                dst = Bitmap.createBitmap(bmp2, 0, 0, bmp2.getWidth(), bmp2.getHeight(), m, false);
                dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
                if (!flipFlag) flipRotate += 180;
            }
        } else {
            if (!flipFlag) flipRotate -= 180;
            dst = bmp2;
        }

        Paint alphaPaint = new Paint();
        alphaPaint.setAlpha(alpha);
        alphaPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
        return overlayTwoBitmaps(bmp1, dst, alphaPaint);
    }

    public static Bitmap overlayTwoBitmaps(Bitmap bitmap1, Bitmap bitmap2, Paint paint) {

        Bitmap bitmap = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), bitmap1.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap1, new Matrix(), null);
        canvas.drawBitmap(bitmap2, new Matrix(), paint);
        return bitmap;
    }


    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inMutable = true;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static String insertImage(ContentResolver cr, Bitmap source, String title, String description) throws IOException {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        Uri uri = null;
        String stringUrl = null;
        try {
            uri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (source != null) {
                OutputStream outputStream = cr.openOutputStream(uri);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                } finally {
                    outputStream.close();
                }
                long id = ContentUris.parseId(uri);
                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                storeThumbail(cr, miniThumb, id, 50f, 50f, MediaStore.Images.Thumbnails.MICRO_KIND);
            } else {
                cr.delete(uri, null, null);
                uri = null;
            }
        } catch (FileNotFoundException e) {
            if (uri != null) {
                cr.delete(uri, null, null);
                uri = null;
            }
        }
        if (uri != null) stringUrl = uri.toString();

        return stringUrl;
    }

    private static final Bitmap storeThumbail(ContentResolver cr, Bitmap source, long id, float width, float height, int kind) {

        Matrix matrix = new Matrix();
        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();
        matrix.setScale(scaleX, scaleY);
        Bitmap thumb = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        ContentValues contentValues = new ContentValues(4);
        contentValues.put(MediaStore.Images.Thumbnails.KIND, kind);
        contentValues.put(MediaStore.Images.Thumbnails.IMAGE_ID, kind);
        contentValues.put(MediaStore.Images.Thumbnails.HEIGHT, kind);
        contentValues.put(MediaStore.Images.Thumbnails.WIDTH, kind);

        Uri uri = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, contentValues);
        try {
            OutputStream outputStream = cr.openOutputStream(uri);
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
            return thumb;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap decodeBitmap(int width, int height, String picturePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, options);
        if (width > 0 && height > 0) {
            options.inSampleSize = BitmapUtils.calculateInSampleSize(options, width, height);
        }
        options.inMutable = true;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(picturePath, options);
    }

    static public BitmapDrawable flip(BitmapDrawable d) {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap src = d.getBitmap();
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return new BitmapDrawable(dst);
    }

    public static Bitmap getScreenShot(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }
}