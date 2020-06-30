# ImagePickerLite
 
[![](https://jitpack.io/v/Peorz/ImagePickerLite.svg)](https://jitpack.io/#Peorz/ImagePickerLite)

### 如何添加
> 1.Add it in your root build.gradle at the end of repositories:
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

> 2. Add the dependency
```
dependencies {
	        implementation 'com.github.Peorz:ImagePickerLite:Tag'
	}
```

### 使用方法
```
ImagePickerLite.builder()
            .pick(MainActivity.this, new ImagePickerCallback() {
              //成功选取图片的回调
              @Override
              public void onPicked(ImagePicker imagePicker, Uri uri) {
                //创建裁剪配置
                ImageOptions imageOptions = new ImageOptions(MainActivity.this);
                imageOptions.setCropSquare(true);//裁剪框是否固定为正方形
                //进入裁剪界面
                imagePicker.edit(MainActivity.this, uri, imageOptions);
              }

              @Override
              public void onPickError(ImagePicker pickerConfig, String msg) {

              }

              //图片裁剪好后的压缩回调，此处异步
              //先调用 onCompress() 再调用 onCropFinish()
              @Override
              public File onCompress(File file) {
                Bitmap bitmap = Utils.resizePictureFromFile(file.getAbsolutePath(), 300, 300);
                return Utils.saveBitmap(bitmap, file, Bitmap.CompressFormat.JPEG, 80);
              }

              //图片裁剪好后的最终回调
              @Override
              public void onCropFinish(File file) {
                imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
              }

              @Override
              public void onCropError(String msg) {

              }
            });
```