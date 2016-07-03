package com.example.android.trendingmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

    public void updateMovies(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String value = sharedPreferences.getString("sort_movie","popular");
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

                bundle.putString(im,parsedData[position]);
                bundle.putString(over,movieOverViewToSend[position]);
                bundle.putString(rel,movieReleaseDate[position]);
                bundle.putString(title,movieTitleToSend[position]);
                bundle.putString(rating,movieRating[position]);
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
                final String API_KEY ="" ;

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
}
