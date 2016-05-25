package com.jie.pictureselector.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.jie.pictureselector.R;
import com.jie.pictureselector.adapter.PhotoWallAdapter;
import com.jie.pictureselector.adapter.PhotoWallAdapter.PhotoImageSelectListener;
import com.jie.pictureselector.model.ImageSelectModel;
import com.jie.pictureselector.utils.ScreenUtils;
import com.jie.pictureselector.utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选择照片页面 Created by liumingjie on 15-9-15.
 */
public class PhotoWallActivity extends BaseActivity implements OnItemClickListener,PhotoImageSelectListener {

	private static final String PHOTO_URLS = "photoUrls";

	public static final String IS_CUT = "isCut";
	
	public static final int CUT = 1;
	
	public static final int NOT_CUT = 2;
	
	public static final String TYPE = "type";
	/**
	 * 选单张
	 */
	public static final int TYPE_SINGLE = 1;
	/**
	 * 选多张
	 */
	public static final int TYPE_MULTIPLE = 2;
	
	public static final String SELECT_LIST = "list";

	private TextView titleTV;

	private ArrayList<ImageSelectModel> list = new ArrayList<ImageSelectModel>();
	private GridView mPhotoWall;
	private PhotoWallAdapter adapter;

	private String cutFileName = "";
	
	/**
	 * 当前文件夹路径
	 */
	private String currentFolder = null;
	/**
	 * 当前展示的是否为最近照片
	 */
	private boolean isLatest = true;

