package com.qbcbyb.xc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.cloud.Bounds;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Symbol;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.qbcbyb.libandroid.BitmapConvert;
import com.qbcbyb.libandroid.CommonUtil.RequestStatus;
import com.qbcbyb.libandroid.Page;
import com.qbcbyb.libandroid.StringUtils;
import com.qbcbyb.libandroid.adapter.ModelAdapter;
import com.qbcbyb.libandroid.adapter.ModelAdapter.OnSelectedChange;
import com.qbcbyb.libandroid.http.ResponseResult;
import com.qbcbyb.libandroid.msg.Msg;
import com.qbcbyb.libandroid.thread.BaseThread;
import com.qbcbyb.xc.SpotOverlay.OnOverlayPopTap;
import com.qbcbyb.xc.model.CGeometry;
import com.qbcbyb.xc.model.CGeometry.CGeometryType;
import com.qbcbyb.xc.model.CGeometry.MapPoint;
import com.qbcbyb.xc.model.SecondMenuModel;
import com.qbcbyb.xc.model.SpotModel;
import com.qbcbyb.xc.server.MessageWhat;
import com.qbcbyb.xc.server.RequestHandler;
import com.qbcbyb.xc.util.ApplicationMain;
import com.qbcbyb.xc.util.BDMapOfflineManager;
import com.qbcbyb.xc.util.GeometryUtil;
import com.qbcbyb.xc.util.ViewBinderImage;

public class MainActivity extends ActivityBase implements MKGeneralListener {

    private final int ID_Share = 1;
    private final int ID_Historical = 2;
    private final int ID_Street = 3;

    private Map<Integer,ResponseResult<SpotModel>> cacheSpot = new HashMap<Integer,ResponseResult<SpotModel>>();

    private int imsb = 1;
    // 默认字号
    private int defaultSize = 1;

    private String imageUrlD = "";

    private final SparseIntArray TextSizeArray = new SparseIntArray() {
        {
            put(0, 12);
            put(1, 18);
            put(2, 24);
        }
    };

    private String[] imageList;
    private StartDialog dialog;

    private ImageButton imgBtnShare;
    private ImageButton imgBtnHistorical;
    private ImageButton imgBtnStreet;
    private ImageButton imgBtnDownload;
    private ImageButton imgBtnExplain;
    private ListView listSecondMenu;
    private ListView listThirdMenu;

    private WebView helpHtml;

    private View txtBtnSetting;
    private View imgBtnSearch;
    private View txtBtnHomepage;
    private EditText txtSearch;

    private ViewGroup layoutSpotContent;
    private ImageView imgSpot;
    private TextView txtSpotName;
    private TextView txtSpotSummary;
    private TextView txtSpotAddress;
    private TextView txtSpotDesc;
    private Spinner spinnerNian;
    private RelativeLayout layoutViewAll;
    private Button btnViewAll;
    private View btnClose;
    private ScrollView scrollSpotDesc;
    private LinearLayout layoutSpotDesc;
    private ScrollView scrollSpotImageList;
    private LinearLayout layoutSpotImageList;
    private Button btnSpotWord;
    private Button btnSpotImageList;
    private Button btnSpotPano;
    private Button btnSpotBaike;

    private View btnSpotScroll;

    private ModelAdapter<SecondMenuModel> listAdapterSecondMenu;
    private Page pageSecondMenu;
    private BaseThread threadSecondMenu;
    private ModelAdapter<SpotModel> listAdapterThirdMenu;
    private BaseThread threadThirdMenu;

    private BMapManager mBMapMan = null;
    private MapView mapView = null;
    private BDMapOfflineManager mapOfflineManager = null;
    private MapController mMapController = null;

    private SpotOverlay spotOverlay;
    private SpotOverlay spotOverlayblue;
    private SpotOverlay spotOverlaygreed;
    private SpotOverlay spotOverlaypurple;
    private SpotOverlay spotOverlayred;
    private SpotOverlay spotOverlayyellow;
    private SpotOverlay spotOverlayorange;
    private GraphicsOverlay graphicsOverlay;

    private View selectedFirstMenu;

    public View getSelectedFirstMenu() {
        return selectedFirstMenu;
    }

    public void setSelectedFirstMenu(View selectedFirstMenu) {
        if (this.selectedFirstMenu != null) {
            this.selectedFirstMenu.setSelected(false);
            listSecondMenu.setVisibility(View.GONE);
            if (listAdapterSecondMenu != null) {
                listAdapterSecondMenu.setSelectedIndex(-1);
            }
        }
        if (this.selectedFirstMenu != selectedFirstMenu) {
            this.selectedFirstMenu = selectedFirstMenu;
            if (this.selectedFirstMenu != null) {
                this.selectedFirstMenu.setSelected(true);
                initSecondMenu((Integer) this.selectedFirstMenu.getTag());
                listSecondMenu.setVisibility(View.VISIBLE);
            } else {
                listSecondMenu.setVisibility(View.GONE);
            }
        } else {
            this.selectedFirstMenu = null;
        }
        listThirdMenu.setVisibility(View.GONE);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            close();
            return false;
        }

