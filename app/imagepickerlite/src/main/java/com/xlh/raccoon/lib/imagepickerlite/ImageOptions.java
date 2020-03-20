package com.xlh.raccoon.lib.imagepickerlite;

import android.content.Context;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class ImageOptions implements Parcelable {

  private String savePath;
  private boolean freeCrop;
  private int maxWidth;
  private int maxHeight;

  public ImageOptions(Context context) {
    this.savePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        + File.separator +
        ImagePickerLite.class.getSimpleName();
  }

  public String getSavePath() {
    return savePath;
  }

  public void setSavePath(String savePath) {
    this.savePath = savePath;
  }

  public boolean isFreeCrop() {
    return freeCrop;
  }

  public void setFreeCrop(boolean freeCrop) {
    this.freeCrop = freeCrop;
  }

  public int getMaxWidth() {
    return maxWidth;
  }

  public void setMaxWidth(int maxWidth) {
    this.maxWidth = maxWidth;
  }

  public int getMaxHeight() {
    return maxHeight;
  }

  public void setMaxHeight(int maxHeight) {
    this.maxHeight = maxHeight;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.savePath);
    dest.writeByte(this.freeCrop ? (byte) 1 : (byte) 0);
    dest.writeInt(this.maxWidth);
    dest.writeInt(this.maxHeight);
  }

  protected ImageOptions(Parcel in) {
    this.savePath = in.readString();
    this.freeCrop = in.readByte() != 0;
    this.maxWidth = in.readInt();
    this.maxHeight = in.readInt();
  }

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
}
