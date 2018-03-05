package com.netonboard.netonboard.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.netonboard.netonboard.Fragment.CalendarFragment;
import com.netonboard.netonboard.Fragment.ClaimFragment;
import com.netonboard.netonboard.Fragment.DashboardFragment;
import com.netonboard.netonboard.Fragment.LateFragment;
import com.netonboard.netonboard.Fragment.LeaveFragment;
import com.netonboard.netonboard.R;
import com.securepreferences.SecurePreferences;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    int userId;
    SharedPreferences sharedPreferences;
    AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Dashboard");

        //TODO save fragment data to file in-case no internet connection

        client = new AsyncHttpClient();
        sharedPreferences = new SecurePreferences(this, "netdeveloper", "loginInfo.xml");
        userId = sharedPreferences.getInt("userId", -1);
        String username = sharedPreferences.getString("username", "error");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView tvUsername = navigationView.getHeaderView(0).findViewById(R.id.tv_drawer_username);
        tvUsername.setText(username);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        DashboardFragment dashboardFragment = new DashboardFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("userId", userId);
        dashboardFragment.setArguments(bundle);
        transaction.add(R.id.main_frame_container, dashboardFragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            setTitle("Dashboard");
            Bundle bundle = new Bundle();
            bundle.putInt("userId", userId);
            FragmentManager fragmentManager = getSupportFragmentManager();
            DashboardFragment dashboardFragment = new DashboardFragment();
            dashboardFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.main_frame_container, dashboardFragment).commit();

        } else if (id == R.id.nav_calendar) {
            setTitle("Company Calendar");
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame_container, new CalendarFragment()).commit();

        } else if (id == R.id.nav_leave) {
            setTitle("Leave");
            Bundle bundle = new Bundle();
            bundle.putInt("userId", userId);
            FragmentManager fragmentManager = getSupportFragmentManager();
            LeaveFragment leaveFragment = new LeaveFragment();
            leaveFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.main_frame_container, leaveFragment).commit();

        } else if (id == R.id.nav_claim) {
            setTitle("Claim");
            Bundle bundle = new Bundle();
            bundle.putInt("userId", userId);
            FragmentManager fragmentManager = getSupportFragmentManager();
            ClaimFragment claimFragment = new ClaimFragment();
            claimFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.main_frame_container, claimFragment).commit();

        } else if (id == R.id.nav_late) {
            setTitle("Late");
            Bundle bundle = new Bundle();
            bundle.putInt("userId", userId);//TODO WHETHER OR NOT TO PASS YEAR TO API
            FragmentManager fragmentManager = getSupportFragmentManager();
            LateFragment lateFragment = new LateFragment();
            lateFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.main_frame_container, lateFragment).commit();
        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(this, ChangePinActivity.class));
        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout() {
        SharedPreferences sharedPreferences = new SecurePreferences(this, "netdeveloper", "loginInfo.xml");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        if (editor.commit())
            finish();
    }


}
