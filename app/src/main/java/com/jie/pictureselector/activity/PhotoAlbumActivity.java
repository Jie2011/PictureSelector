package com.jie.pictureselector.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jie.pictureselector.R;
import com.jie.pictureselector.adapter.PhotoAlbumLVAdapter;
import com.jie.pictureselector.constant.Constant;
import com.jie.pictureselector.model.PhotoAlbumLVItem;
import com.jie.pictureselector.utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * 分相册查看SD卡所有图片。
 * Created by liumingjie on 14-10-14.
 */
public class PhotoAlbumActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private PhotoAlbumLVItem mLastestAlbum;
    private ArrayList<PhotoAlbumLVItem> mPhotoAlbumLVItems;

    @Override
    protected int getLayoutId() {
        return R.layout.photo_album;
    }

    @Override
    protected void initView() {
        if (!Utility.isSDcardOK()) {
            Utility.showToast(this, "SD卡不可用。");
            return;
        }
        Intent t = getIntent();
        if (!t.hasExtra("latest_count")) {
            return;
        }
        mLastestAlbum = new PhotoAlbumLVItem(getResources().getString(R.string.latest_image),
                t.getIntExtra("latest_count", -1), t.getStringExtra("latest_first_img"));
        initBaseView();
        initAlbumListview();
    }


    private void initBaseView() {
        TextView titleTV = (TextView) findViewById(R.id.topbar_title_tv);
        titleTV.setText(R.string.select_album);
        Button cancelBtn = (Button) findViewById(R.id.topbar_right_btn);
        cancelBtn.setText(R.string.main_cancel);
        cancelBtn.setVisibility(View.VISIBLE);
        cancelBtn.setOnClickListener(this);
    }

    private void initAlbumListview() {
        ListView listView = (ListView) findViewById(R.id.select_img_listView);
        mPhotoAlbumLVItems = new ArrayList<PhotoAlbumLVItem>();
        //“最近照片”
        mPhotoAlbumLVItems.add(mLastestAlbum);
        //相册
        mPhotoAlbumLVItems.addAll(getImagePathsByContentProvider());
        PhotoAlbumLVAdapter adapter = new PhotoAlbumLVAdapter(this, mPhotoAlbumLVItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    /**
     * 点击返回时，回到相册页面
     */
    private void backAction() {
        Intent intent = new Intent(PhotoAlbumActivity.this, PhotoWallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("isExist", true);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        backAction();
    }

    /**
     * 获取目录中图片的个数。
     */
    private int getImageCount(File folder) {
        int count = 0;
        File[] files = folder.listFiles();
        for (File file : files) {
            if (Utility.isImage(file.getName())) {
                count++;
            }
        }

        return count;
    }

    /**
     * 获取目录中最新的一张图片的绝对路径。
     */
    private String getFirstImagePath(File folder) {
        File[] files = folder.listFiles();
        for (int i = files.length - 1; i >= 0; i--) {
            File file = files[i];
            if (Utility.isImage(file.getName())) {
                return file.getAbsolutePath();
            }
        }

        return null;
    }

    /**
     * 使用ContentProvider读取SD卡所有图片。
     */
    private ArrayList<PhotoAlbumLVItem> getImagePathsByContentProvider() {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;
        ContentResolver mContentResolver = getContentResolver();
        // 只查询jpg和png的图片
        Cursor cursor = mContentResolver.query(mImageUri, new String[]{key_DATA},
                key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);
        ArrayList<PhotoAlbumLVItem> list = null;
        if (cursor != null) {
            if (cursor.moveToLast()) {
                //路径缓存，防止多次扫描同一目录
                HashSet<String> cachePath = new HashSet<String>();
                list = new ArrayList<PhotoAlbumLVItem>();

                while (true) {
                    // 获取图片的路径
                    String imagePath = cursor.getString(0);

                    File parentFile = new File(imagePath).getParentFile();
                    String parentPath = parentFile.getAbsolutePath();

                    //不扫描重复路径
                    if (!cachePath.contains(parentPath)) {
                        list.add(new PhotoAlbumLVItem(parentPath, getImageCount(parentFile),
                                getFirstImagePath(parentFile)));
                        cachePath.add(parentPath);
                    }

                    if (!cursor.moveToPrevious()) {
                        break;
                    }
                }
            }
            cursor.close();
        }

        return list;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //动画
        overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topbar_right_btn:
                backAction();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(PhotoAlbumActivity.this, PhotoWallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //第一行为“最近照片”
        if (position == 0) {
            intent.putExtra(Constant.TYPE_ALBUM, Constant.TYPE_ALBUM_LASTEST);
        } else {
            intent.putExtra(Constant.TYPE_ALBUM, Constant.TYPE_ALBUM_OTHER);
            intent.putExtra(Constant.TYPE_FOLDERPATH, mPhotoAlbumLVItems.get(position).getPathName());
        }
        startActivity(intent);
        PhotoAlbumActivity.this.finish();
    }
}
