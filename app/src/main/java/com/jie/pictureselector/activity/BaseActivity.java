package com.jie.pictureselector.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * Created by liumingjie on 2016/5/25.
 */
public abstract class BaseActivity extends FragmentActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initSwipeBackInfo();
        setContentView(getLayoutId());
        initView();
    }

  /*  *//**
     * 初始化滑动关闭
     *//*
    protected void initSwipeBackInfo() {
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(true)
                .setSwipeRelateEnable(true)
                .setSwipeEdgePercent(0.05f)
                .setSwipeSensitivity(1);
        SwipeBackHelper.getCurrentPage(this).getSwipeBackLayout().setEdgeTrackingEnabled(ViewDragHelper.EDGE_BOTTOM);
    }

    *//**
     * 设置是否可以滑动关闭
     *
     *//*
    protected void setSwipeBackEnable(boolean enable) {
        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(enable);
    }*/

    protected <T extends View> T $(@IdRes int id) {
        return (T) findViewById(id);
    }

    /**
     * layout id
     *
     * @return
     */
    protected abstract int getLayoutId();

    protected abstract void initView();
}
