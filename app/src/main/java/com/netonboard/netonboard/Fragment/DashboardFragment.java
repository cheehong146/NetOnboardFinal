package com.netonboard.netonboard.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.netonboard.netonboard.Object.GlobalFileIO;
import com.netonboard.netonboard.R;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class DashboardFragment extends Fragment {
    private static final String TAG = "DashboardFragment";
    GlobalFileIO fileIO;
    int userId;

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
        fileIO = new GlobalFileIO(getContext());



        loadData(view);

        return view;
    }

    public void loadData(View view) {
        loadSupport(view);
        loadGarbageCollector(view);
        loadLeaveAndClaim(view);
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
                try {
                    String body = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(body);
                    String primarySupport = jsonObject.getString("s_user_id_standby");
                    String secondarySupport = jsonObject.getString("s_user_id_standby_backup");
                    tvPrimarySupport.setText(primarySupport);
                    tvSecondarySupport.setText(secondarySupport);
                } catch (JSONException e) {
                    e.printStackTrace();
                    tvPrimarySupport.setText("Failed to load data");
                    tvSecondarySupport.setText("Failed to load data");
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
                TextView tvGarbagePerson = view.findViewById(R.id.tv_garbage_collector_name);
                TextView tvGarbageDate = view.findViewById(R.id.tv_garbage_collector_date);
                try {
                    String body = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(body);
                    String person = jsonObject.getString("s_user_duty");
                    String date = jsonObject.getString("d_duty");
                    tvGarbagePerson.setText(person);
                    tvGarbageDate.setText(date);
                } catch (JSONException e) {
                    e.printStackTrace();
                    tvGarbagePerson.setText("Failed to load data");
                    tvGarbageDate.setText("Failed to load data");
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    tvGarbagePerson.setText("Failed to load data");
                    tvGarbageDate.setText("Failed to load data");
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
                try {
                    String body = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(body);
                    String leave_balance = jsonObject.getString("leave_balance");
                    String available_claim = jsonObject.getString("available_claim");
                    String total_late = jsonObject.getString("total_late");
                    tvClaim.setText(available_claim);
                    tvLeave.setText(leave_balance);
                    tvLate.setText(total_late);
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
