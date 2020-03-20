package com.xlh.raccoon.lib.imagepickerlite;

import android.net.Uri;

public interface ImagePickerCallback {
  void onPicked(ImagePicker imagePicker, Uri uri);

  void onPickError(ImagePicker imagePicker, String msg);

  void onEdited(String filePath);

  void onEditError(String msg);
}
