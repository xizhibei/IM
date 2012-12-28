package com.qicq.im.overlayitem;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Projection;
import com.qicq.im.R;
import com.qicq.im.WantDetailActivity;
import com.qicq.im.api.User;
import com.qicq.im.app.LBSApp;

public class OverItemT extends ItemizedOverlay<AbstaractOverlayItem> {

	private List<AbstaractOverlayItem> mGeoList = new ArrayList<AbstaractOverlayItem>();
	// public List<LocationCluster> clusters;

	private Context mContext;
	private MapView mMapView;
	private View mPopView;
	
	private LBSApp app;

	private List<User> userList;
	private int clickedID;
	
	private Drawable defaultAvatar;

	private Drawable marker_l;
	private Drawable marker_m;
	private Drawable marker_s;
	private Drawable marker_t;

	public OverItemT(LBSApp app, Context context, MapView mMapView, View mPopView) {
		super(null);
		
		this.app = app;

		this.mContext = context;
		this.mMapView = mMapView;
		this.mPopView = mPopView;

		marker_l = context.getResources().getDrawable(R.drawable.cluster_l);
		marker_l = boundCenter(marker_l);

		marker_m = context.getResources().getDrawable(R.drawable.cluster_m);
		marker_m = boundCenter(marker_m);

		marker_s = context.getResources().getDrawable(R.drawable.cluster_s);
		marker_s = boundCenter(marker_s);

		marker_t = context.getResources().getDrawable(R.drawable.cluster_t);
		marker_t = boundCenter(marker_t);

		defaultAvatar = mContext.getResources().getDrawable(R.drawable.avatar);
		defaultAvatar.setBounds(0, 0, defaultAvatar.getIntrinsicWidth(),
				defaultAvatar.getIntrinsicHeight());

		populate(); // createItem(int)方法构造item。一旦有了数据，在调用其它方法前，首先调用这个方法
		
		OnClickListener clickListener = new OnClickListener() {
			public void onClick(View v) {
				SearchButtonProcess(v);
			}
		};
		
		Button button = (Button) mMapView.findViewById(R.id.popButton);
		button.setOnClickListener(clickListener);
	}

	public void updateOverlay() {
		populate();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);

		Projection projection = mapView.getProjection();
		for (int index = size() - 1; index >= 0; index--) { // 遍历mGeoList
			AbstaractOverlayItem overLayItem = mGeoList.get(index); // 得到给定索引的item

			Point point = projection.toPixels(overLayItem.getPoint(), null);
			int type = overLayItem.getType();
			if (type == IOverlayItemType.TYPE_PEOPLE) {
				if (index == 0) {
					Paint paintC = new Paint();
					paintC.setColor(Color.GREEN);
					paintC.setAlpha(70);
					float dis = projection.metersToEquatorPixels(1000);
					canvas.drawCircle(point.x, point.y, dis, paintC);
				}
			} else if (type == IOverlayItemType.TYPE_CLUSTER) {

				Paint paintText = new Paint();
				paintText.setColor(Color.BLACK);
				paintText.setTextSize(24);
				paintText.setStrokeWidth(10);
				canvas.drawText(overLayItem.getSnippet(), point.x - 24,
						point.y + 8, paintText); // 绘制文本
			}
		}
		// 调整一个drawable边界，使得（0，0）是这个drawable底部最后一行中心的一个像素
		// boundCenterBottom(marker);
	}

	public void clear() {
		mGeoList.clear();
	}

	private AbstaractOverlayItem autoSetMarker(AbstaractOverlayItem item) {
		if (item.getType() == IOverlayItemType.TYPE_CLUSTER) {
			ClusterOverlayItem tmp = (ClusterOverlayItem) item;
			int radix = tmp.getRadix();
			Drawable dtmp;

			if (radix > 100) {
				dtmp = marker_l;
			} else if (radix > 50) {
				dtmp = marker_m;
			} else if (radix > 20) {
				dtmp = marker_s;
			} else {
				dtmp = marker_t;
			}
			tmp.setMarker(dtmp);
			return tmp;
		}
		return item;
	}

	public void add(AbstaractOverlayItem item) {
		item = autoSetMarker(item);
		mGeoList.add(item);
	}

	public void set(int idx, AbstaractOverlayItem item) {
		item = autoSetMarker(item);
		mGeoList.set(idx, item);
	}

	@Override
	protected AbstaractOverlayItem createItem(int i) {
		return mGeoList.get(i);
	}

	@Override
	public int size() {
		return mGeoList.size();
	}

	@Override
	// 处理当点击事件
	protected boolean onTap(int i) {
		clickedID = i;
		
		setFocus(mGeoList.get(i));
		// 更新气泡位置,并使之显示
		GeoPoint pt = mGeoList.get(i).getPoint();
		mMapView.updateViewLayout(mPopView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, pt,
				MapView.LayoutParams.BOTTOM_CENTER));

		TextView old = (TextView) mMapView.findViewById(R.id.popOld);
		ImageView sex = (ImageView) mMapView.findViewById(R.id.popSex);
		TextView num = (TextView) mMapView.findViewById(R.id.popNum);
		ImageView head = (ImageView) mMapView.findViewById(R.id.popHead);
		ImageView need = (ImageView) mMapView.findViewById(R.id.popNeed);
		Button button = (Button) mMapView.findViewById(R.id.popButton);
		//设置相应控件的内容，添加按钮响应
		if(mMapView.getZoomLevel() > 11){
			Log.v(">11","clicked");
			if(userList.isEmpty())
				setUserList(app.getNearbyPeople(true));
			//设置年龄
			old.setVisibility(View.VISIBLE);
			old.setText(""+userList.get(i).age);
			//设置性别
			sex.setVisibility(View.VISIBLE);
			if(userList.get(i).sex.equals("male"))
				sex.setImageResource(R.drawable.gender_male);
			else if(userList.get(i).sex.equals("female"))
				sex.setImageResource(R.drawable.gender_female);
			head.setVisibility(View.VISIBLE);
			need.setVisibility(View.VISIBLE);
			button.setVisibility(View.VISIBLE);
			num.setVisibility(View.GONE);
		}
		else
		{
			Log.v("<=11","clicked");
			old.setVisibility(View.GONE);
			sex.setVisibility(View.GONE);
			head.setVisibility(View.GONE);
			need.setVisibility(View.GONE);
			button.setVisibility(View.GONE);
			num.setVisibility(View.VISIBLE);
		}
		mPopView.setVisibility(View.VISIBLE);
//		LBSToast.makeText(mContext, mGeoList.get(i).getSnippet(),
//				Toast.LENGTH_SHORT).show();
		Log.v("onTap", "clicked");
		return true;
	}

	@Override
	public boolean onTap(GeoPoint arg0, MapView arg1) {
		// 消去弹出的气泡
		mPopView.setVisibility(View.GONE);
		Log.v("onTap", "pop");
		return super.onTap(arg0, arg1);
	}
	
	public void setUserList(List<User> userList){
		this.userList = userList;
	}
	
	protected void SearchButtonProcess(View v) {
		Intent i = new Intent(mContext, WantDetailActivity.class);
		i.putExtra("name", userList.get(clickedID).name);
		mContext.startActivity(i);
	}
}
