package com.example.android.trendingmovies;

import android.content.Intent;
import android.content.SharedPreferences;
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
        return rootView;
    }

    public class getMovies extends AsyncTask<String, Void , String[]>{

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            parsedData = strings;
            gridView.setAdapter(new ImageAdapter(getActivity(),parsedData));
        }

        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String[] temp_data = new String[0];


            try{

                final String baseQuery = "http://api.themoviedb.org/3";
                final String movies = "movie";
                final String popular = "popular";
                final String top_rated = "top_rated";
                final String api_param = "api_key";
                final String API_KEY ="fc53fdb027975aaacc7595aeb259107d" ;

                String type = params[0];
                Log.d(TAG,type);

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
                Log.d(TAG,movies_data);

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
}
