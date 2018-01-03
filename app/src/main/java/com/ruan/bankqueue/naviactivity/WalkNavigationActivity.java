package com.ruan.bankqueue.naviactivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviLatLng;
import com.ruan.bankqueue.R;
import com.ruan.bankqueue.naviutil.TTSController;

import java.util.Arrays;

/**
 * @author  by ruan on 2017/12/30.
 */

public class WalkNavigationActivity extends NaviBaseActivity {
    NaviLatLng naviStart;
    NaviLatLng naviEnd;
    double startLat,startLon,endLat,endLon;
    String poiName,markerName;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_navi);
        context = getApplicationContext();
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        initNaviListener();
        getNaviStartAndNaviEnd();
        naviCallBack();

    }

    private void naviCallBack(){
        //传入起点
        Poi start = new Poi(poiName, new LatLng(startLat,startLon), null);
        //传入终点
        Poi end = new Poi(markerName, new LatLng(endLat, endLon), null);
        //设置出行方式 AmapNaviType.WALK 第二个参数设置沿途poi
        AmapNaviPage.getInstance().showRouteActivity(context,
                new AmapNaviParams(start, null, end, AmapNaviType.WALK), new INaviInfoCallback() {
                    /**
                     * 导航初始化失败时的回调函数
                     **/
                    @Override
                    public void onInitNaviFailure() {
                        Log.e("WalkNavigationActivity", "导航初始化失败----");
                    }
                    /**
                     * 导航播报信息回调函数。
                     * @param s 语音播报文字
                     **/
                    @Override
                    public void onGetNavigationText(String s) {
                        Log.e("WalkNavigationActivity", s);
                    }
                    /**
                     * 当GPS位置有更新时的回调函数。
                     *@param aMapNaviLocation 当前坐标位置
                     **/
                    @Override
                    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
                       double altitude =  aMapNaviLocation.getAltitude();
                       float speed = aMapNaviLocation.getSpeed();
                       long time = aMapNaviLocation.getTime();
                        Log.e("WalkNavigationActivity", "speed:" + speed);
                    }
                    /**
                     * 到达目的地后回调函数。
                     **/
                    @Override
                    public void onArriveDestination(boolean b) {

                    }
                    /**
                     * 启动导航后的回调函数
                     **/
                    @Override
                    public void onStartNavi(int i) {

                    }
                    /**
                     * 算路成功回调
                     * @param ints 路线id数组
                     */
                    @Override
                    public void onCalculateRouteSuccess(int[] ints) {
                        Log.e("WalkNavigationActivity", "ints:" + Arrays.toString(ints));
                        WalkNavigationActivity.super.onCalculateRouteSuccess(ints);
                        mAMapNavi.startNavi(NaviType.GPS);
                    }
                    /**
                     * 步行或者驾车路径规划失败后的回调函数
                     **/
                    @Override
                    public void onCalculateRouteFailure(int i) {

                    }
                    /**
                     * 停止语音回调，收到此回调后用户可以停止播放语音
                     **/
                    @Override
                    public void onStopSpeaking() {

                    }
                });


    }

    private void initNaviListener(){
        //实例化语音引擎
        mTtsManager = TTSController.getInstance(getApplicationContext());
        mTtsManager.init();
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.addAMapNaviListener(mTtsManager);
        mAMapNaviView.setAMapNaviViewListener(this);
        mAMapNaviView.setNaviMode(AMapNaviView.NORTH_UP_MODE);

    }

    private void getNaviStartAndNaviEnd(){
        startLat =getIntent().getDoubleExtra("startLat",0);
        startLon =getIntent().getDoubleExtra("startLon",0);
        endLat =getIntent().getDoubleExtra("endLat",0);
        endLon =getIntent().getDoubleExtra("endLon",0);
        poiName = getIntent().getStringExtra("poiName");
        markerName = getIntent().getStringExtra("markerName");

        mStartLatlng = new NaviLatLng(startLat,startLon);
        mEndLatlng = new NaviLatLng(endLat,endLon);

        Log.e("WalkNavigationActivity", "mStartLatlng:" + mStartLatlng);
    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        Log.e("WalkNavigationActivity", "导航初始化成功----");
        mAMapNavi.calculateWalkRoute(new NaviLatLng(startLat,startLon), new NaviLatLng(endLat,endLon));
        //设置模拟导航的行车速度
        mAMapNavi.setEmulatorNaviSpeed(75);
        sList.add(mStartLatlng);
        eList.add(mEndLatlng);

    }

    @Override
    public void onCalculateRouteSuccess(int[] ids) {
        super.onCalculateRouteSuccess(ids);
        mAMapNavi.startNavi(NaviType.GPS);
    }

}
