package com.ruan.bankqueue.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.ruan.bankqueue.R;
import com.ruan.bankqueue.activity.QueueActivity;
import com.ruan.bankqueue.activity.SubscribeActivity;
import com.ruan.bankqueue.javabean.User;
import com.ruan.bankqueue.javabean.UserIntegral;
import com.ruan.bankqueue.naviactivity.WalkNavigationActivity;
import com.ruan.bankqueue.other.BaseConstants;
import com.ruan.bankqueue.util.ConfirmDialog;
import com.ruan.bankqueue.util.LogUtil;
import com.ruan.overlay.PoiOverlay;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @author by ruan  预约
 */

public class SubscribeFragment extends BaseFragment {

    View view;
    static String ARG = "arg";
    protected Context mContext;
    protected AMapNavi aMapNavi;
    protected View viewBank;
    protected TextView tvBankTitle;
    protected ImageView imgBank;
    private double endLat;
    private double endLon;
    User user;
    public SubscribeFragment() {
        super();
    }

    public static SubscribeFragment newInstance(String param) {
        SubscribeFragment fragment = new SubscribeFragment();
        Bundle args = new Bundle();
        args.putString(ARG, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        user = BmobUser.getCurrentUser(User.class);
        view = inflater.inflate(R.layout.fragment_subscribe, container, false);
        textureMapView = (TextureMapView)view.findViewById(R.id.map_bank);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        textureMapView.onCreate(savedInstanceState);
        setPoiCode("160100",9500);
        mContext = getActivity().getApplicationContext();
        aMapNavi = AMapNavi.getInstance(mContext);
        myLocation.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
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
                "请选择",marker.getTitle(),"预约","取消","导航");
        dialog.show();
        dialog.setClickListener(new ConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                BmobQuery<UserIntegral> query = new BmobQuery<UserIntegral>();
                query.addWhereEqualTo("phone",user.getUsername());
                query.findObjects(new FindListener<UserIntegral>() {
                    @Override
                    public void done(List<UserIntegral> list, BmobException e) {
                        if (e == null){
                            Integer i = list.get(0).getIntegral();
                            if (i < 90){
                                AlertDialog.Builder dia = new AlertDialog.Builder(getActivity());
                                dia.setCancelable(true);
                                dia.setTitle("很抱歉");
                                dia.setIcon(R.drawable.ic_x);
                                dia.setMessage("你的信誉积分低于90分，已被加入黑名单，无法正常使用该功能," +
                                        "请到任意银行网点办理相关手续提升信誉积分");
                                dia.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dia, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                dia.show();
                            }else {
                                Intent intent = new Intent(getActivity(), SubscribeActivity.class);
                                intent.putExtra("bankName",marker.getTitle());
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        }else {
                            Toast.makeText(getActivity(), e.getMessage() + e.getErrorCode(),
                                    Toast.LENGTH_SHORT).show();
                            Log.e("UserInfoActivity", e.getMessage() + e.getErrorCode());
                        }
                    }
                });

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
