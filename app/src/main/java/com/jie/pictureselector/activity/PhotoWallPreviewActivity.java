package com.jie.pictureselector.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jie.pictureselector.R;
import com.jie.pictureselector.activity.view.IPhotoPreviewView;
import com.jie.pictureselector.adapter.GalleryPagerAdapter;
import com.jie.pictureselector.model.ImageSelectModel;
import com.jie.pictureselector.presenter.PhotoPreviewPresenter;
import com.jie.pictureselector.view.CustomViewPager;

import java.util.List;

public class PhotoWallPreviewActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener, IPhotoPreviewView {

    public static String IMAGES = "images";
    public static String INDEX = "index";
    public static String SELECTED_LIST = "selectList";
    public static int REQUEST_CODE_PREVIEW = 101;

    private CustomViewPager mViewPager;
    private GalleryPagerAdapter mAdapter;
    private ImageView mImageSelectStateView;
    private ImageSelectModel mCurrentImageModel;
    private TextView mSureTextView;
    private TextView mPageIndexTextView;

    private PhotoPreviewPresenter mPhotoPreviewPresenter;

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
        initBaseView();
        initPresenter();
    }

    private void initPresenter() {
        mPhotoPreviewPresenter = new PhotoPreviewPresenter(this);
    }

    @Override
    public void updateCurrentImageState(boolean selected) {
        if (selected) {
            mImageSelectStateView.setImageResource(R.mipmap.checkbox_checked);
        } else {
            mImageSelectStateView.setImageResource(R.mipmap.checkbox_normal);
        }
    }

    private void initBaseView() {
        mViewPager = (CustomViewPager) findViewById(R.id.gallery_layout_pager);
        mAdapter = new GalleryPagerAdapter(this, this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPagerPageChangeListener());
        mImageSelectStateView = (ImageView) findViewById(R.id.iv_photo_select_state);
        mSureTextView = (TextView) findViewById(R.id.tv_sure);
        mPageIndexTextView = (TextView) findViewById(R.id.tv_title);
    }

    public void onSelectClick(View v) {
        mPhotoPreviewPresenter.onSelectClick();
    }

    private class ViewPagerPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            if (mPhotoPreviewPresenter != null) {
                mPhotoPreviewPresenter.setCurrentImageModel(arg0);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    @Override
    public void updateImagePageIndexText(int page, int size) {
        mPageIndexTextView.setText(this.getResources().getString(R.string.image_page_index, page, size));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void onSureClick(View v) {
        onBack(RESULT_OK);
    }

    protected void onBack(int state) {
        Intent intent = new Intent();
        intent.putExtra(INDEX, mPhotoPreviewPresenter.getCurrentIndex());
        intent.putExtra(SELECTED_LIST, mPhotoPreviewPresenter.getSelectMap());
        intent.putExtra(IMAGES, mPhotoPreviewPresenter.getPics());
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


    @Override
    public Intent getActivityIntent() {
        return getIntent();
    }

    public void setSureBtnString(String sureTextView) {
        mSureTextView.setText(sureTextView);
    }

    @Override
    public void setCurrentItem(int currentItem) {
        mViewPager.setCurrentItem(currentItem);
    }

    @Override
    public void updateAdapter(List<ImageSelectModel> list) {
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void toastBeyondPicSize() {
        Toast.makeText(this, "最多选择9张图片", Toast.LENGTH_SHORT).show();
    }
}
