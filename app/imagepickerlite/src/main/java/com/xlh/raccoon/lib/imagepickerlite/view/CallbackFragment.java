package com.xlh.raccoon.lib.imagepickerlite.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.xlh.raccoon.lib.imagepickerlite.ImagePicker;
import com.xlh.raccoon.lib.imagepickerlite.ImagePickerCallback;
import com.xlh.raccoon.lib.imagepickerlite.ImagePickerLite;

public class CallbackFragment extends Fragment {

  public static final String TAG = "CallbackFragment";

  private ImagePickerCallback imagePickerCallback;

  private ImagePicker imagePicker;

  public static CallbackFragment newInstance() {
    Bundle args = new Bundle();
    CallbackFragment fragment = new CallbackFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public void setImagePicker(ImagePicker imagePicker) {
    this.imagePicker = imagePicker;
    this.imagePickerCallback = imagePicker.getImagePickerCallback();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    System.out.println("xxxxxxx" + requestCode);
    if (imagePickerCallback != null) {
      switch (requestCode) {
        case ImagePickerLite.ENTRY_GALLERY_REQUEST:
          if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
              imagePickerCallback.onPicked(imagePicker, data.getData());
            } else {
              imagePickerCallback.onPickError(imagePicker, "intent is null");
            }
          }
          break;
        case ImagePickerLite.ENTRY_EDIT_REQUEST:
          if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
              String filePath = data.getStringExtra(ImagePickerLite.EDITED_IMAGE_PATH);
              imagePickerCallback.onEdited(filePath);
            } else {
              imagePickerCallback.onEditError("intent is null");
            }
          }
          break;
      }
    }
  }
}
