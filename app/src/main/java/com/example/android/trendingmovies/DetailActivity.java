package com.example.android.trendingmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        String Title = bundle.getString("title");
        String Image_Path = bundle.getString("image");
        String Movie_OverView = bundle.getString("overview");
        String Movie_Release = bundle.getString("release");
        String Movie_Rating = bundle.getString("rating");

        ImageView imageView = (ImageView)findViewById(R.id.movieImage);
        TextView movie_title = (TextView)findViewById(R.id.movie_title);
        TextView movie_release = (TextView)findViewById(R.id.movie_release);
        TextView movie_overview = (TextView)findViewById(R.id.movie_OverView);
        TextView movie_rating = (TextView)findViewById(R.id.movie_rating);

        Context context = getApplicationContext();
        final String BASE_URL = "http://image.tmdb.org/t/p/.";
        final String SIZE_POSTER = "w185";
        final String POSTER_URL = Image_Path;
        Uri url = Uri.parse(BASE_URL)
                .buildUpon()
                .appendEncodedPath(SIZE_POSTER)
                .appendEncodedPath(POSTER_URL)
                .build();

        Picasso.with(context).load(url).into(imageView);

        movie_title.setText("Title: "+Title);
        movie_release.setText("Release Date: "+Movie_Release);
        movie_overview.setText("Synopsis: "+Movie_OverView);
        movie_rating.setText("Rating: "+Movie_Rating);

    }

}
