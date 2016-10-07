package com.qbcbyb.xc.server;

import java.util.List;
import java.util.Map;

import com.qbcbyb.libandroid.http.ResponseResult;
import com.qbcbyb.xc.model.SecondMenuModel;
import com.qbcbyb.xc.model.SpotModel;

public interface IRequest {

    public ResponseResult<List<SecondMenuModel>> loadSecondMenu(int firstMenuId, int nowPage, int pageSize)
        throws Exception;

    public ResponseResult<List<SpotModel>> loadThirdMenu(int secondMenuId) throws Exception;

    public ResponseResult<SpotModel> querySpotDetail(SpotModel spotModel) throws Exception;

    public ResponseResult<List<SpotModel>> searchSpotDetail(Integer firstMenuId, Integer secondMenuId, String keyWord)
        throws Exception;
    

}
