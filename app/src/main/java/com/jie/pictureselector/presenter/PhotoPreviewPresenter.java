package com.jie.pictureselector.presenter;

import android.content.Intent;

import com.jie.pictureselector.activity.PhotoWallPreviewActivity;
import com.jie.pictureselector.activity.view.IPhotoPreviewView;
import com.jie.pictureselector.model.ImageSelectModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by liumingjie on 2016/6/1.
 */
public class PhotoPreviewPresenter extends BasePresenter<IPhotoPreviewView> {

    private ArrayList<ImageSelectModel> mPics;
    private int mCurrentIndex = 0;
    private HashMap<String, String> mSelectMap = new HashMap<String, String>();

    private ImageSelectModel mCurrentImageModel;

    public PhotoPreviewPresenter(IPhotoPreviewView baseView) {
        super(baseView);
        initIntentData();
    }

    private void initIntentData() {
        Intent intent = getBaseView().getActivityIntent();
        mPics = (ArrayList<ImageSelectModel>) intent.getSerializableExtra(PhotoWallPreviewActivity.IMAGES);
        mCurrentIndex = intent.getIntExtra(PhotoWallPreviewActivity.INDEX, 0);
        mSelectMap = (HashMap<String, String>) intent.getSerializableExtra(PhotoWallPreviewActivity.SELECTED_LIST);
        mCurrentImageModel = mPics.get(mCurrentIndex);
        String sureTextString = "";
        if (mSelectMap.size() > 0) {
            sureTextString = "确定(" + mSelectMap.size() + ")";
        } else {
            sureTextString = "确定";
        }
        getBaseView().setSureBtnString(sureTextString);
        getBaseView().updateCurrentImageState(mCurrentImageModel.select);
        getBaseView().updateImagePageIndexText(mCurrentIndex + 1, mPics.size());
        getBaseView().updateAdapter(mPics);
        getBaseView().setCurrentItem(mCurrentIndex);
    }

    /**
     * 选中/未选中icon点击时逻辑
     */
    public void onSelectClick() {
        mCurrentImageModel.select = !mCurrentImageModel.select;
        if (mCurrentImageModel.select) {
            if (mSelectMap.size() >= 9) {
                getBaseView().toastBeyondPicSize();
            } else {
                mSelectMap.put(mCurrentImageModel.url, mCurrentImageModel.url);
            }
        } else {
            if (mSelectMap.containsKey(mCurrentImageModel.url)) {
                mSelectMap.remove(mCurrentImageModel.url);
            }
        }
        mPics.get(mCurrentIndex).select = mCurrentImageModel.select;
        getBaseView().setSureBtnString("确定(" + mSelectMap.size() + ")");
        getBaseView().updateCurrentImageState(mCurrentImageModel.select);
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    public ArrayList<ImageSelectModel> getPics() {
        return mPics;
    }

    public HashMap<String, String> getSelectMap() {
        return mSelectMap;
    }

    /**
     * 滑动时设置当前的model
     *
     * @param pos
     */
    public void setCurrentImageModel(int pos) {
        mCurrentIndex = pos;
        mCurrentImageModel = mPics.get(pos);
        getBaseView().updateCurrentImageState(mCurrentImageModel.select);
        getBaseView().updateImagePageIndexText(mCurrentIndex + 1, mPics.size());
    }
}
