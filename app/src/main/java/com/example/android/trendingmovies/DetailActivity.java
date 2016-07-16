package com.example.android.trendingmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {
    String youTubeURL = null;
    final String TAG="DetailActivity";
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
        String MovieID = bundle.getString("id");


        ImageView imageView = (ImageView)findViewById(R.id.movieImage);
        TextView movie_title = (TextView)findViewById(R.id.movie_title);
        TextView movie_release = (TextView)findViewById(R.id.movie_release);
        TextView movie_overview = (TextView)findViewById(R.id.movie_OverView);
        TextView movie_rating = (TextView)findViewById(R.id.movie_rating);
        Button trailer_button = (Button)findViewById(R.id.trailer);

        new getYoutubeLink().execute(MovieID);


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


    public class getYoutubeLink extends AsyncTask<String,Void,String>{

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }

        @Override
        protected String doInBackground(String... params) {


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String tempdata = null;

            try{
                final String BASEURL = "http://api.themoviedb.org/3";
                final String MOVIES = "movie";
                final String VIDEOS = "videos";
                final String APIPARAM = "api_key";
                final String APIKEY = "fc53fdb027975aaacc7595aeb259107d";
                String movID = params[0];

                Uri builtUri = Uri.parse(BASEURL).buildUpon()
                        .appendEncodedPath(MOVIES)
                        .appendEncodedPath(movID)
                        .appendEncodedPath(VIDEOS)
                        .appendQueryParameter(APIPARAM,APIKEY)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                youTubeURL = buffer.toString();
                Log.d(TAG,youTubeURL);
            }catch (IOException e){
            e.printStackTrace();
             }finally {
                if (urlConnection != null) {
                urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                        } catch (final IOException e) {
                            Log.e(TAG, "Error closing stream", e);
                }
            }
        }
            try{
                tempdata = parseYoutubePath();
            }catch (JSONException e){
                e.printStackTrace();
            }
            Log.d(TAG,tempdata);
            return tempdata;
        }
    }

    public String parseYoutubePath() throws JSONException {
        String list = "results";
        String id = "key";
        JSONObject moviesOBJ = new JSONObject(youTubeURL);
        JSONArray movieArr = moviesOBJ.getJSONArray(list);
        int i =0;
            JSONObject obj = movieArr.getJSONObject(i);
            String posterparse = obj.getString(id);
        return posterparse;
    }

}
