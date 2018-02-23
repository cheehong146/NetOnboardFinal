package com.netonboard.netonboard.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.netonboard.netonboard.Adapter.AnnualLeaveAdapter;
import com.netonboard.netonboard.Adapter.UnpaidLeaveAdapter;
import com.netonboard.netonboard.Object.AnnualLeave;
import com.netonboard.netonboard.Object.GlobalFileIO;
import com.netonboard.netonboard.Object.UnpaidLeave;
import com.netonboard.netonboard.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;

public class LeaveActivity extends AppCompatActivity {

    Button btn_apply_leave;
    Spinner spn_leave_type;

    private static final String TAG = "UnpaidLeaveHistory";
    ArrayList<UnpaidLeave> al_unpaid_leave;
    SortableTableView table_leave_history;
    UnpaidLeaveAdapter unpaidLeaveAdapter;

    ArrayList<AnnualLeave> al_annual_leave;
    AnnualLeaveAdapter annualLeaveAdapter;

    GlobalFileIO fileIO;
    Handler handlerLeave;
    Runnable runnableLeave;

    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_leave);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Leave");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spn_leave_type = findViewById(R.id.spn_leave_type);
        loadLeaveType();
    }

    public void loadLeaveType() {
        List<String> list = new ArrayList<String>();
        list.add("Annual Leave");
        list.add("Unpaid Leave");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_leave_type.setAdapter(dataAdapter);
        spn_leave_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {

                if (spn_leave_type.getSelectedItem().toString().equals("Annual Leave")) {

                    updateAnnualLeave();
                } else if (spn_leave_type.getSelectedItem().toString().equals("Unpaid Leave")) {

                    updateUnpaidLeave();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void updateAnnualLeave() {
        fileIO = new GlobalFileIO(getApplicationContext());
        al_annual_leave = new ArrayList<>();
        //tv_no_server_down_history = (TextView) findViewById(R.id.tv_error_history_no_server_down);
        table_leave_history = findViewById(R.id.leave_history);
        if (table_leave_history != null) {
            table_leave_history.setColumnCount(2);
            TableColumnWeightModel weightModel = new TableColumnWeightModel(3);
            weightModel.setColumnWeight(0, 2);
            weightModel.setColumnWeight(1, 3);

            table_leave_history.setColumnModel(weightModel);
            String[] table_header = {"Date", "Reason"};
            table_leave_history.setHeaderAdapter(new SimpleTableHeaderAdapter(getApplicationContext(), table_header));
            annualLeaveAdapter = new AnnualLeaveAdapter(getApplicationContext(), al_annual_leave);
            table_leave_history.setDataAdapter(annualLeaveAdapter);
            table_leave_history.setColumnComparator(0, new LeaveActivity.AnnualLeaveApplyDateComparator());


            table_leave_history.setHeaderBackgroundColor(getResources().getColor(R.color.colorTextAreaBG));
//            table_history.setElevation(10);
            int colorEvenRow = getResources().getColor(R.color.colorTableRowEven);
            int colorOddRow = getResources().getColor(R.color.colorTableRowOdd);

            table_leave_history.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRow, colorOddRow));
        }
        fileIO = new GlobalFileIO(getBaseContext());
        loadAnnualLeave();
    }

    public void loadAnnualLeave() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("id", userId);
        client.get("http://cloudsub04.trio-mobile.com/curl/mobile/hr_info/annual_leave_history.php", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String body = new String(responseBody);
                JSONArray jArray;
                if (body.equals("[]") || body.equals("")) {
                    Log.i(TAG, "No history");
                    al_annual_leave.clear();
                } else {
                    try {
                        al_annual_leave.clear();

                        jArray = new JSONArray(body);
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jAnnualLeaveListObj = jArray.getJSONObject(i);
                            AnnualLeave leaveObj = new AnnualLeave(jAnnualLeaveListObj.getString("d_apply"), jAnnualLeaveListObj.getString("s_remark"));
                            al_annual_leave.add(leaveObj);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    annualLeaveAdapter.notifyDataSetChanged();
                    Log.i(TAG, "History Table Updated");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void updateUnpaidLeave() {
        fileIO = new GlobalFileIO(getApplicationContext());
        al_unpaid_leave = new ArrayList<>();
        table_leave_history = findViewById(R.id.leave_history);
        if (table_leave_history != null) {
            table_leave_history.setColumnCount(2);
            TableColumnWeightModel weightModel = new TableColumnWeightModel(3);
            weightModel.setColumnWeight(0, 2);
            weightModel.setColumnWeight(1, 3);

            table_leave_history.setColumnModel(weightModel);
            String[] table_header = {"Date", "Reason"};
            table_leave_history.setHeaderAdapter(new SimpleTableHeaderAdapter(getApplicationContext(), table_header));
            unpaidLeaveAdapter = new UnpaidLeaveAdapter(getApplicationContext(), al_unpaid_leave);
            table_leave_history.setDataAdapter(unpaidLeaveAdapter);
            table_leave_history.setColumnComparator(0, new LeaveActivity.UnpaidLeaveApplyDateComparator());


            table_leave_history.setHeaderBackgroundColor(getResources().getColor(R.color.colorTextAreaBG));
//            table_history.setElevation(10);
            int colorEvenRow = getResources().getColor(R.color.colorTableRowEven);
            int colorOddRow = getResources().getColor(R.color.colorTableRowOdd);

            table_leave_history.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRow, colorOddRow));
        } else {
            table_leave_history.setVisibility(View.INVISIBLE);
        }
        fileIO = new GlobalFileIO(getBaseContext());
        loadUnpaidLeave();
    }

    public void loadUnpaidLeave() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("id", userId);
        client.get("http://cloudsub04.trio-mobile.com/curl/mobile/hr_info/unpaid_leave_history.php", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String body = new String(responseBody);
                JSONArray jArray;
                if (body.equals("[]") || body.equals("")) {
                    Log.i(TAG, "No history");
                    //table_leave_history.setEmptyDataIndicatorView(tv_no_server_down_history);
                    al_unpaid_leave.clear();
                } else {
                    try {
                        al_unpaid_leave.clear();

                        jArray = new JSONArray(body);
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jUnpaidLeaveListObj = jArray.getJSONObject(i);
                            UnpaidLeave leaveObj = new UnpaidLeave(jUnpaidLeaveListObj.getString("d_apply"), jUnpaidLeaveListObj.getString("s_remark"));
                            al_unpaid_leave.add(leaveObj);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    unpaidLeaveAdapter.notifyDataSetChanged();
                    Log.i(TAG, "History Table Updated");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


    private static class UnpaidLeaveApplyDateComparator implements Comparator<UnpaidLeave> {
        @Override
        public int compare(UnpaidLeave unpaidLeave, UnpaidLeave t1) {
            return unpaidLeave.getS_date_apply().compareTo(t1.getS_date_apply());
        }
    }

    private static class AnnualLeaveApplyDateComparator implements Comparator<AnnualLeave> {
        @Override
        public int compare(AnnualLeave annualLeave, AnnualLeave t1) {
            return annualLeave.getS_date_apply().compareTo(t1.getS_date_apply());
        }
    }
}
