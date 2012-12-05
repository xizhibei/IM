package com.qicq.im.api;

import com.baidu.mapapi.GeoPoint;
import com.qicq.im.overlayitem.ClusterOverlayItem;

public class LocationCluster {

	public int latitude;
	public int longtitude;
	public int radix;
	
	public LocationCluster(int latitude,int longtitude,int radix){
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.radix = radix;
	}
	
	public ClusterOverlayItem toOverlayItem() {
		return new ClusterOverlayItem(new GeoPoint(latitude,longtitude),"cluster",String.valueOf(radix),null,radix);
	}

}
