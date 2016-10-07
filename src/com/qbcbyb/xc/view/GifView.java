package com.qbcbyb.xc.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;

public class GifView extends View {

    public static final int IMAGE_TYPE_UNKNOWN = 0;
    public static final int IMAGE_TYPE_STATIC = 1;
    public static final int IMAGE_TYPE_DYNAMIC = 2;

    public static final int DECODE_STATUS_UNDECODE = 0;
    public static final int DECODE_STATUS_DECODING = 1;
    public static final int DECODE_STATUS_DECODED = 2;

    private GifDecoder decoder;
    private Bitmap bitmap;
    private Matrix matrix = new Matrix();

    public int imageType = IMAGE_TYPE_UNKNOWN;
    public int decodeStatus = DECODE_STATUS_UNDECODE;

    private int width;
    private int height;

    private long time;
    private int index;

    private int resId;
    private String filePath;

    private boolean playFlag = false;

    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor
     */
    public GifView(Context context) {
        super(context);
    }

    private InputStream getInputStream() {
        if (filePath != null)
            try {
                return new FileInputStream(filePath);
            } catch (FileNotFoundException e) {
            }
        if (resId > 0)
            return getContext().getResources().openRawResource(resId);
        return null;
    }

    /**
     * set gif file path
     * 
     * @param filePath
     */
    public void setGif(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        setGif(filePath, bitmap);
    }

    /**
     * set gif file path and cache image
     * 
     * @param filePath
     * @param cacheImage
     */
    public void setGif(String filePath, Bitmap cacheImage) {
        this.resId = 0;
        this.filePath = filePath;
        imageType = IMAGE_TYPE_UNKNOWN;
        decodeStatus = DECODE_STATUS_UNDECODE;
        playFlag = false;
        bitmap = cacheImage;
        if (bitmap != null) {
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }
        // setLayoutParams(new LayoutParams(width, height));
    }

    /**
     * set gif resource id
     * 
     * @param resId
     */
    public void setGif(int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
        setGif(resId, bitmap);
    }

    /**
     * set gif resource id and cache image
     * 
     * @param resId
     * @param cacheImage
     */
    public void setGif(int resId, Bitmap cacheImage) {
        this.filePath = null;
        this.resId = resId;
        imageType = IMAGE_TYPE_UNKNOWN;
        decodeStatus = DECODE_STATUS_UNDECODE;
        playFlag = false;
        bitmap = cacheImage;
        if (bitmap != null) {
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }
        // setLayoutParams(new LayoutParams(width, height));
    }

    private void decode() {
        release();
        index = 0;

        decodeStatus = DECODE_STATUS_DECODING;

        new Thread() {
            @Override
            public void run() {
                decoder = new GifDecoder();
                decoder.read(getInputStream());
                if (decoder.width == 0 || decoder.height == 0) {
                    imageType = IMAGE_TYPE_STATIC;
                } else {
                    imageType = IMAGE_TYPE_DYNAMIC;
                }
                postInvalidate();
                time = System.currentTimeMillis();
                decodeStatus = DECODE_STATUS_DECODED;
            }
        }.start();
    }

    public void release() {
        decoder = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (width == 0 || height == 0) {
            matrix.setScale(1, 1);
        } else {
            float scaleX = this.getWidth() / width, scaleY = this.getHeight() / height;
            matrix.setScale(scaleX, scaleY);
        }
        if (decodeStatus == DECODE_STATUS_UNDECODE) {
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, matrix, null);
            }
            if (playFlag) {
                decode();
                invalidate();
            }
        } else if (decodeStatus == DECODE_STATUS_DECODING) {
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, matrix, null);
            }
            invalidate();
        } else if (decodeStatus == DECODE_STATUS_DECODED) {
            if (imageType == IMAGE_TYPE_STATIC) {
                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, matrix, null);
                }
            } else if (imageType == IMAGE_TYPE_DYNAMIC) {
                if (playFlag) {
                    long now = System.currentTimeMillis();
                    long delay = decoder.getDelay(index);
                    if (time + delay < now) {
                        time += delay;
                        incrementFrameIndex();
                    }
                    Bitmap bitmap = decoder.getFrame(index);
                    if (bitmap != null) {
                        canvas.drawBitmap(bitmap, matrix, null);
                    }
                    postInvalidateDelayed(Math.max(100, delay));
                } else {
                    Bitmap bitmap = decoder.getFrame(index);
                    if (bitmap != null) {
                        canvas.drawBitmap(bitmap, matrix, null);
                    }
                }
            } else {
                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, matrix, null);
                }
            }
        }
    }

    private void incrementFrameIndex() {
        index++;
        if (index >= decoder.getFrameCount()) {
            index = 0;
        }
    }

    private void decrementFrameIndex() {
        index--;
        if (index < 0) {
            index = decoder.getFrameCount() - 1;
        }
    }

    public void play() {
        time = System.currentTimeMillis();
        playFlag = true;
        invalidate();
    }

    public void pause() {
        playFlag = false;
        invalidate();
    }

    public void stop() {
        playFlag = false;
        index = 0;
        invalidate();
    }

    public void nextFrame() {
        if (decodeStatus == DECODE_STATUS_DECODED) {
            incrementFrameIndex();
            invalidate();
        }
    }

    public void prevFrame() {
        if (decodeStatus == DECODE_STATUS_DECODED) {
            decrementFrameIndex();
            invalidate();
        }
    }
}
