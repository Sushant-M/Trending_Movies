package com.example.android.trendingmovies;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sushant on 1/7/16.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private String[] parsedlist;

    public ImageAdapter(Context c, String[] list) {
        mContext = c;
        parsedlist = list;
    }

    public int getCount() {
        return 20;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
        } else {
            imageView = (ImageView) convertView;
        }

        final String BASE_URL = "http://image.tmdb.org/t/p/.";
        final String SIZE_POSTER = "w185";
        if(parsedlist != null) {
            final String POSTER_URL = parsedlist[position];
            Uri url = Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendEncodedPath(SIZE_POSTER)
                    .appendEncodedPath(POSTER_URL)
                    .build();
            Picasso.with(mContext).load(url).into(imageView);
            return imageView;
        }
        return null;
    }

}
