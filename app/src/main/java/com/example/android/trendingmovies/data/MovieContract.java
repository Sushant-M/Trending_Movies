package com.example.android.trendingmovies.data;

import android.provider.BaseColumns;

/**
 * Created by sushant on 11/8/16.
 */
public class MovieContract implements BaseColumns {
    public static final String TABLE_NAME = "movies";
    public static final String COLUMN_MOVIE_NAME = "name";
    public static final String COLUMN_MOVIE_POSTER = "poster";
    public static final String COLUMN_MOVIE_RATING = "rating";
    public static final String COLUMN_MOVIE_REVIEW = "review";
    public static final String COLUMN_YOUTUBE_LINK = "youtube";
    public static final String COLUMN_SYNOPSIS = "synopsis";
    public static final String COLUMN_MOVIE_RELEASE = "release";
    public static final String COLUMN_MOVIE_ID = "movieid";
}