	private int mType,isCut;
	private Button confirmBtn;
	private ArrayList<String> mSelectList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.photo_wall;
	}

	@Override
	protected void initView() {
// 获取屏幕像素
		ScreenUtils.initScreen(this);
		setContentView(R.layout.photo_wall);
		mType = getIntent().getIntExtra(TYPE, TYPE_SINGLE);
		isCut = getIntent().getIntExtra(IS_CUT, CUT);
		if(getIntent().hasExtra(SELECT_LIST)){
			mSelectList = (ArrayList<String>)getIntent().getSerializableExtra(SELECT_LIST);
		}
		cutFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/senba/cache/photo/";

		titleTV = (TextView) findViewById(R.id.topbar_title_tv);
		titleTV.setText(R.string.latest_image);

		Button backBtn = (Button) findViewById(R.id.topbar_left_btn);
		confirmBtn = (Button) findViewById(R.id.topbar_right_btn);
		backBtn.setText(R.string.photo_album);
		backBtn.setVisibility(View.VISIBLE);
		if (mType == TYPE_SINGLE) {
			confirmBtn.setText(R.string.main_cancel);
		} else {
			confirmBtn.setText(R.string.main_confirm);
		}
		confirmBtn.setVisibility(View.VISIBLE);

		mPhotoWall = (GridView) findViewById(R.id.photo_wall_grid);
		List<String> l = getLatestImagePaths(100);
		if(l!=null){
			for(String s:l){
				ImageSelectModel m = new ImageSelectModel(s,false);
				list.add(m);
			}
		}
		//list = getLatestImagePaths(100);
		adapter = new PhotoWallAdapter(this, list, mType,this,mSelectList);
		mPhotoWall.setAdapter(adapter);
		mPhotoWall.setOnItemClickListener(this);
		// 选择照片完成
		confirmBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 选择图片完成,回到起始页面
				// TODO
				if(mType == TYPE_SINGLE){
					setResult(RESULT_OK);
					PhotoWallActivity.this.finish();
				}else{
					ArrayList<String> paths = getSelectImagePaths(adapter.getSelectionMap());
					multipleTypeFinish(paths);
				}
			}
		});

		if(mSelectList!=null && mSelectList.size()>0){
			confirmBtn.setText("确定("+mSelectList.size()+")");
		}
		// 点击返回，回到选择相册页面
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backAction();
			}
		});
	}

	/**
	 * 多张图片选择时finish方法
	 * @param paths
	 */
	private void multipleTypeFinish(ArrayList<String> paths){
		Intent intent = new Intent();
		intent.putStringArrayListExtra(PHOTO_URLS, paths);
		setResult(RESULT_OK, intent);
		PhotoWallActivity.this.finish();
	}

	/* *//**
	 * 第一次跳转至相册页面时，传递最新照片信息
	 */
	/*
	 * private boolean firstIn = true;
	 */

	/**
	 * 点击返回时，跳转至相册页面
	 */
	private void backAction() {
		Intent intent = new Intent(this, PhotoAlbumActivity.class);

		// 传递“最近照片”分类信息
		// if (firstIn) {
		if (list != null && list.size() > 0) {
			intent.putExtra("latest_count", list.size());
			intent.putExtra("latest_first_img", list.get(0).url);
		}
		// firstIn = false;
		// }

		startActivity(intent);
		// 动画
		overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		this.finish();
	}

	/**
	 * 根据图片所属文件夹路径，刷新页面
	 */
	private void updateView(int code, String folderPath) {
		list.clear();
		adapter.clearSelectionMap();
		adapter.notifyDataSetChanged();

		if (code == 100) { // 某个相册
			int lastSeparator = folderPath.lastIndexOf(File.separator);
			String folderName = folderPath.substring(lastSeparator + 1);
			titleTV.setText(folderName);
			List<String> l = getAllImagePathsByFolder(folderPath);
			if(l != null && l.size()>0){
				for(String s:l){
					ImageSelectModel m = new ImageSelectModel(s,false);
					list.add(m);
				}
			}
		} else if (code == 200) { // 最近照片
			titleTV.setText(R.string.latest_image);
			List<String> l = getLatestImagePaths(100);
			if(l != null && l.size()>0){
				for(String s:l){
					ImageSelectModel m = new ImageSelectModel(s,false);
					list.add(m);
				}
			}
		}

		adapter.notifyDataSetChanged();
		if (list.size() > 0) {
			// 滚动至顶部
			mPhotoWall.smoothScrollToPosition(0);
		}
	}

	/**
	 * 获取指定路径下的所有图片文件。
	 */
	private ArrayList<String> getAllImagePathsByFolder(String folderPath) {
		File folder = new File(folderPath);
		String[] allFileNames = folder.list();
		if (allFileNames == null || allFileNames.length == 0) {
			return null;
		}

		ArrayList<String> imageFilePaths = new ArrayList<String>();
		for (int i = allFileNames.length - 1; i >= 0; i--) {
			if (Utility.isImage(allFileNames[i])) {
				imageFilePaths.add(folderPath + File.separator + allFileNames[i]);
			}
		}

		return imageFilePaths;
	}

	/**
	 * 使用ContentProvider读取SD卡最近图片。
	 */
	private ArrayList<String> getLatestImagePaths(int maxCount) {
		Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

		String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
		String key_DATA = MediaStore.Images.Media.DATA;

		ContentResolver mContentResolver = getContentResolver();

		// 只查询jpg和png的图片,按最新修改排序
		Cursor cursor = mContentResolver.query(mImageUri, new String[] { key_DATA }, key_MIME_TYPE + "=? or "
				+ key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?", new String[] { "image/jpg", "image/jpeg",
				"image/png" }, MediaStore.Images.Media.DATE_MODIFIED);

		ArrayList<String> latestImagePaths = null;
		if (cursor != null) {
			// 从最新的图片开始读取.
			// 当cursor中没有数据时，cursor.moveToLast()将返回false
			if (cursor.moveToLast()) {
				latestImagePaths = new ArrayList<String>();

				while (true) {
					// 获取图片的路径
					String path = cursor.getString(0);
					latestImagePaths.add(path);

					if (latestImagePaths.size() >= maxCount || !cursor.moveToPrevious()) {
						break;
					}
				}
			}
			cursor.close();
		}

		return latestImagePaths;
	}

	// 获取已选择的图片路径
	private ArrayList<String> getSelectImagePaths(Map<String,String> map) {
		if (map.size() == 0) {
			return null;
		}

		if(mSelectList != null && mSelectList.size()>0){
			for(String s:mSelectList){
				if(map.containsKey(s)){
					map.remove(s);
				}
			}
		}
		ArrayList<String> selectedImageList = new ArrayList<String>();

		for(Map.Entry<String, String> entry:map.entrySet()){    
		     selectedImageList.add(entry.getValue());
		}
		return selectedImageList;
	}

	// 从相册页面跳转至此页
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.hasExtra("isExist")) {
			onBackPressed();
		} else {
			// 动画
			overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);

			int code = intent.getIntExtra("code", -1);
			if (code == 100) {
				// 某个相册
				String folderPath = intent.getStringExtra("folderPath");
				if (isLatest || (folderPath != null && !folderPath.equals(currentFolder))) {
					currentFolder = folderPath;
					updateView(100, currentFolder);
					isLatest = false;
				}
			} else if (code == 200) {
				// “最近照片”
				if (!isLatest) {
					updateView(200, null);
					isLatest = true;
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (mType == TYPE_SINGLE) {
			if (isCut == CUT) {
				cutFileName = cutFileName + "senbaCut_" + System.currentTimeMillis() + ".jpg";
				Uri uri = Uri.fromFile(new File(list.get(position).url));

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
				startActivityForResult(intent, 1);
			} else {
				ArrayList<String> paths = new ArrayList<String>();
				paths.add(list.get(position).url);
				Intent intent = new Intent();
				intent.putStringArrayListExtra(PHOTO_URLS, paths);
				setResult(RESULT_OK, intent);
				PhotoWallActivity.this.finish();
			}
		}else{
			Intent intent = new Intent(this, PhotoWallPreviewActivity.class);
			intent.putExtra(PhotoWallPreviewActivity.IMAGES,list);
			intent.putExtra(PhotoWallPreviewActivity.INDEX,position);
			HashMap<String,String> map = adapter.getSelectionMap();
			intent.putExtra(PhotoWallPreviewActivity.SELECTED_LIST,map);
			startActivityForResult(intent, PhotoWallPreviewActivity.REQUEST_CODE_PREVIEW);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK ){
			if(requestCode == 1) {
				Intent imageIntent = new Intent();
				ArrayList<String> paths = new ArrayList<String>();
				paths.add(cutFileName);
				imageIntent.putExtra("photoUrls", paths);
				setResult(RESULT_OK, imageIntent);
				this.finish();
			}else if(requestCode == PhotoWallPreviewActivity.REQUEST_CODE_PREVIEW){
				Map<String ,String > selectMap = (HashMap<String,String>)data.getSerializableExtra(PhotoWallPreviewActivity.SELECTED_LIST);
				ArrayList<String> paths = getSelectImagePaths(selectMap);
				multipleTypeFinish(paths);
			}
		}else if(resultCode == RESULT_CANCELED){
			if(requestCode == PhotoWallPreviewActivity.REQUEST_CODE_PREVIEW){
				HashMap<String ,String > selectMap = (HashMap<String,String>)data.getSerializableExtra(PhotoWallPreviewActivity.SELECTED_LIST);
				adapter.setSelectionMap(selectMap);
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onSelectCount(int count) {
		if(count>0){
			confirmBtn.setText("确定("+count+")");
		}else{
			confirmBtn.setText("确定");
		}
	}

}
