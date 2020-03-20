package com.xlh.raccoon.lib.imagepickerlite.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.xlh.raccoon.lib.imagepickerlite.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class CropImageView extends View {

  private static final String TAG = CropImageView.class.getSimpleName();

  public float padding;
  /**
   * 选取框参数
   */
  public boolean isRatio = false;
  public float x_ratio = 1;
  public float y_ratio = 1;
  public float rectangle_point_radius = 30;
  public float rectangle_line_width = 10;
  float Ox;
  float Oy;
  float rectangle_width;
  float rectangle_height;
  /**
   * 背景资源参数
   */
  private Bitmap bitmap = null;
  private float bitmap_width;
  private float bitmap_height;
  private Matrix bitmap_matrix;
  private float scale;
  private float bitmap_scaled_width;
  private float bitmap_scaled_height;
  private float rectangle_min_length = rectangle_point_radius * 3;

  private Paint rectangle_point_paint = new Paint();
  private Paint rectangle_line_paint = new Paint();

  private PointF pointF_1 = new PointF();
  private PointF pointF_2 = new PointF();
  private PointF pointF_3 = new PointF();
  private PointF pointF_4 = new PointF();
  private PointF[] pointFS = new PointF[]{
      pointF_1, pointF_2, pointF_3, pointF_4
  };

  /**
   * 其他
   */
  private int cur_touch_point = 0;
  private boolean isTouchRectangleArea = false;
  private PointF last_point = new PointF();

  /**
   *
   */
  public CropImageView(Context context) {
    super(context);
    initVal(context);
  }

  public CropImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initVal(context);
  }

  public CropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initVal(context);
  }

  public Bitmap getBitmap() {
    return bitmap;
  }

  public void setBitmap(Bitmap bitmap) {
    this.bitmap = bitmap;
    init();
  }

  public void setRatio(boolean isRatio) {
    this.isRatio = isRatio;
    init();
  }

  private void initVal(Context context) {
    padding = Utils.dp2px(context, 32);
  }

  private void init() {
    //初始化背景
    bitmap_width = bitmap.getWidth();
    bitmap_height = bitmap.getHeight();

    float scale_x = (getWidth() - padding) / bitmap_width;
    float scale_y = (getHeight() - padding) / bitmap_height;
    scale = Math.min(scale_x, scale_y);

    bitmap_matrix = new Matrix();
    bitmap_matrix.setScale(scale, scale);

    bitmap_scaled_width = bitmap_width * scale;
    bitmap_scaled_height = bitmap_height * scale;

    Ox = (getWidth() - bitmap_scaled_width) / 2;
    Oy = (getHeight() - bitmap_scaled_height) / 2;

    //初始化选取框
    rectangle_point_paint.setColor(Color.WHITE);

    rectangle_line_paint.setStrokeWidth(rectangle_line_width);
    rectangle_line_paint.setStyle(Paint.Style.STROKE);
    rectangle_line_paint.setColor(Color.WHITE);

    rectangle_width = bitmap_scaled_width;
    rectangle_height = bitmap_scaled_height;
    //
    if (isRatio) {
      if (x_ratio == y_ratio) {
        rectangle_width = rectangle_height = rectangle_width < rectangle_height ? rectangle_width : rectangle_height;
      } else {
        float ratio = x_ratio > y_ratio ? rectangle_width / x_ratio : rectangle_height / y_ratio;
        rectangle_width = x_ratio * ratio;
        rectangle_height = y_ratio * ratio;
      }
    }
    //设置选取框四个点初始位置
    pointF_1.set(0, 0);
    pointF_2.set(rectangle_width, 0);
    pointF_3.set(rectangle_width, rectangle_height);
    pointF_4.set(0, rectangle_height);

    invalidate();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (bitmap == null)
      return;
    canvas.translate(Ox, Oy);
    //绘制图像
    canvas.drawBitmap(bitmap, bitmap_matrix, new Paint());
    //
    canvas.save();
    canvas.clipRect(new RectF(pointF_1.x, pointF_1.y, pointF_3.x, pointF_3.y), Region.Op.DIFFERENCE);
    canvas.drawColor(Color.argb(120, 0, 0, 0));
    canvas.restore();
    //绘制选取框四个遍边
    canvas.drawLine(pointF_1.x, pointF_1.y, pointF_2.x, pointF_2.y, rectangle_line_paint);
    canvas.drawLine(pointF_2.x, pointF_2.y, pointF_3.x, pointF_3.y, rectangle_line_paint);
    canvas.drawLine(pointF_3.x, pointF_3.y, pointF_4.x, pointF_4.y, rectangle_line_paint);
    canvas.drawLine(pointF_4.x, pointF_4.y, pointF_1.x, pointF_1.y, rectangle_line_paint);
    //绘制选取框四个点
    for (PointF pointF : pointFS) {
      canvas.drawCircle(pointF.x, pointF.y, rectangle_point_radius, rectangle_point_paint);
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    PointF touch_point = new PointF(event.getX() - Ox, event.getY() - Oy);
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        last_point.set(touch_point);
        cur_touch_point = isTouchPoint(touch_point);
        if (cur_touch_point == 0) {
          isTouchRectangleArea = isRectangleArea(touch_point);
        }
        break;
      case MotionEvent.ACTION_MOVE:
        //拖拽选取框的点
        if (cur_touch_point != 0 && !isTouchRectangleArea) {
          float d_x = touch_point.x - last_point.x;
          float d_y = touch_point.y - last_point.y;
          last_point.set(touch_point);
          float tmp_x = 0;
          float tmp_y = 0;
          boolean bool_x;
          boolean bool_y;
          boolean bool_tmp_x;
          boolean bool_tmp_y;
          switch (cur_touch_point) {
            case 1:
              if (isRatio) {
                float ratio = d_x / x_ratio;
                d_y = ratio * y_ratio;
              }
              tmp_x = pointF_1.x + d_x;
              tmp_y = pointF_1.y + d_y;
              bool_x = pointF_2.x - tmp_x >= rectangle_min_length;
              bool_y = pointF_4.y - tmp_y >= rectangle_min_length;
              bool_tmp_x = tmp_x >= 0;
              bool_tmp_y = tmp_y >= 0;
              if ((bool_x && bool_tmp_x) && (bool_y && bool_tmp_y)) {
                pointF_1.x += d_x;
                pointF_4.x += d_x;
                pointF_1.y += d_y;
                pointF_2.y += d_y;
              } else if (bool_x && bool_tmp_x && !isRatio) {
                pointF_1.x += d_x;
                pointF_4.x += d_x;
              } else if (bool_y && bool_tmp_y && !isRatio) {
                pointF_1.y += d_y;
                pointF_2.y += d_y;
              }
              break;
            case 2:
              if (isRatio) {
                float ratio = d_x / x_ratio;
                d_y = -(ratio * y_ratio);
//                                d_y = -d_x;
              }
              tmp_x = pointF_2.x + d_x;
              tmp_y = pointF_2.y + d_y;
              bool_x = tmp_x - pointF_1.x >= rectangle_min_length;
              bool_y = pointF_3.y - tmp_y >= rectangle_min_length;
              bool_tmp_x = tmp_x <= bitmap_scaled_width;
              bool_tmp_y = tmp_y >= 0;
              if ((bool_x && bool_tmp_x) && (bool_y && bool_tmp_y)) {
                pointF_2.x += d_x;
                pointF_3.x += d_x;
                pointF_1.y += d_y;
                pointF_2.y += d_y;
              } else if (bool_x && bool_tmp_x && !isRatio) {
                pointF_2.x += d_x;
                pointF_3.x += d_x;
              } else if (bool_y && bool_tmp_y && !isRatio) {
                pointF_2.y += d_y;
                pointF_1.y += d_y;
              }
              break;
            case 3:
              if (isRatio) {
                float ratio = d_x / x_ratio;
                d_y = ratio * y_ratio;
//                                d_y = d_x;
              }
              tmp_x = pointF_3.x + d_x;
              tmp_y = pointF_3.y + d_y;
              bool_x = tmp_x - pointF_4.x >= rectangle_min_length;
              bool_y = tmp_y - pointF_2.y >= rectangle_min_length;
              bool_tmp_x = tmp_x <= bitmap_scaled_width;
              bool_tmp_y = tmp_y <= bitmap_scaled_height;
              if ((bool_x && bool_tmp_x) && (bool_y && bool_tmp_y)) {
                pointF_3.x += d_x;
                pointF_2.x += d_x;
                pointF_3.y += d_y;
                pointF_4.y += d_y;
              } else if (bool_x && bool_tmp_x && !isRatio) {
                pointF_3.x += d_x;
                pointF_2.x += d_x;
              } else if (bool_y && bool_tmp_y && !isRatio) {
                pointF_3.y += d_y;
                pointF_4.y += d_y;
              }
              break;
            case 4:
              if (isRatio) {
                float ratio = d_x / x_ratio;
                d_y = -(ratio * y_ratio);
//                                d_y = -d_x;
              }
              tmp_x = pointF_4.x + d_x;
              tmp_y = pointF_4.y + d_y;
              bool_x = pointF_3.x - tmp_x >= rectangle_min_length;
              bool_y = tmp_y - pointF_1.y >= rectangle_min_length;
              bool_tmp_x = tmp_x >= 0;
              bool_tmp_y = tmp_y <= bitmap_scaled_height;
              if ((bool_x && bool_tmp_x) && (bool_y && bool_tmp_y)) {
                pointF_4.x += d_x;
                pointF_1.x += d_x;
                pointF_4.y += d_y;
                pointF_3.y += d_y;
              } else if (bool_x && bool_tmp_x && !isRatio) {
                pointF_4.x += d_x;
                pointF_1.x += d_x;
              } else if (bool_y && bool_tmp_y && !isRatio) {
                pointF_4.y += d_y;
                pointF_3.y += d_y;
              }
              break;
          }
          invalidate();
        } else if (isTouchRectangleArea) {
          float d_x = touch_point.x - last_point.x;
          float d_y = touch_point.y - last_point.y;
          last_point.set(touch_point);
          boolean bool_point_1_x = pointF_1.x + d_x >= 0;
          boolean bool_point_1_y = pointF_1.y + d_y >= 0;
          boolean bool_point_3_x = pointF_3.x + d_x <= bitmap_scaled_width;
          boolean bool_point_3_y = pointF_3.y + d_y <= bitmap_scaled_height;
          if (bool_point_1_x && bool_point_1_y && bool_point_3_x && bool_point_3_y) {
            pointF_1.x += d_x;
            pointF_1.y += d_y;
            pointF_2.x += d_x;
            pointF_2.y += d_y;
            pointF_3.x += d_x;
            pointF_3.y += d_y;
            pointF_4.x += d_x;
            pointF_4.y += d_y;
          } else if (bool_point_1_x && bool_point_3_x) {
            pointF_1.x += d_x;
            pointF_2.x += d_x;
            pointF_3.x += d_x;
            pointF_4.x += d_x;
          } else if (bool_point_1_y && bool_point_3_y) {
            pointF_1.y += d_y;
            pointF_2.y += d_y;
            pointF_3.y += d_y;
            pointF_4.y += d_y;
          }
          invalidate();
        }
        break;
      case MotionEvent.ACTION_UP:
        cur_touch_point = 0;
        isTouchRectangleArea = false;
        break;
    }
    return true;
  }

  /**
   * 判断是否点击中选取框的点
   */
  private int isTouchPoint(PointF touch) {
    for (int i = 0; i < 4; i++) {
      if (getDistance(pointFS[i], touch) < rectangle_point_radius)
        return i + 1;
    }
    return 0;
  }

  /**
   * 计算两个点直接的距离
   * 计算点击的位置距离选取框的四个点的距离
   */
  private double getDistance(PointF p1, PointF p2) {
    return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
  }

  /**
   * 判断点的位置是否在选取框内
   */
  private boolean isRectangleArea(PointF touch) {
    if (touch.x > pointF_1.x && touch.x < pointF_2.x && touch.y > pointF_1.y && touch.y < pointF_4.y) {
      return true;
    }
    return false;
  }

  /**
   * 检测选取框边长
   */
  private boolean isRectangleMin() {
    float min_length = rectangle_point_radius * 3;
    if (getDistance(pointF_1, pointF_2) <= min_length)
      return true;
    if (getDistance(pointF_1, pointF_4) <= min_length)
      return true;
    return false;
  }

  /**
   * 获取选中区域的bitmap
   */
  public Bitmap getCropBitmap() {
    int x = (int) (pointF_1.x / scale);
    int y = (int) (pointF_1.y / scale);
    return Bitmap.createBitmap(bitmap,
        x, y, (int) (pointF_3.x / scale) - x, (int) (pointF_3.y / scale) - y);
  }

  public String saveCropBitmap(String path, String name) {
    Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
    String buff = name.substring(name.indexOf(".")).toLowerCase();
    switch (buff) {
      case ".png":
        format = Bitmap.CompressFormat.PNG;
        break;
      case ".webp":
        format = Bitmap.CompressFormat.WEBP;
        break;
    }
    File file = new File(path, "." + name);
    if (file.exists()) {
      file.delete();
    }
    try {
      FileOutputStream out = new FileOutputStream(file);
      getCropBitmap().compress(format, 100, out);
      out.flush();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return file.getAbsolutePath();
  }

  public byte[] getCropBitmapByteArray(Bitmap.CompressFormat format) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      getCropBitmap().compress(format, 100, baos);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return baos.toByteArray();
  }
}
