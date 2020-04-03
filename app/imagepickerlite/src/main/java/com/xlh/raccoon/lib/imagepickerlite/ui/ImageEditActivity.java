package com.xlh.raccoon.lib.imagepickerlite.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.xlh.raccoon.lib.easyasynctask.EasyAsyncTask;
import com.xlh.raccoon.lib.easyasynctask.EasyCallback;
import com.xlh.raccoon.lib.easyasynctask.EasyTask;
import com.xlh.raccoon.lib.easyasynctask.EasyTaskProgressBar;
import com.xlh.raccoon.lib.easyasynctask.ForceThrowException;
import com.xlh.raccoon.lib.imagepickerlite.ImageOptions;
import com.xlh.raccoon.lib.imagepickerlite.ImagePickerLite;
import com.xlh.raccoon.lib.imagepickerlite.R;
import com.xlh.raccoon.lib.imagepickerlite.Utils;
import com.xlh.raccoon.lib.imagepickerlite.view.CropImageView;

import java.io.File;
import java.util.Date;

public class ImageEditActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

  private CropImageView cropImageView;
  private Uri uri;
  private ImageOptions imageOptions;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_image);
    Window window = getWindow();
    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.setStatusBarColor(Color.TRANSPARENT);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    toolbar.setOnMenuItemClickListener(this);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
    cropImageView = findViewById(R.id.crop_img_view);
    //
    uri = getIntent().getParcelableExtra(ImagePickerLite.EDIT_IMAGE_URI);
    imageOptions = getIntent().getParcelableExtra(ImageOptions.class.getSimpleName());
    if (uri != null && imageOptions != null) {
      loadImage(uri);
    } else {
      finish();
    }
  }

  private void loadImage(final Uri uri) {
    EasyAsyncTask.builder(this, Uri.class, Bitmap.class)
        .addListener(new EasyCallback<Uri, Bitmap>() {
          @Override
          public void onInt(EasyTask<Uri, Bitmap> easyTask) {
            easyTask.execute(uri);
          }

          @Override
          public void onStart(EasyTaskProgressBar easyTaskProgressBar) {
            easyTaskProgressBar.setMsg("正在加载图片...");
            easyTaskProgressBar.show();
          }

          @Override
          public Bitmap onWork(EasyTask<Uri, Bitmap> easyTask, Uri... uris) {
            Bitmap bitmap = null;
            try {
              bitmap = Utils.uriToBitmap(ImageEditActivity.this, uris[0]);
            } catch (Exception e) {
              throw new ForceThrowException(e.getMessage());
            }
            return bitmap;
          }

          @Override
          public void onFinish(Bitmap bitmap) {
            cropImageView.setBitmap(bitmap);
            cropImageView.setRatio(imageOptions.isCropSquare());
          }

          @Override
          public void onFail(int i, String s, String s1) {
            Toast.makeText(ImageEditActivity.this, s + "  " + s1, Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onProgressUpdate(EasyTaskProgressBar easyTaskProgressBar, String... strings) {

          }
        });
  }

  private void editImage(final int action) {
    EasyAsyncTask.builder(this, Integer.class, Bitmap.class)
        .addListener(new EasyCallback<Integer, Bitmap>() {
          @Override
          public void onInt(EasyTask<Integer, Bitmap> easyTask) {
            easyTask.execute(action);
          }

          @Override
          public void onStart(EasyTaskProgressBar easyTaskProgressBar) {
            easyTaskProgressBar.show();
          }

          @Override
          public Bitmap onWork(EasyTask<Integer, Bitmap> easyTask, Integer... integers) {
            Bitmap bitmap = cropImageView.getBitmap();
            int action = integers[0];
            switch (action) {
              case 1:
                Matrix rotateMatrix = new Matrix();
                rotateMatrix.setRotate(90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, false);
                break;
              case 2:
                Matrix ScaleMatrix = new Matrix();
                ScaleMatrix.setScale(-1, 1);//水平翻转
                bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), ScaleMatrix, false);
                break;
            }
            return bitmap;
          }

          @Override
          public void onFinish(Bitmap bitmap) {
            cropImageView.setBitmap(bitmap);
          }

          @Override
          public void onFail(int i, String s, String s1) {
            Toast.makeText(ImageEditActivity.this, s + "  " + s1, Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onProgressUpdate(EasyTaskProgressBar easyTaskProgressBar, String... strings) {

          }
        });
  }

  private void cropImage() {
    EasyAsyncTask.builder(this, Void.class, File.class)
        .addListener(new EasyCallback<Void, File>() {
          @Override
          public void onInt(EasyTask<Void, File> easyTask) {
            easyTask.execute();
          }

          @Override
          public void onStart(EasyTaskProgressBar easyTaskProgressBar) {
            easyTaskProgressBar.setMsg("裁剪图片中...");
            easyTaskProgressBar.show();
          }

          @Override
          public File onWork(EasyTask<Void, File> easyTask, Void... voids) {
            Bitmap bitmap = cropImageView.getCropBitmap();
            return Utils.saveBitmap(bitmap, new File(imageOptions.getSavePath(), new Date().getTime() + ".jpg"), Bitmap.CompressFormat.JPEG, 80);
          }

          @Override
          public void onFinish(File file) {
            if (file != null) {
              setResult(Activity.RESULT_OK, new Intent().putExtra(ImagePickerLite.EDITED_IMAGE_PATH, file.getAbsolutePath()));
              finish();
            } else {
              Toast.makeText(ImageEditActivity.this, "失败", Toast.LENGTH_SHORT).show();
            }
          }

          @Override
          public void onFail(int i, String s, String s1) {
            Toast.makeText(ImageEditActivity.this, s + "  " + s1, Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onProgressUpdate(EasyTaskProgressBar easyTaskProgressBar, String... strings) {
            easyTaskProgressBar.setMsg(strings[0]);
          }
        });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.edit_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onMenuItemClick(MenuItem item) {
    int i = item.getItemId();
    if (i == R.id.action_crop) {
      cropImage();
    } else if (i == R.id.rotate_btn) {
      editImage(1);
    } else if (i == R.id.h_rotate_btn) {
      editImage(2);
    } else if (i == R.id.reset_btn) {
      loadImage(uri);
    }
    return true;
  }
}
