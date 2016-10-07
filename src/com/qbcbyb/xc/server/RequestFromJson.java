package com.qbcbyb.xc.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.qbcbyb.libandroid.http.DownLoadingProgress;
import com.qbcbyb.libandroid.http.HttpRequestTools;
import com.qbcbyb.libandroid.http.ResponseResult;
import com.qbcbyb.xc.model.SecondMenuModel;
import com.qbcbyb.xc.model.SpotModel;

public class RequestFromJson implements IRequest {

    @SuppressWarnings("unused")
    private final String KEYSUCCESS = "success";
    @SuppressWarnings("unused")
    private final String KEYTOTALCOUNT = "totalcount";
    @SuppressWarnings("unused")
    private final String KEYMESSAGE = "message";

    @SuppressWarnings("unused")
    private String formatReqParam(String[] paramNames, String... paramValues) {

        StringBuilder sb = new StringBuilder();

        String encodingType = "UTF-8";
        if (paramValues != null)
            for (int i = 0, count = paramNames.length; i < count; i++) {
                if (sb.length() != 0) {
                    sb.append("&");
                }
                sb.append(paramNames[i]);
                sb.append("=");
                if (paramValues.length > i && paramValues[i] != null && paramValues[i].length() > 0) {
                    try {
                        sb.append(URLEncoder.encode(paramValues[i], encodingType));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

        return sb.toString();
    }

    private String RequestPage(String url, String postString, DownLoadingProgress downLoadingProgress, int handlerStatus)
        throws IOException {

        String reResult = HttpRequestTools
            .getReturnString(url + "?" + postString, downLoadingProgress, handlerStatus, null).trim()
            .replace("(\r\n|\r|\n)", "");

        return reResult;

    }

    @SuppressWarnings("unused")
    private String RequestPage(String url, String postString) throws IOException {
        return RequestPage(url, postString, null, 0);
    }

    @Override
    public ResponseResult<List<SecondMenuModel>> loadSecondMenu(int firstMenuId, int nowPage, int pageSize)
        throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResponseResult<List<SpotModel>> loadThirdMenu(int secondMenuId) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResponseResult<SpotModel> querySpotDetail(SpotModel spotModel) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResponseResult<List<SpotModel>> searchSpotDetail(Integer firstMenuId, Integer secondMenuId, String keyWord)
        throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