        return super.onKeyDown(keyCode, event);
    }
    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == imgBtnShare || v == imgBtnHistorical || v == imgBtnStreet) {
                // clicked imgBtnShare\imgBtnHistorical\imgBtnStreet
                // to load secondmenu
                setSelectedFirstMenu(v);
            } else if (v == btnViewAll) {
                scrollSpotDesc.setOverScrollMode(1);
                layoutViewAll.setVisibility(View.GONE);
            } else if (v == btnClose) {
                closeSpotContent();
            } else if (v == btnSpotWord || v == btnSpotImageList) {
                setNowSelectBtn(v);
                if (v == btnSpotWord) {
                    spinnerNian.setVisibility(View.INVISIBLE);
                } else {
                    spinnerNian.setVisibility(View.VISIBLE);
                }
            } else if (v == btnSpotPano) {
                openActivityWithSpot(v,PanoActivity.class);
            } else if (v == btnSpotBaike) {
                openActivityWithSpot(v,BaikeActivity.class);
            } else if (v == txtBtnSetting) {
                // TODO设置
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).setTitle("字号选择");
                DialogInterface.OnClickListener singleClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        defaultSize = which;
                        txtSpotDesc.setTextSize(TextSizeArray.get(which));
                    }
                };
                builder.setNegativeButton("取消", singleClickListener);
                builder.setSingleChoiceItems(new String[] { "小号", "中号", "大号" }, defaultSize, singleClickListener);
                AlertDialog dialog = builder.create();
                dialog.show();

            } else if (v == imgBtnSearch) {
                startSearch();
            } else if (v == txtBtnHomepage) {
                clearAllSpotOverlay();
                setSelectedFirstMenu(getSelectedFirstMenu());
                mMapController.setCenter(new GeoPoint(39913226, 116362203));
                mMapController.setZoom(13);

                spotOverlay.hidePopup();
            } else if (v == imgBtnDownload) {
                new DownloadUtils(MainActivity.this).showInputDialog();
            } else if (v == imgBtnExplain) {
                helpHtml = new WebView(MainActivity.this);
                helpHtml.loadUrl("file:///android_asset/readme.html");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                DialogInterface.OnClickListener singleClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };
                // builder.setNegativeButton("确  定", singleClickListener);
                builder.setView(helpHtml);
                // builder
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    };

    private OnEditorActionListener onEditorActionListener = new OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
            if (arg1 == EditorInfo.IME_ACTION_SEARCH && arg0 == txtSearch) {
                startSearch();
            }
            return false;
        }
    };
    private OnKeyListener onKeyListener = new OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                startSearch();
            }
            return false;
        }

    };

    private OnSelectedChange onSelectedChange = new OnSelectedChange() {

        @Override
        public void onChange(BaseAdapter adapter, int oldIndex, int newIndex) {
            if (adapter == listAdapterSecondMenu) {
                if (oldIndex >= listSecondMenu.getFirstVisiblePosition()
                        && oldIndex <= listSecondMenu.getLastVisiblePosition()) {
                    ViewGroup viewGroup = (ViewGroup) listSecondMenu.getChildAt(oldIndex
                            - listSecondMenu.getFirstVisiblePosition());
                    viewGroup.setSelected(false);
                    graphicsOverlay.removeAll();
                }
                if (newIndex >= listSecondMenu.getFirstVisiblePosition()
                        && newIndex <= listSecondMenu.getLastVisiblePosition()) {
                    ViewGroup viewGroup = (ViewGroup) listSecondMenu.getChildAt(newIndex
                            - listSecondMenu.getFirstVisiblePosition());
                    viewGroup.setSelected(true);

                    Object selectedData = listAdapterSecondMenu.getItem(newIndex);
                    if (selectedData != null) {
                        if (selectedData instanceof SecondMenuModel) {
                            SecondMenuModel secondMenu = (SecondMenuModel) selectedData;

                            loadThirdMenu(secondMenu.getId());
                            if (secondMenu.getId().intValue() == 18) {
                                imsb = 0;
                            } else if (secondMenu.getId().intValue() == 22) {
                                imsb = 22;
                            }

                            initRegion(secondMenu.getGeometry());
                            if (listAdapterThirdMenu != null) {
                                listAdapterThirdMenu.setSelectedIndex(-1);
                            }
                            // initRegion(secondMenu.getGeometry());
                        }
                    }
                } else {
                    listThirdMenu.setVisibility(View.GONE);
                    if (listAdapterThirdMenu != null) {
                        listAdapterThirdMenu.setSelectedIndex(-1);
                    }
                }
            } else if (adapter == listAdapterThirdMenu) {
                if (oldIndex >= listThirdMenu.getFirstVisiblePosition()
                        && oldIndex <= listThirdMenu.getLastVisiblePosition()) {
                    ViewGroup viewGroup = (ViewGroup) listThirdMenu.getChildAt(oldIndex
                            - listThirdMenu.getFirstVisiblePosition());
                    viewGroup.setSelected(false);
                }
                if (newIndex >= listThirdMenu.getFirstVisiblePosition()
                        && newIndex <= listThirdMenu.getLastVisiblePosition()) {
                    ViewGroup viewGroup = (ViewGroup) listThirdMenu.getChildAt(newIndex
                            - listThirdMenu.getFirstVisiblePosition());
                    viewGroup.setSelected(true);

                    Object selectedData = listAdapterThirdMenu.getItem(newIndex);
                    if (selectedData != null) {
                        if (selectedData instanceof SpotModel) {
                            closeSpotContent();
                            SpotModel spotData = (SpotModel) selectedData;
                            final SpotOverlayItem overlayItem = SpotOverlayItem.genrateOverlayItem(spotData);
                            if (overlayItem != null) {
                                Handler handler = new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        super.handleMessage(msg);
                                        // 显示指定景点弹窗
                                        spotOverlay.showPopup(overlayItem);
                                    }
                                };
                                mapView.getController().animateTo(overlayItem.getPoint(), handler.obtainMessage(1));
                            }
                        }
                    }
                } else {
                    clearAllSpotOverlay();
                    mapView.refresh();
                    closeSpotContent();
                }
            }
        }
    };
    private OnItemClickListener itemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (arg0 == listSecondMenu) {
                listAdapterSecondMenu.setSelectedIndex(arg2);
            } else if (arg0 == listThirdMenu) {
                listAdapterThirdMenu.setSelectedIndex(arg2);
            }
        }
    };
    private OnScrollListener onScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
                    && view.getLastVisiblePosition() + 1 == view.getCount()) {
                if (threadSecondMenu == null || !threadSecondMenu.isThreadGoing()) {
                    if (pageSecondMenu.hasNextPage() && getSelectedFirstMenu() != null) {
                        loadSecondMenu((Integer) getSelectedFirstMenu().getTag());
                    }
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }
    };
    /**
     * 图片列表年度选择
     */
    private OnItemSelectedListener onItemSelectListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView parent, View view, int position, long id) {
            String yearstr = parent.getItemAtPosition(position).toString();// 选择的值
            // 加载图片列表
            layoutSpotImageList.removeAllViews();
            final android.widget.LinearLayout.LayoutParams layoutParams = new android.widget.LinearLayout.LayoutParams(
                0, 200);
            layoutParams.weight = 1;
            layoutParams.setMargins(10, 10, 10, 10);
            int i = 0;
            LinearLayout layout = null;
            /**
             * 按照选择的年份 重新加载图片
             */
            for (String imgUrl : imageList) {
                if (yearstr.startsWith("20") && !imgUrl.contains(yearstr)) {
                    continue;
                }
                imageUrlD = imgUrl;
                if (i % 3 == 0) {
                    layout = new LinearLayout(MainActivity.this);
                    layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    layout.setWeightSum(3);
                    layout.setGravity(Gravity.CENTER_VERTICAL);
                    layoutSpotImageList.addView(layout);
                }
                ImageView imageView = new ImageView(MainActivity.this);
                imageView.setLayoutParams(layoutParams);
                try {
                    Bitmap bitmap = BitmapConvert.resizeBitmap(ApplicationMain.getInstance().getAssets().open(imgUrl),
                        200);
                    imageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // imageView.setImageDrawable(Drawable.createFromPath(imgUrl));
                imageView.setTag(R.id.tag_first, imageList);
                imageView.setTag(R.id.tag_second, i);
                layout.addView(imageView);
                imageView.setOnClickListener(imageClickListener);
                i++;
            }

        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            // TODO Auto-generated method stub
        }
    };

    private OnOverlayPopTap onOverlayPopTap = new OnOverlayPopTap() {

        @Override
        public void onTap(SpotModel spotItem) {
            querySpotDetail(spotItem);
        }
    };
    private Runnable searchRunnable = new Runnable() {

        @Override
        public void run() {
            final String keyWord = txtSearch.getText().toString();
            if (StringUtils.isNotEmpty(keyWord)) {

                Integer secondId = null;
                if (listSecondMenu.getVisibility() == View.VISIBLE && listAdapterSecondMenu.getSelectedIndex() != -1) {
                    Object selectedData = listAdapterSecondMenu.getItem(listAdapterSecondMenu.getSelectedIndex());
                    if (selectedData != null && selectedData instanceof SecondMenuModel) {
                        SecondMenuModel secondMenu = (SecondMenuModel) selectedData;
                        secondId = secondMenu.getId();
                    }
                }
                final Integer secondMenuId = secondId;
                final Integer firstMenuId = (Integer) (getSelectedFirstMenu() != null ? getSelectedFirstMenu().getTag()
                        : null);

                new BaseThread(baseHandler) {
                    @Override
                    public void runThread() {
                        Message msg = this.obtainMessage();
                        msg.what = MessageWhat.SearchSpotDetail.ordinal();
                        try {
                            ResponseResult<List<SpotModel>> result = RequestHandler.getInstance().searchSpotDetail(
                                firstMenuId, secondMenuId, keyWord);
                            if (result.isSuccess()) {
                                msg.obj = result;
                                msg.arg1 = RequestStatus.OK.ordinal();
                            } else {
                                msg.arg1 = RequestStatus.FAIL.ordinal();
                            }

                        } catch (Exception e) {
                            msg.obj = e;
                            msg.arg1 = RequestStatus.ERROR.ordinal();
                        }
                        sendMessage(msg);
                    }
                }.start();
            } else {
                Msg.showInfo(MainActivity.this, "请先输入搜索关键字");
            }
        }
    };
    private OnClickListener imageClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(MainActivity.this, ImagesActivity.class);
            intent.putExtra(ImagesActivity.BITMAPKEY, (String[]) v.getTag(R.id.tag_first));
            intent.putExtra(ImagesActivity.FIRSTSHOWINDEX, (Integer) v.getTag(R.id.tag_second));
            startActivity(intent);
        }
    };

    @Override
    protected void onPause() {

        super.onPause();
        mBMapMan.stop();
    }

    @Override
    protected void onResume() {

        // dialog.dismiss();
        super.onResume();
        mBMapMan.start();
    }

    @Override
    protected void onDestroy() {
        mapOfflineManager.destroy();
        try {
            mapView.destroy();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.getMessage();
        }
        if (mBMapMan != null) {
            mBMapMan.destroy();
            mBMapMan = null;
        }
        super.onDestroy();
    }

    @Override
    protected void setLayout() {
        mBMapMan = new BMapManager(getApplication());
        mBMapMan.init("A13B465F3C16FC85DFE5D1643AF349C4DD0E4F10", this);
        setContentView(R.layout.activity_main);

        dialog = new StartDialog(this);
        dialog.show();

        imgBtnShare = (ImageButton) findViewById(R.id.imgBtnShare);
        imgBtnHistorical = (ImageButton) findViewById(R.id.imgBtnHistorical);
        imgBtnStreet = (ImageButton) findViewById(R.id.imgBtnStreet);
        imgBtnDownload = (ImageButton) findViewById(R.id.imgBtnDownload);
        imgBtnExplain = (ImageButton) findViewById(R.id.imgBtnExplain);
        listSecondMenu = (ListView) findViewById(R.id.listSecondMenu);
        listThirdMenu = (ListView) findViewById(R.id.listThirdMenu);

        txtBtnSetting = (View) findViewById(R.id.txtBtnSetting);
        imgBtnSearch = (View) findViewById(R.id.imgBtnSearch);
        txtBtnHomepage = (View) findViewById(R.id.txtBtnHomepage);
        txtSearch = (EditText) findViewById(R.id.txtSearch);

        layoutSpotContent = (ViewGroup) findViewById(R.id.layoutSpotContent);
        imgSpot = (ImageView) findViewById(R.id.imgSpot);
        txtSpotName = (TextView) findViewById(R.id.txtSpotName);
        txtSpotSummary = (TextView) findViewById(R.id.txtSpotSummary);
        txtSpotAddress = (TextView) findViewById(R.id.txtSpotAddress);
        txtSpotDesc = (TextView) findViewById(R.id.txtSpotDesc);
        spinnerNian = (Spinner) findViewById(R.id.spinnerNian);
        layoutViewAll = (RelativeLayout) findViewById(R.id.layoutViewAll);
        btnViewAll = (Button) findViewById(R.id.btnViewAll);
        scrollSpotDesc = (ScrollView) findViewById(R.id.scrollSpotDesc);
        layoutSpotDesc = (LinearLayout) findViewById(R.id.layoutSpotDesc);
        btnClose = (View) findViewById(R.id.btnClose);
        scrollSpotImageList = (ScrollView) findViewById(R.id.scrollSpotImageList);
        layoutSpotImageList = (LinearLayout) findViewById(R.id.layoutSpotImageList);
        btnSpotWord = (Button) findViewById(R.id.btnSpotWord);
        btnSpotImageList = (Button) findViewById(R.id.btnSpotImageList);
        btnSpotPano = (Button) findViewById(R.id.btnSpotPano);
        btnSpotBaike = (Button) findViewById(R.id.btnSpotBaike);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mapView = (MapView) findViewById(R.id.mapView);
        mMapController = mapView.getController();
        mapOfflineManager = new BDMapOfflineManager(this, mapView, progressBar);

        pageSecondMenu = new Page();
        spotOverlay = new SpotOverlay(getResources().getDrawable(R.drawable.map_marker_small), mapView);
        spotOverlayblue = new SpotOverlay(getResources().getDrawable(R.drawable.map_marker_small_blue), mapView);
        spotOverlaygreed = new SpotOverlay(getResources().getDrawable(R.drawable.map_marker_small_greed), mapView);
        spotOverlaypurple = new SpotOverlay(getResources().getDrawable(R.drawable.map_marker_small_purple), mapView);
        spotOverlayred = new SpotOverlay(getResources().getDrawable(R.drawable.map_marker_small_red), mapView);
        spotOverlayyellow = new SpotOverlay(getResources().getDrawable(R.drawable.map_marker_small_yellow), mapView);
        spotOverlayorange = new SpotOverlay(getResources().getDrawable(R.drawable.map_marker_small_orange), mapView);
        mapView.getOverlays().add(spotOverlay);
        mapView.getOverlays().add(spotOverlayblue);
        mapView.getOverlays().add(spotOverlaygreed);
        mapView.getOverlays().add(spotOverlaypurple);
        mapView.getOverlays().add(spotOverlayred);
        mapView.getOverlays().add(spotOverlayyellow);
        mapView.getOverlays().add(spotOverlayorange);
        graphicsOverlay = new GraphicsOverlay(mapView);
        mapView.getOverlays().add(graphicsOverlay);
    }

    /**
     * 此处注册监听
     */
    @Override
    protected void addEventListener() {
        imgBtnShare.setOnClickListener(clickListener);
        imgBtnHistorical.setOnClickListener(clickListener);
        imgBtnStreet.setOnClickListener(clickListener);
        imgBtnDownload.setOnClickListener(clickListener);
        imgBtnExplain.setOnClickListener(clickListener);

        txtBtnSetting.setOnClickListener(clickListener);
        imgBtnSearch.setOnClickListener(clickListener);
        txtBtnHomepage.setOnClickListener(clickListener);
        txtSearch.setOnEditorActionListener(onEditorActionListener); // 键盘监听事件
        txtSearch.setOnKeyListener(onKeyListener); // 键盘监听事件

        listSecondMenu.setOnItemClickListener(itemClickListener);
        listThirdMenu.setOnItemClickListener(itemClickListener);

        listSecondMenu.setOnScrollListener(onScrollListener);

        btnViewAll.setOnClickListener(clickListener);
        btnClose.setOnClickListener(clickListener);
        btnSpotWord.setOnClickListener(clickListener);
        btnSpotImageList.setOnClickListener(clickListener);
        btnSpotPano.setOnClickListener(clickListener);
        btnSpotBaike.setOnClickListener(clickListener);
        spinnerNian.setOnItemSelectedListener(onItemSelectListener);
        spotOverlay.setOnOverlayPopTap(onOverlayPopTap);
        spotOverlayblue.setOnOverlayPopTap(onOverlayPopTap);
        spotOverlaygreed.setOnOverlayPopTap(onOverlayPopTap);
        spotOverlaypurple.setOnOverlayPopTap(onOverlayPopTap);
        spotOverlayred.setOnOverlayPopTap(onOverlayPopTap);
        spotOverlayyellow.setOnOverlayPopTap(onOverlayPopTap);
        spotOverlayorange.setOnOverlayPopTap(onOverlayPopTap);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void handleMessage(Message msg) {
        switch (MessageWhat.values()[msg.what]) {
        case LoadSecondMenu:

            if (msg.arg1 == RequestStatus.OK.ordinal()) {
                ResponseResult<List<SecondMenuModel>> result = (ResponseResult<List<SecondMenuModel>>) msg.obj;
                pageSecondMenu.setTotalCount(result.getAllCount());
                List<SecondMenuModel> modelList = result.getResultObj();

                if (pageSecondMenu.getNowPage() == 0) {
                    listAdapterSecondMenu = new ModelAdapter<SecondMenuModel>(this, modelList,
                        R.layout.menulistview_second, new String[] { "name" }, new int[] { R.id.txtMenuSecond });
                    listAdapterSecondMenu.setViewBinder(new ViewBinderImage(MainActivity.this));
                    listAdapterSecondMenu.setOnSelectedChange(onSelectedChange);
                    listSecondMenu.setAdapter(listAdapterSecondMenu);
                } else {
                    listAdapterSecondMenu.addData(modelList);
                    listAdapterSecondMenu.notifyDataSetChanged();
                }
                pageSecondMenu.nextPage();
            } else if (msg.arg1 == RequestStatus.FAIL.ordinal()) {
                listSecondMenu.setVisibility(View.GONE);
                Msg.showInfo(this, "对不起，当前选择无数据！");
            } else {
                // Msg.showInfo(this, "获取二级菜单出错！");
                ((Exception) msg.obj).printStackTrace();
            }
            break;
        case LoadThirdMenu:
            if (msg.arg1 == RequestStatus.OK.ordinal()) {
                ResponseResult<List<SpotModel>> result = (ResponseResult<List<SpotModel>>) msg.obj;

                List<SpotModel> modelList = result.getResultObj();

                addSpotListData(modelList);
                listThirdMenu.setVisibility(View.VISIBLE);
            } else if (msg.arg1 == RequestStatus.FAIL.ordinal()) {
                Msg.showInfo(this, "对不起，当前选择无数据！");
                listThirdMenu.setVisibility(View.GONE);
            } else {
                // Msg.showInfo(this, "获取景点列表出错！");
                ((Exception) msg.obj).printStackTrace();
                listThirdMenu.setVisibility(View.GONE);
            }
            break;
        case SearchSpotDetail:
            if (msg.arg1 == RequestStatus.OK.ordinal()) {
                ResponseResult<List<SpotModel>> result = (ResponseResult<List<SpotModel>>) msg.obj;

                List<SpotModel> modelList = result.getResultObj();

                addSpotListData(modelList);

                listThirdMenu.setVisibility(View.VISIBLE);

            } else if (msg.arg1 == RequestStatus.FAIL.ordinal()) {
                Msg.showInfo(this, "对不起，当前选择无数据！");
                listThirdMenu.setVisibility(View.GONE);
            } else {
                // Msg.showInfo(this, "查询景点列表出错！");
                ((Exception) msg.obj).printStackTrace();
                listThirdMenu.setVisibility(View.GONE);
            }
            break;
        case QuerySpotDetail:
            if (msg.arg1 == RequestStatus.OK.ordinal()) {
                ResponseResult<SpotModel> result = (ResponseResult<SpotModel>) msg.obj;
                final SpotModel spotModel = result.getResultObj();
                Drawable drawable = null;
                try {
                    if (spotModel.getDetailImages() != null && spotModel.getDetailImages().size() > 0) {

                        drawable = Drawable
                            .createFromStream(
                                ApplicationMain.getInstance().getAssets().open(spotModel.getDetailImages().get(0)),
                                "first");

                    } else {
                        drawable = Drawable.createFromStream(
                            ApplicationMain.getInstance().getAssets().open("img" + spotModel.getImage()), "im");
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (drawable != null) {
                    imgSpot.setAdjustViewBounds(true);
                    imgSpot.setMaxHeight(358);
                    imgSpot.setMaxWidth(600);
                    imgSpot.setImageDrawable(drawable);
                } else {
                    imgSpot.setImageResource(R.drawable.nophoto);
                }
                // Drawable cachedImage =
                // DrawableCache.getInstance().loadDrawable(MainActivity.this,
                // spotModel.getImage(), new ImageCallback() {
                // public void imageLoaded(Drawable imageDrawable, String url) {
                // if (imageDrawable != null &&
                // url.equals(spotModel.getImage())) {
                // Bitmap bmp = ((BitmapDrawable) imageDrawable).getBitmap();
                // imgSpot.setImageBitmap(bmp);
                // }
                // }
                // });
                //
                // if (cachedImage != null &&
                // StringUtils.isNotEmpty(spotModel.getImage())) {
                // Bitmap bmp = ((BitmapDrawable) cachedImage).getBitmap();
                // imgSpot.setImageBitmap(bmp);
                // }

                txtSpotName.setText(spotModel.getName());
                txtSpotSummary.setText(spotModel.getDetailSummary());
                txtSpotAddress.setText(spotModel.getDetailAddress());
                txtSpotDesc.setText(spotModel.getDetailDesc());

                // 加载图片列表
                layoutSpotImageList.removeAllViews();
                spinnerNian.setSelection(0);
                spinnerNian.setVisibility(View.INVISIBLE);
                final android.widget.LinearLayout.LayoutParams layoutParams = new android.widget.LinearLayout.LayoutParams(
                    0, 200);
                layoutParams.weight = 1;
                layoutParams.setMargins(10, 10, 10, 10);
                int i = 0;
                LinearLayout layout = null;
                imageList = spotModel.getDetailImages().toArray(new String[0]);
                for (String imgUrl : imageList) {
                    imageUrlD = imgUrl;
                    // if(!imgUrl.contains("2015")){
                    // continue;
                    // }
                    if (i % 3 == 0) {
                        layout = new LinearLayout(MainActivity.this);
                        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                        layout.setWeightSum(3);
                        layout.setGravity(Gravity.CENTER_VERTICAL);
                        layoutSpotImageList.addView(layout);
                    }
                    ImageView imageView = new ImageView(MainActivity.this);
                    imageView.setLayoutParams(layoutParams);
                    try {
                        Bitmap bitmap = BitmapConvert.resizeBitmap(
                            ApplicationMain.getInstance().getAssets().open(imgUrl), 200);
                        imageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // imageView.setImageDrawable(Drawable.createFromPath(imgUrl));
                    imageView.setTag(R.id.tag_first, imageList);
                    imageView.setTag(R.id.tag_second, i);
                    layout.addView(imageView);
                    imageView.setOnClickListener(imageClickListener);
                    i++;
                }
                baseHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (scrollSpotDesc.getChildAt(0).getHeight() <= scrollSpotDesc.getHeight()) {// 无需显示“阅读全文”按钮
                            layoutViewAll.setVisibility(View.GONE);
                        }
                    }
                }, 300);
                layoutSpotContent.setVisibility(View.VISIBLE);

                btnSpotPano.setTag(spotModel);
                btnSpotBaike.setTag(spotModel);

            } else if (msg.arg1 == RequestStatus.FAIL.ordinal()) {
                Msg.showInfo(this, "对不起，当前选择无数据！");
            } else {
                // Msg.showInfo(this, "获取景点出错！");
                ((Exception) msg.obj).printStackTrace();
            }
            break;
        default:
            break;
        }
    }

    @Override
    protected void onCancelProgress(DialogInterface arg0) {
    }

    @Override
    protected void doInit() {

        mapView.setBuiltInZoomControls(true);
        mMapController.setCenter(new GeoPoint(39913226, 116362203));
        mMapController.setZoom(13);
        mapView.setLongClickable(true);

        imgBtnShare.setTag(ID_Share);
        imgBtnHistorical.setTag(ID_Historical);
        imgBtnStreet.setTag(ID_Street);

        btnSpotWord.setTag(layoutSpotDesc);
        btnSpotImageList.setTag(scrollSpotImageList);

        setNowSelectBtn(btnSpotWord);
    }

    private void initSecondMenu(int firstMenuId) {
        pageSecondMenu.initPage(0, getConfig().getPageSize());
        loadSecondMenu(firstMenuId);
    }

    private void loadSecondMenu(final int firstMenuId) {
        threadSecondMenu = new BaseThread(baseHandler) {
            @Override
            public void runThread() {
                Message msg = this.obtainMessage();
                msg.what = MessageWhat.LoadSecondMenu.ordinal();
                try {
                    ResponseResult<List<SecondMenuModel>> result = RequestHandler.getInstance().loadSecondMenu(
                        firstMenuId, pageSecondMenu.getNowPage(), pageSecondMenu.getPageSize());
                    if (result.isSuccess()) {
                        msg.obj = result;
                        msg.arg1 = RequestStatus.OK.ordinal();
                    } else {
                        msg.arg1 = RequestStatus.FAIL.ordinal();
                    }

                } catch (Exception e) {
                    msg.obj = e;
                    msg.arg1 = RequestStatus.ERROR.ordinal();
                }
                sendMessage(msg);
            }
        };
        threadSecondMenu.start();
    }

    private void loadThirdMenu(final int secondMenuId) {
        threadThirdMenu = new BaseThread(baseHandler) {
            @Override
            public void runThread() {
                Message msg = this.obtainMessage();
                msg.what = MessageWhat.LoadThirdMenu.ordinal();
                try {
                    ResponseResult<List<SpotModel>> result = RequestHandler.getInstance().loadThirdMenu(secondMenuId);
                    if (result.isSuccess()) {
                        msg.obj = result;
                        msg.arg1 = RequestStatus.OK.ordinal();
                    } else {
                        msg.arg1 = RequestStatus.FAIL.ordinal();
                    }

                } catch (Exception e) {
                    msg.obj = e;
                    msg.arg1 = RequestStatus.ERROR.ordinal();
                }
                sendMessage(msg);
            }
        };
        threadThirdMenu.start();
    }

    public void initRegion(CGeometry cGeometry) {
        int i = 0;
        graphicsOverlay.removeAll();
        if (cGeometry != null && cGeometry.getType() == CGeometryType.polygon && cGeometry.getPoints().length > 3) {

            MapPoint[] points = cGeometry.getPoints();
            Bounds bounds = null;
            GeoPoint[] polylinePoints = new GeoPoint[points.length];

            for (MapPoint mapPoint : points) {
                polylinePoints[i] = new GeoPoint((int) (mapPoint.getY() * 1E6), (int) (mapPoint.getX() * 1E6));
                if (bounds == null) {
                    bounds = new Bounds(polylinePoints[i].getLatitudeE6(), polylinePoints[i].getLongitudeE6(),
                        polylinePoints[i].getLatitudeE6(), polylinePoints[i].getLongitudeE6());
                } else if (!GeometryUtil.isPointInRect(bounds, polylinePoints[i])) {
                    GeometryUtil.unionPointAndRect(bounds, polylinePoints[i]);
                }
                i++;
            }

            // 构建点并显示
            Geometry polygonGeometry = new Geometry();
            polygonGeometry.setPolyLine(polylinePoints);
            // polygonGeometry.setPolygon(polygonPoints);

            Symbol polygonSymbol = new Symbol();
            // Symbol.Color surfaceColor = polygonSymbol.new Color();
            // surfaceColor.red = 0;
            // surfaceColor.green = 0;
            // surfaceColor.blue = 255;
            // surfaceColor.alpha = 127;
            Symbol.Color lineColor = polygonSymbol.new Color();
            lineColor.red = 0;
            lineColor.green = 0;
            lineColor.blue = 255;
            lineColor.alpha = 255;
            polygonSymbol.setLineSymbol(lineColor, 2);
            polygonSymbol.setPointSymbol(lineColor);
            // polygonSymbol.setSurface(surfaceColor, 1, 1);

            Graphic polygonGraphic = new Graphic(polygonGeometry, polygonSymbol);

            graphicsOverlay.setData(polygonGraphic);
            int aa = (bounds.rightTop.getLatitudeE6() + bounds.leftBottom.getLatitudeE6()) / 2;
            mapView.getController().setCenter(
                new GeoPoint((bounds.rightTop.getLatitudeE6() + bounds.leftBottom.getLatitudeE6()) / 2,
                    (bounds.rightTop.getLongitudeE6() + bounds.leftBottom.getLongitudeE6()) / 2));
            if (imsb == 0) {
                mapView.getController().setZoom(16);
                imsb++;
            } else if (imsb == 22) {
                mapView.getController().setZoom(15);
                imsb++;
            } else {
                mapView.getController().zoomToSpan(bounds.rightTop.getLatitudeE6() - bounds.leftBottom.getLatitudeE6(),
                    bounds.rightTop.getLongitudeE6() - bounds.leftBottom.getLongitudeE6());
            }

            // mapView.getController().setZoom(13);
            // mapView.getController().animateTo(
            // new GeoPoint((bounds.leftBottom.getLatitudeE6() +
            // bounds.rightTop.getLatitudeE6()) / 2,
            // (bounds.leftBottom.getLongitudeE6() +
            // bounds.rightTop.getLongitudeE6()) / 2));
        } else {
            mapView.getController().setZoom(13);
        }
        mapView.refresh();

    }

    protected void querySpotDetail(final SpotModel spotModel) {
        // final ProgressDialog dialog = new ProgressDialog(this);
        // dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//
        // 设置进度条的形式为圆形转动的进度条
        // dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        // dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        // //dialog.setIcon(R.drawable.ic_launcher);//
        // // 设置提示的title的图标，默认是没有的，如果没有设置title的话只设置Icon是不会显示图标的
        // dialog.setTitle("提示");
        // dialog.setMessage("正在加载图片……请等待");
        // dialog.show();
        new BaseThread(baseHandler) {
            @Override
            public void runThread() {
                Message msg = this.obtainMessage();
                msg.what = MessageWhat.QuerySpotDetail.ordinal();
                try {
                    ResponseResult<SpotModel> result = cacheSpot.get(spotModel.getId());
                    if (result == null) {
                        result = RequestHandler.getInstance().querySpotDetail(spotModel);
                        cacheSpot.put(spotModel.getId(), result);
                    }

                    if (result.isSuccess()) {
                        msg.obj = result;
                        msg.arg1 = RequestStatus.OK.ordinal();
                    } else {
                        msg.arg1 = RequestStatus.FAIL.ordinal();
                    }
                    // dialog.dismiss();
                } catch (Exception e) {
                    msg.obj = e;
                    msg.arg1 = RequestStatus.ERROR.ordinal();
                }
                sendMessage(msg);
            }
        }.start();
    }

    public void closeSpotContent() {
        layoutSpotContent.setVisibility(View.GONE);
        layoutViewAll.setVisibility(View.VISIBLE);
        setNowSelectBtn(btnSpotWord);
    }

    protected void close() {
        AlertDialog.Builder builder = new Builder(MainActivity.this);
        builder.setMessage("确定要退出吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void setNowSelectBtn(View btnSpotScroll) {
        if (this.btnSpotScroll != null) {
            this.btnSpotScroll.setSelected(false);
            ((View) this.btnSpotScroll.getTag()).setVisibility(View.GONE);
        }
        this.btnSpotScroll = btnSpotScroll;
        if (this.btnSpotScroll != null) {
            this.btnSpotScroll.setSelected(true);
            ((View) this.btnSpotScroll.getTag()).setVisibility(View.VISIBLE);
        }
    }

    private void addSpotListData(List<SpotModel> modelList) {
        listAdapterThirdMenu = new ModelAdapter<SpotModel>(this, modelList, R.layout.menulistview_third, new String[] {
                "image", "name", "detailAddress" }, new int[] { R.id.imgMenuThird, R.id.txtMenuThirdTitle,
                R.id.txtMenuThirdContent });
        listAdapterThirdMenu.setViewBinder(new ViewBinderImage(MainActivity.this));
        listAdapterThirdMenu.setOnSelectedChange(onSelectedChange);
        listThirdMenu.setAdapter(listAdapterThirdMenu);

        // 显示地图坐标
        clearAllSpotOverlay();

        for (SpotModel spotModel : modelList) {
            List<SpotOverlayItem> list = SpotOverlayItem.genrateOverlayItemList(spotModel);
            if (spotModel.getGrade() == null) {
                spotOverlay.addItem(list);
            } else if (spotModel.getGrade().equals(Integer.valueOf(4))) {
                spotOverlayred.addItem(list);
            } else if (spotModel.getGrade().equals(Integer.valueOf(5))) {
                spotOverlaygreed.addItem(list);
            } else if (spotModel.getGrade().equals(Integer.valueOf(6))) {
                spotOverlayblue.addItem(list);
            } else if (spotModel.getGrade().equals(Integer.valueOf(7))) {
                spotOverlayyellow.addItem(list);
            } else if (spotModel.getGrade().equals(Integer.valueOf(8))) {
                spotOverlayorange.addItem(list);
            } else if (spotModel.getGrade().equals(Integer.valueOf(9))) {
                spotOverlaypurple.addItem(list);
            } else {
                spotOverlay.addItem(list);
            }
        }
        // mapView.getController().animateTo(spotOverlay.getCenter());
        // mapView.getController().zoomToSpan(spotOverlay.getLatSpanE6(),
        // spotOverlay.getLonSpanE6());
        mapView.refresh();
    }

    private void startSearch() {
        baseHandler.removeCallbacks(searchRunnable);
        txtSearch.clearFocus();
        hideSoftInputFromWindow(txtSearch.getWindowToken());
        baseHandler.postDelayed(searchRunnable, 200);
    }

    /**
     * 去除所有覆盖层（气泡）
     */
    private void clearAllSpotOverlay() {
        spotOverlay.removeAll();
        spotOverlayblue.removeAll();
        spotOverlaygreed.removeAll();
        spotOverlaypurple.removeAll();
        spotOverlayred.removeAll();
        spotOverlayyellow.removeAll();
        spotOverlayorange.removeAll();
    }

    @Override
    public void onGetNetworkState(int arg0) {
    }

    @Override
    public void onGetPermissionState(int arg0) {
    }

    public String getImageUrlD() {
        return imageUrlD;
    }

    public void setImageUrlD(String imageUrlD) {
        this.imageUrlD = imageUrlD;
    }

    private void openActivityWithSpot(View btn,Class<? extends Activity> clasz) {
        if (btn.getTag() != null && btn.getTag() instanceof SpotModel) {
            SpotModel model = (SpotModel) btn.getTag();
            Intent intent=new Intent(MainActivity.this, clasz);
            intent.putExtra(KEY_SPOT, model);
            startActivity(intent);
        }
    }

}
