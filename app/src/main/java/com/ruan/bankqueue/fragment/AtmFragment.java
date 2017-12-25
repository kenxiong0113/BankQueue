package com.ruan.bankqueue.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.google.gson.Gson;
import com.ruan.bankqueue.R;
import com.ruan.bankqueue.other.BaseConstants;
import com.ruan.bankqueue.util.LogUtil;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * @author by ruan on 2017/12/16.
 */

public class AtmFragment extends Fragment implements PoiSearch.OnPoiSearchListener {
    View view;
    static String ARG = "arg";
    @BindView(R.id.map_view)
    TextureMapView textureMapView;
    Message message = new Message();
    String city = null;
    String cityCode = null;
    private AMap aMap;
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
        textureMapView.onCreate(savedInstanceState);
        checkThePermissions();
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
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
        mLocationClient.onDestroy();//销毁定位客户端。
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        textureMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        textureMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mLocationClient.stopLocation();//停止定位
    }

    @Override
    public void onDestroy() {
        //保存地图状态
        super.onDestroy();
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
                    //定位成功回调信息，设置相关消息
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    amapLocation.getLatitude();//获取纬度
                    amapLocation.getLongitude();//获取经度
                    amapLocation.getAccuracy();//获取精度信息
                    SimpleDateFormat df = new SimpleDateFormat(time);
                    Date date = new Date(amapLocation.getTime());
                    //定位时间
                    df.format(date);
                    //地址，如果option中设置isNeedAddress为false，则没有此结果，
                    // 网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    amapLocation.getAddress();
                    amapLocation.getCountry();//国家信息
                    amapLocation.getProvince();//省信息
                    amapLocation.getCity();//城市信息
                    amapLocation.getDistrict();//城区信息
                    amapLocation.getStreet();//街道信息
                    amapLocation.getStreetNum();//街道门牌号信息
                    amapLocation.getCityCode();//城市编码
                    amapLocation.getAdCode();//地区编码
                    amapLocation.getAoiName();//获取当前定位点的AOI信息
                    lat = amapLocation.getLatitude();
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

                    // 设置当前地图显示为当前位置
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15.5f));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(lat, lon));
                    markerOptions.title("当前位置");
                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_direction));
                    markerOptions.icon(bitmapDescriptor);
                    markerOptions.visible(true);
                    aMap.addMarker(markerOptions);

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
        // TODO: 2017/12/25  设置周边搜索的中心点以及半径 暂设置为10公里范围内
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(lat,
                lon), 8000));
    }

    /**
     * 查询poi回调
     * @param poiResult 回调接口
     * @param i 返回码
     */
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
//        LogUtil.e("AtmFragment", "poiResult.getPois():----" + poiResult.getPois());
//        String object =  new Gson().toJson(poiResult.getBound());
//        LogUtil.e("AtmFragment", "poiResult.getBound():" + object);
        // TODO: 2017/12/25 在地图上面显示兴趣点
        MarkerOptions marker = new MarkerOptions();
        for (int k = 0;k <= poiResult.getPois().size();k++){
            double atmLat,atmLon;
            atmLon = poiResult.getPois().get(k).getLatLonPoint().getLatitude();
            atmLat = poiResult.getPois().get(k).getLatLonPoint().getLongitude();
            LogUtil.e("AtmFragment----", "经度 ：" + atmLat);
            LogUtil.e("AtmFragment----", "纬度 ：" + atmLon);
            marker.position(new LatLng(atmLat,atmLon));
            marker.title("ATM");
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_atm));
            marker.icon(bitmapDescriptor);
            marker.visible(true);
            aMap.addMarker(marker).setClickable(true);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
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
}

