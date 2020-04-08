package com.xlh.raccoon.lib.imagepickerlite.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xlh.raccoon.lib.imagepickerlite.R;
import com.xlh.raccoon.lib.imagepickerlite.model.ImageModel;

public class ImageGalleryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, ImageGalleryClickListener {

  public static Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
  public static String[] projection = new String[]{
      MediaStore.Images.Media._ID,
      MediaStore.Images.Media.BUCKET_ID, // 直接包含该图片文件的文件夹ID，防止在不同下的文件夹重名
      MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // 直接包含该图片文件的文件夹名
      MediaStore.Images.Media.DISPLAY_NAME, // 图片文件名
  };
  public static String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";

  //
  private RecyclerView imageListView;
  private GridLayoutManager manager;
  //
  private Cursor cursor;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image_gallery);
    Window window = getWindow();
    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.setStatusBarColor(Color.TRANSPARENT);
    imageListView = findViewById(R.id.image_list_view);
    manager = new GridLayoutManager(this, 3);
    imageListView.setLayoutManager(manager);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
      requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    } else {
      loadImage();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (cursor != null) {
      cursor.close();
      cursor.close();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      loadImage();
    } else {
      Toast.makeText(this, "获取权限失败，无法读取图库", Toast.LENGTH_SHORT).show();
      finish();
    }
  }

  private void loadImage() {
    LoaderManager.getInstance(this).initLoader(1, null, this);
  }

  @NonNull
  @Override
  public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
    return new CursorLoader(this, uri, projection, null, null, sortOrder);
  }

  @Override
  public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
    this.cursor = data;
    imageListView.setAdapter(new ImageGalleryAdapter(new ImageGalleryAsyncUtil(this, 6, manager, imageListView, data), this));
  }

  @Override
  public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    System.out.println(123);
    if (cursor != null) {
      cursor.close();
    }
  }

  @Override
  public void onImagePicker(ImageModel imageModel) {
    setResult(RESULT_OK, new Intent().setData(imageModel.getUri()));
    finish();
  }
}
