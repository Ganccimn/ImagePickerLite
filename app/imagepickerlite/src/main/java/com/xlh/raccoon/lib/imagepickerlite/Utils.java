package com.xlh.raccoon.lib.imagepickerlite;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import java.io.FileDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by GCM on 2018/2/7.
 */

public class Utils {
  /**
   * 将px转换为与之相等的dp
   */
  public static int px2dp(Context context, float pxValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }

  /**
   * 将dp转换为与之相等的px
   */
  public static int dp2px(Context context, float dipValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dipValue * scale + 0.5f);
  }

  /**
   * 将dp转换为与之相等的px
   */
  public static int dpToPx(float dipValue, Resources resources) {
    final float scale = resources.getDisplayMetrics().density;
    return (int) (dipValue * scale + 0.5f);
  }

  public static boolean contain(Rect rect, Point point) {
    return point.x >= rect.left &&
        point.y >= rect.top &&
        point.x <= rect.right &&
        point.y <= rect.bottom;
  }

  public static boolean contain(RectF rect, PointF point) {
    return point.x >= rect.left &&
        point.y >= rect.top &&
        point.x <= rect.right &&
        point.y <= rect.bottom;
  }

  public static boolean contain(Rect rect, MotionEvent event) {
    return contain(rect, new Point((int) event.getX(), (int) event.getY()));
  }

  public static boolean contain(RectF rect, MotionEvent event) {
    return contain(rect, new PointF(event.getX(), event.getY()));
  }

  /**
   * 将px转换为sp
   */
  public static int px2sp(Context context, float pxValue) {
    final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
    return (int) (pxValue / fontScale + 0.5f);
  }

  /**
   * 将sp转换为px
   */
  public static int sp2px(Context context, float spValue) {
    final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
    return (int) (spValue * fontScale + 0.5f);
  }

  /**
   * 获取状态栏高度
   */
  public static int getStatusHeight(Activity activity) {
    int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      return activity.getResources().getDimensionPixelSize(resourceId);
    }
    return 0;
  }

  /**
   * 获得导航栏高度
   */
  public static int getNavigationBarHeight(Activity activity) {
    int resourceId = activity.getResources().getIdentifier("navigation_bar_height",
        "dimen", "android");
    return activity.getResources().getDimensionPixelSize(resourceId);
  }


  /**
   * 获取是否存在NavigationBar
   */
  public static boolean checkDeviceHasNavigationBar(Context context) {
    boolean hasNavigationBar = false;
    Resources rs = context.getResources();
    int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
    if (id > 0) {
      hasNavigationBar = rs.getBoolean(id);
    }
    try {
      Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
      Method m = systemPropertiesClass.getMethod("get", String.class);
      String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
      if ("1".equals(navBarOverride)) {
        hasNavigationBar = false;
      } else if ("0".equals(navBarOverride)) {
        hasNavigationBar = true;
      }
    } catch (Exception e) {
    }
    return hasNavigationBar;
  }

  /**
   * 获取屏幕大小
   */
  public static Map<String, Integer> getScreenSize(Context context) {
    Map<String, Integer> map = new HashMap<>();
    DisplayMetrics dm = context.getResources().getDisplayMetrics();
    map.put("width", dm.widthPixels);
    map.put("height", dm.heightPixels);
    return map;
  }

  public static Bitmap uriToBitmap(Context context, Uri uri) throws Exception {
    ParcelFileDescriptor parcelFileDescriptor =
        context.getContentResolver().openFileDescriptor(uri, "r");
    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
    Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
    parcelFileDescriptor.close();
    return bitmap;
  }

  public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
        .getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(output);
    final int color = 0xff424242;
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    final RectF rectF = new RectF(rect);
    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(color);
    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(bitmap, rect, rect, paint);
    return output;
  }

  public static Bitmap resizePictureFromFile(String path, int reqWidth, int reqHeight) {
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(path, options);
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeFile(path, options);
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
        inSampleSize *= 2;
      }
    }
    return inSampleSize;
  }
}
