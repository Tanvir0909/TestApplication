package com.example.testapplication.utility;

import android.content.Context;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.testapplication.R;


public class GlideBindingAdapter {

    @BindingAdapter("imageResource")
    public static void setImageResource(ImageView imageView, int imageUrl ){
        Context context = imageView.getContext();

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background);

        Glide.with(context).setDefaultRequestOptions(requestOptions).load(imageUrl).into(imageView);

    }

    @BindingAdapter("imageResource")
    public static void setImageResource(ImageView imageView, String imageUrl ){
        Context context = imageView.getContext();

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background);

        Glide.with(context).setDefaultRequestOptions(requestOptions).load(imageUrl).into(imageView);

    }
}
