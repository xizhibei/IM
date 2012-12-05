package com.qicq.im.service;

import java.util.EventListener;

public interface NetworkStateListener extends EventListener{
	public void onNetworkUnconnected();
	public void onNetworkConnected();
}
