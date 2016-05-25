package com.jie.pictureselector.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.jie.pictureselector.R;

import java.util.ArrayList;

/**
 * 选择照片
 * @author liumingjie
 * @date 2015.07.14
 *
 */
public class SelectImageHomeActivity extends BaseActivity {
	
	public static final String PHOTO_URLS = "photoUrls";
	public static final int CODE_PHOTO = 7;
	
	private LinearLayout mainLinearLayout;
	private static final int CODE_CAMERA = 1;
	private static final int CODE_SELECT_PHOTO = 2;
	
	private int mType = PhotoWallActivity.TYPE_SINGLE;
	private int mIsCut = PhotoWallActivity.NOT_CUT;

	private ArrayList<String> mSelectList;
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_select_image_home;
	}

	@Override
	protected void initView() {
		mType = getIntent().getIntExtra(PhotoWallActivity.TYPE, PhotoWallActivity.TYPE_SINGLE);
		mIsCut = getIntent().getIntExtra(PhotoWallActivity.IS_CUT, PhotoWallActivity.NOT_CUT);
		if(getIntent().hasExtra(PhotoWallActivity.SELECT_LIST)){
			mSelectList = (ArrayList<String>)getIntent().getSerializableExtra(PhotoWallActivity.SELECT_LIST);
		}
		mainLinearLayout = (LinearLayout) findViewById(R.id.layout_main);
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.popup_menu_entry);
		Interpolator interpolator = AnimationUtils.loadInterpolator(this,
				android.R.anim.accelerate_decelerate_interpolator);
		anim.setInterpolator(interpolator);

		mainLinearLayout.startAnimation(anim);
	}

	public void selectImageForTakePhontes(View v) {
		Intent intent = new Intent(this, CameraActivity.class);
		intent.putExtra(PhotoWallActivity.TYPE, mType);
		intent.putExtra(PhotoWallActivity.IS_CUT, mIsCut);
		this.startActivityForResult(intent,CODE_CAMERA);
	}

	public void selectImageForPhone(View v) {
		Intent intent = new Intent(this, PhotoWallActivity.class);
		intent.putExtra(PhotoWallActivity.TYPE, mType);
		intent.putExtra(PhotoWallActivity.IS_CUT, mIsCut);
		if(mSelectList != null){
			intent.putExtra(PhotoWallActivity.SELECT_LIST, mSelectList);
		}
		this.startActivityForResult(intent,CODE_SELECT_PHOTO);
	}

	public void onCancel(View v) {
		onBack();
	}

	@Override
	public void onBackPressed() {
		onBack();
	}

	protected void onBack() {
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.popup_menu_exit);
		Interpolator interpolator = AnimationUtils.loadInterpolator(this,
				android.R.anim.accelerate_decelerate_interpolator);
		anim.setInterpolator(interpolator);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation paramAnimation) {
			}

			@Override
			public void onAnimationRepeat(Animation paramAnimation) {
			}

			@Override
			public void onAnimationEnd(Animation paramAnimation) {

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						SelectImageHomeActivity.this.finish();
						SelectImageHomeActivity.this.overridePendingTransition(0, 0);
					}
				});
			}
		});
		mainLinearLayout.startAnimation(anim);

	}

	private static Handler mHandler = new Handler();

	@Override
	public void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		
		if (resultCode == RESULT_OK) {

			if (requestCode == CODE_CAMERA || requestCode == CODE_SELECT_PHOTO) {
				setResult(RESULT_OK, data);
				this.finish();
			}
		}else{
			this.finish();
		}
	}
	
	/**
	 * 进入图片选择方式页面
	 * @param activity
	 * @param typeId  类型 单选还是多选  PhotoWallActivity.TYPE_SINGLE为单选 PhontWallActivity.TYPE_MULTIPLE为多选
	 * @param selectList 已经选择图片的列表 值为图片路径
	 * @param isCut 
	 * 
	 * 在onActivityForResult方法中 requestCode == SelectImageHomeActivity.CODE_PHOTO
	 *  获取选择的图片的list intent 参数值为SelectImageHomeActivity.PHOTO_URLS 返回值为ArrayList<String>
	 * intent.getStringArrayListExtra(SelectImageHomeActivity.PHOTO_URLS)
	 */
	public static void gotoSelectImageActivity(Activity activity,int typeId,ArrayList<String> selectList,int isCut){
		Intent intent = new Intent(activity, SelectImageHomeActivity.class);
		intent.putExtra(PhotoWallActivity.TYPE, typeId);
		if(selectList != null){
			intent.putExtra(PhotoWallActivity.SELECT_LIST, selectList);
		}
		intent.putExtra(PhotoWallActivity.IS_CUT, isCut);
		activity.startActivityForResult(intent, CODE_PHOTO);
	}
}
