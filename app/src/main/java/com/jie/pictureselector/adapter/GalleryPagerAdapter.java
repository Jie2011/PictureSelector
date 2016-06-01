package com.jie.pictureselector.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.jie.pictureselector.R;
import com.jie.pictureselector.model.ImageSelectModel;
import com.jie.pictureselector.view.PhotoWallPreviewView;

import java.util.ArrayList;
import java.util.List;

public class GalleryPagerAdapter extends PagerAdapter {
    private List<ImageSelectModel> mData = new ArrayList<>();
    private View.OnClickListener mClickListener;
    private Context mContext;

    public GalleryPagerAdapter(Context context,
                               View.OnClickListener clickListener) {
        super();
        mClickListener = clickListener;
        mContext = context;
    }

    public void setData(List<ImageSelectModel> data) {
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        PhotoWallPreviewView viewLogic = new PhotoWallPreviewView(
                mContext, mContext.getResources().getString(R.string.sd_image_url,mData.get(position).url));
        View view = viewLogic.getView();
        viewLogic.setGestureImageViewClickListener(mClickListener);
        viewLogic.loadAndShowImage();
        container.addView(view, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}