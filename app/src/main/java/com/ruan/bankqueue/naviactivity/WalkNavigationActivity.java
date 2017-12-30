package com.ruan.bankqueue.naviactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.ruan.bankqueue.R;
import com.ruan.bankqueue.naviactivity.NaviBaseActivity;
import com.ruan.bankqueue.naviutil.TTSController;

/**
 * @author  by ruan on 2017/12/30.
 */

public class WalkNavigationActivity extends NaviBaseActivity{
    NaviLatLng naviStart;
    NaviLatLng naviEnd;
    double startLat,startLon,endLat,endLon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_navi);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
        mAMapNaviView.setNaviMode(AMapNaviView.NORTH_UP_MODE);
        getNaviStartAndNaviEnd();
    }

    private void getNaviStartAndNaviEnd(){
//        Intent intent = new Intent();
//        naviStart = (NaviLatLng)intent.getSerializableExtra("startPoint");
//        naviEnd = (NaviLatLng)intent.getSerializableExtra("endPoint");

        startLat =getIntent().getDoubleExtra("startLat",0);
        startLon =getIntent().getDoubleExtra("startLon",0);
        endLat =getIntent().getDoubleExtra("endLat",0);
        endLon =getIntent().getDoubleExtra("endLon",0);
        Log.e("WalkNavigationActivity", "startLat:----" + startLat);
    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        mAMapNavi.calculateWalkRoute(new NaviLatLng(startLat,startLon), new NaviLatLng(endLat,endLon));
    }

    @Override
    public void onCalculateRouteSuccess(int[] ids) {
        super.onCalculateRouteSuccess(ids);
        mAMapNavi.startNavi(NaviType.GPS);
    }
}
