package com.netonboard.netonboard.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.netonboard.netonboard.Object.GlobalFileIO;
import com.netonboard.netonboard.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class DashboardFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "DashboardFragment";
    GlobalFileIO fileIO;
    int userId;
    SwipeRefreshLayout swipeRefreshLayout;
    View masterView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();

        if (bundle != null)
            userId = bundle.getInt("userId", -1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);
        masterView = view;
        fileIO = new GlobalFileIO(getContext());
        swipeRefreshLayout = view.findViewById(R.id.main_dashboard_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        loadData();
        viewListener(masterView);

        return masterView;
    }

    @Override
    public void onRefresh() {
        loadData();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Dashboard");
    }

    public void loadData() {
        loadSupport(masterView);
        loadGarbageCollector(masterView);
        loadLeaveAndClaim(masterView);
    }

    public void viewListener(View view) {
        RelativeLayout leaveLayout = view.findViewById(R.id.layout_dashboard_leave);
        RelativeLayout lateLayout = view.findViewById(R.id.layout_dashboard_late);//TODO CLICK TO REPLACE WITH LATE FRAGMENT
        RelativeLayout claimLayout = view.findViewById(R.id.layout_dashboard_claim);

        leaveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LeaveFragment leaveFragment = new LeaveFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("userId", userId);
                replaceFragment(leaveFragment);
            }
        });

        claimLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClaimFragment claimFragment = new ClaimFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("userId", userId);
                replaceFragment(claimFragment);
            }
        });

        lateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LateFragment lateFragment = new LateFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("userId", userId);
                replaceFragment(lateFragment);
            }
        });
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadSupport(final View view) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://cloudsub04.trio-mobile.com/curl/mobile/sos/standby_support.php", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                fileIO.writeToFile(fileIO.FILENAMESUPPORT, responseBody.toString());
                Log.i(TAG, "onSuccess: loadSupport");

                TextView tvPrimarySupport = view.findViewById(R.id.tv_homepage_support_primary_data);
                TextView tvSecondarySupport = view.findViewById(R.id.tv_homepage_support_secondary_data);
                TextView tvTakeOverSupport = view.findViewById(R.id.tv_homepage_support_takeover_data);
                try {
                    String body = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(body);
                    tvPrimarySupport.setText(jsonObject.getString("s_user_id_standby"));
                    tvSecondarySupport.setText(jsonObject.getString("s_user_id_standby_backup"));
                    if (jsonObject.getString("s_user_id_take_over_backup").equals(""))
                        tvTakeOverSupport.setText("None");
                    else
                        tvTakeOverSupport.setText(jsonObject.getString("s_user_id_take_over_backup"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    tvPrimarySupport.setText("Failed to load data");
                    tvSecondarySupport.setText("Failed to load data");
                    tvTakeOverSupport.setText("Failed to load data");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void loadGarbageCollector(final View view) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://cloudsub04.trio-mobile.com/curl/mobile/hr_info/garbage_collector.php", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                fileIO.writeToFile(fileIO.FILENAMEGARBAGECOLLECTOR, responseBody.toString());
                Log.i(TAG, "onSuccess: loadGarbageCollector");
                TextView tvGarbagePersonPrimary = view.findViewById(R.id.tv_garbage_collector_name);
                TextView tvGarbageDatePrimary = view.findViewById(R.id.tv_garbage_collector_date);
                TextView tvGarbagePersonSecondary = view.findViewById(R.id.tv_garbage_collector_name_secondary);
                TextView tvGarbageDateSecondary = view.findViewById(R.id.tv_garbage_collector_date_secondary);
                try {
                    String body = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(body);
                    JSONObject primaryPerson = jsonObject.getJSONObject("1");
                    JSONObject secondaryPerson = jsonObject.getJSONObject("2");
                    tvGarbagePersonPrimary.setText(primaryPerson.getString("s_user_duty"));
                    tvGarbagePersonSecondary.setText(secondaryPerson.getString("s_user_duty"));
                    tvGarbageDatePrimary.setText(primaryPerson.getString("d_duty"));
                    tvGarbageDateSecondary.setText(secondaryPerson.getString("d_duty"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    tvGarbagePersonPrimary.setText("Failed to load data");
                    tvGarbageDatePrimary.setText("Failed to load data");
                    tvGarbagePersonSecondary.setText("Failed to load data");
                    tvGarbageDateSecondary.setText("Failed to load data");
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    tvGarbagePersonPrimary.setText("Failed to load data");
                    tvGarbageDatePrimary.setText("Failed to load data");
                    tvGarbagePersonSecondary.setText("Failed to load data");
                    tvGarbageDateSecondary.setText("Failed to load data");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void loadLeaveAndClaim(final View view) {
        AsyncHttpClient client = new AsyncHttpClient();
        final RequestParams requestParams = new RequestParams();
        requestParams.put("id", userId);
        client.get("http://cloudsub04.trio-mobile.com/curl/mobile/hr_info/general_info.php", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                TextView tvClaim = view.findViewById(R.id.tv_homepage_claim_data);
                TextView tvLeave = view.findViewById(R.id.tv_homepage_leave_data);
                TextView tvLate = view.findViewById(R.id.tv_homepage_late_data);
                TextView tvFirstYear = view.findViewById(R.id.tv_homepage_performance_first_year);
                TextView tvFirstScore = view.findViewById(R.id.tv_homepage_performance_first_score);
                TextView tvFirstAttendance = view.findViewById(R.id.tv_homepage_performance_first_attendance);
                TextView tvSecondYear = view.findViewById(R.id.tv_homepage_performance_second_year);
                TextView tvSecondScore = view.findViewById(R.id.tv_homepage_performance_second_score);
                TextView tvSecondAttendance = view.findViewById(R.id.tv_homepage_performance_second_attendance);
                try {
                    String body = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(body);
                    String leave_balance = jsonObject.getString("leave_balance");
                    String available_claim = jsonObject.getString("available_claim");
                    String total_late = jsonObject.getString("total_late");
                    tvClaim.setText(available_claim);
                    tvLeave.setText(leave_balance);
                    tvLate.setText(total_late);//TODO ASK SHADOW API CHANGE TO ARRAY
                } catch (JSONException e) {
                    e.printStackTrace();
                    tvClaim.setText("Failed to load data");
                    tvLeave.setText("Failed to load data");
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    tvClaim.setText("Failed to load data");
                    tvLeave.setText("Failed to load data");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
