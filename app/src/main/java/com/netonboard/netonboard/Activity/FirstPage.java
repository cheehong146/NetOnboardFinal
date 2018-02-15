package com.netonboard.netonboard.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.netonboard.netonboard.R;
import com.securepreferences.SecurePreferences;

import cz.msebera.android.httpclient.Header;
import com.netonboard.netonboard.Object.GlobalFileIO;

public class FirstPage extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    GlobalFileIO globalFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        //TODO uncomment and progress bar loading
        globalFile = new GlobalFileIO(this);
        sharedPreferences = new SecurePreferences(this, "netdeveloper", "loginInfo.xml");
        int userId = sharedPreferences.getInt("userId", -1);
        String password = sharedPreferences.getString("password", null);

        checkValidUser(userId, password);

        startActivity(new Intent(FirstPage.this, LoginActivity.class));
        finish();

    }

    public void checkValidUser(int userId, String password){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://cloudsub04.trio-mobile.com/curl/mobile/user/login.php?id=" + userId + "&p=" + password, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String body = new String(responseBody);
                if(body.equals("success")){
                    startActivity(new Intent(FirstPage.this, PinActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(FirstPage.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            //TODO no connection
            }
        });
    }
}
