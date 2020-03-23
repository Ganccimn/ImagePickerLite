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

              //成功裁剪图片的回调
              @Override
              public void onEdited(String filePath) {
                imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));
              }

              @Override
              public void onEditError(String msg) {

              }
            });
```