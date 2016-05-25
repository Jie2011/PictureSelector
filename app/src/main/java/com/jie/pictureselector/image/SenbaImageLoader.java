package com.jie.pictureselector.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.UriUtil;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.GenericDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.jie.pictureselector.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片加载loader
 * Created by liumingjie on 2016/4/18.
 */
public class SenbaImageLoader {

    private static int DEFAULT_HOLDER_RESOURCE_ID = R.drawable.main_background;
    private static int DEFAULT_ERROR_RESOURCE_ID = R.drawable.main_background;

    private static SenbaImageLoader mSenbaImageLoader;
    private Context mContext;

    public static synchronized SenbaImageLoader newInstance(Context context) {
        if (mSenbaImageLoader == null) {
            mSenbaImageLoader = new SenbaImageLoader(context);
        }
        return mSenbaImageLoader;
    }

    private SenbaImageLoader(Context context) {
        this.mContext = context;
    }

    /**
     * 设置图片 图片默认占位图和加载错误占位图为 R.drawable.main_background
     *
     * @param simpleDraweeView
     * @param imageUrl         图片Url地址
     */
    public void setImage(GenericDraweeView simpleDraweeView, @Nullable String imageUrl) {
        setImage(simpleDraweeView, imageUrl, DEFAULT_HOLDER_RESOURCE_ID, DEFAULT_ERROR_RESOURCE_ID, null);
    }

    /**
     * 设置图片 根据设置的宽高来加载图片，图片默认占位图和加载错误占位图为 R.drawable.main_background
     *
     * @param simpleDraweeView
     * @param imageUrl         图片Url地址
     * @param resizeOptions    设置图片显示的宽高，防止出现OOM现象，一般对于本地图片
     */
    public void setImage(GenericDraweeView simpleDraweeView, @Nullable String imageUrl, ResizeOptions resizeOptions) {
        setImage(simpleDraweeView, imageUrl, DEFAULT_HOLDER_RESOURCE_ID, DEFAULT_ERROR_RESOURCE_ID, resizeOptions);
    }

    /**
     * 设置图片 默认占位图为defaultHolderResourceId
     *
     * @param simpleDraweeView
     * @param imageUrl
     * @param defaultHolderResourceId 默认图片占位图
     */
    public void setImage(GenericDraweeView simpleDraweeView, @Nullable String imageUrl, int defaultHolderResourceId) {
        setImage(simpleDraweeView, imageUrl, defaultHolderResourceId, defaultHolderResourceId, null);
    }

    /**
     * 设置图片 默认占位图，设置图片显示的宽高
     *
     * @param simpleDraweeView
     * @param imageUrl
     * @param defaultHolderResourceId 默认图片占位图
     * @param resizeOptions           设置图片显示的宽高，防止出现OOM现象，一般对于本地图片
     */
    public void setImage(GenericDraweeView simpleDraweeView, @Nullable String imageUrl, int defaultHolderResourceId, ResizeOptions resizeOptions) {
        setImage(simpleDraweeView, imageUrl, defaultHolderResourceId, defaultHolderResourceId, resizeOptions);
    }

