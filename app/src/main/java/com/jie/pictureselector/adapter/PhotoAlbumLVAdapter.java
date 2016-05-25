package com.jie.pictureselector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.jie.pictureselector.R;
import com.jie.pictureselector.image.SenbaImageLoader;
import com.jie.pictureselector.model.PhotoAlbumLVItem;
import com.jie.pictureselector.utils.UITools;

import java.io.File;
import java.util.ArrayList;

/**
 * 选择相册页面,ListView的adapter
 * Created by liumingjie on 14-10-14.
 */
public class PhotoAlbumLVAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PhotoAlbumLVItem> list;

    private ResizeOptions mResizeOptions;
    private SenbaImageLoader senbaImageLoader;

    private void initSenbaImageLoader(){
        senbaImageLoader = SenbaImageLoader.newInstance(context);
        int w = UITools.getScreenWidth(context)/3;
        mResizeOptions = senbaImageLoader.createResizeOptions(w,w);
    }

    public PhotoAlbumLVAdapter(Context context, ArrayList<PhotoAlbumLVItem> list) {
        this.context = context;
        this.list = list;

        initSenbaImageLoader();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.photo_album_lv_item, null);
            holder = new ViewHolder();

            holder.firstImageIV = (SimpleDraweeView) convertView.findViewById(R.id.select_img_gridView_img);
            holder.pathNameTV = (TextView) convertView.findViewById(R.id.select_img_gridView_path);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //图片（缩略图）
        String filePath = list.get(position).getFirstImagePath();
        holder.firstImageIV.setTag(filePath);
        senbaImageLoader.setImage(holder.firstImageIV, context.getResources().getString(R.string.sd_image_url, filePath), mResizeOptions);
        holder.pathNameTV.setText(getPathNameToShow(list.get(position)));

        return convertView;
    }

    private class ViewHolder {
        SimpleDraweeView firstImageIV;
        TextView pathNameTV;
    }

    /**根据完整路径，获取最后一级路径，并拼上文件数用以显示。*/
    private String getPathNameToShow(PhotoAlbumLVItem item) {
        String absolutePath = item.getPathName();
        int lastSeparator = absolutePath.lastIndexOf(File.separator);
        return absolutePath.substring(lastSeparator + 1) + "(" + item.getFileCount() + ")";
    }

}
