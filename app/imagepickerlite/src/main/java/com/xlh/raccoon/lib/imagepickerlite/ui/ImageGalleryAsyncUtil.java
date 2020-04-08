package com.xlh.raccoon.lib.imagepickerlite.ui;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xlh.raccoon.lib.imagepickerlite.ImagePickerLite;
import com.xlh.raccoon.lib.imagepickerlite.Utils;
import com.xlh.raccoon.lib.imagepickerlite.model.ImageModel;

public class ImageGalleryAsyncUtil extends AsyncListUtil<ImageModel> {
  public ImageGalleryAsyncUtil(final Context context, int tileSize, final LinearLayoutManager manager, final RecyclerView recyclerView, final Cursor cursor) {
    super(ImageModel.class, tileSize, new DataCallback<ImageModel>() {
          @Override
          public int refreshData() {
            return cursor.getCount();
          }

          @Override
          public void fillData(@NonNull ImageModel[] data, int startPosition, int itemCount) {
            for (int i = 0; i < itemCount; i++) {
              int index = startPosition + i;
              cursor.moveToPosition(index);
              ImageModel imageModel = new ImageModel();
              long imageId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
              imageModel.setImageId(imageId);
              imageModel.setUri(ContentUris.withAppendedId(
                  ImageGalleryActivity.uri,
                  imageId
              ));
              ImagePickerLite.lruCache.put(imageId, Utils.resizePictureFromUri(context, imageModel.getUri(), 300, 300));
              data[i] = imageModel;
            }
          }
        }, new ViewCallback() {
          @Override
          public void getItemRangeInto(@NonNull int[] outRange) {
            outRange[0] = manager.findFirstVisibleItemPosition();
            outRange[1] = manager.findLastVisibleItemPosition();
          }

          @Override
          public void onDataRefresh() {
            recyclerView.getAdapter().notifyDataSetChanged();
          }

          @Override
          public void onItemLoaded(int position) {
            recyclerView.getAdapter().notifyItemChanged(position);
          }
        }
    );
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        onRangeChanged();
      }
    });
  }
}
