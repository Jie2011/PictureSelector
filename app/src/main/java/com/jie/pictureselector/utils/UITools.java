package com.jie.pictureselector.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

/**
 * 类说明：
 * 
 * @author liumingjie
 * @date 2015.05.04
 * @version 1.0
 */

public class UITools {

	/**
	 * 隐藏键盘
	 * 
	 * @param view
	 */
	public static void hideSoftInput(View view) {
		final InputMethodManager im = (InputMethodManager) view.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static String getText(TextView view) {
		if (view == null)
			return "";
		else
			return view.getText().toString().trim();
	}

	public static int[] getDeviceScreenWidthHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int[] wh = new int[2];
		wh[0] = dm.widthPixels;
		wh[1] = dm.heightPixels;
		return wh;
	}

	/**
	 * 该方法用于将dip大小转换为px大小
	 * 
	 * @param context
	 *            上下文对象
	 * @param dipValue
	 *            dip大小值
	 * @return 转换后的px像素值
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 该方法用于将px大小转换为dip大小
	 * 
	 * @param context
	 *            上下文对象
	 * @param pxValue
	 *            px大小值
	 * @return 转换后的dip值
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 该方法用于将sp大小转换为px大小
	 * 
	 * @param context
	 *            上下文对象
	 * @param pxValue
	 *            sp大小值
	 * @return 转换后的dip值
	 */
	public static int sp2px(Context context, float spValue) {
		final float scale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * scale + 0.5f);
	}

	public static float getDenisty(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		float density = dm.density;
		return density;
	}

	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}
}
