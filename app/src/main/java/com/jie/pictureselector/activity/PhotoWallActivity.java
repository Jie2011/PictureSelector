package com.jie.pictureselector.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.jie.pictureselector.R;
import com.jie.pictureselector.activity.view.IPhotoWallView;
import com.jie.pictureselector.adapter.PhotoWallAdapter;
import com.jie.pictureselector.adapter.PhotoWallAdapter.PhotoImageSelectListener;
import com.jie.pictureselector.constant.Constant;
import com.jie.pictureselector.model.ImageSelectModel;
import com.jie.pictureselector.presenter.PhotoWallPresenter;
import com.jie.pictureselector.utils.ScreenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 选择照片页面 Created by liumingjie on 15-9-15.
 */
public class PhotoWallActivity extends BaseActivity implements OnItemClickListener, PhotoImageSelectListener, View.OnClickListener, IPhotoWallView {

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

    private int mType, isCut;
    private Button confirmBtn;
    private ArrayList<String> mSelectList = new ArrayList<String>();

    @Override
    protected int getLayoutId() {
        return R.layout.photo_wall;
    }

    private PhotoWallPresenter mPhotoWallPresenter;

    @Override
    protected void initView() {
        ScreenUtils.initScreen(this);
        initIntentData();
        initPersenter();
        initBaseView();
        initGridView();
    }


    private void initPersenter(){
        mPhotoWallPresenter = new PhotoWallPresenter(this);
        mPhotoWallPresenter.initLastestScreen();
        list = mPhotoWallPresenter.getList();
    }

    private void initIntentData() {
        mType = getIntent().getIntExtra(TYPE, TYPE_SINGLE);
        isCut = getIntent().getIntExtra(IS_CUT, CUT);
        if (getIntent().hasExtra(SELECT_LIST)) {
            mSelectList = (ArrayList<String>) getIntent().getSerializableExtra(SELECT_LIST);
        }
        cutFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/senba/cache/photo/";
    }

    private void initBaseView() {
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
        backBtn.setOnClickListener(this);
    }

    private void initGridView() {
        mPhotoWall = (GridView) findViewById(R.id.photo_wall_grid);
        adapter = new PhotoWallAdapter(this,list, mType, this, mSelectList);
        mPhotoWall.setAdapter(adapter);
        mPhotoWall.setOnItemClickListener(this);
        // 选择照片完成
        confirmBtn.setOnClickListener(this);

        if (mSelectList != null && mSelectList.size() > 0) {
            confirmBtn.setText("确定(" + mSelectList.size() + ")");
        }
    }

    /**
     * 多张图片选择时finish方法
     *
     * @param paths
     */
    private void multipleTypeFinish(ArrayList<String> paths) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(PHOTO_URLS, paths);
        setResult(RESULT_OK, intent);
        PhotoWallActivity.this.finish();
    }


    /**
     * 点击返回时，跳转至相册页面
     */
    private void backAction() {
        Intent intent = new Intent(this, PhotoAlbumActivity.class);
        if (list != null && list.size() > 0) {
            intent.putExtra("latest_count", list.size());
            intent.putExtra("latest_first_img", list.get(0).url);
        }
        startActivity(intent);
        // 动画
        overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        this.finish();
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
            int code = intent.getIntExtra(Constant.TYPE_ALBUM, -1);
            String folderPath = null;
            if (code == Constant.TYPE_ALBUM_OTHER) {
                folderPath = intent.getStringExtra(Constant.TYPE_FOLDERPATH);
            }
            mPhotoWallPresenter.enterAlbum(code, folderPath);
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
        } else {
            Intent intent = new Intent(this, PhotoWallPreviewActivity.class);
            intent.putExtra(PhotoWallPreviewActivity.IMAGES, list);
            intent.putExtra(PhotoWallPreviewActivity.INDEX, position);
            HashMap<String, String> map = adapter.getSelectionMap();
            intent.putExtra(PhotoWallPreviewActivity.SELECTED_LIST, map);
            startActivityForResult(intent, PhotoWallPreviewActivity.REQUEST_CODE_PREVIEW);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Intent imageIntent = new Intent();
                ArrayList<String> paths = new ArrayList<String>();
                paths.add(cutFileName);
                imageIntent.putExtra("photoUrls", paths);
                setResult(RESULT_OK, imageIntent);
                this.finish();
            } else if (requestCode == PhotoWallPreviewActivity.REQUEST_CODE_PREVIEW) {
                Map<String, String> selectMap = (HashMap<String, String>) data.getSerializableExtra(PhotoWallPreviewActivity.SELECTED_LIST);
                ArrayList<String> paths = mPhotoWallPresenter.getSelectImagePaths(selectMap, mSelectList);
                multipleTypeFinish(paths);
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == PhotoWallPreviewActivity.REQUEST_CODE_PREVIEW) {
                HashMap<String, String> selectMap = (HashMap<String, String>) data.getSerializableExtra(PhotoWallPreviewActivity.SELECTED_LIST);
                adapter.setSelectionMap(selectMap);
                adapter.notifyDataSetChanged();
                onSelectCount(selectMap.size());
            }
        }
    }

    @Override
    public void onSelectCount(int count) {
        if (count > 0) {
            confirmBtn.setText("确定(" + count + ")");
        } else {
            confirmBtn.setText("确定");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topbar_right_btn:
                if (mType == TYPE_SINGLE) {
                    setResult(RESULT_OK);
                    PhotoWallActivity.this.finish();
                } else {
                    ArrayList<String> paths = mPhotoWallPresenter.getSelectImagePaths(adapter.getSelectionMap(), mSelectList);
                    multipleTypeFinish(paths);
                }
                break;
            case R.id.topbar_left_btn:
                backAction();
                break;
        }

    }

    @Override
    public void showLoadingView() {

    }

    @Override
    public void updatePhotoWall() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public ContentResolver getActivityContentResolver() {
        return getContentResolver();
    }

    @Override
    public void setTitleString(String title) {
        titleTV.setText(title);
    }

    @Override
    public void setTitleString(int titleId) {
        titleTV.setText(titleId);
    }

    @Override
    public void smoothScrollToPosition(int pos) {
        mPhotoWall.smoothScrollToPosition(pos);
    }

}
