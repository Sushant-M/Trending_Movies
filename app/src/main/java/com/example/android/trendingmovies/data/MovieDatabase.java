package com.example.android.trendingmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sushant on 11/8/16.
 */
public class MovieDatabase extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movie.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + MovieContract.TABLE_NAME + " ("
            + MovieContract._ID + " INTEGER PRIMARY KEY,"
            + MovieContract.COLUMN_MOVIE_NAME + TEXT_TYPE +COMMA_SEP
            + MovieContract.COLUMN_MOVIE_POSTER + TEXT_TYPE + COMMA_SEP
            + MovieContract.COLUMN_MOVIE_RATING + TEXT_TYPE + COMMA_SEP
            + MovieContract.COLUMN_MOVIE_REVIEW + TEXT_TYPE + COMMA_SEP
            + MovieContract.COLUMN_SYNOPSIS + TEXT_TYPE + COMMA_SEP
            + MovieContract.COLUMN_YOUTUBE_LINK + TEXT_TYPE + COMMA_SEP
            + MovieContract.COLUMN_MOVIE_RELEASE + TEXT_TYPE +COMMA_SEP
            + MovieContract.COLUMN_MOVIE_ID + TEXT_TYPE
            + " )";

    private static final String DELETE_TABLE =
            "DROP TABLE IF EXISTS " + MovieContract.TABLE_NAME;

    public MovieDatabase(Context context) {
        super(context,DATABASE_NAME,null ,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db,oldVersion,newVersion);
    }
}
