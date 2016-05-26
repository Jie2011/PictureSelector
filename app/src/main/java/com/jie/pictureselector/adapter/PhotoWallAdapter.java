package com.jie.pictureselector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.jie.pictureselector.R;
import com.jie.pictureselector.image.SenbaImageLoader;
import com.jie.pictureselector.model.ImageSelectModel;
import com.jie.pictureselector.activity.PhotoWallActivity;
import com.jie.pictureselector.utils.UITools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * PhotoWall中GridView的适配器
 *
 * @author liumingjie
 */

public class PhotoWallAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ImageSelectModel> imagePathList = null;

    private int type;

    //记录是否被选择
    private HashMap<String, String> selectionMap;
    private PhotoImageSelectListener photoImageSelectListener;
    private ResizeOptions mResizeOptions;
    private SenbaImageLoader senbaImageLoader;

    private void initSenbaImageLoader() {
        senbaImageLoader = SenbaImageLoader.newInstance(context);
        int w = UITools.getScreenWidth(context) / 3;
        mResizeOptions = senbaImageLoader.createResizeOptions(w, w);
    }

    public PhotoWallAdapter(Context context, ArrayList<ImageSelectModel> imagePathList, int type, PhotoImageSelectListener photoImageSelectListener, List<String> mSelectList) {
        this.context = context;
        this.imagePathList = imagePathList;
        this.type = type;
        selectionMap = new LinkedHashMap<String, String>();
        if (mSelectList != null && mSelectList.size() > 0) {
            for (String s : mSelectList) {
                selectionMap.put(s, s);
            }
        }
        this.photoImageSelectListener = photoImageSelectListener;
        updateImageState();
        initSenbaImageLoader();
    }

    public void setImagePathList(ArrayList<ImageSelectModel> imagePathList) {
        this.imagePathList = imagePathList;
        selectionMap.clear();
        for (ImageSelectModel model : imagePathList) {
            if (model.select) {
                selectionMap.put(model.url, model.url);
            }
        }
    }

    private void updateImageState() {
        if (selectionMap != null) {
            for (ImageSelectModel imageSelectModel : imagePathList) {
                if (selectionMap.containsKey(imageSelectModel.url)) {
                    imageSelectModel.select = true;
                } else {
                    imageSelectModel.select = false;
                }
            }
        }
    }

    public void setSelectionMap(HashMap<String, String> selectionMap) {
        this.selectionMap = selectionMap;
        updateImageState();
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
        final ImageSelectModel model = imagePathList.get(position);

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.photo_wall_item, null);
            holder = new ViewHolder();

            holder.imageView = (SimpleDraweeView) convertView.findViewById(R.id.photo_wall_item_photo);
            holder.checkBox = (ImageView) convertView.findViewById(R.id.iv_photo_wall_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (type == PhotoWallActivity.TYPE_MULTIPLE) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
        //tag的key必须使用id的方式定义以保证唯一，否则会出现IllegalArgumentException.
        holder.checkBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                model.select = !model.select;

                if (model.select) {
                    if (selectionMap.size() >= 9) {
                        Toast.makeText(context, "最多选择9张图片", Toast.LENGTH_SHORT).show();
                    } else {
                        selectionMap.put(model.url, model.url);
                        holder.checkBox.setBackgroundResource(R.mipmap.checkbox_checked);
                    }
                } else {
                    if (selectionMap.containsKey(model.url)) {
                        selectionMap.remove(model.url);
                    }
                    holder.checkBox.setBackgroundResource(R.mipmap.checkbox_normal);
                }
                photoImageSelectListener.onSelectCount(selectionMap.size());
            }
        });
        if (model.select || selectionMap.containsKey(model.url)) {
            holder.checkBox.setBackgroundResource(R.mipmap.checkbox_checked);
        } else {
            holder.checkBox.setBackgroundResource(R.mipmap.checkbox_normal);
        }
        holder.imageView.setTag(model.url);
        senbaImageLoader.setImage(holder.imageView, context.getResources().getString(R.string.sd_image_url, model.url), mResizeOptions);
        return convertView;
    }

    private class ViewHolder {
        SimpleDraweeView imageView;
        ImageView checkBox;
    }

    public HashMap<String, String> getSelectionMap() {
        return selectionMap;
    }

    public void clearSelectionMap() {
        selectionMap.clear();
    }

    public interface PhotoImageSelectListener {
        void onSelectCount(int count);
    }

}
