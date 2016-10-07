package com.qbcbyb.xc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Message;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

import com.qbcbyb.xc.util.ApplicationMain;
import com.qbcbyb.xc.view.FlingView;

public class ImagesActivity extends ActivityBase {

    public static final String BITMAPKEY = "BITMAPKEY";

    public static final String FIRSTSHOWINDEX = "FIRSTSHOWINDEX";

    private FlingView flingView;

    private ImageView imageForIndex;

    private List<Bitmap> imageList;
    private int nowViewingIndex = -1;

    public int getNowViewingIndex() {
        return nowViewingIndex;
    }

    public void setNowViewingIndex(int nowViewingIndex) {
        if (getImageList() != null && getImageList().size() > 0) {
            if (nowViewingIndex >= 0 && nowViewingIndex < getImageList().size()) {
                this.nowViewingIndex = nowViewingIndex;
                // titlebar.setTitleText("图片" + (this.nowViewingIndex + 1));
            }
            final int oneStep = 16;
            final int size = getImageList().size();
            Bitmap bitmap = Bitmap.createBitmap(oneStep * size, oneStep, Bitmap.Config.RGB_565);
            Canvas c = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setARGB(255, 28, 28, 28);
            for (int i = 0; i < size; i++) {
                if (i != nowViewingIndex) {
                    c.drawCircle((float) (oneStep * (i + .5)), oneStep / 2, 4, paint);
                } else {
                    Paint paintOn = new Paint();
                    paintOn.setARGB(255, 50, 168, 243);
                    c.drawCircle((float) (oneStep * (i + .5)), oneStep / 2, 4, paintOn);
                }
            }
            imageForIndex.setImageBitmap(bitmap);
        } else {
            this.nowViewingIndex = nowViewingIndex;
        }
        flingView.setPosition(nowViewingIndex);
    }
    private OnDoubleTapListener doubleClickListener = new OnDoubleTapListener() {

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			finish();
			System.gc();
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}
       
    };
    
    
    public List<Bitmap> getImageList() {
        return imageList;
    }

    public void setImageList(List<Bitmap> imageList) {
        this.imageList = imageList;
        flingView.setBitmaps(imageList.toArray(new Bitmap[0]));
    }

    protected void addEventListener() {
        flingView.setOnIndexChange(flingView.new OnIndexChange() {
            @Override
            public void onIndexChanged(int nowIndex) {
                setNowViewingIndex(nowIndex);
            }
        });
        flingView.setDoubleClickListener(doubleClickListener);
    }
   
    protected void setLayout() {
        // initActivity();
        this.setContentView(R.layout.activity_images);
        flingView = (FlingView) findViewById(R.id.viewFlipperContainer);
        imageForIndex = (ImageView) findViewById(R.id.imageForIndex);
    }

    @Override
    protected void handleMessage(Message msg) {

    }

    protected void doInit() {
        Intent intent = getIntent();
        List<Bitmap> bitmapList = getCachedImage(intent.getStringArrayExtra(BITMAPKEY));
        setImageList(bitmapList);
        setNowViewingIndex(intent.getIntExtra(FIRSTSHOWINDEX, 0));
    }

    @Override
    protected void onCancelProgress(DialogInterface arg0) {
    }
    @Override
	public void finish() {
    	try {
			if (imageList != null && imageList.size() > 0) {
				for (Bitmap bitmap : imageList) {
					if (bitmap != null) {
						bitmap.recycle();
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.finish();
    }
    protected List<Bitmap> getCachedImage(String[] intentimagelist) {
        List<Bitmap> bitmapList = new ArrayList<Bitmap>();
        for (int i = 0; i < intentimagelist.length; i++) {

            final String imageUrl = intentimagelist[i];
            try {
				bitmapList.add(BitmapFactory.decodeStream(ApplicationMain.getInstance().getAssets().open(imageUrl)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        return bitmapList;
    }

}
