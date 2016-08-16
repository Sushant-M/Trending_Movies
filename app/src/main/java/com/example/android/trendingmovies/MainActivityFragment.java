package com.example.android.trendingmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

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
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    String[] MovieName = new String[50];
    String[] MovieID = new String[50];
    String[] MoviePoster = new String[50];
    String[] MovieRelease = new String[50];
    String[] MovieRating = new String[50];
    String[] MovieReview = new String[50];
    String[] MovieSynopsis = new String[50];
    String[] MovieLink = new String[50];


    GridView gridView;
    public MainActivityFragment() {
    }

    final static String TAG = "MainActivityFragment";
    String movies_data;
    String[] parsedData;
    String[] movieTitleToSend;
    String[] movieOverViewToSend;
    String[] movieReleaseDate;
    String[] movieRating;
    String[] movieID;

    public void updateMovies(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String value = sharedPreferences.getString("sort_movie","popular");
        new ReadFromDatabase().execute(null,null,null);

        new getMovies().execute(value);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.moviesfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings1){
            Intent intent = new Intent(getActivity(),SettingsActivity.class);
            startActivity(intent);
        }
        if(id == R.id.action_refresh){
            updateMovies();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        updateMovies();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.movies_gridview);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();

                String im = "image";
                String over = "overview";
                String rel = "release";
                String title = "title";
                String rating = "rating";
                String ID = "id";

                bundle.putString(im,parsedData[position]);
                bundle.putString(over,movieOverViewToSend[position]);
                bundle.putString(rel,movieReleaseDate[position]);
                bundle.putString(title,movieTitleToSend[position]);
                bundle.putString(rating,movieRating[position]);
                bundle.putString(ID,movieID[position]);
                Intent intent = new Intent(getActivity(),DetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public class getMovies extends AsyncTask<String, Void , String[]>{

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            parsedData = strings;
            gridView.setAdapter(new ImageAdapter(getActivity(),parsedData));
            try {
                movieTitleToSend = parseMovieTitle();
                movieOverViewToSend = parseMovieOverView();
                movieReleaseDate = parseMovieReleaseDate();
                movieRating = parseMovieRating();
                movieID = parseMovieID();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String[] temp_data = new String[0];

            try{
                final String baseQuery = "http://api.themoviedb.org/3";
                final String movies = "movie";
                final String api_param = "api_key";
                //Insert your API key here.
                final String API_KEY ="fc53fdb027975aaacc7595aeb259107d" ;

                String type = params[0];

                Uri builtUri = Uri.parse(baseQuery).buildUpon()
                        .appendEncodedPath(movies)
                        .appendEncodedPath(type)
                        .appendQueryParameter(api_param,API_KEY)
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
                movies_data = buffer.toString();

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

            try {
                temp_data = parseMoviePosters();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return temp_data;
        }
    }

    public String[] parseMoviePosters() throws JSONException {
        String poster ="poster_path";
        String list = "results";
        JSONObject moviesOBJ = new JSONObject(movies_data);
        JSONArray movieArr = moviesOBJ.getJSONArray(list);
        String[] parsedMovies = new String[20];
        for(int i =0 ; i<movieArr.length(); i++){
            JSONObject obj = movieArr.getJSONObject(i);
            String posterparse = obj.getString(poster);
            parsedMovies[i] = posterparse;
        }
        return parsedMovies;
    }

    public String[] parseMovieTitle() throws JSONException {
        String list = "results";
        String id = "original_title";
        JSONObject moviesOBJ = new JSONObject(movies_data);
        JSONArray movieArr = moviesOBJ.getJSONArray(list);
        String[] MovieTitle = new String[20];
        for(int i =0 ; i<movieArr.length(); i++){
            JSONObject obj = movieArr.getJSONObject(i);
            String posterparse = obj.getString(id);
            MovieTitle[i] = posterparse;
        }
        return MovieTitle;
    }
    public String[] parseMovieOverView() throws JSONException {
        String list = "results";
        String id = "overview";
        JSONObject moviesOBJ = new JSONObject(movies_data);
        JSONArray movieArr = moviesOBJ.getJSONArray(list);
        String[] MovieOverView = new String[20];
        for(int i =0 ; i<movieArr.length(); i++){
            JSONObject obj = movieArr.getJSONObject(i);
            String posterparse = obj.getString(id);
            MovieOverView[i] = posterparse;
        }
        return MovieOverView;
    }

    public String[] parseMovieReleaseDate() throws JSONException {
        String list = "results";
        String id = "release_date";
        JSONObject moviesOBJ = new JSONObject(movies_data);
        JSONArray movieArr = moviesOBJ.getJSONArray(list);
        String[] MovieReleaseDate = new String[20];
        for(int i =0 ; i<movieArr.length(); i++){
            JSONObject obj = movieArr.getJSONObject(i);
            String posterparse = obj.getString(id);
            MovieReleaseDate[i] = posterparse;
        }
        return MovieReleaseDate;
    }
    public String[] parseMovieRating() throws JSONException {
        String list = "results";
        String id = "vote_average";
        JSONObject moviesOBJ = new JSONObject(movies_data);
        JSONArray movieArr = moviesOBJ.getJSONArray(list);
        String[] MovieRating = new String[20];
        for(int i =0 ; i<movieArr.length(); i++){
            JSONObject obj = movieArr.getJSONObject(i);
            String posterparse = obj.getString(id);
            MovieRating[i] = posterparse;
        }
        return MovieRating;
    }
    public String[] parseMovieID() throws JSONException {
        String list = "results";
        String id = "id";
        JSONObject moviesOBJ = new JSONObject(movies_data);
        JSONArray movieArr = moviesOBJ.getJSONArray(list);
        String[] MovieID = new String[20];
        for(int i =0 ; i<movieArr.length(); i++){
            JSONObject obj = movieArr.getJSONObject(i);
            String idparse = obj.getString(id);
            MovieID[i] = idparse;
        }
        return MovieID;
    }


    public class ReadFromDatabase extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

        @Override
        protected Void doInBackground(Void... params) {

            SQLiteOpenHelper mOpenHelper = new MovieDatabase(getContext());
            SQLiteDatabase db = mOpenHelper.getReadableDatabase();

            String[] projection = {
                    MovieContract._ID,
                    MovieContract.COLUMN_MOVIE_NAME,
                    MovieContract.COLUMN_MOVIE_POSTER,
                    MovieContract.COLUMN_MOVIE_RATING,
                    MovieContract.COLUMN_MOVIE_RELEASE,
                    MovieContract.COLUMN_YOUTUBE_LINK,
                    MovieContract.COLUMN_MOVIE_REVIEW,
                    MovieContract.COLUMN_MOVIE_ID,
                    MovieContract.COLUMN_SYNOPSIS
            };
            String sortOrder = MovieContract.COLUMN_MOVIE_NAME + " DESC";

            Cursor cursor = db.query(
                    MovieContract.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    sortOrder
            );

            if(cursor != null) {

                cursor.moveToFirst();
                long itemid = cursor.getCount();

                Log.d(TAG, Long.toString(itemid));


                for (int i = 0; i < itemid; i++) {
                    MovieName[i] = cursor.getString(1);
                    MoviePoster[i] = cursor.getString(2);
                    MovieRating[i] = cursor.getString(3);
                    MovieReview[i] = cursor.getString(4);
                    MovieSynopsis[i] = cursor.getString(5);
                    MovieLink[i] = cursor.getString(6);
                    MovieRelease[i] = cursor.getString(7);
                    MovieID[i] = cursor.getString(8);
                    cursor.moveToNext();
                }
            }
            return null;
        }
    }

}
