package com.ruan.bankqueue.util;

import android.graphics.Bitmap;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

/**
 * @author  by ruan on 2018/1/3. 图片转 bitmap 设置Marker图标
 */

public class DrawMarkerUtil {
    private  AMap aMap;
    public DrawMarkerUtil(AMap aMap){
        this.aMap = aMap;
    }
    /**
     * @param view 转 Bitmap
     * @return
     */
    public  Bitmap convertViewToBitmap(View view) {
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
    public  Marker drawMarkerOnMap(LatLng point, Bitmap markerIcon, String title) {
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
}
