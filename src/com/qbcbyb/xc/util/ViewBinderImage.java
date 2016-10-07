package com.qbcbyb.xc.util;

import java.io.IOException;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.qbcbyb.libandroid.StringUtils;
import com.qbcbyb.libandroid.adapter.ModelAdapter.ViewBinder;
import com.qbcbyb.xc.R;

/**
 * 根据适配器下图片URL解析为图片
 * 
 * @author Administrator 当前图片资源来自assets目录
 */
public class ViewBinderImage implements ViewBinder {
    @SuppressWarnings("unused")
    private Context context;

    public ViewBinderImage(Context context) {
        super();
        this.context = context;
    }

    @Override
    public boolean setViewValue(View view, Object data, String textRepresentation) {
        if (view instanceof ImageView) {
            final ImageView imageView = (ImageView) view;
            if (StringUtils.isEmpty(textRepresentation)) {
                imageView.setImageResource(R.drawable.nophoto);
            } else {
                String imageUrl = textRepresentation;
                imageView.setTag(imageUrl);

                Drawable drawable;
				try {
					drawable = Drawable.createFromStream(ApplicationMain.getInstance().getAssets().open("img"+imageUrl), "");
					if (drawable != null && drawable instanceof BitmapDrawable
	                        && ((BitmapDrawable) drawable).getBitmap() != null) {
	                    imageView.setImageDrawable(drawable);
	                } else {
	                    imageView.setImageResource(R.drawable.nophoto);
	                }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                // Drawable cachedImage =
                // DrawableCache.getInstance().loadDrawable(context, imageUrl,
                // new
                // ImageCallback() {
                // public void imageLoaded(Drawable imageDrawable, String url) {
                // if (imageDrawable != null && url != null) {
                // if (imageView != null && url.equals(imageView.getTag())) {
                // Bitmap bmp = ((BitmapDrawable) imageDrawable).getBitmap();
                // imageView.setImageBitmap(bmp);
                // }
                // }
                // }
                // });
                //
                // if (cachedImage != null &&
                // StringUtils.isNotEmpty(textRepresentation)) {
                // Bitmap bmp = ((BitmapDrawable) cachedImage).getBitmap();
                // imageView.setImageBitmap(bmp);
                // } else {
                // imageView.setImageBitmap(defaultShowBitmap);
                // }
            }
            return true;
        }
        return false;
    }
}
