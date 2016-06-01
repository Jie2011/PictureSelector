package com.jie.pictureselector.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jie.pictureselector.R;
import com.jie.pictureselector.activity.view.IPhotoAlbumView;
import com.jie.pictureselector.adapter.PhotoAlbumLVAdapter;
import com.jie.pictureselector.presenter.PhotoAlbumPresenter;
import com.jie.pictureselector.utils.Utility;

/**
 * 分相册查看SD卡所有图片。
 * Created by liumingjie on 14-10-14.
 */
public class PhotoAlbumActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, IPhotoAlbumView {

    private PhotoAlbumPresenter mPhotoAlbumPresenter;

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
        initPresenter();
        initBaseView();
        initAlbumListview();
    }

    private void initPresenter() {
        mPhotoAlbumPresenter = new PhotoAlbumPresenter(this, this);
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
        PhotoAlbumLVAdapter adapter = new PhotoAlbumLVAdapter(this, mPhotoAlbumPresenter.getPhotoAlbumLVItems());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    /**
     * 点击返回时，回到相册页面
     */
    private void backAction() {
        mPhotoAlbumPresenter.backAction(this);
    }

    @Override
    public void onBackPressed() {
        backAction();
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
        mPhotoAlbumPresenter.onItemClick(this, position);
    }

    @Override
    public ContentResolver getActivityContentResolver() {
        return getContentResolver();
    }

    @Override
    public Intent getActivityIntent() {
        return getIntent();
    }

    @Override
    public void gotoPhotoWallActivity(Intent intent) {
        startActivity(intent);
        finish();
    }
}
