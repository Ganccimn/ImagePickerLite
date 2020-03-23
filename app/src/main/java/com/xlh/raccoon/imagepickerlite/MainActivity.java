package com.xlh.raccoon.imagepickerlite;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.xlh.raccoon.lib.imagepickerlite.ImageOptions;
import com.xlh.raccoon.lib.imagepickerlite.ImagePicker;
import com.xlh.raccoon.lib.imagepickerlite.ImagePickerCallback;
import com.xlh.raccoon.lib.imagepickerlite.ImagePickerLite;

import java.io.File;

public class MainActivity extends AppCompatActivity {

  private ImageView imageView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    imageView = findViewById(R.id.img);
    imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ImagePickerLite.builder()
            .pick(MainActivity.this, new ImagePickerCallback() {
              //成功选取图片的回调
              @Override
              public void onPicked(ImagePicker imagePicker, Uri uri) {
                //创建裁剪配置
                ImageOptions imageOptions = new ImageOptions(MainActivity.this);
                imageOptions.setCropSquare(true);//裁剪框是否固定为正方形
                //进入裁剪界面
                imagePicker.edit(MainActivity.this, uri, imageOptions);
              }

              @Override
              public void onPickError(ImagePicker pickerConfig, String msg) {

              }

              //图片裁剪好后的压缩回调，此处异步
              //先调用 onCompress() 再调用 onCropFinish()
              @Override
              public File onCompress(File file) {
                //模拟压缩耗时
                SystemClock.sleep(4000);
                return file;
              }

              //图片裁剪好后的最终回调
              @Override
              public void onCropFinish(File file) {
                imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
              }

              @Override
              public void onCropError(String msg) {

              }
            });
      }
    });

  }
}
