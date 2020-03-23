package com.xlh.raccoon.lib.imagepickerlite;

import android.content.Context;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class ImageOptions implements Parcelable {

  public static final Creator<ImageOptions> CREATOR = new Creator<ImageOptions>() {
    @Override
    public ImageOptions createFromParcel(Parcel source) {
      return new ImageOptions(source);
    }

    @Override
    public ImageOptions[] newArray(int size) {
      return new ImageOptions[size];
    }
  };
  private String savePath;
  private boolean isCropSquare;

  public ImageOptions(Context context) {
    this.savePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        + File.separator +
        ImagePickerLite.class.getSimpleName();
  }

  protected ImageOptions(Parcel in) {
    this.savePath = in.readString();
    this.isCropSquare = in.readByte() != 0;
  }

  public String getSavePath() {
    return savePath;
  }

  public void setSavePath(String savePath) {
    this.savePath = savePath;
  }

  public boolean isCropSquare() {
    return isCropSquare;
  }

  public void setCropSquare(boolean cropSquare) {
    this.isCropSquare = cropSquare;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.savePath);
    dest.writeByte(this.isCropSquare ? (byte) 1 : (byte) 0);
  }
}
