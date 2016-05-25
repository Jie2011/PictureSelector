package com.jie.pictureselector.image;

import android.app.Application;
import android.os.Environment;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.logging.FLog;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.jie.pictureselector.constant.FileConstant;

import java.io.File;

/**
 * ImageConfig
 * <p>
 * Created by liumingjie on 2016/4/18.
 */
public class SenbaImageConfig {

    private static final int MAX_DISK_CACHE_SIZE = 60 * ByteConstants.MB;

    private static SenbaImageConfig senbaImageConfig;
    private Application mApplication;

    public synchronized static SenbaImageConfig getInstance(Application application) {
        if (senbaImageConfig == null) {
            senbaImageConfig = new SenbaImageConfig(application);
        }
        return senbaImageConfig;
    }

    private void initDir() {
        try {
            File file = new File(FileConstant.IMAGE_DIR);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {

        }
    }

    private SenbaImageConfig(Application application) {
        this.mApplication = application;
        initDir();
    }

    public void initImageConfig() {
        ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(mApplication);
        configureCaches(configBuilder);
        configureOptions(configBuilder);
        FLog.setMinimumLoggingLevel(FLog.VERBOSE);
        Fresco.initialize(mApplication, configBuilder.build());
    }

    private void configureCaches(ImagePipelineConfig.Builder configBuilder) {
        configBuilder
                .setMainDiskCacheConfig(
                        DiskCacheConfig.newBuilder(mApplication)
                                .setBaseDirectoryPath(Environment.getExternalStorageDirectory().getAbsoluteFile())//缓存图片基路径
                                .setBaseDirectoryName(FileConstant.IMAGE_DIR)//文件夹名
                                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)
                                .build());
    }

    private void configureOptions(ImagePipelineConfig.Builder configBuilder) {
        configBuilder.setDownsampleEnabled(true);
    }
}
