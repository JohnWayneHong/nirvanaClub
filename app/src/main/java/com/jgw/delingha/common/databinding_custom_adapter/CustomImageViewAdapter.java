package com.jgw.delingha.common.databinding_custom_adapter;

import android.net.Uri;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.jgw.delingha.R;

/**
 * Created by XiongShaoWu
 * on 2019/9/29
 */
public class CustomImageViewAdapter {

    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, String url) {
        setImageUrl(imageView, url, R.drawable.img_empty_icon);
    }

    @BindingAdapter(value = {"imageUrl", "placeDrawableId"}, requireAll = true)
//requireAll表示所有参数都必须有
    public static void setImageUrl(ImageView imageView, String imageUrl, int placeDrawableId) {
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .placeholder(placeDrawableId)
                .into(imageView);
    }

    @BindingAdapter("imageResId")
    public static void setImageRes(ImageView imageView, int resId) {
        if (resId == 0) {
            return;
        }
        Glide.with(imageView.getContext())
                .load(resId)
                .placeholder(R.drawable.img_empty_icon)
                .into(imageView);
    }


    @BindingAdapter("imageUri")
    public static void setImageUri(ImageView imageView, Uri uri) {
        if (uri == null) {
            return;
        }
        Glide.with(imageView.getContext())
                .load(uri)
                .placeholder(R.drawable.img_empty_icon)
                .into(imageView);
    }


}
