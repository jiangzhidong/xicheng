package com.qbcbyb.xc.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.baidu.mapapi.map.MKOLUpdateElement;
import com.baidu.mapapi.map.MKOfflineMap;
import com.baidu.mapapi.map.MKOfflineMapListener;
import com.baidu.mapapi.map.MapView;
import com.qbcbyb.libandroid.msg.Msg;
import com.qbcbyb.libandroid.msg.Msg.MsgCallback;
import com.qbcbyb.libandroid.msg.Msg.Which;

public class BDMapOfflineManager implements MKOfflineMapListener {

    private static final int OFFLINE_CITYID_BJ = 131;
    private static final int OFFLINE_CITYID_CHINA = 1;

    private final Context context;
    private final ProgressBar progressBar;
    private MKOfflineMap mapOffline = null;
    private List<Integer> toDownloadCitys = new ArrayList<Integer>();

    public BDMapOfflineManager(Context context, MapView mapView, ProgressBar progressBar) {
        super();
        this.context = context;
        this.progressBar = progressBar;
        mapOffline = new MKOfflineMap();
        // init offlinemap
        mapOffline.init(mapView.getController(), this);

        if (mapOffline != null) {
            MKOLUpdateElement chinaOfflineData = mapOffline.getUpdateInfo(OFFLINE_CITYID_CHINA);
            MKOLUpdateElement hkOfflineData = mapOffline.getUpdateInfo(OFFLINE_CITYID_BJ);
            if (hkOfflineData == null || chinaOfflineData == null) {
                confirmToDownload("离线地图", "离线地图尚未下载，确认下载？\n请在WIFI环境下载离线地图以节省流量！", OFFLINE_CITYID_CHINA,
                    OFFLINE_CITYID_BJ);
            } else if (hkOfflineData.ratio < 100 || chinaOfflineData.ratio < 100) {
                if (hkOfflineData.ratio < 100 && chinaOfflineData.ratio < 100) {
                    confirmToDownload("离线地图", "离线地图未下载完成，继续下载？\n请在WIFI环境下载离线地图以节省流量！", chinaOfflineData.cityID,
                        hkOfflineData.cityID);
                }
            } else if (hkOfflineData.update || chinaOfflineData.update) {
                if (hkOfflineData.update && chinaOfflineData.update) {
                    confirmToDownload("离线地图", "离线地图有更新，是否下载？\n请在WIFI环境下载离线地图以节省流量！", chinaOfflineData.cityID,
                        hkOfflineData.cityID);
                }
            } else {
                // Msg.showInfo(context,
                // String.format("全国离线包大小: %.2fMB 已下载  %d%%\n香港离线包大小：%.2fMB 已下载  %d%%",
                // ((double) chinaOfflineData.size) / 1000000,
                // chinaOfflineData.ratio,
                // ((double) hkOfflineData.size) / 1000000,
                // hkOfflineData.ratio));
            }
        }
    }

    @Override
    public void onGetOfflineMapState(int type, int state) {

        switch (type) {
        case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
            MKOLUpdateElement update = mapOffline.getUpdateInfo(state);
            if (update != null) {
                progressBar.setProgress(update.ratio);
                if (update.ratio != 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    if (toDownloadCitys.size() > 0) {
                        downloadNextCity();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Msg.showInfo(context, String.format(update.cityName + "离线包大小: %.2fMB 已下载  %d%%",
                        ((double) update.size) / 1000000, update.ratio));
                }
            }
            break;
        case MKOfflineMap.TYPE_NEW_OFFLINE:
            // Msg.showInfo(context, String.format("新安装%d个离线地图", state));
            break;
        case MKOfflineMap.TYPE_VER_UPDATE:
            // MKOLUpdateElement e = mOffline.getUpdateInfo(state);
            // if (e != null) {
            // // Log.d("OfflineDemo",
            // // String.format("%d has new offline map: ",e.cityID));
            // mText.setText(String.format("%s 有离线地图更新", e.cityName));
            // }
            break;
        }
    }

    private void confirmToDownload(String title, String msg, final int... cityIDs) {
        Msg.confirm(context, title, msg, new MsgCallback() {

            @Override
            public void callBack(Which which) {
                if (which == Which.POSITIVE) {
                    for (int cityID : cityIDs) {
                        if (!toDownloadCitys.contains(cityID)) {
                            toDownloadCitys.add(cityID);
                        }
                    }
                    if (toDownloadCitys.size() > 0) {
                        downloadNextCity();
                    }
                }
            }
        });
    }

    private void downloadNextCity() {
        if (toDownloadCitys.size() > 0) {
            int cityID = toDownloadCitys.remove(0);
            MKOLUpdateElement update = mapOffline.getUpdateInfo(cityID);
            if (update == null || update.update || update.ratio < 100) {
                mapOffline.start(cityID);
                progressBar.setProgress(0);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                downloadNextCity();
            }
        }
    }

    public void destroy() {
        if (mapOffline != null) {
            mapOffline.destroy();
        }
    }

}
