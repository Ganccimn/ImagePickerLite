package com.xlh.raccoon.lib.imagepickerlite.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xlh.raccoon.lib.imagepickerlite.ImagePickerLite;
import com.xlh.raccoon.lib.imagepickerlite.R;
import com.xlh.raccoon.lib.imagepickerlite.model.ImageModel;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ImageGalleryVH> {

  private ImageGalleryAsyncUtil imageGalleryAsyncUtil;

  private ImageGalleryClickListener imageGalleryClickListener;

  public ImageGalleryAdapter(ImageGalleryAsyncUtil imageGalleryAsyncUtil, ImageGalleryClickListener imageGalleryClickListener) {
    this.imageGalleryAsyncUtil = imageGalleryAsyncUtil;
    this.imageGalleryClickListener = imageGalleryClickListener;
  }

  @NonNull
  @Override
  public ImageGalleryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_item_layout, null);
    return new ImageGalleryVH(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ImageGalleryVH holder, int position) {
    final ImageModel imageModel = imageGalleryAsyncUtil.getItem(position);
    if (imageModel != null) {
      holder.img.setImageBitmap(ImagePickerLite.lruCache.get(imageModel.getImageId()));
      holder.img.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          imageGalleryClickListener.onImagePicker(imageModel);
        }
      });
    }
  }

  @Override
  public int getItemCount() {
    return imageGalleryAsyncUtil.getItemCount();
  }

  static class ImageGalleryVH extends RecyclerView.ViewHolder {

    private ImageView img;

    public ImageGalleryVH(@NonNull View itemView) {
      super(itemView);
      img = itemView.findViewById(R.id.img);
    }
  }
}
