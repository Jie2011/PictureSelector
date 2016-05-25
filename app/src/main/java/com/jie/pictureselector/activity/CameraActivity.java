package com.jie.pictureselector.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.jie.pictureselector.R;
import com.jie.pictureselector.constant.FileConstant;

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
						Uri uri = Uri.fromFile(new File(tempFileName));

						Intent intent = new Intent("com.android.camera.action.CROP");
						intent.setDataAndType(uri, "image/*");
						intent.putExtra("crop", "true");
						// aspectX aspectY 是宽高的比例
						intent.putExtra("aspectX", 1);
						intent.putExtra("aspectY", 1);
						// outputX outputY 是裁剪图片宽高
						intent.putExtra("outputX", 400);
						intent.putExtra("outputY", 400);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(cutFileName)));
						intent.putExtra("scale", true);
						intent.putExtra("return-data", false);
						intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
						startActivityForResult(intent, REQUEST_CUT_PHOTO);
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
