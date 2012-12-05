package com.qicq.im;

import java.util.List;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.Projection;
import com.qicq.im.api.LocationCluster;
import com.qicq.im.api.User;
import com.qicq.im.app.LBSApp;
import com.qicq.im.overlayitem.OverItemT;
import com.qicq.im.popwin.LBSToast;
import com.qicq.im.popwin.NearbyOptionPopupWindow;

public class NearbyActivity extends MapActivity {

	BMapManager mBMapMan = null;
	//MKSearch mSearch = null;
	private MapView mMapView = null;
	
	MyLocationOverlay mLocationOverlay = null;
	static GeoPoint point = null;

	private Button getLoc = null;
	private Button maxZoom = null;
	private Button webButton = null;
	private Button nearby = null;
	private Button publish = null;
	private Button option = null;

	private View mPopView = null;	// 点击mark时弹出的气泡View
	private OverItemT overitem = null;

	private LBSApp app;
	private NearbyOptionPopupWindow menuWindow;
	private int gender = 0;
	private String updateTime = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.nearby);

		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init("5AD5689000995481C5CC182EF8ACDD91596C98CE",null);
		mBMapMan.start();

		super.initMapActivity(mBMapMan);

		initView();

		/*mSearch = new MKSearch();
		mSearch.init(mBMapMan, new MKSearchListener(){

			public void onGetAddrResult(MKAddrInfo res, int errno) {
				if(errno != 0){
					Log.v("MKSearch","get add error!" + errno);
				}

				String strInfo = String.format("纬度：%f 经度：%f 地址：%s\r\n", res.geoPt.getLatitudeE6() / 1e6,
						res.geoPt.getLongitudeE6() / 1e6,
						res.addressComponents.province +
						res.addressComponents.city + 
						res.addressComponents.district + 
						res.addressComponents.street +
						res.addressComponents.streetNumber);
				//LBSToast.makeText(NearbyActivity.this, strInfo,Toast.LENGTH_LONG).show();
				Log.v("MKSearch",strInfo);
				Log.v("MKSearch strAddr",res.strAddr);
				Log.v("MKSearch strBusiness",res.strBusiness);
			}

			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			}

			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
					int arg1) {
			}

			public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			}

			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			}

			public void onGetTransitRouteResult(MKTransitRouteResult arg0,
					int arg1) {
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0,
					int arg1) {	
			}

		});*/
		// 添加定位图层
		mLocationOverlay = new MyLocationOverlay(this, mMapView);
		mMapView.getOverlays().add(mLocationOverlay);

		overitem = new OverItemT(this,mMapView,mPopView);
		mMapView.getOverlays().add(overitem); //添加ItemizedOverlay实例到mMapView

		app = (LBSApp)this.getApplication();
		GeoPoint me = app.getUserLocation();
		if(me != null)
			mMapView.getController().animateTo(me);

		mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
		
		mMapView.getController().setZoom(5);
	}

	private LocationListener mLocationListener = new LocationListener(){

		public void onLocationChanged(Location location) {
			if(location != null){
				point = new GeoPoint((int)(location.getLatitude() * 1E6),(int)(location.getLongitude() * 1E6));
				//mSearch.reverseGeocode(point);

				//mMapView.getController().animateTo(point);

				String strLog = String.format("Lng:%f Lat:%f",location.getLatitude(),location.getLongitude() );
				Log.v("Location", strLog);
			}
		}
	};
	
	private void initView(){
		getLoc = (Button)findViewById(R.id.getlocation);
		maxZoom = (Button)findViewById(R.id.maxzoom);
		webButton = (Button)findViewById(R.id.web);
		nearby = (Button)findViewById(R.id.nearby);
		publish = (Button)findViewById(R.id.publish);
		option = (Button)findViewById(R.id.option);


		OnClickListener clickListener = new OnClickListener(){
			public void onClick(View v) {
				SearchButtonProcess(v);
			}
		};

		getLoc.setOnClickListener(clickListener);
		maxZoom.setOnClickListener(clickListener);
		webButton.setOnClickListener(clickListener);
		nearby.setOnClickListener(clickListener);
		publish.setOnClickListener(clickListener);
		option.setOnClickListener(clickListener);


		mMapView = (MapView) findViewById(R.id.bmapsView);
		mMapView.setBuiltInZoomControls(true);

		mMapView.setDrawOverlayWhenZooming(true);

		// 创建点击mark时的弹出泡泡
		mPopView = super.getLayoutInflater().inflate(R.layout.popview, null);
		mMapView.addView( mPopView,
				new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, 
						LayoutParams.WRAP_CONTENT,
						null, MapView.LayoutParams.TOP_LEFT)
				);
		mPopView.setVisibility(View.GONE);
	}

	protected void SearchButtonProcess(View v) {
		if(getLoc.equals(v)){
			if(point != null){
				mMapView.getController().animateTo(point);
			}
		}
		else if(maxZoom.equals(v)){
			mMapView.getController().setZoom( 18 );
		}else if(webButton.equals(v)){
			Location l = mBMapMan.getLocationManager().getLocationInfo();
			point = new GeoPoint((int)(l.getLatitude() * 1E6),
					(int)(l.getLongitude() * 1E6));				
			int ret = app.LocationUpdate(point.getLatitudeE6(), point.getLongitudeE6());
			if(ret == 0){
				LBSToast.makeText(this, "更新成功", Toast.LENGTH_LONG).show();
			}else
				LBSToast.makeText(this, "更新失败", Toast.LENGTH_LONG).show();
			mMapView.getController().animateTo(point);
			//mSearch.reverseGeocode(point);
		}else if(nearby.equals(v)){
			Log.v("Zoom level",String.valueOf(mMapView.getZoomLevel()));
			updateOverlayItems(gender,-1,updateTime);

		}else if(publish.equals(v)){
			Intent i = new Intent(this,PublishActivity.class);
			startActivity(i);
		}else if(option.equals(v)){
			menuWindow = new NearbyOptionPopupWindow(this);
			menuWindow.setGender(gender);
			menuWindow.setUpdateTime(updateTime);
			menuWindow.setOnOk(new OnClickListener(){
			public void onClick(View v) {		
				gender = menuWindow.getGender();
				updateTime = menuWindow.getUpdateTime();
				menuWindow.dismiss();
			}			
		});
			menuWindow.show();
		}
	}
	
	private void updateOverlayItems(int gender,int ageLevel,String updatetime){
		if(mMapView.getZoomLevel() <= 11){
			
			Projection p = mMapView.getProjection();
			GeoPoint p1 = p.fromPixels(0, 0);
			GeoPoint p2 = p.fromPixels(mMapView.getWidth() - 1, mMapView.getHeight() - 1);
			String info = String.format("Window rect %d %d, %d %d", 
					p1.getLatitudeE6(),p1.getLongitudeE6(),
					p2.getLatitudeE6(),p2.getLongitudeE6());
			Log.v("Width and height",info);

			List<LocationCluster> clusters = app.GetLocationCluster(p1.getLatitudeE6(),p1.getLongitudeE6(),
					p2.getLatitudeE6(),p2.getLongitudeE6(),gender,ageLevel,updatetime);

			//overitem.clusters = clusters;
			if(clusters.size() != 0){
				overitem.clear();
				User tmp = app.getUser();
				if(tmp != null)
					overitem.add(tmp.toOverlayItem());

				int size = clusters.size();
				for(int i = 0;i < size;i++){
					overitem.add(clusters.get(i).toOverlayItem());
				}
				overitem.updateOverlay();
			}else
				LBSToast.makeText(this, "获取周围人数据失败", Toast.LENGTH_LONG).show();
		}else{
			List<User> list = app.getNearbyPeople(true);
			if(list.size() != 0){					
				overitem.clear();
				overitem.add(app.getUser().toOverlayItem());

				int size = list.size();
				for(int i = 0;i < size;i++){
					overitem.add(list.get(i).toOverlayItem());
				}
				overitem.updateOverlay();
			}else
				LBSToast.makeText(this, "获取周围人数据失败", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onPause(){
		if( mBMapMan != null){
			mBMapMan.getLocationManager().removeUpdates(mLocationListener);
			mLocationOverlay.disableMyLocation();
			mLocationOverlay.disableCompass(); // 关闭指南针
			mBMapMan.stop();
		}
		super.onPause();
	}
	@Override
	protected void onResume(){
		if( mBMapMan != null){
			mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
			mLocationOverlay.enableMyLocation();
			mLocationOverlay.enableCompass(); // 打开指南针
			mBMapMan.start();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy(){
		mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
		super.onDestroy();
	}
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}

