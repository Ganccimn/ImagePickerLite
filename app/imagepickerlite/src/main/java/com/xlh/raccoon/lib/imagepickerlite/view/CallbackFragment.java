package com.xlh.raccoon.lib.imagepickerlite.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.xlh.raccoon.lib.easyasynctask.EasyAsyncTask;
import com.xlh.raccoon.lib.easyasynctask.EasyCallback;
import com.xlh.raccoon.lib.easyasynctask.EasyTask;
import com.xlh.raccoon.lib.easyasynctask.EasyTaskProgressBar;
import com.xlh.raccoon.lib.imagepickerlite.ImagePicker;
import com.xlh.raccoon.lib.imagepickerlite.ImagePickerCallback;
import com.xlh.raccoon.lib.imagepickerlite.ImagePickerLite;

import java.io.File;

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

  private void compress(final String filepath) {
    EasyAsyncTask.builder(getContext(), String.class, File.class)
        .addListener(new EasyCallback<String, File>() {
          @Override
          public void onInt(EasyTask<String, File> easyTask) {
            easyTask.execute(filepath);
          }

          @Override
          public void onStart(EasyTaskProgressBar easyTaskProgressBar) {
            easyTaskProgressBar.setMsg("压缩图片中...");
            easyTaskProgressBar.show();
          }

          @Override
          public File onWork(EasyTask<String, File> easyTask, String... files) {
            return CallbackFragment.this.imagePickerCallback.onCompress(new File(files[0]));
          }

          @Override
          public void onFinish(File file) {
            imagePickerCallback.onCropFinish(file);
          }

          @Override
          public void onFail(int i, String s, String s1) {
            Toast.makeText(getContext(), s + "   " + s1, Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onProgressUpdate(EasyTaskProgressBar easyTaskProgressBar, String... strings) {

          }
        });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
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
              compress(filePath);
            } else {
              imagePickerCallback.onCropError("intent is null");
            }
          }
          break;
      }
    }
  }
}
