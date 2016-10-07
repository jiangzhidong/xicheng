package com.qbcbyb.xc.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qbcbyb.libandroid.StringUtils;
import com.qbcbyb.libandroid.dbhelp.DbExecuteHelper;
import com.qbcbyb.libandroid.http.ResponseResult;
import com.qbcbyb.xc.MainActivity;
import com.qbcbyb.xc.model.CGeometry;
import com.qbcbyb.xc.model.CGeometry.CGeometryType;
import com.qbcbyb.xc.model.CGeometry.MapPoint;
import com.qbcbyb.xc.model.SecondMenuModel;
import com.qbcbyb.xc.model.SpotModel;
import com.qbcbyb.xc.util.ApplicationMain;

public class RequestFromLocal implements IRequest {

    @Override
    public ResponseResult<List<SecondMenuModel>> loadSecondMenu(final int firstMenuId, final int nowPage,
            final int pageSize) throws Exception {
        final ResponseResult<List<SecondMenuModel>> result = new ResponseResult<List<SecondMenuModel>>();

        try {

            ApplicationMain.getInstance().getDbHelper().exeSql(new DbExecuteHelper() {
                @Override
                public void exeSqlMethod(SQLiteDatabase db) {
                    String sql = " select id,name,geometry from secondmenu where pid=? limit ? offset ? ";
                    Cursor cursor = db.rawQuery(
                        sql,
                        new String[] { Integer.toString(firstMenuId), Integer.toString(pageSize),
                                Integer.toString(nowPage * pageSize) });
                    if (cursor != null && cursor.getCount() > 0) {
                        List<SecondMenuModel> list = new ArrayList<SecondMenuModel>();
                        while (cursor.moveToNext()) {
                            SecondMenuModel secondMenuModel = new SecondMenuModel();
                            secondMenuModel.setId(cursor.getInt(0));
                            secondMenuModel.setName(cursor.getString(1));
                            secondMenuModel.setPid(firstMenuId);
                            String geometry = cursor.getString(2);
                            if (StringUtils.isNotEmpty(geometry)) {
                                try {
                                    JSONObject geoObj = new JSONObject(geometry);
                                    String type = geoObj.getString("type");
                                    JSONArray pointsArr = geoObj.getJSONArray("points");
                                    MapPoint[] points = new MapPoint[pointsArr.length()];
                                    for (int i = 0; i < points.length; i++) {
                                        JSONArray point = pointsArr.getJSONArray(i);
                                        points[i] = new MapPoint(point.getDouble(0), point.getDouble(1));
                                    }

                                    CGeometry cGeometry = new CGeometry();
                                    cGeometry.setType(CGeometryType.valueOf(type));
                                    cGeometry.setPoints(points);

                                    secondMenuModel.setGeometry(cGeometry);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            list.add(secondMenuModel);
                        }
                        result.setResultObj(list);
                        result.setSuccess(true);
                    }
                }
            });
        } catch (Exception e) {
            result.setHadError(true);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public ResponseResult<List<SpotModel>> loadThirdMenu(final int secondMenuId) throws Exception {
        final ResponseResult<List<SpotModel>> result = new ResponseResult<List<SpotModel>>();

        try {

            ApplicationMain.getInstance().getDbHelper().exeSql(new DbExecuteHelper() {
                @Override
                public void exeSqlMethod(SQLiteDatabase db) {
                    String sql = " select t.id,t.name,t.image,t.desc,t.geometry,t.detailAddress,t.grade from spot t left join menuspotlink l on t.id=l.spotid where l.secondmenuid=? ";
                    Cursor cursor = db.rawQuery(sql, new String[] { Integer.toString(secondMenuId) });
                    if (cursor != null && cursor.getCount() > 0) {
                        List<SpotModel> list = new ArrayList<SpotModel>();
                        while (cursor.moveToNext()) {
                            SpotModel spotModel = new SpotModel();
                            spotModel.setId(cursor.getInt(0));
                            spotModel.setName(cursor.getString(1));
                            spotModel.setImage(cursor.getString(2));
                            spotModel.setDesc(cursor.getString(3));
                            spotModel.setDetailAddress(cursor.getString(5));
                            spotModel.setGrade(cursor.getInt(6));
                            spotModel.setPid(secondMenuId);

                            String geometry = cursor.getString(4);
                            if (StringUtils.isNotEmpty(geometry)) {
                                try {
                                    JSONObject geoObj = new JSONObject(geometry);
                                    String type = geoObj.getString("type");
                                    JSONArray pointsArr = geoObj.getJSONArray("points");
                                    MapPoint[] points = new MapPoint[pointsArr.length()];
                                    for (int i = 0; i < points.length; i++) {
                                        JSONArray point = pointsArr.getJSONArray(i);
                                        points[i] = new MapPoint(point.getDouble(0), point.getDouble(1));
                                    }

                                    CGeometry cGeometry = new CGeometry();
                                    cGeometry.setType(CGeometryType.valueOf(type));
                                    cGeometry.setPoints(points);

                                    spotModel.setGeometry(cGeometry);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            list.add(spotModel);
                        }
                        result.setResultObj(list);
                        result.setSuccess(true);
                    }
                }
            });
        } catch (Exception e) {
            result.setHadError(true);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public ResponseResult<SpotModel> querySpotDetail(final SpotModel spotModel) throws Exception {
        final ResponseResult<SpotModel> result = new ResponseResult<SpotModel>();

        try {

            ApplicationMain.getInstance().getDbHelper().exeSql(new DbExecuteHelper() {
                @Override
                public void exeSqlMethod(SQLiteDatabase db) {

                    String sql = " select t.detailSummary,t.detailDesc from spot t where t.id=? ";
                    Cursor cursor = db.rawQuery(sql, new String[] { Integer.toString(spotModel.getId().intValue()) });
                    if (cursor != null && cursor.getCount() > 0) {
                        if (cursor.moveToNext()) {
                            spotModel.setDetailSummary(cursor.getString(0));
                            spotModel.setDetailDesc(cursor.getString(1));
                        }
                    }

                    List<String> imageUrlList = new ArrayList<String>();// TODO
                    String foldName = String.format("/%03d", spotModel.getId().intValue());
                    long a = System.currentTimeMillis();
                    try {
						imageUrlList = loadAllImg( spotModel.getId().intValue());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                   // getImageListAssets(foldName,imageUrlList);
                    
                    //FileimageFold = new File(ApplicationMain.getInstance().getImagePath(foldName));
                    //getImageList(imageFold, imageUrlList,spotModel.getId().toString());
                    // sql =
                    // " select t.imageurl from spotimage t where t.spotid=? order by t.id";
                    // cursor = db.rawQuery(sql, new String[] {
                    // Integer.toString(spotModel.getId().intValue()) });
                    // if (cursor != null && cursor.getCount() > 0) {
                    // while (cursor.moveToNext()) {
                    // imageUrlList.add(cursor.getString(0));
                    // }
                    // }
                    spotModel.setDetailImages(imageUrlList);
                    result.setResultObj(spotModel);
                    result.setSuccess(true);
                }
            });
        } catch (Exception e) {
            result.setHadError(true);
            result.setMessage(e.getMessage());
        }
        return result;
    }
    /**
     * assets 文件获取
     * @param foldName
     * @param imageUrlList
     */
	protected void getImageListAssets(String foldName, List<String> imageUrlList) {
		try {
			String[] imageList = ApplicationMain.getInstance().getAssets()
					.list("img" + foldName);
			for (String ss : imageList) {
				if (ss.startsWith("20")) {
					String newFoleName = foldName + "/" + ss;
					getImageListAssets(newFoleName, imageUrlList);
				} else {
					imageUrlList.add("img" + foldName + "/" + ss);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    protected  void getImageList(File imageFold, List<String> imageUrlList,String soptid ) {
        if (imageFold.isFile()) {
        	if(imageFold.getName().contains("_")){
        		imageUrlList.add(imageFold.getAbsolutePath());
        	}
        } else if (imageFold.isDirectory()) {
            String[] filelist = imageFold.list();
            if (filelist != null && filelist.length > 0) {
                Arrays.sort(filelist);
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(imageFold, filelist[i]);
                    getImageList(readfile, imageUrlList,soptid);
                }
            }
        }
    }
    
    public List<String> loadAllImg( final int spotid)throws Exception{
    	final List<String> list = new ArrayList<String>();
    	  ApplicationMain.getInstance().getDbHelper().exeSql(new DbExecuteHelper() {
              @Override
              public void exeSqlMethod(SQLiteDatabase db) {
            	  List<String> params = new ArrayList<String>();
            	  	String listsql = "select imgpath  from spotimage t where spotid= ?";
            	  	 params.add(Integer.toString(spotid));
                	  Cursor cursor = db.rawQuery(listsql,params.toArray(new String[0]));
                	  if (cursor != null && cursor.getCount() > 0) {
	                	  while (cursor.moveToNext()) {
	                		  String spotpath = cursor.getString(0);
	                		  list.add(spotpath);
	                	  }
                	  }
              }
              });
    	  
    	 return list;
    }

    @Override
    public ResponseResult<List<SpotModel>> searchSpotDetail(final Integer firstMenuId, final Integer secondMenuId,
            final String keyWord) throws Exception {
        final ResponseResult<List<SpotModel>> result = new ResponseResult<List<SpotModel>>();

        try {

            ApplicationMain.getInstance().getDbHelper().exeSql(new DbExecuteHelper() {
                @Override
                public void exeSqlMethod(SQLiteDatabase db) {
                    List<String> params = new ArrayList<String>();
                    StringBuffer sql = new StringBuffer();
                    sql.append(" select distinct t.id,t.name,t.image,t.desc,t.geometry,t.detailAddress,t.grade from spot t ");
                    sql.append(" left join menuspotlink l on t.id=l.spotid ");
                    sql.append(" left join secondmenu s on l.secondmenuid=s.id ");
                    sql.append(" where replace(t.name,' ','') like ? ");//
                    params.add("%" + keyWord + "%");
                    if (firstMenuId != null) {
                        sql.append(" and s.pid = ? ");
                        params.add(Integer.toString(firstMenuId.intValue()));
                    }
                    if (secondMenuId != null) {
                        sql.append(" and l.secondmenuid = ? ");
                        params.add(Integer.toString(secondMenuId.intValue()));
                    }
                    Cursor cursor = db.rawQuery(sql.toString(), params.toArray(new String[0]));
                    if (cursor != null && cursor.getCount() > 0) {
                        List<SpotModel> list = new ArrayList<SpotModel>();
                        while (cursor.moveToNext()) {
                            SpotModel spotModel = new SpotModel();
                            spotModel.setId(cursor.getInt(0));
                            spotModel.setName(cursor.getString(1));
                            spotModel.setImage(cursor.getString(2));
                            spotModel.setDesc(cursor.getString(3));
                            spotModel.setDetailAddress(cursor.getString(5));
                            spotModel.setGrade(cursor.getInt(6));
                            spotModel.setPid(secondMenuId);
                            String geometry = cursor.getString(4);
                            if (StringUtils.isNotEmpty(geometry)) {
                                try {
                                    JSONObject geoObj = new JSONObject(geometry);
                                    String type = geoObj.getString("type");
                                    JSONArray pointsArr = geoObj.getJSONArray("points");
                                    MapPoint[] points = new MapPoint[pointsArr.length()];
                                    for (int i = 0; i < points.length; i++) {
                                        JSONArray point = pointsArr.getJSONArray(i);
                                        points[i] = new MapPoint(point.getDouble(0), point.getDouble(1));
                                    }

                                    CGeometry cGeometry = new CGeometry();
                                    cGeometry.setType(CGeometryType.valueOf(type));
                                    cGeometry.setPoints(points);

                                    spotModel.setGeometry(cGeometry);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            list.add(spotModel);
                        }
                        result.setResultObj(list);
                        result.setSuccess(true);
                    }
                }
            });
        } catch (Exception e) {
            result.setHadError(true);
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
