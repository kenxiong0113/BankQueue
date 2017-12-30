package com.ruan.bankqueue.routePlanning;

import android.content.Context;
import android.os.Message;
import android.widget.TextView;
import com.amap.api.maps.AMap;
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
    TextView tvDistance;
    public Route(){

    }
    public Route(AMap aMap, Context context,TextView tvDistance){
        this.aMap = aMap;
        this.mContext = context;
        this.tvDistance = tvDistance;
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
                    final WalkPath walkPath = result.getPaths()
                            .get(0);
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            mContext, aMap, walkPath,
                            result.getStartPos(),
                            result.getTargetPos());
//                    去掉BusRouteOverlay上所有的Marker
                    walkRouteOverlay.removeFromMap();
//                    添加步行路线到地图中。
                    walkRouteOverlay.addToMap();
//                    移动镜头到当前的视角。
                    walkRouteOverlay.zoomToSpan();
//                    路段节点图标控制显示
                    walkRouteOverlay.setNodeIconVisibility(true);
                    //距离
                    int dis = (int) walkPath.getDistance();
                    //时间
                    int dur = (int) walkPath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
                    tvDistance.setText(des);
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

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

}
