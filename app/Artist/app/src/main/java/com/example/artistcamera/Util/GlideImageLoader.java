package com.example.artistcamera.Util;
import android.app.Activity;
import android.graphics.drawable.Drawable;
//import cn.finalteam.galleryfinal.sample.R;
import cn.finalteam.galleryfinal.widget.GFImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.ImageViewTarget;

/**
 * used by galleryfinal
 */
public class GlideImageLoader implements cn.finalteam.galleryfinal.ImageLoader {

    @Override
    public void displayImage(Activity activity, String path, final GFImageView imageView, Drawable defaultDrawable, int width, int height) {
        Glide.with(activity)
                .load("file://" + path)
                .placeholder(defaultDrawable)
                .error(defaultDrawable)
                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.NONE) //不缓存到SD卡
                .skipMemoryCache(true)
                //.centerCrop()
                .into(new ImageViewTarget<GlideDrawable>(imageView) {
                    @Override
                    protected void setResource(GlideDrawable resource) {
                        imageView.setImageDrawable(resource);
                    }

                    @Override
                    public void setRequest(Request request) {
                        //imageView.setTag(R.id.adapter_item_tag_key,request);
                    }

                    @Override
                    public Request getRequest() {
                        //return (Request) imageView.getTag(R.id.adapter_item_tag_key);
                        return null;
                    }
                });
    }

    @Override
    public void clearMemoryCache() {
    }
}
