package com.xlh.raccoon.lib.imagepickerlite.model;

import android.net.Uri;

public class ImageModel {

  private Uri uri;
  private long imageId;

  public long getImageId() {
    return imageId;
  }

  public void setImageId(long imageId) {
    this.imageId = imageId;
  }

  public Uri getUri() {
    return uri;
  }

  public void setUri(Uri uri) {
    this.uri = uri;
  }
}
