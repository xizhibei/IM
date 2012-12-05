package com.qicq.im.overlayitem;

public interface IOverlayItemType {
	public final static int TYPE_PEOPLE = 0;
	public final static int TYPE_CLUSTER = 1;
	public final static int TYPE_REQUEST_TO_ME = 2;
	public final static int TYPE_WANT = 4;
	public int getType();
}
