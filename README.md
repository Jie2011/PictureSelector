# PictureSelector－－图片选择控件

  控件主要用于选择sd卡图片和利用系统相机拍照功能,可以选择单张或者最多9张图片：
  
1，选择图片
   选择图片类似与一个图库功能，有相册列表，可以选择单张或者多张图片（最多选择9张），以及可以预览大图
   
2，系统拍照功能
   可以选择系统相机拍照，拍照后的图片可以进行切割
   
   
使用方法：
调用SelectImageHomeActivity的gotoSelectImageActivity


/**
	 * 进入图片选择方式页面
	 * @param activity
	 * @param typeId  类型 单选还是多选  PhotoWallActivity.TYPE_SINGLE为单选 PhontWallActivity.TYPE_MULTIPLE为多选
	 * @param selectList 已经选择图片的列表 值为图片路径
	 * @param isCut 
	 * 
	 * 在onActivityForResult方法中 requestCode == SelectImageHomeActivity.CODE_PHOTO
	 *  获取选择的图片的list intent 参数值为SelectImageHomeActivity.PHOTO_URLS 返回值为ArrayList<String>
	 * intent.getStringArrayListExtra(SelectImageHomeActivity.PHOTO_URLS)
	 */
	public static void gotoSelectImageActivity(Activity activity,int typeId,ArrayList<String> selectList,int isCut){
		Intent intent = new Intent(activity, SelectImageHomeActivity.class);
		intent.putExtra(PhotoWallActivity.TYPE, typeId);
		if(selectList != null){
			intent.putExtra(PhotoWallActivity.SELECT_LIST, selectList);
		}
		intent.putExtra(PhotoWallActivity.IS_CUT, isCut);
		activity.startActivityForResult(intent, CODE_PHOTO);
	}

	
采用的方式是startActivityForResult，在调用页面onActivityResult逻辑：
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
       if (resultCode == RESULT_OK){
           if (requestCode == SelectImageHomeActivity.CODE_PHOTO) {
               if (intent != null && intent.hasExtra(SelectImageHomeActivity.PHOTO_URLS)) {
                   ArrayList<String> images = intent.getStringArrayListExtra(SelectImageHomeActivity.PHOTO_URLS);  //获取图片路径列表，大小为1是单选的
                   if (images != null && images.size() > 0) {
                      String  mNewImageUrl = images.get(0);
                       if (!TextUtils.isEmpty(mNewImageUrl)) {
                           SenbaImageLoader.newInstance(this).setImage(mUserPhotoImageView, getResources().getString(R.string.sd_image_url, mNewImageUrl));
                       }
                   }
               }
           }
       }
    }
	
