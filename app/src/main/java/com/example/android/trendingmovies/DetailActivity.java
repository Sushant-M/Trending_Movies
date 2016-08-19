package com.example.android.trendingmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

import com.example.android.trendingmovies.data.MovieContract;
import com.example.android.trendingmovies.data.MovieDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {
    final String APIKEY = "fc53fdb027975aaacc7595aeb259107d";
    String youTubeURL = null;
    String tempdata = null;
    final String TAG="DetailActivity";
    private boolean toggle_bool = false;
    String Title;
    String Image_Path;
    String Movie_OverView;
    String Movie_Release;
    String Movie_Rating;
    String MovieID;
    String youtube;
    ContentValues contentValues =  new ContentValues();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        Title = bundle.getString("title");
        Image_Path = bundle.getString("image");
        Movie_OverView = bundle.getString("overview");
        Movie_Release = bundle.getString("release");
        Movie_Rating = bundle.getString("rating");
        MovieID = bundle.getString("id");
        String check = bundle.getString("favorite");
        String toCompare = new String("false");

        if(check.equals(toCompare)) {
            toggle_bool = false;
            contentValues.put(MovieContract.COLUMN_MOVIE_NAME,Title);
            contentValues.put(MovieContract.COLUMN_MOVIE_POSTER,Image_Path);
            contentValues.put(MovieContract.COLUMN_MOVIE_RATING,Movie_Rating);
            contentValues.put(MovieContract.COLUMN_MOVIE_REVIEW,"review goes here");
            contentValues.put(MovieContract.COLUMN_SYNOPSIS,Movie_OverView);
            contentValues.put(MovieContract.COLUMN_YOUTUBE_LINK,"youtube link");
            contentValues.put(MovieContract.COLUMN_MOVIE_RELEASE,Movie_Release);
            contentValues.put(MovieContract.COLUMN_MOVIE_ID,MovieID);

            String gottenreview = null;

            new GetReview().execute(MovieID,null,gottenreview);

            new GetYoutubeLink().execute(MovieID);

            String review = bundle.getString("review");
            TextView text = (TextView)findViewById(R.id.review_textview);
            text.setText(review);

            Button favb = (Button)findViewById(R.id.toggleButton);
            favb.setText("Not in Fav");

        }
        toggle_bool = true;
        String fav_value = null;
        Log.d(TAG,MovieID);
        new CheckIfDataIsPresent().execute(MovieID, null, fav_value);

        ImageView imageView = (ImageView)findViewById(R.id.movieImage);
        TextView movie_title = (TextView)findViewById(R.id.movie_title);
        TextView movie_release = (TextView)findViewById(R.id.movie_release);
        TextView movie_overview = (TextView)findViewById(R.id.movie_OverView);
        TextView movie_rating = (TextView)findViewById(R.id.movie_rating);
        TextView movie_review = (TextView)findViewById(R.id.review_textview);
        String review = bundle.getString("review");
        String synopsis = bundle.getString("overview");

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
        movie_overview.setText("Synopsis: "+synopsis);
        movie_rating.setText("Rating: "+Movie_Rating);
        movie_review.setText("Review: " + review);
        Button favb = (Button)findViewById(R.id.toggleButton);
        favb.setText("In Favorite");

    }

    public void toggleFav(View view){
        TextView textView = (TextView)findViewById(R.id.toggle);


        toggle_bool = !toggle_bool;
        if(toggle_bool == true){
            textView.setText("In Favorite");
            //add entry to database
            new AddEntry().execute(contentValues);

        }else if(toggle_bool == false){
            textView.setText("Not in Favorite");
            //delete entry from database
            new DeleteEntry().execute(MovieID);
        }
    }


    public void launchYoutube(View view) {

        Intent thisintent = this.getIntent();
        Bundle bundle = thisintent.getExtras();

        String toCheck = bundle.getString("favorite");
        String toCompare = new String("flase");

        if(toCheck.equals(toCompare)){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube.com:" + tempdata));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + tempdata));
            startActivity(intent);
        }
        return;
        }
        else {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube.com:" + youtube));
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + youtube));
                startActivity(intent);
            }
        }

    }

    public class GetReview extends AsyncTask<String,Void,String>{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TextView movie_review = (TextView)findViewById(R.id.review_textview);
            movie_review.setText(s);
            contentValues.put(MovieContract.COLUMN_MOVIE_REVIEW,s);
        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String reviewJSON = null;
            try{
                final String BASEURL = "http://api.themoviedb.org/3";
                final String MOVIES = "movie";
                final String REVIEW = "reviews";
                final String APIPARAM = "api_key";
                String movID = params[0];

                Uri builtUri = Uri.parse(BASEURL).buildUpon()
                        .appendEncodedPath(MOVIES)
                        .appendEncodedPath(movID)
                        .appendEncodedPath(REVIEW)
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
                reviewJSON = buffer.toString();
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
                String review = parseReview(reviewJSON);
                return review;
            }catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public class GetYoutubeLink extends AsyncTask<String,Void,String>{

        @Override
        protected void onPostExecute(String s) {
            new ModifyYoutubeLink().execute(s);
            super.onPostExecute(s);

        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try{
                final String BASEURL = "http://api.themoviedb.org/3";
                final String MOVIES = "movie";
                final String VIDEOS = "videos";
                final String APIPARAM = "api_key";

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
            return tempdata;
        }
    }

    public String parseReview(String json) throws JSONException{
        String list = "results";
        String id = "content";
        JSONObject moviesOBJ = new JSONObject(json);
        JSONArray movieArr = moviesOBJ.getJSONArray(list);
        int i = 0;
        JSONObject obj = movieArr.getJSONObject(i);
        String review = obj.getString(id);
        return review;
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

    public class DeleteEntry extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... params) {
            SQLiteOpenHelper sqLiteOpenHelper = new MovieDatabase(getApplicationContext());
            SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
            String TABLENAME = MovieContract.TABLE_NAME;
            String selection = MovieContract.COLUMN_MOVIE_ID + " LIKE ?";
            String[] selectionargs = { params[0] };
            db.delete(TABLENAME,selection,selectionargs);
            return null;
        }
    }

    public class AddEntry extends AsyncTask<ContentValues,Void,Long>{

        @Override
        protected Long doInBackground(ContentValues... params) {
            SQLiteOpenHelper mHelper = new MovieDatabase(getApplicationContext());
            SQLiteDatabase db = mHelper.getWritableDatabase();
            long number = db.insert(
                    MovieContract.TABLE_NAME,
                    null,
                    params[0]
            );
            Log.d(TAG,Long.toString(number));
            return number;
        }
    }
    public class ModifyYoutubeLink extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... params) {
            ContentValues values = new ContentValues();
            values.put(MovieContract.COLUMN_YOUTUBE_LINK,params[0]);
            SQLiteOpenHelper mHelper = new MovieDatabase(getApplicationContext());
            SQLiteDatabase db = mHelper.getWritableDatabase();
            String selection = MovieContract.COLUMN_MOVIE_ID + " LIKE ?";
            db.update(
                    MovieContract.TABLE_NAME,
                    values,
                    selection,
                    null
            );
            return null;
        }
    }
    public class CheckIfDataIsPresent extends AsyncTask<String,Void,String>{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TextView textView = (TextView)findViewById(R.id.toggle);
            textView.setText(s);
        }

        @Override
        protected String doInBackground(String... params) {
            SQLiteOpenHelper mHelper = new MovieDatabase(getApplicationContext());
            SQLiteDatabase db = mHelper.getWritableDatabase();
            String query = "SELECT * FROM " + MovieContract.TABLE_NAME + " WHERE " + MovieContract.COLUMN_MOVIE_ID + " = " + params[0];
            Cursor cursor = db.rawQuery(query,null);
            if(cursor.getCount()<=0){
                return "Not in Favorite";
            }
            cursor.close();
            return "In Favorite";
        }
    }
}
