package com.ruan.bankqueue.fragment;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
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
import com.ruan.bankqueue.other.BaseConstants;
import com.ruan.bankqueue.util.DrawMarkerUtil;
import com.ruan.bankqueue.util.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author  by ruan on 2018/1/3.
 */

public class BaseFragment extends Fragment implements PoiSearch.OnPoiSearchListener,
        AMap.OnMarkerClickListener ,AMap.InfoWindowAdapter,AMap.OnInfoWindowClickListener,
        View.OnClickListener,RouteSearch.OnRouteSearchListener {
    protected Message message = new Message();
    protected String city = null;
    protected String cityCode = null;
    protected String poiName;
    protected String markerName;
    protected LatLonPoint startPoint;
    protected LatLonPoint endPoint;
    protected NaviLatLng naviStart;
    protected NaviLatLng naviEnd;
    protected AMap aMap;
    protected MyLocationStyle myLocation;
    protected UiSettings uiSettings;
    protected TextureMapView textureMapView;
    protected DrawMarkerUtil markerUtil;
    protected View view;
    protected String poiCode;
    protected int poiAtmScope = 5000;
    protected double lat;
    protected double lon;
    /**
     *   声明AMapLocationClient类对象
     */
    public AMapLocationClient mLocationClient = null;
    /**
     * 声明mLocationOption对象
     */
    public AMapLocationClientOption mLocationOption = null;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseConstants.POI_SEARCH:
                    poiSearch();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //初始化定位
        mLocationClient = new AMapLocationClient(getActivity().getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        checkThePermissions();
        myLocation = new MyLocationStyle();
        return view;
    }

    /**
     * 检查是否获取定位权限
     */
    protected void checkThePermissions(){
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CHANGE_WIFI_STATE}, 3);
    }

    /**
     * * 初始化AMap对象
     */
    protected void initAMap() {
        if (aMap == null) {
            aMap = textureMapView.getMap();
        }
        markerUtil = new DrawMarkerUtil(aMap);
        uiSettings = aMap.getUiSettings();
        setUpMap();
    }

    /**
     * 设置Map属性和监听事件
     */
    protected void setMapAttributeListener(){
        // 设置默认定位按钮是否显示
        uiSettings.setMyLocationButtonEnabled(true);
        // 是否可触发定位并显示定位层
        aMap.setMyLocationEnabled(true);
    }

    /**
     * 配置定位参数
     */
    protected void setUpMap() {
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
                    poiName = amapLocation.getPoiName();
                    LogUtil.e("pcw----", "lat : " + lat + " lon : " + lon+"poiName : " +poiName);
                    LogUtil.e("pcw----", " Country : " + amapLocation.getCountry() +
                            " province : " + amapLocation.getProvince() +
                            " City : " + amapLocation.getCity() +
                            " District : " + amapLocation.getDistrict()+
                            " AoiName : " + amapLocation.getAoiName()+
                            " CityCode : " + amapLocation.getCityCode()+
                            " AdCode : " + amapLocation.getAdCode()
                    );
                    startPoint = new LatLonPoint(lat,lon);
                    naviStart = new NaviLatLng(lat,lon);
                    // 设置当前地图显示为当前位置
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 14.5f));

                    // 连续定位、且将视角移动到地图中心点，
                    // 定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
                    myLocation.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
                    //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
                    myLocation.interval(2000);
                    aMap.setMyLocationStyle(myLocation);
                    // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
                    aMap.setMyLocationEnabled(true);
                    // 完成定位发送消息，默认搜索ATM
                    message.what = BaseConstants.POI_SEARCH;
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
     * 设置搜索poi 码
     * @param vrg
     */
    protected void setPoiCode(String vrg,int scope){
        poiCode = vrg;
        poiAtmScope = scope;
    }
    /**
     * 周边POI搜索
     */
    private void poiSearch(){
        PoiSearch.Query query = new PoiSearch.Query("",poiCode,cityCode);
        PoiSearch poiSearch = new PoiSearch(getActivity(),query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
        // TODO: 2017/12/25  设置周边搜索的中心点以及半径 暂设置为5公里范围内
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(lat,
                lon), poiAtmScope));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
    public void onClick(View v) {

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

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }
}