    /**
     * 设置图片
     *
     * @param simpleDraweeView
     * @param imageUrl                图片Url地址
     * @param defaultHolderResourceId 图片占位图
     * @param deafultErrorResourceId  图片加载错误占位图
     * @param resizeOptions           设置图片显示的宽高，防止出现OOM现象，一般对于本地图片
     */
    public void setImage(GenericDraweeView simpleDraweeView, @Nullable String imageUrl, int defaultHolderResourceId, int deafultErrorResourceId, ResizeOptions resizeOptions) {
        if (TextUtils.isEmpty(imageUrl)) {
            return;
        }
        ImageRequestBuilder imageRequestBuilder =
                ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUrl));
        if (UriUtil.isNetworkUri(Uri.parse(imageUrl))) {
            imageRequestBuilder.setProgressiveRenderingEnabled(true);
        } else {
            if (resizeOptions != null) {
                imageRequestBuilder.setResizeOptions(resizeOptions);
            }
        }
        GenericDraweeHierarchy draweeHierarchy = createDraweeHierarchy(simpleDraweeView, defaultHolderResourceId, deafultErrorResourceId);
        DraweeController controller = createDraweeController(simpleDraweeView, imageUrl).setImageRequest(imageRequestBuilder.build()).build();
        controller.setHierarchy(draweeHierarchy);
        simpleDraweeView.setController(controller);
    }

    /**
     * 创建DraweeHieraychy对象,由于每个DraweeView只能跟一个DraweeHierarchy绑定，
     * 所以只能先通过simpleDraweeView.getHierarchy()获取，判断是否为空，空则重新new一个
     *
     * @param simpleDraweeView
     * @param defaultHolderResourceId 图片占位图
     * @param deafultErrorResourceId  图片加载错误占位图
     * @return GenericDraweeHierarchy
     */
    private GenericDraweeHierarchy createDraweeHierarchy(GenericDraweeView simpleDraweeView, int defaultHolderResourceId, int deafultErrorResourceId) {
        GenericDraweeHierarchy draweeHierarchy = simpleDraweeView.getHierarchy();
        if (draweeHierarchy == null) {
            draweeHierarchy = GenericDraweeHierarchyBuilder.newInstance(mContext.getResources()).build();
        }
        draweeHierarchy.setPlaceholderImage(defaultHolderResourceId);
        draweeHierarchy.setFailureImage(mContext.getResources().getDrawable(deafultErrorResourceId));
        return draweeHierarchy;
    }

    /**
     * 创建DraweeController对象
     *
     * @param simpleDraweeView
     * @param imageUrl
     * @return PipelineDraweeControllerBuilder
     */
    private PipelineDraweeControllerBuilder createDraweeController(GenericDraweeView simpleDraweeView, String imageUrl) {
        return Fresco.newDraweeControllerBuilder().setUri(imageUrl).setOldController(simpleDraweeView.getController());
    }

    /**
     * 根据宽高创建ResizeOptions
     * @param width
     * @param height
     * @return
     */
    public ResizeOptions createResizeOptions(int width,int height){
        return  new ResizeOptions(width,height);
    }

    /**
     * 从resource下获取Bitmap（一般情况下不建议使用该方法）
     * @param id
     * @return
     */
    public Bitmap getResoureImage(int id){
        return BitmapFactory.decodeResource(mContext.getResources(), id);
    }

    /**
     * 从sd卡上获取bitmap（一般情况下不建议使用该方法）
     * @param file
     * @return
     */
    public Bitmap getSdImage(File file){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    /**
     * recycle ImageView的src图片
     * @param imageView
     */
    public void recycleImageViewBitMap(ImageView imageView) {
        if (imageView != null && imageView.getDrawable() instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) imageView.getDrawable();
            rceycleBitmapDrawable(bd);
        }
    }

    /**
     * 回收BitmapDreawable图片
     * @param bitmapDrawable
     */
    public void rceycleBitmapDrawable(BitmapDrawable bitmapDrawable) {
        if (bitmapDrawable != null) {
            Bitmap bitmap = bitmapDrawable.getBitmap();
            rceycleBitmap(bitmap);
        }
        bitmapDrawable = null;
    }

    public void rceycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
    }

    /**
     *recycle Imageview的background背景图
     *
     */
    public void recycleBackgroundBitMap(ImageView view) {
        if (view != null) {
            BitmapDrawable bd = (BitmapDrawable) view.getBackground();
            rceycleBitmapDrawable(bd);
        }
    }
    public void clearCache(){
        Fresco.getImagePipeline().clearCaches();
    }

    public void savaImage(final Context context, final String imageUri) {
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUri)).build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchImageFromBitmapCache(imageRequest, null);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(@Nullable Bitmap bitmap) {
                final boolean state = saveBitmap(bitmap, URLUtil.guessFileName(imageUri, null, null));
                Toast.makeText(context, state ? "图片保存成功" : "图片保存失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                Toast.makeText(context, "图片保存失败", Toast.LENGTH_SHORT).show();
            }
        }, CallerThreadExecutor.getInstance());

    }

    /** 保存方法 */
    private  boolean saveBitmap(Bitmap bm, String fileName) {
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera",
                fileName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            Log.e("e", e.toString() + "");
            return false;
        } finally {

        }
    }

    /**
     * 根据imageurl获取sd卡中缓存的file
     * @param imageUrl
     * @return file
     */
    public File getFileFromSdByUrl(String imageUrl){
        FileBinaryResource resource = (FileBinaryResource) Fresco.getImagePipelineFactory()
                .getMainDiskStorageCache()
                .getResource(new SimpleCacheKey(imageUrl));
        if(resource == null){
            return null;
        }
        return resource.getFile();
    }
}
