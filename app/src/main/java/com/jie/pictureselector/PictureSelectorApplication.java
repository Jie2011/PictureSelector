package com.jie.pictureselector;

import android.app.Application;

import com.jie.pictureselector.image.SenbaImageConfig;

/**
 * Created by liumingjie on 2016/5/25.
 */
public class PictureSelectorApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        SenbaImageConfig.getInstance(this).initImageConfig();
    }
}
