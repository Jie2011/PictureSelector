package com.jie.pictureselector.presenter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.jie.pictureselector.R;
import com.jie.pictureselector.activity.PhotoWallActivity;
import com.jie.pictureselector.activity.view.IPhotoAlbumView;
import com.jie.pictureselector.constant.Constant;
import com.jie.pictureselector.model.PhotoAlbumLVItem;
import com.jie.pictureselector.utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by liumingjie on 2016/6/1.
 */
public class PhotoAlbumPresenter extends BasePresenter<IPhotoAlbumView> {

    private ArrayList<PhotoAlbumLVItem> mPhotoAlbumLVItems;
    private PhotoAlbumLVItem mLastestAlbum;

    public PhotoAlbumPresenter(Context context, IPhotoAlbumView baseView) {
        super(baseView);
        initActivityIntentData(context);
    }

    private void initActivityIntentData(Context context) {
        Intent intent = getBaseView().getActivityIntent();
        mLastestAlbum = new PhotoAlbumLVItem(context.getApplicationContext().getResources().getString(R.string.latest_image),
                intent.getIntExtra("latest_count", -1), intent.getStringExtra("latest_first_img"));
        mPhotoAlbumLVItems = new ArrayList<PhotoAlbumLVItem>();
        //“最近照片”
        mPhotoAlbumLVItems.add(mLastestAlbum);
        //相册
        mPhotoAlbumLVItems.addAll(getImagePathsByContentProvider());
    }

    public ArrayList<PhotoAlbumLVItem> getPhotoAlbumLVItems() {
        return mPhotoAlbumLVItems;
    }

    /**
     * 使用ContentProvider读取SD卡所有图片。
     */
    private ArrayList<PhotoAlbumLVItem> getImagePathsByContentProvider() {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;
        ContentResolver mContentResolver = getBaseView().getActivityContentResolver();
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

    public void onItemClick(Context context, int position) {
        Intent intent = new Intent(context.getApplicationContext(), PhotoWallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //第一行为“最近照片”
        if (position == 0) {
            intent.putExtra(Constant.TYPE_ALBUM, Constant.TYPE_ALBUM_LASTEST);
        } else {
            intent.putExtra(Constant.TYPE_ALBUM, Constant.TYPE_ALBUM_OTHER);
            intent.putExtra(Constant.TYPE_FOLDERPATH, mPhotoAlbumLVItems.get(position).getPathName());
        }
        getBaseView().gotoPhotoWallActivity(intent);
    }

    public void backAction(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), PhotoWallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("isExist", true);
        getBaseView().gotoPhotoWallActivity(intent);
    }
}
