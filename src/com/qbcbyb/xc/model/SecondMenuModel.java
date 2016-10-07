package com.qbcbyb.xc.model;

import com.qbcbyb.libandroid.model.BaseModel;

public class SecondMenuModel extends BaseModel {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private Integer pid;
    private CGeometry geometry;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public CGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(CGeometry geometry) {
        this.geometry = geometry;
    }

}
