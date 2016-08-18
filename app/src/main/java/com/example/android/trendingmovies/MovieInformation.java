package com.example.android.trendingmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieInformation.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieInformation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieInformation extends Fragment {
    final static String TAG = "MOVIEINFORMATION";
    String youTubeURL = null;
    String tempdata = null;
    String check = null;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MovieInformation() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MovieInformation.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieInformation newInstance(String param1, String param2) {
        MovieInformation fragment = new MovieInformation();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_information, container, false);
        Button launch = (Button)view.findViewById(R.id.trailerbutton);
        launch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"SOME SHIT");
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube.com:" + tempdata));
                        startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + tempdata));
                        startActivity(intent);

                    return;
                }
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void updateInformation(Bundle bundle){
        String Title = bundle.getString("title");
        String Movie_OverView = bundle.getString("overview");
        String Movie_Release = bundle.getString("release");
        String Movie_Rating = bundle.getString("rating");
        String MovieID = bundle.getString("id");

        check = bundle.getString("favorite");
        String toCompare = new String("false");

        TextView titletext = (TextView)getView().findViewById(R.id.movietitle);
        titletext.setText(Title);

        TextView ratingtext = (TextView)getView().findViewById(R.id.rating);
        ratingtext.setText(Movie_Rating);

        TextView datetext = (TextView)getView().findViewById(R.id.date);
        datetext.setText(Movie_Release);

        TextView synopsistext = (TextView)getView().findViewById(R.id.synposis);
        synopsistext.setText(Movie_OverView);

        String Image_Path = bundle.getString("image");

        ImageView posterimg = (ImageView)getView().findViewById(R.id.image);

        Context context = getContext();
        final String BASE_URL = "http://image.tmdb.org/t/p/.";
        final String SIZE_POSTER = "w185";
        final String POSTER_URL = Image_Path;
        Uri url = Uri.parse(BASE_URL)
                .buildUpon()
                .appendEncodedPath(SIZE_POSTER)
                .appendEncodedPath(POSTER_URL)
                .build();

        Picasso.with(context).load(url).into(posterimg);

        new GetYoutubeLink().execute(MovieID);
    }


    public class GetYoutubeLink extends AsyncTask<String,Void,String> {

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

    public class ModifyYoutubeLink extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... params) {
            ContentValues values = new ContentValues();
            values.put(MovieContract.COLUMN_YOUTUBE_LINK,params[0]);
            SQLiteOpenHelper mHelper = new MovieDatabase(getContext());
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
