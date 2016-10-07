package com.qbcbyb.xc.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

/**
 * 照片浏览View
 */
public class FlingView extends View implements OnGestureListener{
    public abstract class OnIndexChange {
        public abstract void onIndexChanged(int nowIndex);
    }
    private OnIndexChange onIndexChange;
    
    private OnDoubleTapListener doubleClickListener;
    private Bitmap bitmap;

    private Bitmap nBitmap;

    private Bitmap fBitmap;
    private Matrix matrix = new Matrix();
    private Paint p = new Paint();

    private int OffsetX = 0;

    private int position = 0;

    int mLastFlingX = 0;

    boolean OffsetRight = false;

    private Bitmap[] bitmaps;


	public OnIndexChange getOnIndexChange() {
        return onIndexChange;
    }

    public void setOnIndexChange(OnIndexChange onIndexChange) {
        this.onIndexChange = onIndexChange;
    }

    public int getPosition() {
        return position;
    }
    public boolean onTouch(View v, MotionEvent event) {  
        
        return true;  
    }  
    public void setPosition(int position) {
        if (this.position != position) {
            this.position = position;
            bitmap = getBitmap(position);
            if (position < bitmaps.length - 1) {
                nBitmap = getBitmap(position + 1);
            } else {
                nBitmap = null;
            }
            if (position > 0) {
                fBitmap = getBitmap(position - 1);
            } else {
                fBitmap = null;
            }
            invalidate();
        }
    }

    public Bitmap[] getBitmaps() {
        return bitmaps;
    }

    public void setBitmaps(Bitmap[] bitmaps) {
        this.bitmaps = bitmaps;
        bitmap = getBitmap(position);
        if (position < bitmaps.length - 1) {
            nBitmap = getBitmap(position + 1);
        } else {
            nBitmap = null;
        }
        if (position > 0) {
            fBitmap = getBitmap(position - 1);
        } else {
            fBitmap = null;
        }
        invalidate();
    }

    public FlingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        if (OffsetX < 0) {
            if (nBitmap != null) {
                float scaleX = this.getWidth() / nBitmap.getWidth(), scaleY = this.getHeight() / nBitmap.getHeight();
                float scale = Math.min(scaleX, scaleY);
                matrix.setScale(scale, scale);
                matrix.postTranslate(this.getWidth() + 15 + OffsetX + (this.getWidth() - nBitmap.getWidth() * scale)
                        / 2, (this.getHeight() - nBitmap.getHeight() * scale) / 2);
                canvas.drawBitmap(nBitmap, matrix, p);
            }
        } else if (OffsetX > 0) {
            if (fBitmap != null) {
                float scaleX = this.getWidth() / fBitmap.getWidth(), scaleY = this.getHeight() / fBitmap.getHeight();
                float scale = Math.min(scaleX, scaleY);
                matrix.setScale(scale, scale);
                matrix.postTranslate(-this.getWidth() - 15 + OffsetX + (this.getWidth() - fBitmap.getWidth() * scale)
                        / 2, (this.getHeight() - fBitmap.getHeight() * scale) / 2);
                canvas.drawBitmap(fBitmap, matrix, p);
            }
        }
        if (bitmap != null) {
            float scaleX = this.getWidth() / bitmap.getWidth(), scaleY = this.getHeight() / bitmap.getHeight();
            float scale = Math.min(scaleX, scaleY);
            matrix.setScale(scale, scale);
            matrix.postTranslate(OffsetX + (this.getWidth() - bitmap.getWidth() * scale) / 2,
                (this.getHeight() - bitmap.getHeight() * scale) / 2);
            canvas.drawBitmap(bitmap, matrix, p);
        }
    }

    public void handleScroll(int deltaX) {
        if (deltaX > 0) {
            OffsetX -= -deltaX;
        } else {
            OffsetX += deltaX;
        }
        invalidate();
    }

    // 标记为可以切换到下一张
    boolean flag = false;

    // 标记为需要向右滑动
    boolean flag1 = false;

    // 标记为需要向左滑动
    boolean flag2 = false;

    @SuppressWarnings("deprecation")
    private GestureDetector myGesture = new GestureDetector(this);

    class MyAnimation extends Animation {

        private int temp;

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            temp = OffsetX;
            super.initialize(width, height, parentWidth, parentHeight);
            setDuration(500);
            setFillAfter(true);
            setInterpolator(new LinearInterpolator());
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            // Log.i("bb", "OffsetX==>"+OffsetX);
            // 需要滑动图片时根据方向来变换OffsetX大小
            if (flag) {
                if (temp > 0) {
                    OffsetX = (int) ((FlingView.this.getWidth() + 15 - temp) * interpolatedTime + temp);
                } else {
                    OffsetX = (int) ((-FlingView.this.getWidth() - 15 - temp) * interpolatedTime + temp);
                }
                // 不需要变换的情况
            } else {
                OffsetX = (int) (temp * (1 - interpolatedTime));
            }

            invalidate();
        }
    }

    // 动画结束后需要做一些工作
    @Override
    protected void onAnimationEnd() {
        if (flag1) {
            nBitmap = bitmap;
            bitmap = fBitmap;
            fBitmap = null;
            position = position - 1;
        } else if (flag2) {
            fBitmap = bitmap;
            bitmap = nBitmap;
            nBitmap = null;
            position = position + 1;
        }
        flag1 = false;
        flag2 = false;
        OffsetX = 0;
        if (fBitmap == null && OffsetX == 0) {
            if (position > 0) {
                fBitmap = getBitmap(position - 1);
            }

        } else if (nBitmap == null && OffsetX == 0) {
            if (position < bitmaps.length - 1) {
                nBitmap = getBitmap(position + 1);
            }
        }
        clearAnimation();
        flag = false;
        if (onIndexChange != null) {
            onIndexChange.onIndexChanged(position);
        }
    }

    public void startFling(int paramFloat1) {
        if (OffsetX > this.getWidth() / 3) {
            if (fBitmap != null) {
                flag = true;
                flag1 = true;
            }
        } else if (OffsetX < -this.getWidth() / 3) {
            if (nBitmap != null) {
                flag = true;
                flag2 = true;
            }
        }
        // 开始动画效果
        startAnimation(new MyAnimation());
        invalidate();

    }

    /**
     * 获得当前位置的图片
     * 
     * @param currentPos
     * @return
     */
    public Bitmap getBitmap(int currentPos) {
        if (currentPos > bitmaps.length - 1) {
            return null;
        }
        Bitmap currBitmap = bitmaps[currentPos];
        OffsetX = 0;

        return currBitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
        case MotionEvent.ACTION_UP:
            this.startFling(0);
            break;
        }
        myGesture.onTouchEvent(event);
        //myGesture.setOnDoubleTapListener(doubleClickListener);
        return true;
    }
   
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        this.startFling((int) -velocityX);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        this.handleScroll(-1 * (int) distanceX);
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

	public OnDoubleTapListener getDoubleClickListener() {
		return doubleClickListener;
	}

	public void setDoubleClickListener(OnDoubleTapListener doubleClickListener) {
		this.doubleClickListener = doubleClickListener;
	}


}