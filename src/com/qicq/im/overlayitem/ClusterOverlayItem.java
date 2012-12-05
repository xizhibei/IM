package com.qicq.im.overlayitem;

import android.graphics.drawable.Drawable;

import com.baidu.mapapi.GeoPoint;

public class ClusterOverlayItem extends AbstaractOverlayItem{
	private int radix;
	private int drawableSize;
	
	public ClusterOverlayItem(GeoPoint pt, String title, String snippet,Drawable drawable) {
		super(pt, title, snippet,drawable);
	}
	
	public ClusterOverlayItem(GeoPoint pt, String title, String snippet,Drawable drawable,int radix) {
		super(pt, title, snippet,drawable);
		this.radix = radix;
	}
	
	public int getRadix() {
		return radix;
	}
	public void setRadix(int radix) {
		this.radix = radix;
	}
	
	public int getType() {
		return IOverlayItemType.TYPE_CLUSTER;
	}

	public int getDrawableSize() {
		return drawableSize;
	}

	public void setDrawableSize(int drawableSize) {
		this.drawableSize = drawableSize;
	}
}
