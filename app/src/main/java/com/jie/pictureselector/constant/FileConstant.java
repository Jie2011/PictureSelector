package com.jie.pictureselector.constant;

import android.os.Environment;

/**
 * Created by liumingjie on 2016/4/18.
 */
public class FileConstant {
    /**
     * 缓存图片目录
     */
    public static String IMAGE_DIR = "/pictureselector/cache/img";

    /** SD卡目录 */
    public static String SDCARD_DIR = Environment.getExternalStorageDirectory()
            .getAbsolutePath();

    /** 软件根目录 */
    public static String ROOT_DIR = SDCARD_DIR + "/pictureselector";

    /** 缓存根目录 */
    public static String CACHE_DIR = ROOT_DIR + "/cache";

    /**
     * 缓存图片目录
     */
    public static String CACHE_PHOTE = CACHE_DIR + "/photo/";
}
