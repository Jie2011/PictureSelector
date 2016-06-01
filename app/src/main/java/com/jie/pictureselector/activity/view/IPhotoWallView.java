package com.jie.pictureselector.activity.view;

import android.content.ContentResolver;

import java.util.ArrayList;

/**
 * Created by liumingjie on 2016/5/31.
 */
public interface IPhotoWallView extends IBaseView {

    /**
     * 获取ContentResolver
     *
     * @return
     */
    ContentResolver getActivityContentResolver();

    /**
     * 更新图片
     */
    void updatePhotoWall();

    /**
     * 设置title
     *
     * @param title
     */

    void setTitleString(String title);

    void setTitleString(int titleId);

    /**
     * 滚动到指定位置
     *
     * @param pos
     */
    void smoothScrollToPosition(int pos);

    /**
     * 图片多选完成后结束逻辑
     *
     * @param selectedImageList
     */
    void onMultipleTypeFinish(ArrayList<String> selectedImageList);

}
