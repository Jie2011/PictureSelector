package com.jie.pictureselector.activity.view;

import android.content.ContentResolver;

/**
 * Created by liumingjie on 2016/5/31.
 */
public interface IPhotoWallView extends BaseView {
    ContentResolver getActivityContentResolver();

    void updatePhotoWall();

    void setTitleString(String title);

    void setTitleString(int titleId);

    void smoothScrollToPosition(int pos);

}
