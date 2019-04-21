package com.chetan.projects.cover.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chetan.projects.cover.AAC.CategoriesEntry;
import com.chetan.projects.cover.R;
import com.chetan.projects.cover.WallpaperActivity;

import java.util.List;

import static com.chetan.projects.cover.Network.JsonRequestConstants.LOADING_TYPE_PEXELS;
import static com.chetan.projects.cover.Network.JsonRequestConstants.SEND_LOADING_TYPE;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ImageViewHolder> {

    private final Context mContext;
    private final List<CategoriesEntry> mCategories;
    private final LayoutInflater mLayoutInflater;
    private Bitmap mResourceBitmap;
    private GradientDrawable mGradientDrawable;
    private Palette.Swatch textSwatch;


    public FolderAdapter(Context context, List<CategoriesEntry> categories) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mCategories = categories;

    }

    @NonNull
    @Override
    public FolderAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mLayoutInflater.inflate(R.layout.list_item_folder, viewGroup , false);
        return new FolderAdapter.ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FolderAdapter.ImageViewHolder imageViewHolder, final int i) {
        imageViewHolder.mCurrentPosition = i;
        CategoriesEntry category = mCategories.get(i);
        String previewUrl = category.getPreviewUrl();
        String largeUrl = category.getMediumUrl();
        imageViewHolder.mWallpaperText.setText(category.getName());

        RequestBuilder<Drawable> load = Glide
                .with(mContext)
                .load(previewUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Log.v(LOG_TAG , "-------------- failed " );

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        mResourceBitmap = ((BitmapDrawable) resource).getBitmap();


                        Palette.from(mResourceBitmap)
                                .generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(Palette palette) {
                                        textSwatch = palette.getVibrantSwatch();
                                        if (textSwatch == null) {
                                            return;
                                        }

                                        int rgb = textSwatch.getRgb();

                                        int alpha = Color.alpha(rgb);
                                        int red = Color.red(rgb);
                                        int green = Color.green(rgb);
                                        int blue = Color.blue(rgb);

                                        alpha *= 1.00;

                                        rgb = Color.argb(alpha, red, green, blue);

                                        imageViewHolder.mWallpaperText.setBackgroundColor(rgb);


                                    }
                                });
                        return false;
                    }

                });

        Glide.with(mContext)
                .load(largeUrl)
                .thumbnail(load)
                .into(imageViewHolder.mWallpaperImage);
    }



    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    public void add(CategoriesEntry value) {
        mCategories.add(value);
    }

    public void addList(List<CategoriesEntry> entries) {
        mCategories.clear();
        for(CategoriesEntry entry: entries){
            mCategories.add(entry);
        }
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public final ImageView mWallpaperImage;
        public final TextView mWallpaperText;
        public int mCurrentPosition;

        public ImageViewHolder(@NonNull final View itemView) {
            super(itemView);
            mWallpaperImage =   (ImageView) itemView.findViewById(R.id.imageView_list_item_folder);
            mWallpaperText  = (TextView) itemView.findViewById(R.id.textView_list_item_folder);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent  = new Intent(mContext , WallpaperActivity.class);
                    intent.putExtra(WallpaperActivity.SEND_FOLDER_INFO, mCategories.get(mCurrentPosition).getName());
                    intent.putExtra(SEND_LOADING_TYPE ,LOADING_TYPE_PEXELS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    mContext.startActivity(intent );
                }
            });

            AnimationDrawable animationDrawable = (AnimationDrawable) mWallpaperImage.getBackground();
            animationDrawable.setEnterFadeDuration(400);
            animationDrawable.setExitFadeDuration(400);
            animationDrawable.start();
        }
    }
}
