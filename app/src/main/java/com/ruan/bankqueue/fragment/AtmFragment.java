package com.ruan.bankqueue.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.ruan.bankqueue.R;
import com.ruan.bankqueue.naviactivity.WalkNavigationActivity;
import com.ruan.bankqueue.other.BaseConstants;
import com.ruan.bankqueue.routePlanning.Route;
import com.ruan.bankqueue.util.LogUtil;
import com.ruan.bankqueue.util.ToastUtil;
import com.ruan.overlay.PoiOverlay;
import java.util.List;

/**
 * @author by ruan on 2017/12/16.
 */

public class AtmFragment extends BaseFragment{
    static String ARG = "arg";
    TextView tvTitle;
    TextView tvPoiAtm;
    TextView tvDistance;
    PopupWindow popupWindow;
    LinearLayout layout;
    View viewAtm;
    Context mContext;
    AMapNavi aMapNavi;
    private double endLat;
    private double endLon;
    private RouteSearch mRouteSearch;
    public AtmFragment() {
        super();
    }

    public static AtmFragment newInstance(String param) {
        AtmFragment fragment = new AtmFragment();
        Bundle args = new Bundle();
        args.putString(ARG, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(R.layout.fragment_atm, container, false);
        textureMapView = (TextureMapView)view.findViewById(R.id.map_view);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        textureMapView.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        initSearchAtm();
        aMapNavi = AMapNavi.getInstance(mContext);
        initAMap();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        textureMapView.onDestroy();
        //销毁定位客户端。
        mLocationClient.onDestroy();
    }

    private void initSearchAtm(){
        setPoiCode("160300",5000);
    }

    @Override
    protected void initAMap() {
        super.initAMap();
        mRouteSearch = new RouteSearch(getActivity());
        mRouteSearch.setRouteSearchListener(this);
    }

    @Override
    protected void setMapAttributeListener() {
        super.setMapAttributeListener();
        // 设置点击marker事件监听器
        aMap.setOnMarkerClickListener(this);
        // 设置点击infoWindow事件监听器
        aMap.setOnInfoWindowClickListener(this);
        // 设置自定义InfoWindow样式
        aMap.setInfoWindowAdapter(this);

    }

    /**
     * 查询poi回调
     * @param poiResult 回调接口
     * @param i 返回码
     */
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        // TODO: 2017/12/25 在地图上面显示兴趣点
        if (i == BaseConstants.CODE_MAP_SUCCESS){
            List<PoiItem> poiItems = poiResult.getPois();
            if (poiItems != null && poiItems.size() > 0) {
                // TODO: 2017/12/28 搜索完兴趣点，是否清除之前的图标 ，aMap.clean;
                PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                poiOverlay.removeFromMap();
                poiOverlay.zoomToSpan();
            }
            for (int j = 0; j < poiResult.getPois().size(); j++) {
                viewAtm = View.inflate(getActivity(),R.layout.view_atm, null);
                tvTitle = (TextView)viewAtm.findViewById(R.id.tv_title);
                Bitmap bitmap = markerUtil.convertViewToBitmap(viewAtm);
                markerUtil.drawMarkerOnMap(new LatLng(poiResult.getPois().get(j).getLatLonPoint().getLatitude(),
                                poiResult.getPois().get(j).getLatLonPoint().getLongitude()),
                        bitmap, poiResult.getPois().get(j).getTitle());

            }
            setMapAttributeListener();
        }else {
            LogUtil.e("AtmFragment", "i:" + i);
        }

    }

    /**
     * 地图自定义marker点击事件
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        String title = null;
        title = marker.getTitle();
        if (marker.getTitle() == null){
            title = "我的位置";
        }
            //计算两点之间的距离，并保留两位小数
            float distance = AMapUtils.calculateLineDistance(new LatLng(lat, lon),
                    new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
            float b = (float) (Math.round(distance / 10)) / 100;
            bottomWindow(viewAtm, title, String.valueOf(b));
            endPoint = new LatLonPoint(marker.getPosition().latitude, marker.getPosition().longitude);
            naviEnd = new NaviLatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            endLat = marker.getPosition().latitude;
            endLon = marker.getPosition().longitude;
            markerName = marker.getTitle();
            return true;

    }



    void bottomWindow(View view,String poi,String distance) {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        layout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.window_popup_bottom, null);
        popupWindow = new PopupWindow(layout,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        setButtonListeners(layout,poi,distance);
        dismissPopupWindow(view);
    }

    /**
     * 初始化底部弹出窗口控件
     * @param layout
     */
    public void setButtonListeners(LinearLayout layout,String poi,String distance) {
        tvPoiAtm = (TextView) layout.findViewById(R.id.tv_poi_atm);
        tvDistance = (TextView) layout.findViewById(R.id.tv_distance);
        RelativeLayout layRoute = (RelativeLayout)layout.findViewById(R.id.lay_route);
        RelativeLayout layNavigation = (RelativeLayout)layout.findViewById(R.id.lay_navigation);
        layRoute.setOnClickListener(this);
        layNavigation.setOnClickListener(this);
        tvPoiAtm.setText(poi);
        tvDistance.setText("距您"+distance+"公里");
    }

    /**
     * pop 窗口的弹出与隐藏
     */
    public void dismissPopupWindow(View view) {
             //点击空白处时，隐藏掉pop窗口
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setAnimationStyle(R.style.Popupwindow);
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            popupWindow.showAtLocation(view, Gravity.LEFT | Gravity.BOTTOM, 0, -location[1]);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lay_route:
                    searchRouteResult(3);
                break;
            case R.id.lay_navigation:
                Intent intent = new Intent(getActivity(),WalkNavigationActivity.class);
                intent.putExtra("startPoint", naviStart);
                intent.putExtra("endPoint",naviEnd);
                intent.putExtra("startLat",lat);
                intent.putExtra("startLon",lon);
                intent.putExtra("endLat",endLat);
                intent.putExtra("endLon",endLon);
                intent.putExtra("poiName",poiName);
                intent.putExtra("markerName",markerName);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 开始搜索步行路径规划方案
     */
    public void searchRouteResult(int routeType) {
        if (startPoint == null) {
            ToastUtil.show(getActivity(), "定位中，稍后再试...");
            return;
        }
        if (endPoint == null) {
            ToastUtil.show(getActivity(), "终点未设置");
        }
        /**
         * fromAndTo 路径的起终点
         */
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                startPoint, endPoint);
        // 步行路径规划
        if (routeType == BaseConstants.ROUTE_TYPE_WALK) {
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
            // 异步路径规划步行模式查询
            mRouteSearch.calculateWalkRouteAsyn(query);

        }
    }

    /**
     * 步行路线规划
     * @param result 回调
     * @param errorCode 错误码
     */
    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        if (errorCode == BaseConstants.CODE_MAP_SUCCESS){
            Route route = new Route(aMap,mContext,tvDistance);
            route.onWalkRouteSearched(result,errorCode);
        }else {
            Toast.makeText(mContext, "路径规划失败，错误码：" + errorCode, Toast.LENGTH_SHORT).show();
        }
    }
}

