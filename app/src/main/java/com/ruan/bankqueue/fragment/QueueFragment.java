package com.ruan.bankqueue.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.ruan.bankqueue.R;
import com.ruan.bankqueue.activity.QueueActivity;
import com.ruan.bankqueue.naviactivity.WalkNavigationActivity;
import com.ruan.bankqueue.other.BaseConstants;
import com.ruan.bankqueue.util.ConfirmDialog;
import com.ruan.bankqueue.util.LogUtil;
import com.ruan.overlay.PoiOverlay;
import java.util.List;

/**
 * @author by ruan on 2017/12/16. 银行排队
 */

public class QueueFragment extends BaseFragment{
    private static String ARG = "arg";
    protected Context mContext;
    protected AMapNavi aMapNavi;
    protected View viewBank;
    protected TextView tvBankTitle;
    protected ImageView imgBank;
    private double endLat;
    private double endLon;
    public QueueFragment() {
        super();
    }
    public static QueueFragment newInstance(String param) {
        QueueFragment fragment = new QueueFragment();
        Bundle args = new Bundle();
        args.putString(ARG, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(R.layout.fragment_queue, container, false);
        textureMapView = (TextureMapView)view.findViewById(R.id.map_bank);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        textureMapView.onCreate(savedInstanceState);
        setPoiCode("160100",8000);
        mContext = getActivity().getApplicationContext();
        aMapNavi = AMapNavi.getInstance(mContext);

        myLocation.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);
        myLocation.interval(30000);
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
    @Override
    protected void initAMap() {
        super.initAMap();

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
                viewBank = View.inflate(getActivity(),R.layout.view_bank, null);
                tvBankTitle = (TextView)viewBank.findViewById(R.id.tv_bank_title);
                imgBank = (ImageView)viewBank.findViewById(R.id.img_bank);
                Bitmap bitmap = markerUtil.convertViewToBitmap(viewBank);
                markerUtil.drawMarkerOnMap(new LatLng(poiResult.getPois().get(j).getLatLonPoint().getLatitude(),
                                poiResult.getPois().get(j).getLatLonPoint().getLongitude()),
                        bitmap, poiResult.getPois().get(j).getTitle());
            }
            setMapAttributeListener();
        }else {
            LogUtil.e("AtmFragment", "i:" + i);
        }
    }

    @Override
    protected void setMapAttributeListener() {
        super.setMapAttributeListener();
        aMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Log.e("QueueFragment", marker.getTitle());
        endLat = marker.getPosition().latitude;
        endLon = marker.getPosition().longitude;
        final ConfirmDialog dialog = new ConfirmDialog(getActivity(),
                "请选择",marker.getTitle(),"排队","取消","导航");
        dialog.show();
        dialog.setClickListener(new ConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                Intent intent = new Intent(getActivity(), QueueActivity.class);
                intent.putExtra("bankName",marker.getTitle());
                startActivity(intent);
                dialog.dismiss();
            }

            @Override
            public void doCancel() {
                dialog.dismiss();
            }

            @Override
            public void doNavigation() {
                dialog.dismiss();
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
            }
        });

        return super.onMarkerClick(marker);
    }
}
