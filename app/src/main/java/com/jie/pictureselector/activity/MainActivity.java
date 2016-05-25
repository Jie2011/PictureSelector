package com.jie.pictureselector.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jie.pictureselector.R;
import com.jie.pictureselector.image.SenbaImageLoader;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private SimpleDraweeView mUserPhotoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mUserPhotoImageView = (SimpleDraweeView) findViewById(R.id.iv_image);
    }

    public void onSelectPicture(View v){
        SelectImageHomeActivity.gotoSelectImageActivity(this, PhotoWallActivity.TYPE_SINGLE, null, PhotoWallActivity.CUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
       if (resultCode == RESULT_OK){
           if (requestCode == SelectImageHomeActivity.CODE_PHOTO) {
               if (intent != null && intent.hasExtra(SelectImageHomeActivity.PHOTO_URLS)) {
                   ArrayList<String> images = intent.getStringArrayListExtra(SelectImageHomeActivity.PHOTO_URLS);
                   if (images != null && images.size() > 0) {
                      String  mNewImageUrl = images.get(0);
                       if (!TextUtils.isEmpty(mNewImageUrl)) {
                           SenbaImageLoader.newInstance(this).setImage(mUserPhotoImageView, getResources().getString(R.string.sd_image_url, mNewImageUrl));
                       }
                   }
               }
           }
       }
    }
}
