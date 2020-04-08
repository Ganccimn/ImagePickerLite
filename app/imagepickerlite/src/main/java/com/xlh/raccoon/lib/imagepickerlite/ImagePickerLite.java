package com.xlh.raccoon.lib.imagepickerlite;

import android.graphics.Bitmap;
import android.util.LruCache;

public class ImagePickerLite {


  public static final int ENTRY_GALLERY_REQUEST = 123;
  public static final int ENTRY_EDIT_REQUEST = 321;

  public static final String EDIT_IMAGE_URI = "EDIT_IMAGE_URI";
  public static final String EDITED_IMAGE_PATH = "EDITED_IMAGE_PATH";

  public static LruCache<Long, Bitmap> lruCache;

  public static ImagePicker builder() {
    if (lruCache == null) {
      initLruCache();
    }
    return new ImagePicker();
  }

  private static void initLruCache() {
    int maxMemory = (int) Runtime.getRuntime().maxMemory();
    System.out.println(maxMemory);
    int cacheSize = maxMemory / 8;
    lruCache = new LruCache<Long, Bitmap>(cacheSize) {
      @Override
      protected int sizeOf(Long key, Bitmap value) {
        return value.getByteCount();
      }
    };
  }
}
