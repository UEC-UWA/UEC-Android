package com.appulse.uec;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, FragNewsList.onNewsItemSelectedListener,
FragNewsListDetail.onNewsDetailSelectedListener, FragAbout.onAboutListener, FragEventList.onEventsItemSelectedListener
{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        FragNewsList.setOnMySignalListener(this);
        FragNewsListDetail.setOnMySignalListener(this);
        FragEventList.setOnMySignalListener(this);
        FragAbout.setOnMySignalListener(this);
        if (mNavigationDrawerFragment != null) {
            //mNavigationDrawerFragment.showIndicator(false);

        }

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment fragment = getFragment(position);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        mTitle = getTitle(position);
       setTitle(mTitle);
      // getSupportActionBar().setTitle(getTitle(position));
        //while(getSupportFragmentManager().popBackStackImmediate()){}
    }

    public String getTitle(int position) {
        if (position ==1) {
            return "About";
        } else if (position == 2) {
            return "Calendar";
        } else {
            return "News";
        }
    }
    public Fragment getFragment(int position) {
        if (position ==1) {
            return new FragAbout();
        } else if (position == 2) {
            return new FragEventList();
        } else {
            return new FragNewsList();
        }

    }
    public void onSectionAttached(int number) {
        mTitle = getTitle(number);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
     //   actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //if (id == R.id.action_settings) {
          //  return true;
        //}
        if (id == android.R.id.home) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            //fragmentManager.beginTransaction()
                   // .replace(R.id.container, new FragNewsList()).commit();

           // getActionBar().setDisplayHomeAsUpEnabled(false);
            if (fragmentManager.getBackStackEntryCount() == 0) {
                return super.onOptionsItemSelected(item);
            }

            fragmentManager.popBackStack();

            if (fragmentManager.getBackStackEntryCount() == 1 && mNavigationDrawerFragment != null) {
                 mNavigationDrawerFragment.showIndicator(true);
            }
            //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            return true;
        }

      //  return true;

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onNewsItemSelected(int value) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new FragNewsListDetail()).addToBackStack(null).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationDrawerFragment.showIndicator(false);
    }

    @Override
    public void onBackPressed() {

        /**
         *  Propagate back press txo the fragments first
         */
       // FragmentManager fragmentManager = getSupportFragmentManager();
        //fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new FragNewsList()).addToBackStack(null).commit();

        Log.e("MainActivity","Back Press~!!!!!!!!!!");
        super.onBackPressed();

    }

    @Override
    public void onNewsDetailSelected(int value) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new FragNewsListDetail()).addToBackStack(null).commit();

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //mNavigationDrawerFragment.showIndicator(false);
    }

    /*
        About section listeners
     */
    @Override
    public void onAboutUECSelected() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new FragAboutUEC()).addToBackStack(null).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationDrawerFragment.showIndicator(false);
    }

    @Override
    public void onAboutAppSelected() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new FragAboutApp()).addToBackStack(null).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationDrawerFragment.showIndicator(false);

    }

    @Override
    public void onEventItemSelected(int id) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment eventDetail = new FragEventsListDetail();
        Log.e("EventListDetail", "" + id);
        Bundle args = new Bundle();
        args.putInt("id", id);
        eventDetail.setArguments(args);

        fragmentManager.beginTransaction()
                .replace(R.id.container, eventDetail).addToBackStack(null).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationDrawerFragment.showIndicator(false);
    }
}
