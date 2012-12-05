package com.qicq.im.overlayitem;

import android.graphics.drawable.Drawable;

import com.baidu.mapapi.GeoPoint;

public class PeopleOverlayItem extends AbstaractOverlayItem{

	public PeopleOverlayItem(GeoPoint pt, String title, String snippet,Drawable drawable) {
		super(pt, title, snippet,drawable);
	}

	public int getType() {
		return IOverlayItemType.TYPE_PEOPLE;
	}
}
