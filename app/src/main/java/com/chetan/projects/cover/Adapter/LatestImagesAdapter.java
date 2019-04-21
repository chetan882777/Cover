package com.chetan.projects.cover.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chetan.projects.cover.AAC.AppDatabase;
import com.chetan.projects.cover.Data.LoadedImageInfo;
import com.chetan.projects.cover.R;
import com.chetan.projects.cover.WallpaperDetailActivity;

import java.util.List;

public class LatestImagesAdapter extends RecyclerView.Adapter<LatestImagesAdapter.ImageViewHolder> {

    private final Context mContext;
    private final List<LoadedImageInfo> mImagesInfo;
    private final LayoutInflater mLayoutInflater;

    public LatestImagesAdapter(Context context, List<LoadedImageInfo> imageInfos) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mImagesInfo = imageInfos;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mLayoutInflater.inflate(R.layout.list_item_wallpaper, viewGroup , false);
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LatestImagesAdapter.ImageViewHolder imageViewHolder, int i) {
        imageViewHolder.mCurrentPosition = i;

        String url1 = mImagesInfo.get(i).getPreviewUrl();
        String url2 = mImagesInfo.get(i).getLargeImageUrl();

        RequestBuilder<Drawable> load = Glide
                .with(mContext)
                .load(url1)
                .transition(DrawableTransitionOptions.withCrossFade(700));

        // pass the request as a a parameter to the thumbnail request
        Glide
                .with(mContext)
                .load( url2 )
                .thumbnail(load)
                .into(imageViewHolder.mWallpaperImage);




    }

    @Override
    public int getItemCount() {
        return mImagesInfo.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {


        private final String LOG_TAG = AppDatabase.class.getSimpleName();

        public final ImageView mWallpaperImage;
        public int mCurrentPosition;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            mWallpaperImage =   (ImageView) itemView.findViewById(R.id.imageView_list_item_wallpaper);

            mWallpaperImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent  = new Intent(mContext , WallpaperDetailActivity.class);
                    intent.putExtra(WallpaperDetailActivity.SEND_IMAGE_INFO, mImagesInfo.get(mCurrentPosition).toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });


            AnimationDrawable animationDrawable = (AnimationDrawable) mWallpaperImage.getBackground();
            animationDrawable.setExitFadeDuration(400);
            animationDrawable.setExitFadeDuration(400);
            animationDrawable.start();
        }
    }
}
