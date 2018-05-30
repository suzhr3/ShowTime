package edu.sysu.showtime.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import edu.sysu.showtime.APP;
import edu.sysu.showtime.R;

public class ImgUtils {
    public static void loadRound(int resId, ImageView targetImgView){
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.default_head_pic);
        requestOptions.circleCrop();
        Glide.with(APP.getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(resId)
                .into(targetImgView);
    }
    public static void loadRound(String faceUrl, ImageView targetImgView){
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.default_head_pic);
        requestOptions.circleCrop();
        Glide.with(APP.getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(faceUrl)
                .into(targetImgView);
    }
    public static void load(String coverUrl, ImageView targetImgView) {
        RequestOptions requestOptions = new RequestOptions();
        Glide.with(APP.getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(coverUrl)
                .into(targetImgView);
    }
    public static void load(int resId, ImageView targetImgView){
        RequestOptions requestOptions = new RequestOptions();
        Glide.with(APP.getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(resId)
                .into(targetImgView);
    }
}
