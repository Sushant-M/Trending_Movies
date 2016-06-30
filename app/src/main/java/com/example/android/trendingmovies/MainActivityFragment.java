package com.example.android.trendingmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public MainActivityFragment() {
    }

    final static String TAG = "MainActivityFragment";

    @Override
    public void onStart() {
        super.onStart();
        new getMovies().execute("Hello world");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    public class getMovies extends AsyncTask<String, Void , String[]>{

        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try{

                final String baseQuery = "http://api.themoviedb.org/3";
                final String movies = "movie";
                final String popular = "popular";
                final String top_rated = "top_rated";
                final String api_param = "api_key";
                final String API_KEY ="fc53fdb027975aaacc7595aeb259107d" ;
                String retrievedInfo;

                Uri builtUri = Uri.parse(baseQuery).buildUpon()
                        .appendEncodedPath(movies)
                        .appendEncodedPath(popular)
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
                retrievedInfo = buffer.toString();
                Log.d(TAG,retrievedInfo);

            }catch (IOException e){
                e.printStackTrace();
            }



            return new String[0];
        }
    }
}
