package com.ruan.bankqueue.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.ruan.bankqueue.R;
import com.ruan.bankqueue.naviactivity.WalkNavigationActivity;
import com.ruan.bankqueue.naviutil.TTSController;
import com.ruan.bankqueue.other.BaseConstants;
import com.ruan.bankqueue.routePlanning.Route;
import com.ruan.bankqueue.util.LogUtil;
import com.ruan.bankqueue.util.ToastUtil;
import com.ruan.overlay.PoiOverlay;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * @author by ruan on 2017/12/16.
 */

public class AtmFragment extends Fragment implements PoiSearch.OnPoiSearchListener,
        AMap.OnMarkerClickListener ,AMap.InfoWindowAdapter,AMap.OnInfoWindowClickListener,
        View.OnClickListener,RouteSearch.OnRouteSearchListener{
    View view;
    static String ARG = "arg";
    @BindView(R.id.map_view)
    TextureMapView textureMapView;
    Message message = new Message();
    String city = null;
    String cityCode = null;
    TextView tvTitle;
    TextView tvPoiAtm;
    TextView tvDistance;
    PopupWindow popupWindow;
    LinearLayout layout;
    View viewAtm;

    Context mContext;
    AMapNaviView aMapNaviView;
    AMapNavi aMapNavi;
    TTSController ttsController;
    private AMap aMap;
    private LatLonPoint startPoint;
    private LatLonPoint endPoint;

    private NaviLatLng naviSatrt;
    private NaviLatLng naviEnd;

    private double endLat;
    private double endLon;
    private RouteSearch mRouteSearch;
    private UiSettings uiSettings;
    MyLocationStyle myLocation;

    /**
     *   声明AMapLocationClient类对象
     */
    public AMapLocationClient mLocationClient = null;
    /**
     * 声明mLocationOption对象
     */
    public AMapLocationClientOption mLocationOption = null;
    private double lat;
    private double lon;
    Unbinder unbinder;

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
        view = inflater.inflate(R.layout.fragment_atm, container, false);
        unbinder = ButterKnife.bind(this, view);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        textureMapView.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        aMapNavi = AMapNavi.getInstance(mContext);
        checkThePermissions();
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        init();
        return view;
    }

    /**
     * * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = textureMapView.getMap();
        }
        mRouteSearch = new RouteSearch(getActivity());
        mRouteSearch.setRouteSearchListener(this);
        uiSettings = aMap.getUiSettings();
        setUpMap();
    }

    /**
     * 检查是否获取定位权限
     */
    private void checkThePermissions(){
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CHANGE_WIFI_STATE}, 3);
    }

    /**
     * 设置Map属性和监听事件
     */
    private void setMapAttributeListener(){
        // 设置默认定位按钮是否显示
        uiSettings.setMyLocationButtonEnabled(true);
        // 是否可触发定位并显示定位层
        aMap.setMyLocationEnabled(true);
        // 设置点击marker事件监听器
        aMap.setOnMarkerClickListener(this);
        // 设置点击infoWindow事件监听器
        aMap.setOnInfoWindowClickListener(this);
        // 设置自定义InfoWindow样式
        aMap.setInfoWindowAdapter(this);
    }




    /**
     * 配置定位参数
     */
    private void setUpMap() {
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(8000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        textureMapView.onDestroy();
        //销毁定位客户端。
        mLocationClient.onDestroy();
        unbinder.unbind();
    }


    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
            //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，
            // 实现地图生命周期管理
        textureMapView.onSaveInstanceState(outState);
        }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 声明定位回调监听器
     */
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            String time = "yyyy-MM-dd HH:mm:ss";
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    SimpleDateFormat df = new SimpleDateFormat(time);
                    Date date = new Date(amapLocation.getTime());
                    //定位时间
                    df.format(date);
                    //获取纬度
                    lat = amapLocation.getLatitude();
                    //获取经度
                    lon = amapLocation.getLongitude();
                    city = amapLocation.getCity();
                    cityCode = amapLocation.getCityCode();
                    LogUtil.e("pcw----", "lat : " + lat + " lon : " + lon);
                    LogUtil.e("pcw----", " Country : " + amapLocation.getCountry() +
                            " province : " + amapLocation.getProvince() +
                            " City : " + amapLocation.getCity() +
                            " District : " + amapLocation.getDistrict()+
                            " AoiName : " + amapLocation.getAoiName()+
                            " CityCode : " + amapLocation.getCityCode()+
                            " AdCode : " + amapLocation.getAdCode()
                    );
                    startPoint = new LatLonPoint(lat,lon);
                    naviSatrt = new NaviLatLng(lat,lon);
                    // 设置当前地图显示为当前位置
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15.5f));
                    myLocation = new MyLocationStyle();
                    //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
                    // 连续定位、且将视角移动到地图中心点，
                    // 定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
                    myLocation.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);
                    //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
                    myLocation.interval(2000);
                    aMap.setMyLocationStyle(myLocation);
                    // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
                    aMap.setMyLocationEnabled(true);
                    // 完成定位发送消息，默认搜索ATM
                    message.what = BaseConstants.POI_SEARCH_ATM;
                    handler.sendMessage(message);
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    LogUtil.e("AmapError----", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };

    /**
     * 周边POI搜索
     */
    private void poiSearch(){
        PoiSearch.Query query = new PoiSearch.Query("","160300",cityCode);
        PoiSearch poiSearch = new PoiSearch(getActivity(),query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
        // TODO: 2017/12/25  设置周边搜索的中心点以及半径 暂设置为5公里范围内
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(lat,
                lon), 5000));
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
                Bitmap bitmap = convertViewToBitmap(viewAtm);
                drawMarkerOnMap(new LatLng(poiResult.getPois().get(j).getLatLonPoint().getLatitude(),
                                poiResult.getPois().get(j).getLatLonPoint().getLongitude()),
                        bitmap, poiResult.getPois().get(j).getTitle());

            }
            setMapAttributeListener();
        }else {
            LogUtil.e("AtmFragment", "i:" + i);
        }

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
        // TODO: 2017/12/26 搜索poi定位回调信息
