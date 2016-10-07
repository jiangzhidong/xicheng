package com.qbcbyb.xc.model;

import java.util.List;

import com.qbcbyb.libandroid.model.BaseModel;

public class SpotModel extends BaseModel {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer pid;
    private String name;
    private String image;
    private String desc;
    private Integer grade;
    private CGeometry geometry;

    private String detailAddress;

    private String detailSummary;
    private String detailDesc;
    private List<String> detailImages;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public CGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(CGeometry geometry) {
        this.geometry = geometry;
    }

    public String getDetailSummary() {
        return detailSummary;
    }

    public void setDetailSummary(String detailSummary) {
        this.detailSummary = detailSummary;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getDetailDesc() {
        return detailDesc;
    }

    public void setDetailDesc(String detailDesc) {
        this.detailDesc = detailDesc;
    }

    public List<String> getDetailImages() {
        return detailImages;
    }

    public void setDetailImages(List<String> detailImages) {
        this.detailImages = detailImages;
    }

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

}
