package com.jie.pictureselector.presenter;

import com.jie.pictureselector.activity.view.BaseView;

/**
 * Created by liumingjie on 2016/5/31.
 */
public class BasePresenter<T extends BaseView> {

    private T mBaseView;

    public BasePresenter(T baseView){
        mBaseView = baseView;
    }

    public T getBaseView() {
        return mBaseView;
    }

    public void setBaseView(T baseView) {
        mBaseView = baseView;
    }

}