//        LogUtil.e("AtmFragment", "----" + poiItem.getCityCode());

    }

    private  Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseConstants.POI_SEARCH_ATM:
                    poiSearch();
                    break;
                default:
                    break;
            }

        }
    };

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
            float distance = AMapUtils.calculateLineDistance(new LatLng(lat, lon), new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
            LogUtil.e("AtmFragment----", "distance:" + distance);
            float b = (float) (Math.round(distance / 10)) / 100;
            bottomWindow(viewAtm, title, String.valueOf(b));
            endPoint = new LatLonPoint(marker.getPosition().latitude, marker.getPosition().longitude);
            naviEnd = new NaviLatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            endLat = marker.getPosition().latitude;
            endLon = marker.getPosition().longitude;
            return true;

    }

    /**
     * @param view 转 Bitmap
     * @return
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * 在地图上画marker
     * @param point
     * @param markerIcon 图标
     * @return Marker对象
     */
    private Marker drawMarkerOnMap(LatLng point, Bitmap markerIcon, String title) {
        if (aMap != null && point != null) {
            //设置定位蓝点图标的锚点方法。
            Marker marker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
                    .position(point)
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromBitmap(markerIcon)));
            return marker;
        }
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        LogUtil.e("AtmFragment----", marker.getTitle());
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
//            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                @Override
//                public void onDismiss() {
//
//                }
//            });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lay_route:
                    searchRouteResult(3);

                break;
            case R.id.lay_navigation:
                Toast.makeText(mContext, "开始导航", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),WalkNavigationActivity.class);

                intent.putExtra("startPoint",naviSatrt);
                intent.putExtra("endPoint",naviEnd);
                Log.e("AtmFragment", "naviEnd:----" + naviEnd);
                intent.putExtra("startLat",lat);
                intent.putExtra("startLon",lon);
                intent.putExtra("endLat",endLat);
                intent.putExtra("endLon",endLon);
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

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        if (errorCode == BaseConstants.CODE_MAP_SUCCESS){
            Route route = new Route(aMap,mContext,tvDistance);
            route.onWalkRouteSearched(result,errorCode);
        }else {
            Toast.makeText(mContext, "路径规划失败，错误码：" + errorCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }
}

