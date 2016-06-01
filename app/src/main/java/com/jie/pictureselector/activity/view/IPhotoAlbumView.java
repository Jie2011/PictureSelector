package com.jie.pictureselector.activity.view;

import android.content.ContentResolver;
import android.content.Intent;

/**
 * Created by liumingjie on 2016/6/1.
 */
public interface IPhotoAlbumView extends IBaseView {
    /**
     * 获取ContentResolver
     *
     * @return
     */
    ContentResolver getActivityContentResolver();

    /**
     * 获取Intent数据
     *
     * @return
     */
    Intent getActivityIntent();

    void gotoPhotoWallActivity(Intent intent);
}
