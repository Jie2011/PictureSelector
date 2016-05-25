package com.jie.pictureselector.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.jie.pictureselector.R;
import com.jie.pictureselector.image.SenbaImageLoader;
import com.jie.pictureselector.zoomable.ZoomableDraweeView;

/**
 * 类说明：
 * 
 * @author liumingjie
 * @date 2015.06.09
 * @version 1.0
 */
public class PhotoWallPreviewView {

	private Context mContext;
	private View mView;
	private ZoomableDraweeView mImageView;

	private String mBigImageUrl;

	private boolean mIsLoaded = false;

	public PhotoWallPreviewView(Context context, String bigImageUrl) {
		mBigImageUrl = bigImageUrl;
		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
		mView = LayoutInflater.from(context).inflate(
				R.layout.view_gallery_image, null);
		mImageView = (ZoomableDraweeView) mView
				.findViewById(R.id.big_image_browse_gestureimageview);
	}

	public View getView() {
		return mView;
	}

	public void setGestureImageViewClickListener(OnClickListener listener) {
		mImageView.setOnDraweeClickListener(listener);
	}

	/**
	 * 加载并显示初始图片,如果图片已经加载过或者图片正在加载中，则该方法不会有任何效果
	 */
	public void loadAndShowImage() {
		reloadImageView();
	}

	public String getBigImageUrl() {
		return mBigImageUrl;
	}

	private void reloadImageView() {
		if (!TextUtils.isEmpty(mBigImageUrl)) {
			SenbaImageLoader.newInstance(mContext).setImage(mImageView, mBigImageUrl, R.color.black);
			mIsLoaded = true;
		}

	}

}
