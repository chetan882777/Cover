package com.chetan.projects.cover.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chetan.projects.cover.R;

import java.util.List;

public class SearchListAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private List<String> mList;
    private List<Drawable> mDrawables;

    public SearchListAdapter(Context context , List<String> list , List<Drawable> drawables){

        super(context , 0 , list);
        mContext = context;
        mList = list;
        mDrawables = drawables;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_search_options , parent , false);
        }

        TextView textView = convertView.findViewById(R.id.textView_list_item_search_options);
        ImageView imageView = convertView.findViewById(R.id.imageView_list_item_search_options);
        imageView.setMaxHeight(16);
        imageView.setMaxWidth(16);

            imageView.setImageDrawable(mDrawables.get(position));
            textView.setText(mList.get(position));

        return convertView;
    }

}
