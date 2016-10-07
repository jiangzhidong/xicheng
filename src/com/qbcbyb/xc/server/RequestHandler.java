package com.qbcbyb.xc.server;

import java.util.List;
import java.util.Map;

import com.qbcbyb.libandroid.http.ResponseResult;
import com.qbcbyb.xc.model.SecondMenuModel;
import com.qbcbyb.xc.model.SpotModel;
import com.qbcbyb.xc.util.ApplicationMain;

public class RequestHandler {
    private IRequest requestFormat; // 接口IRequest实例

    private static RequestHandler instance;

    private static final Object lock = new Object();

    public static RequestHandler getInstance() throws Exception {
        synchronized (lock) {
            if (instance == null) {
                instance = new RequestHandler();
            }
            return instance;
        }
    }

    public RequestHandler() throws Exception {
        try {
            requestFormat = (IRequest) Class.forName(
                this.getClass().getPackage().getName() + "."
                        + ApplicationMain.getInstance().getConfig().getNowRequestFormat()).newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new Exception("无法访问到后台请求的类！");
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new Exception("无法实例化后台请求的类！");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new Exception("找不到后台请求的类！");
        }

    }

    public ResponseResult<List<SecondMenuModel>> loadSecondMenu(int firstMenuId, int nowPage, int pageSize)
        throws Exception {
        return requestFormat.loadSecondMenu(firstMenuId, nowPage, pageSize);
    }

    public ResponseResult<List<SpotModel>> loadThirdMenu(int secondMenuId) throws Exception {
        return requestFormat.loadThirdMenu(secondMenuId);
    }

    public ResponseResult<SpotModel> querySpotDetail(SpotModel spotModel) throws Exception {
        return requestFormat.querySpotDetail(spotModel);
    }

    public ResponseResult<List<SpotModel>> searchSpotDetail(Integer firstMenuId, Integer secondMenuId, String keyWord) throws Exception {
        return requestFormat.searchSpotDetail(firstMenuId, secondMenuId,keyWord);
    }
   
}
