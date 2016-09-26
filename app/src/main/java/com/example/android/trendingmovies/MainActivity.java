package com.example.android.trendingmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MovieInformation.OnFragmentInteractionListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       /* FragmentManager manager = getSupportFragmentManager();
        Fragment myMainFrag = getSupportFragmentManager().findFragmentById(R.id.movies_gridview);
        Fragment mMovieInfo = getSupportFragmentManager().findFragmentById(R.id.movie_fragment);
        manager.putFragment(outState,MainActivityFragment.TAG,myMainFrag);
        manager.putFragment(outState,MovieInformation.TAG,mMovieInfo);*/
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
       // instantiateFragments(savedInstanceState);
    }

   /* private void instantiateFragments(Bundle inState) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        MainActivityFragment mMyFragment;
        MovieInformation mMovieInfo;
        if (inState != null) {
            mMyFragment = (MainActivityFragment) manager.getFragment(inState, MainActivityFragment.TAG);
            mMovieInfo = (MovieInformation)manager.getFragment(inState,MovieInformation.TAG);
        } else {
            mMyFragment = new MainActivityFragment();
            mMovieInfo = new MovieInformation();
            transaction.add(R.id.movies_gridview, mMyFragment, MainActivityFragment.TAG);
            transaction.add(R.id.movie_fragment, mMovieInfo,MovieInformation.TAG);
            transaction.commit();
        }
    }*/



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
