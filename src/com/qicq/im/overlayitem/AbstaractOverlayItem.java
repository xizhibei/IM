package com.qicq.im.overlayitem;

import android.graphics.drawable.Drawable;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.OverlayItem;

public abstract class AbstaractOverlayItem extends OverlayItem implements IOverlayItemType{

	public AbstaractOverlayItem(GeoPoint pt, String title, String snippet,Drawable drawable) {
		super(pt, title, snippet);
		if(drawable != null)
			super.setMarker(drawable);
	}
	
	public Drawable boundCenter(Drawable drawable){
		int h = drawable.getIntrinsicHeight() / 2;
		int w = drawable.getIntrinsicWidth() / 2;
		
		drawable.setBounds(-w, -h, w, h);
		
		return drawable;
	}
}
