package com.qicq.im.overlayitem;

import android.graphics.drawable.Drawable;

import com.baidu.mapapi.GeoPoint;

public class WantOverlayItem extends AbstaractOverlayItem{

	public static final int WANT_TYPE_BAR = 0;
	public static final int WANT_TYPE_COFFEE = 1;
	public static final int WANT_TYPE_DINNER = 2;
	
	public WantOverlayItem(GeoPoint pt, String title, String snippet,
			Drawable drawable) {
		super(pt, title, snippet, drawable);
	}

	public int getType() {
		return IOverlayItemType.TYPE_WANT;
	}
	

}
