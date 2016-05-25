package com.jie.pictureselector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.jie.pictureselector.R;
import com.jie.pictureselector.image.SenbaImageLoader;
import com.jie.pictureselector.utils.UITools;

import java.util.ArrayList;

/**
 * 主页面中GridView的适配器
 *
 * @author liumingjie
 */

public class MainGVAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> imagePathList = null;

    private ResizeOptions mResizeOptions;
    private SenbaImageLoader senbaImageLoader;

    public MainGVAdapter(Context context, ArrayList<String> imagePathList) {
        this.context = context;
        this.imagePathList = imagePathList;
        initSenbaImageLoader();
    }

    private void initSenbaImageLoader(){
        senbaImageLoader = SenbaImageLoader.newInstance(context);
        int w = UITools.getScreenWidth(context)/3;
        mResizeOptions = senbaImageLoader.createResizeOptions(w,w);
    }
    @Override
    public int getCount() {
        return imagePathList == null ? 0 : imagePathList.size();
    }

    @Override
    public Object getItem(int position) {
        return imagePathList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String filePath = (String) getItem(position);

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.main_gridview_item, null);
            holder = new ViewHolder();

            holder.imageView = (SimpleDraweeView) convertView.findViewById(R.id.main_gridView_item_photo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageView.setTag(filePath);
        senbaImageLoader.setImage(holder.imageView, context.getResources().getString(R.string.sd_image_url, filePath), mResizeOptions);
        return convertView;
    }

    private class ViewHolder {
        SimpleDraweeView imageView;
    }

}
