package com.hafez.password_manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.room.TypeConverter;
import java.io.ByteArrayOutputStream;

public abstract class Converters {

    /**
     * Converts bitmap to byte array in PNG format
     *
     * @param bitmap source bitmap
     *
     * @return result byte array
     */
    @TypeConverter
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * Converts compressed byte array to bitmap
     *
     * @param src source array
     *
     * @return result bitmap
     */
    @TypeConverter
    public static Bitmap byteArrayToBitmap(byte[] src) {
        return BitmapFactory.decodeByteArray(src, 0, src.length);
    }


    /**
     * Converts a {@link BitmapDrawable} or {@link VectorDrawable} into {@link Bitmap}
     *
     * @param drawableResId Resource Id of the drawable to be converted
     *
     * @return The converted bitmap
     */
    public static Bitmap drawableToBitmap(@DrawableRes int drawableResId) {
        Drawable drawable = App.getInstance().getDrawable(drawableResId);
        return drawableToBitmap(drawable);
    }

    /**
     * Converts a {@link BitmapDrawable} or {@link VectorDrawable} into {@link Bitmap}
     *
     * @param drawable The drawable of type {@link BitmapDrawable} or {@link VectorDrawable} to be
     *                 converted
     *
     * @return The converted bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {

            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        } else {
            throw new IllegalArgumentException("Unsupported drawable type");
        }
    }

    /**
     * Converts a {@link Bitmap} into {@link BitmapDrawable}
     *
     * @param bitmap The bitmap to be converted
     *
     * @return The Drawable Bitmap Object
     */
    @NonNull
    public static BitmapDrawable bitmapToDrawable(@NonNull Bitmap bitmap) {
        return new BitmapDrawable(App.getInstance().getResources(), bitmap);
    }


}
