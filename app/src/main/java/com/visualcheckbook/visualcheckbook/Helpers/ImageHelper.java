package com.visualcheckbook.visualcheckbook.Helpers;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Pair;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

import static android.graphics.BitmapFactory.decodeStream;

public final class ImageHelper {

    // Returns max image width, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    public static Integer getImageMaxWidth(int mImageMaxWidth, ImageView mImageView) {
        if (mImageView == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxWidth = mImageView.getWidth();
        }

        return mImageMaxWidth;
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    public static Integer getImageMaxHeight(int mImageMaxHeight, ImageView mImageView) {
        if (mImageView == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxHeight =
                    mImageView.getHeight();
        }

        return mImageMaxHeight;
    }

    // Gets the targeted width / height.
    public static Pair<Integer, Integer> getTargetedWidthHeight(int mImageMaxWidth, int mImageMaxHeight, ImageView mImageView) {
        int targetWidth;
        int targetHeight;
        int maxWidthForPortraitMode = getImageMaxWidth(mImageMaxWidth, mImageView);
        int maxHeightForPortraitMode = getImageMaxHeight(mImageMaxHeight, mImageView);
        targetWidth = maxWidthForPortraitMode;
        targetHeight = maxHeightForPortraitMode;
        return new Pair<>(targetWidth, targetHeight);
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream is;
        Bitmap bitmap = null;
        try {
            is = assetManager.open(filePath);
            bitmap = decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static Bitmap rotateImage(int angle, Bitmap mSelectedImage) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(mSelectedImage, 0, 0, mSelectedImage.getWidth(), mSelectedImage.getHeight(), matrix, true);
    }
}
