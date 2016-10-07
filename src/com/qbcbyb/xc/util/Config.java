package com.qbcbyb.xc.util;

public class Config {
    private String nowRequestFormat = "RequestFromLocal";
    private String requestUrl = "http://192.168.10.80:8080/GisServer/HKGIS/";
    private int pageSize = 20;

    public String getNowRequestFormat() {
        return nowRequestFormat;
    }

    public void setNowRequestFormat(String nowRequestFormat) {
        this.nowRequestFormat = nowRequestFormat;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
