package com.jie.pictureselector.view;

import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

/**
 * 类说明：
 * 
 * @author liumingjie
 * @date 2015.06.09
 * @version 1.0
 */
public class CustomViewPager extends ViewPager {

	private static final String TAG = "CustomViewPager";

	private boolean mIsNeedHandleEvent = true;
	// 往该方向相反的方向拖动则不处理拖动事件，交给子View处理
	private TouchOrientation mNotHandleEventReverseOrientation = TouchOrientation.NULL;
	private float mStartX = 0;
	private float mStartY = 0;

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		try {
			Field field = ViewPager.class.getDeclaredField("mTouchSlop");
			field.setAccessible(true);
			int touchSlop = ViewConfigurationCompat
					.getScaledPagingTouchSlop(ViewConfiguration.get(context)) * 4;
			if (touchSlop > 100) {
				touchSlop = 100;
			}
			field.set(this, touchSlop);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置一个方向，当向该方向相反的方向拖动时，则事件交给子View处理，仅在一次拖动中有效
	 * 
	 * @param orientation
	 */
	public void setReverseTouchNotHandleEvent(TouchOrientation orientation) {
		mNotHandleEventReverseOrientation = orientation;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (mIsNeedHandleEvent) {
			float x = event.getX();
			float y = event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mStartX = x;
				mStartY = y;
				break;
			case MotionEvent.ACTION_MOVE:
				// 防手抖
				if ((int) Math.abs(x - mStartX) >= 10
						|| (int) Math.abs(y - mStartY) >= 10) {
					switch (mNotHandleEventReverseOrientation) {
					case LEFT:
						mNotHandleEventReverseOrientation = TouchOrientation.NULL;
						if (x - mStartX > 0) {
							return false;
						}
						break;
					case RIGHT:
						mNotHandleEventReverseOrientation = TouchOrientation.NULL;
						if (x - mStartX < 0) {
							return false;
						}
						break;
					case UP:
						mNotHandleEventReverseOrientation = TouchOrientation.NULL;
						if (y - mStartY > 0) {
							return false;
						}
						break;
					case DOWN:
						mNotHandleEventReverseOrientation = TouchOrientation.NULL;
						if (y - mStartY < 0) {
							return false;
						}
						break;
					default:
						break;
					}
				}
				break;
			}
			boolean ret = false;
			try {
				ret = super.onInterceptTouchEvent(event);
			} catch (IllegalArgumentException ex) {
				ex.printStackTrace();
			}
			return ret;
			// return super.onInterceptTouchEvent(event);
		} else {
			return false;
		}
	}

	public void setIsNeedHandleEvent(boolean isNeedHandleEvent) {
		mIsNeedHandleEvent = isNeedHandleEvent;
	}

	public enum TouchOrientation {
		UP, DOWN, LEFT, RIGHT, NULL
	}

}
