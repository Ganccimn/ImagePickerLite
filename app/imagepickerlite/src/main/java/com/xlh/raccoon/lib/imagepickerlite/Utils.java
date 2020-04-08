package com.xlh.raccoon.lib.imagepickerlite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by GCM on 2018/2/7.
 */

public class Utils {

  /**
   * 将dp转换为与之相等的px
   */
  public static int dp2px(Context context, float dipValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dipValue * scale + 0.5f);
  }

  public static Bitmap uriToBitmap(Context context, Uri uri) throws Exception {
    ParcelFileDescriptor parcelFileDescriptor =
        context.getContentResolver().openFileDescriptor(uri, "r");
    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
    Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
    parcelFileDescriptor.close();
    return bitmap;
  }

  public static File saveBitmap(Bitmap bitmap, File file, Bitmap.CompressFormat format, int quality) {
    File dir = file.getParentFile();
    if (dir != null) {
      dir.mkdirs();
    }
    try {
      FileOutputStream out = new FileOutputStream(file);
      bitmap.compress(format, quality, out);
      out.flush();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return file;
  }

  public static Bitmap resizePictureFromFile(String path, int reqWidth, int reqHeight) {
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(path, options);
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeFile(path, options);
  }

  public static Bitmap resizePictureFromUri(Context context, Uri uri, int reqWidth, int reqHeight) {
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    ParcelFileDescriptor parcelFileDescriptor;
    FileDescriptor fileDescriptor;
    try {
      parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
      fileDescriptor = parcelFileDescriptor.getFileDescriptor();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }
    BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
  }

  // the algorithm for calculate the inSampleSize;
  public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    if (reqWidth == 0 || reqHeight == 0) return 1;
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;
    if (height > reqHeight || width > reqWidth) {
      final int halfWidth = width;
      final int halfHeight = height;
      while ((halfHeight / inSampleSize) > reqHeight
          && (halfWidth / inSampleSize) > reqWidth) {
        inSampleSize += 1;
      }
    }
    return inSampleSize;
  }
}
