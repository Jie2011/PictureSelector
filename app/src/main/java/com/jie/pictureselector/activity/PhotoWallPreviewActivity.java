package com.jie.pictureselector.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jie.pictureselector.R;
import com.jie.pictureselector.adapter.GalleryPagerAdapter;
import com.jie.pictureselector.model.ImageSelectModel;
import com.jie.pictureselector.view.CustomViewPager;

import java.util.ArrayList;
import java.util.HashMap;

public class PhotoWallPreviewActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener {

    public static String IMAGES = "images";
    public static String INDEX = "index";
    public static String SELECTED_LIST = "selectList";
    public static String CLASS_TARGET = "targetClass";
    public static int REQUEST_CODE_PREVIEW = 101;

    private CustomViewPager mViewPager;
    private GalleryPagerAdapter mAdapter;
    private ArrayList<ImageSelectModel> mPics;
    private int mCurrentIndex = 0;
    private boolean mIsNeedHideControler = false;
    private RelativeLayout mGuideLayout;
    private Class<?> targetClass;
    private int mSavaType;
    private ImageView mImageSelectStateView;
    private ImageSelectModel mCurrentImageModel;
    private HashMap<String, String> mSelectMap = new HashMap<String, String>();
    private TextView mSureTextView;
    private TextView mPageIndexTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_image_gallery;
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    protected void initView() {
        getPics();
        initBaseView();
        mViewPager.setCurrentItem(mCurrentIndex);
        mCurrentImageModel = mPics.get(mCurrentIndex);
        updateCurrentImageState();
        updateImagePageIndex(mCurrentIndex + 1);
    }

    private void updateCurrentImageState() {
        if (mCurrentImageModel.select) {
            mImageSelectStateView.setBackgroundResource(R.mipmap.checkbox_checked);
        } else {
            mImageSelectStateView.setBackgroundResource(R.mipmap.checkbox_normal);
        }

        if (mSelectMap.size() > 0) {
            mSureTextView.setText("确定(" + mSelectMap.size() + ")");
        } else {
            mSureTextView.setText("确定");
        }
    }

    private void initBaseView() {
        mViewPager = (CustomViewPager) findViewById(R.id.gallery_layout_pager);
        mAdapter = new GalleryPagerAdapter(this, mPics, this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPagerPageChangeListener());
        mImageSelectStateView = (ImageView) findViewById(R.id.iv_photo_select_state);
        mSureTextView = (TextView) findViewById(R.id.tv_sure);
        mPageIndexTextView = (TextView) findViewById(R.id.tv_title);
    }

    public void onSelectClick(View v) {
        mCurrentImageModel.select = !mCurrentImageModel.select;
        updateSelectState();
    }

    private void updateSelectState() {
        if (mCurrentImageModel.select) {
            if (mSelectMap.size() >= 9) {
                Toast.makeText(this, "最多选择9张图片", Toast.LENGTH_SHORT).show();
            } else {
                mSelectMap.put(mCurrentImageModel.url, mCurrentImageModel.url);
                mImageSelectStateView.setBackgroundResource(R.mipmap.checkbox_checked);
            }
        } else {
            if (mSelectMap.containsKey(mCurrentImageModel.url)) {
                mSelectMap.remove(mCurrentImageModel.url);
            }
            mImageSelectStateView.setBackgroundResource(R.mipmap.checkbox_normal);
        }
        mPics.get(mCurrentIndex).select = mCurrentImageModel.select;
        mSureTextView.setText("确定(" + mSelectMap.size() + ")");
    }

    private void getPics() {
        Intent intent = getIntent();
        mPics = (ArrayList<ImageSelectModel>) intent.getSerializableExtra(IMAGES);
        mCurrentIndex = intent.getIntExtra(INDEX, 0);
        mSelectMap = (HashMap<String, String>) intent.getSerializableExtra(SELECTED_LIST);
        if (intent.hasExtra(CLASS_TARGET)) {
            targetClass = (Class<?>) intent.getSerializableExtra(CLASS_TARGET);
        }
    }

    private class ViewPagerPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            mCurrentIndex = arg0;
            mCurrentImageModel = mPics.get(mCurrentIndex);
            updateCurrentImageState();
            updateImagePageIndex(mCurrentIndex + 1);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    private void updateImagePageIndex(int page) {
        mPageIndexTextView.setText(this.getResources().getString(R.string.image_page_index, page, mPics.size()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
          /*  case R.id.tv_title:
                onBack();
                break;
            default:
                onBack();
                break;*/
        }

    }

    public void onSureClick(View v) {
        onBack(RESULT_OK);
    }

    protected void onBack(int state) {
        Intent intent = new Intent();
        intent.putExtra(INDEX, mCurrentIndex);
        intent.putExtra(SELECTED_LIST, mSelectMap);
        intent.putExtra(IMAGES, mPics);
        setResult(state, intent);
        this.finish();
    }

    public void onBackClick(View v) {
        onBack(RESULT_CANCELED);
    }

    @Override
    public void onBackPressed() {
        onBack(RESULT_CANCELED);
    }
}
