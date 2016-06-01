package com.jie.pictureselector.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.jie.pictureselector.R;
import com.jie.pictureselector.constant.FileConstant;
import com.jie.pictureselector.utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class CameraActivity extends BaseActivity {
	
	private static final int REQUEST_CAMERA = 0x1101;
	private static final int REQUEST_CUT_PHOTO = 0x1102;
	private ImageView imageView;
	private String tempFileName,cutFileName;

	private Bitmap bm_default;
	private int mType = PhotoWallActivity.TYPE_SINGLE;
	private int mIsCut = PhotoWallActivity.NOT_CUT;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_camera_test;
	}

	@Override
	protected void initView() {
		mType = getIntent().getIntExtra(PhotoWallActivity.TYPE, PhotoWallActivity.TYPE_SINGLE);
		mIsCut = getIntent().getIntExtra(PhotoWallActivity.IS_CUT, PhotoWallActivity.NOT_CUT);
		openCamera();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//super.onActivityResult(requestCode, resultCode, data);
		Bundle extras = null;
		if (data != null) {
			extras = data.getExtras();
		}
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CAMERA:
				cutFileName = FileConstant.CACHE_PHOTE + "senbaCut_" + System.currentTimeMillis() + ".jpg";
				if (mType == PhotoWallActivity.TYPE_SINGLE) {
					if (mIsCut == PhotoWallActivity.CUT) {
						startActivityForResult(Utils.getCropIntent(tempFileName,cutFileName), REQUEST_CUT_PHOTO);
					} else {
						cutFileName = tempFileName;
						close();
					}
				} else {
					cutFileName = tempFileName;
					close();
				}
				break;
			case REQUEST_CUT_PHOTO:
				close();
				break;
			}
		}else{
			if(requestCode == REQUEST_CAMERA){
				setResult(RESULT_CANCELED);
				this.finish();
			}else{
				openCamera();
			}
		}
	}
	
	private void close(){
		Intent imageIntent = new Intent();
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(cutFileName);
		imageIntent.putStringArrayListExtra(SelectImageHomeActivity.PHOTO_URLS, paths);
		setResult(RESULT_OK, imageIntent);
		this.finish();
	}
	
	private void openCamera() {
		File picDir = new File(FileConstant.CACHE_PHOTE);
		if (!picDir.exists()) {
			picDir.mkdirs();
		}
		tempFileName = FileConstant.CACHE_PHOTE + "picture_" + System.currentTimeMillis()
				+ ".jpg";
		Intent intent = new Intent();
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(tempFileName)));
		startActivityForResult(intent, REQUEST_CAMERA);
	}

}
