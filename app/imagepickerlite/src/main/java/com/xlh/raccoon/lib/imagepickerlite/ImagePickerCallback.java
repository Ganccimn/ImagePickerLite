package com.xlh.raccoon.lib.imagepickerlite;

import android.net.Uri;

import java.io.File;

public interface ImagePickerCallback {
  void onPicked(ImagePicker imagePicker, Uri uri);

  void onPickError(ImagePicker imagePicker, String msg);

  File onCompress(File file);

  void onCropFinish(File file);

  void onCropError(String msg);
}
