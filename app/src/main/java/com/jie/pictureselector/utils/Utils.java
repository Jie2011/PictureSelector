package com.jie.pictureselector.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by ASUS on 2016/6/1.
 */
public class Utils {
    public static Intent getCropIntent(String url,String cutFileName){
        Uri uri = Uri.fromFile(new File(url));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 400);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(cutFileName)));
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        return intent;
    }
}
