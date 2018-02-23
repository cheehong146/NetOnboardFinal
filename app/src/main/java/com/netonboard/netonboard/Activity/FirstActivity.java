package com.netonboard.netonboard.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.netonboard.netonboard.R;
import com.securepreferences.SecurePreferences;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class FirstActivity extends AppCompatActivity {
    private static final String TAG = "FirstActivity";

    SharedPreferences sharedPreferences;
    private ProgressBar spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        spinner = (ProgressBar) findViewById(R.id.progress_bar);
        spinner.setVisibility(View.VISIBLE);
        sharedPreferences = new SecurePreferences(this, "netdeveloper", "loginInfo.xml");

        matchServerVersion();
    }

    public void checkValidUser(int userId, String password){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://cloudsub04.trio-mobile.com/curl/mobile/user/login.php?id=" + userId + "&p=" + password, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String body = new String(responseBody);
                if(body.equals("success")){
                    spinner.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(FirstActivity.this, PinActivity.class));
                    finish();
                }else{
                    spinner.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(FirstActivity.this, LoginActivity.class));
                    //TODO web must return more than false if wrong password to tell user if wrong password
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            //TODO no connection
            }
        });
    }

    public void matchServerVersion() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://cloudsub04.trio-mobile.com/curl/mobile/hr_info/api_version.php", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String body = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(body);
                    object.getString("version");
                    PackageManager manager = FirstActivity.this.getPackageManager();
                    PackageInfo info = manager.getPackageInfo(FirstActivity.this.getPackageName(), 0);
                    if (info.versionCode == object.getInt("version")) {
                        int userId = sharedPreferences.getInt("userId", -1);
                        String password = sharedPreferences.getString("password", null);
                        checkValidUser(userId, password);
                    } else {
                        updateDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(FirstActivity.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void updateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
        builder.setMessage("New version is available");
        builder.setPositiveButton("Update now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.show();
    }
}
