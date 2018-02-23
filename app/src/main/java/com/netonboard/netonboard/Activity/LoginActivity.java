package com.netonboard.netonboard.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.netonboard.netonboard.Object.GlobalFileIO;
import com.netonboard.netonboard.R;
import com.securepreferences.SecurePreferences;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    GlobalFileIO globalFile;

    EditText tf_passcode;
    Spinner spn_username;
    String s_user_id;
    TextView tv_name;
    Button btn_login;

    String userType = "support";
    String s_all_name;
    String[] arr_uid, arr_user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = new SecurePreferences(this, "netdeveloper", "loginInfo.xml");
        editor = sp.edit();

        spn_username = findViewById(R.id.spinner);
        tv_name = findViewById(R.id.tv_login_info);
        tf_passcode = findViewById(R.id.getPasscode);

        loadNameList();

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_click();
            }
        });
    }

    public void loadNameList() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://cloudsub04.trio-mobile.com/curl/mobile/user/get_user.php"
                , new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        s_all_name = new String(responseBody);
                        JSONArray jobject = null;
                        try {
                            jobject = new JSONArray(s_all_name);
                            arr_user_name = new String[jobject.length()];
                            arr_uid = new String[jobject.length()];
                            for (int x = 0; x < jobject.length(); x++) {
                                arr_user_name[x] = jobject.getJSONObject(x).getString("s_first_name");
                                arr_uid[x] = jobject.getJSONObject(x).getString("user_id");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginActivity.this, android.R.layout.simple_list_item_activated_1, arr_user_name);
                        spn_username.setAdapter(adapter);
                        spn_username.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                                s_user_id = arr_uid[i];
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        //TODO FAILURE TO LOAD NAME LIST
                    }
                });
    }

    public void login_click() {

        final String userPassCode = tf_passcode.getText().toString();
        final String userName = spn_username.getSelectedItem().toString();
        if (TextUtils.isEmpty(userPassCode)) {
            Toast.makeText(getApplicationContext(), "Please Key In Your Passcode!!", Toast.LENGTH_SHORT).show();
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get("http://cloudsub04.trio-mobile.com/curl/mobile/user/login.php?id=" + s_user_id + "&p=" + userPassCode
                    , new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (new String(responseBody).equals("success")) {
                                Toast.makeText(getApplicationContext(), "Successful login!", Toast.LENGTH_SHORT).show();
                                editor.putString("username", userName);
                                editor.putString("userId", s_user_id);
                                editor.putString("userType", userType);
                                editor.putString("password", userPassCode);
                                if (editor.commit()) {
                                    Intent i = new Intent(LoginActivity.this, PinActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toast.makeText(getBaseContext(), "Ops something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Wrong Passcode", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(getApplicationContext(), "Fail To Connect!!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
