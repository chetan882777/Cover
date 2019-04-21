package com.chetan.projects.cover.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chetan.projects.cover.Data.LoadedImageInfo;
import com.chetan.projects.cover.R;
import com.chetan.projects.cover.Utilities.BitmapUtils;
import com.chetan.projects.cover.WallpaperDetailActivity;
import com.chetan.projects.cover.ZoomActivity;

import java.io.File;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.DownloadViewHolder> {
    private String[] mFilePaths;
    private Context mContext;
    private final File mPath;

    public DownloadsAdapter(Context context , String[] filePaths){
        mContext = context;
        mFilePaths = filePaths;
        mPath = new File(Environment.getExternalStorageDirectory(),"CoverDownloads");

    }

    @NonNull
    @Override
    public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View root = LayoutInflater.from(mContext).inflate(R.layout.list_item_wallpaper, viewGroup, false);
        return new DownloadViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadViewHolder downloadViewHolder, int i) {
        downloadViewHolder.mCurrentPosition = i;

        String file = mPath.getPath() + "/" + mFilePaths[i];

        Glide.with(mContext)
                .load(file)
                .transition(DrawableTransitionOptions.withCrossFade(700))
                .into(downloadViewHolder.mWallpaperImage);

    }

    @Override
    public int getItemCount() {
        return mFilePaths.length;
    }

    public class DownloadViewHolder extends RecyclerView.ViewHolder{
        public final ImageView mWallpaperImage;
        public int mCurrentPosition;
        public Bitmap mCurrentBitmap;

        public DownloadViewHolder(@NonNull View itemView) {
            super(itemView);
            mWallpaperImage =   (ImageView) itemView.findViewById(R.id.imageView_list_item_wallpaper);

            mWallpaperImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent  = new Intent(mContext , ZoomActivity.class);
                    intent.putExtra(ZoomActivity.ZOOM_BITMAP,
                            mPath.getPath() + "/" + mFilePaths[mCurrentPosition]);

                    intent.putExtra(ZoomActivity.ZOOM_PARENT_ACTIVITY , ZoomActivity.ZOOM_PARENT_ACTIVITY_DOWNLOADS);
                    intent.putExtra(ZoomActivity.ZOOM_BITMAP_SENDER , ZoomActivity.ZOOM_BITMAP_DOWNLOADS);
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

