package com.xlh.raccoon.lib.imagepickerlite;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.xlh.raccoon.lib.imagepickerlite.ui.ImageEditActivity;
import com.xlh.raccoon.lib.imagepickerlite.view.CallbackFragment;

public class ImagePicker {

  private ImagePickerCallback imagePickerCallback;

  public CallbackFragment init(FragmentActivity activity) {
    FragmentManager fragmentManager = activity.getSupportFragmentManager();
    CallbackFragment fragment = (CallbackFragment) fragmentManager.findFragmentByTag(CallbackFragment.TAG);
    if (fragment == null) {
      fragment = CallbackFragment.newInstance();
      FragmentTransaction transaction = fragmentManager.beginTransaction();
      transaction.add(fragment, CallbackFragment.TAG);
      transaction.commitNow();
    }
    return fragment;
  }

  public ImagePickerCallback getImagePickerCallback() {
    return imagePickerCallback;
  }

  private Intent buildEditIntent(Context context, Uri uri, ImageOptions imageOptions) {
    Intent intent = new Intent(context, ImageEditActivity.class);
    intent.putExtra(ImagePickerLite.EDIT_IMAGE_URI, uri);
    intent.putExtra(ImageOptions.class.getSimpleName(), imageOptions);
    return intent;
  }

  private Intent buildSafIntent() {
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
    //文档需要是可以打开的
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    //指定文档的minitype为text类型
    intent.setType("image/*");
    //是否支持多选，默认不支持
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
    return intent;
  }

  public void pick(FragmentActivity activity, ImagePickerCallback imagePickerCallback) {
    this.imagePickerCallback = imagePickerCallback;
    CallbackFragment callbackFragment = init(activity);
    callbackFragment.setImagePicker(this);
    callbackFragment.startActivityForResult(buildSafIntent(), ImagePickerLite.ENTRY_GALLERY_REQUEST);
  }

  public void edit(FragmentActivity activity, Uri uri, ImageOptions imageOptions) {
    CallbackFragment callbackFragment = init(activity);
    callbackFragment.setImagePicker(this);
    callbackFragment.startActivityForResult(
        buildEditIntent(activity.getApplicationContext(), uri, imageOptions),
        ImagePickerLite.ENTRY_EDIT_REQUEST);
  }
}
