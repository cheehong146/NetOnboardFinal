package com.netonboard.netonboard.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;

/**
 * Created by Netonboard on 23/2/2018.
 */

public class LeaveFragment extends Fragment {
    Button btn_apply_leave;
    Spinner spn_leave_type;
    TextView tv_annual_balance, tv_no_late_history;

    private static final String TAG = "UnpaidLeaveHistory";
    ArrayList<UnpaidLeave> al_unpaid_leave;
    SortableTableView table_leave_history;
    UnpaidLeaveAdapter unpaidLeaveAdapter;

    ArrayList<AnnualLeave> al_annual_leave;
    AnnualLeaveAdapter annualLeaveAdapter;

    GlobalFileIO fileIO;

    int userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null)
            userId = bundle.getInt("userId");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leave, container, false);
        spn_leave_type = view.findViewById(R.id.spn_leave_type);
        table_leave_history = view.findViewById(R.id.leave_history);
        table_leave_history = view.findViewById(R.id.leave_history);
        tv_annual_balance = view.findViewById(R.id.tv_annual_balance);
        tv_no_late_history = view.findViewById(R.id.tv_no_late_history);
        getActivity().setTitle("Leave");
        loadLeaveType();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Leave");
    }

    public void loadLeaveType() {
        List<String> list = new ArrayList<String>();
        list.add("Annual Leave");
        list.add("Unpaid Leave");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_leave_type.setAdapter(dataAdapter);
        spn_leave_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {

                if (spn_leave_type.getSelectedItem().toString().equals("Annual Leave")) {
                    tv_no_late_history.setVisibility(View.GONE);
                    tv_annual_balance.setVisibility(View.GONE);
                    updateAnnualLeave();
                } else if (spn_leave_type.getSelectedItem().toString().equals("Unpaid Leave")) {
                    tv_no_late_history.setVisibility(View.GONE);
                    tv_annual_balance.setVisibility(View.GONE);
                    updateUnpaidLeave();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void updateAnnualLeave() {
        fileIO = new GlobalFileIO(getContext());
        al_annual_leave = new ArrayList<>();
        //tv_no_server_down_history = (TextView) findViewById(R.id.tv_error_history_no_server_down);
        if (table_leave_history != null) {
            table_leave_history.setColumnCount(2);
            TableColumnWeightModel weightModel = new TableColumnWeightModel(3);
            weightModel.setColumnWeight(0, 2);
            weightModel.setColumnWeight(1, 3);

            table_leave_history.setColumnModel(weightModel);
            String[] table_header = {"Date", "Reason"};
            table_leave_history.setHeaderAdapter(new SimpleTableHeaderAdapter(getContext(), table_header));
            annualLeaveAdapter = new AnnualLeaveAdapter(getContext(), al_annual_leave);
            table_leave_history.setDataAdapter(annualLeaveAdapter);
            table_leave_history.setColumnComparator(0, new AnnualLeaveApplyDateComparator());


            table_leave_history.setHeaderBackgroundColor(getResources().getColor(R.color.colorTextAreaBG));
//            table_history.setElevation(10);
            int colorEvenRow = getResources().getColor(R.color.colorTableRowEven);
            int colorOddRow = getResources().getColor(R.color.colorTableRowOdd);

            table_leave_history.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRow, colorOddRow));
        }
        fileIO = new GlobalFileIO(getContext());
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
                JSONObject listingJObject;
                try {
                    listingJObject = new JSONObject(body);
                    if (listingJObject.getString("listing").equals("false")) {
                        al_annual_leave.clear();
                        tv_no_late_history.setVisibility(View.VISIBLE);
                    } else {
                        al_annual_leave.clear();
                        JSONArray listingJArray = listingJObject.getJSONArray("listing");
                        tv_annual_balance.setVisibility(View.VISIBLE);
                        tv_annual_balance.setText(listingJObject.getString("balance") + " leave days left");
                        for (int i = 0; i < listingJArray.length(); i++) {
                            JSONObject jAnnualLeaveListObj = listingJArray.getJSONObject(i);
                            AnnualLeave leaveObj = new AnnualLeave(jAnnualLeaveListObj.getString("d_apply"), jAnnualLeaveListObj.getString("s_remark"));
                            al_annual_leave.add(leaveObj);
                        }
                        Collections.sort(al_annual_leave, new AnnualLeaveApplyDateComparator());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Collections.reverse(al_annual_leave);
                annualLeaveAdapter.notifyDataSetChanged();
                Log.i(TAG, "History Table Updated");
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void updateUnpaidLeave() {
        fileIO = new GlobalFileIO(getContext());
        al_unpaid_leave = new ArrayList<>();
        if (table_leave_history != null) {
            table_leave_history.setColumnCount(2);
            TableColumnWeightModel weightModel = new TableColumnWeightModel(3);
            weightModel.setColumnWeight(0, 2);
            weightModel.setColumnWeight(1, 3);

            table_leave_history.setColumnModel(weightModel);
            String[] table_header = {"Date", "Reason"};
            table_leave_history.setHeaderAdapter(new SimpleTableHeaderAdapter(getContext(), table_header));
            unpaidLeaveAdapter = new UnpaidLeaveAdapter(getContext(), al_unpaid_leave);
            table_leave_history.setDataAdapter(unpaidLeaveAdapter);
            table_leave_history.setColumnComparator(0, new UnpaidLeaveApplyDateComparator());


            table_leave_history.setHeaderBackgroundColor(getResources().getColor(R.color.colorTextAreaBG));
//            table_history.setElevation(10);
            int colorEvenRow = getResources().getColor(R.color.colorTableRowEven);
            int colorOddRow = getResources().getColor(R.color.colorTableRowOdd);

            table_leave_history.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRow, colorOddRow));
        } else {
            table_leave_history.setVisibility(View.INVISIBLE);
        }
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
                al_unpaid_leave.clear();
                if (body.equals("false") || body.equals("[]") || body.equals("")) {
                    Log.i(TAG, "No history");
                    tv_no_late_history.setVisibility(View.VISIBLE);
                } else {
                    try {
                        jArray = new JSONArray(body);
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jUnpaidLeaveListObj = jArray.getJSONObject(i);
                            UnpaidLeave leaveObj = new UnpaidLeave(jUnpaidLeaveListObj.getString("d_apply"), jUnpaidLeaveListObj.getString("s_remark"));
                            al_unpaid_leave.add(leaveObj);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Collections.reverse(al_unpaid_leave);
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

    private static class DateComparator implements Comparator<String> {
        @Override
        public int compare(String s, String t1) {
            return s.compareTo(t1);
        }
    }
}
