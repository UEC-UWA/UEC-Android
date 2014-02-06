package com.appulse.uec;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, FragNewsList.onNewsItemSelectedListener, FragAbout.onAboutListener,
        FragEventList.onEventsItemSelectedListener,
        FragTorques.onTorquesListener,
        FragCommitteeList.onCommitteeItemSelectedListener,
        FragAboutApp.onAboutAppVersionListener,
        FragEventsListDetail.onEventMapListener

{

    final String PREFS_NAME = "MyPrefsFile";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Current fragment in the activity
     */
    private Fragment currentFragment;

    /**
     * The action bar menu
     */
    private Menu mMenu;

    /**
     * The current section the user is in. As per Nav Drawer
     */
    private int currentPosition;

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

        //Set up the listeners, such as on item click
        FragEventList.setOnMySignalListener(this);
        FragAbout.setOnMySignalListener(this);
        FragTorques.setOnMySignalListener(this);
        FragCommitteeList.setOnMySignalListener(this);
        FragAboutApp.setOnMySignalListener(this);
        FragEventsListDetail.setOnMySignalListener(this);

        if (mNavigationDrawerFragment != null) {
            //mNavigationDrawerFragment.showIndicator(false);
        }

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisc(true)
        .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .defaultDisplayImageOptions(defaultOptions)
        .build();
        ImageLoader.getInstance().init(config);

        if (savedInstanceState != null) {
           FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() == 0 && mNavigationDrawerFragment != null) {
                mNavigationDrawerFragment.showIndicator(true);
                showRefresh();
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                mNavigationDrawerFragment.showIndicator(false);
                hideRefresh();
            }
        // Do it on Application start
        }


        if (isFirstLaunch()) {
            Intent intent = new Intent(getApplicationContext(), com.appulse.uec.FirstLaunch.class);
            startActivity(intent);
        }

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment fragment = getFragment(position);

        currentPosition = position;
        currentFragment = fragment;

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        mTitle = getTitle(position);

       // while (fragmentManager.getBackStackEntryCount() != 0) fragmentManager.popBackStack();
        setTitle(mTitle);
    }

    public String getTitle(int position) {
        if (position == 1) {
            return "About";
        } else if (position == 2) {
            return "Calendar";
        } else if (position == 3) {
            return "Committee";
        } else if (position == 4) {
            return "Torques";
        } else {
            return "News";
        }
    }

    public Fragment getFragment(int position) {
        if (position == 1) {
            return new FragAbout();
        } else if (position == 2) {
            return new FragEventList();
        } else if (position == 3) {
            return new FragCommitteeList();
        } else if (position == 4) {
            return new FragTorques();
        } else {
            return new FragNewsList();
        }

    }

    public void onSectionAttached(int number) {
        mTitle = getTitle(number);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
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
            mMenu = menu;

            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() == 0 && mNavigationDrawerFragment != null) {
                mNavigationDrawerFragment.showIndicator(true);
                showRefresh();
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                mNavigationDrawerFragment.showIndicator(false);
                hideRefresh();
            }

            return super.onCreateOptionsMenu(menu);
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // MenuItem item= menu.findItem(R.id.action_settings);
        //  item.setVisible(false);
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo n = manager.getActiveNetworkInfo();
        return n != null && n.isAvailable();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            if (fragmentManager.getBackStackEntryCount() == 0) {

                return super.onOptionsItemSelected(item);
            }


            fragmentManager.popBackStack();

            if (fragmentManager.getBackStackEntryCount() == 1 && mNavigationDrawerFragment != null) {
                mNavigationDrawerFragment.showIndicator(true);
                showRefresh();
            }
            //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            return true;
        } else if (id == R.id.action_refresh) {
            if (isNetworkAvailable()) {
              //  item.setActionView(R.layout.progressbar);
                //item.expandActionView();

                MenuItem menuItemRefresh = item;
                menuItemRefresh = MenuItemCompat.setActionView(menuItemRefresh, R.layout.progressbar);

                if (currentPosition == 1) {
                    FragAbout fragmentToUpdate = (FragAbout) currentFragment;
                    if (fragmentToUpdate != null)fragmentToUpdate.updateList(item);
                } else if (currentPosition == 2) {
                    FragEventList fragmentToUpdate = (FragEventList) currentFragment;
                    if (fragmentToUpdate != null) fragmentToUpdate.updateList(item);
                } else if (currentPosition == 3) {
                    FragCommitteeList fragmentToUpdate = (FragCommitteeList) currentFragment;
                    if (fragmentToUpdate != null)fragmentToUpdate.updateList(item);
                } else if (currentPosition == 4) {
                    FragTorques fragmentToUpdate = (FragTorques) currentFragment;

                    if (fragmentToUpdate != null)  fragmentToUpdate.updateList(item);
                } else {
                   FragNewsList fragmentToUpdate = (FragNewsList) currentFragment;
                    if (fragmentToUpdate != null) fragmentToUpdate.updateList(item);
                }

            }
            return super.onOptionsItemSelected(item);
        }
        //  return true;

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            //fragmentManager.beginTransaction()
            // .replace(R.id.container, new FragNewsList()).commit();

            // getActionBar().setDisplayHomeAsUpEnabled(false);
            if (fragmentManager.getBackStackEntryCount() == 0) {

                return true;
            }

            fragmentManager.popBackStack();

            if (fragmentManager.getBackStackEntryCount() == 1 && mNavigationDrawerFragment != null) {
                mNavigationDrawerFragment.showIndicator(true);
                showRefresh();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void addFragmentToStack(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft =  fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);

        ft.commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNavigationDrawerFragment.showIndicator(false);
        hideRefresh();
    }

    @Override
    public void onNewsItemSelected(String value) {
        Fragment newsDetail = new FragNewsListDetail();
        Bundle args = new Bundle();
        args.putString("content", value);
        newsDetail.setArguments(args);

        addFragmentToStack(newsDetail);

    }

    @Override
    public void onCommitteeItemSelected(int id) {
        Fragment committeeDetail = new FragCommitteeListDetail();

        Bundle args = new Bundle();
        args.putInt("id", id);
        committeeDetail.setArguments(args);
        addFragmentToStack(committeeDetail);
    }

    /**
     * Hides the refresh icon in the action bar.
     */
    public void hideRefresh() {
        if (mMenu != null) {
            MenuItem item = mMenu.findItem(R.id.action_refresh);
            item.setVisible(false);
        }
    }

    /**
     * Makes the refresh icon in the action bar visable.
     */
    public void showRefresh() {
        if (mMenu != null) {
            MenuItem item = mMenu.findItem(R.id.action_refresh);
            item.setVisible(true);
        }
    }

    @Override
    public void onAboutAppVersion() {
       addFragmentToStack(new FragAboutAppVersionInfo());
    }

    /*
       About section listeners
    */
    @Override
    public void onAboutUECSelected() {
        addFragmentToStack(new FragAboutUEC());
    }

    @Override
    public void onAboutAppSelected() {
        addFragmentToStack(new FragAboutApp());

    }

    @Override
    public void onEventItemSelected(int id) {
        Fragment eventDetail = new FragEventsListDetail();

        Bundle args = new Bundle();
        args.putInt("id", id);
        eventDetail.setArguments(args);

        addFragmentToStack(eventDetail);
    }

    @Override
    public void onTorquesSelected(String value) {
        Fragment torqueDetail = new FragTorqueDetail();

        Bundle args = new Bundle();
        args.putString("url", value);
        torqueDetail.setArguments(args);

        addFragmentToStack(torqueDetail);
    }

    @Override
    public void onMapButtonSelected(String value) {

        Fragment map = new FragEventMap();

        Bundle args = new Bundle();
        args.putString("address", value);
        map.setArguments(args);

        addFragmentToStack(new FragEventMap());
    }

    public boolean isFirstLaunch() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
      return (settings.getBoolean("my_first_time", true));
    }


}
