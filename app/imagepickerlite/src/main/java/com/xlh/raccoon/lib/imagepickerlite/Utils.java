package com.xlh.raccoon.lib.imagepickerlite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileDescriptor;
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

  public static File saveBitmap(Bitmap bitmap, String path, String name) {
    new File(path).mkdirs();
    File file = new File(path, name);
    try {
      FileOutputStream out = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
      out.flush();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return file;
  }
}
