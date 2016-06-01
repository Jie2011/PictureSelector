package com.jie.pictureselector.presenter;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.jie.pictureselector.R;
import com.jie.pictureselector.activity.view.IPhotoWallView;
import com.jie.pictureselector.model.ImageSelectModel;
import com.jie.pictureselector.utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by liumingjie on 2016/5/31.
 */
public class PhotoWallPresenter extends BasePresenter<IPhotoWallView> {

    private ArrayList<ImageSelectModel> list = new ArrayList<ImageSelectModel>();

    public PhotoWallPresenter(IPhotoWallView baseView) {
        super(baseView);
    }

    public void initLastestScreen() {
        List<String> l = getLatestImagePaths(100);
        if (l != null) {
            for (String s : l) {
                ImageSelectModel m = new ImageSelectModel(s, false);
                list.add(m);
            }
        }
    }

    /**
     * 使用ContentProvider读取SD卡最近图片。
     */
    private ArrayList<String> getLatestImagePaths(int maxCount) {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = getBaseView().getActivityContentResolver();

        // 只查询jpg和png的图片,按最新修改排序
        Cursor cursor = mContentResolver.query(mImageUri, new String[]{key_DATA}, key_MIME_TYPE + "=? or "
                + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?", new String[]{"image/jpg", "image/jpeg",
                "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);

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

    public ArrayList<ImageSelectModel> getList() {
        return list;
    }

    /**
     * 获取已选择的图片路径
     * @param map  选择的map
     * @param mSelectList
     */
    public void onMultiplePicSelectComplete(Map<String, String> map, ArrayList<String> mSelectList) {
        if (mSelectList != null && mSelectList.size() > 0) {
            for (String s : mSelectList) {
                if (map.containsKey(s)) {
                    map.remove(s);
                }
            }
        }
        ArrayList<String> selectedImageList = new ArrayList<String>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            selectedImageList.add(entry.getValue());
        }
       getBaseView().onMultipleTypeFinish(selectedImageList);
    }

    /**
     * 获取指定路径下的所有图片文件。
     */
    public ArrayList<String> getAllImagePathsByFolder(String folderPath) {
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
     * 根据图片所属文件夹路径，刷新页面
     */
    public void enterAlbum(int code, String folderPath) {
        list.clear();
        if (code == 100) { // 某个相册
            int lastSeparator = folderPath.lastIndexOf(File.separator);
            String folderName = folderPath.substring(lastSeparator + 1);
            getBaseView().setTitleString(folderName);
            List<String> l = getAllImagePathsByFolder(folderPath);
            if (l != null && l.size() > 0) {
                for (String s : l) {
                    ImageSelectModel m = new ImageSelectModel(s, false);
                    list.add(m);
                }
            }
        } else if (code == 200) { // 最近照片
            getBaseView().setTitleString(R.string.latest_image);
            List<String> l = getLatestImagePaths(100);
            if (l != null && l.size() > 0) {
                for (String s : l) {
                    ImageSelectModel m = new ImageSelectModel(s, false);
                    list.add(m);
                }
            }
        }
        getBaseView().updatePhotoWall();
        if (list.size() > 0) {
            // 滚动至顶部
            getBaseView().smoothScrollToPosition(0);
        }
    }
}
