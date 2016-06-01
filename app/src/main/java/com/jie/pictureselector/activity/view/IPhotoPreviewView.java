package com.jie.pictureselector.activity.view;

import android.content.Intent;

import com.jie.pictureselector.model.ImageSelectModel;

import java.util.List;

/**
 * Created by liumingjie on 2016/6/1.
 */
public interface IPhotoPreviewView extends IBaseView {

    /**
     * 获取Intent数据
     *
     * @return
     */
    Intent getActivityIntent();

    /**
     * 设置“确定”按钮字符串
     *
     * @param string
     */
    void setSureBtnString(String string);

    /**
     * 更新页面索引文案
     *
     * @param index
     * @param size
     */
    void updateImagePageIndexText(int index, int size);

    /**
     * 根据是否选择更新icon
     *
     * @param selected
     */
    void updateCurrentImageState(boolean selected);

    /**
     * 设置当前显示的页面
     *
     * @param currentItem
     */
    void setCurrentItem(int currentItem);

    /**
     * 更新update
     *
     * @param list
     */
    void updateAdapter(List<ImageSelectModel> list);

    /**
     * 选择数量超过9弹出toast
     */
    void toastBeyondPicSize();
}
