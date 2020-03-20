package com.xlh.raccoon.lib.imagepickerlite;

public class ImagePickerLite {


  public static final int ENTRY_GALLERY_REQUEST = 123;
  public static final int ENTRY_EDIT_REQUEST = 321;

  public static final String EDIT_IMAGE_URI = "EDIT_IMAGE_URI";
  public static final String EDITED_IMAGE_PATH = "EDITED_IMAGE_PATH";

  public static ImagePicker builder() {
    return new ImagePicker();
  }
}
