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
import android.widget.Button;
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
        Button trailer_button = (Button)findViewById(R.id.trailer);

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

    public void OnTrailerClicked(View view){
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        String MovieID = bundle.getString("id");
        final String BASEURI = "http://api.themoviedb.org/3";
        final String movie = "movie";
        final String videos = "videos";
        final String api_param = "api_key";
        //Insert your API key here.
        final String API_KEY ="fc53fdb027975aaacc7595aeb259107d" ;

        Uri mUri = Uri.parse(BASEURI).buildUpon()
                .appendEncodedPath(movie)
                .appendEncodedPath(MovieID)
                .appendEncodedPath(videos)
                .appendQueryParameter(api_param,API_KEY)
                .build();

        Intent intentYoutube = new Intent(Intent.ACTION_VIEW, mUri);
        //intent.setDataAndType(mUri, "video/*");
        startActivity(intentYoutube);

    }

}
