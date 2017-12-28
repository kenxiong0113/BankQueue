package com.ruan.bankqueue.routePlanning;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.ruan.bankqueue.R;
import com.ruan.bankqueue.util.AMapUtil;
import com.ruan.bankqueue.util.ToastUtil;
import com.ruan.overlay.WalkRouteOverlay;

/**
 * @author  by ruan on 2017/12/28.
 *
 */

public class Route implements RouteSearch.OnRouteSearchListener {
    AMap aMap;
    Context mContext;
    WalkRouteResult mWalkRouteResult;
    public Route(){

    }
    public Route(AMap aMap, Context context, WalkRouteResult walkRouteResult){
        this.aMap = aMap;
        this.mContext = context;
        this.mWalkRouteResult = walkRouteResult;
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        // 清理地图上的所有覆盖物
        aMap.clear();
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mWalkRouteResult = result;
                    final WalkPath walkPath = mWalkRouteResult.getPaths()
                            .get(0);
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            mContext, aMap, walkPath,
                            mWalkRouteResult.getStartPos(),
                            mWalkRouteResult.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                    int dis = (int) walkPath.getDistance();
                    int dur = (int) walkPath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
//                    tvDistance.setText(des);

                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, R.string.no_result);
                }
            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(mContext, errorCode);
        }


    }
    public  interface CallBack{
        String distance();
    }


    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }
}
