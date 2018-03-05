package com.netonboard.netonboard.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.netonboard.netonboard.Adapter.ClaimAdapter;
import com.netonboard.netonboard.Adapter.LateAdapter;
import com.netonboard.netonboard.Object.Claim;
import com.netonboard.netonboard.Object.Late;
import com.netonboard.netonboard.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import cz.msebera.android.httpclient.Header;
import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;

/**
 * Created by Netonboard on 26/2/2018.
 */

public class LateFragment extends Fragment {
    int userId;
    SortableTableView table_late;
    TextView tv_no_late_history, tv_tot_late_minute;

    ArrayList<Late> al_late;
    LateAdapter lateAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null)
            userId = bundle.getInt("userId");

        al_late = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_late, container, false);
        tv_tot_late_minute = (TextView) view.findViewById(R.id.tv_tot_late_minute);
        tv_no_late_history = (TextView) view.findViewById(R.id.tv_no_late_history);
        table_late = (SortableTableView) view.findViewById(R.id.late_history);
        getActivity().setTitle("Late");
        loadLateData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Late");
    }

    public void loadLateData() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://cloudsub04.trio-mobile.com/curl/mobile/hr_info/late_history.php?id=" + userId, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String body = new String(responseBody);
                updateLate(body);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateLate(String body) {
        if (table_late != null) {
            table_late.setColumnCount(3);
            TableColumnWeightModel weightModel = new TableColumnWeightModel(2);
            weightModel.setColumnWeight(0, 3);
            weightModel.setColumnWeight(1, 5);

            table_late.setColumnModel(weightModel);
            String[] table_header = {"Date", "Minute"};
            table_late.setHeaderAdapter(new SimpleTableHeaderAdapter(getContext(), table_header));
            table_late.setColumnComparator(0, new LateDateComparator());
            table_late.setColumnComparator(1, new LateMinuteComparator());

            table_late.setHeaderBackgroundColor(getResources().getColor(R.color.colorTextAreaBG));
            int colorEvenRow = getResources().getColor(R.color.colorTableRowEven);
            int colorOddRow = getResources().getColor(R.color.colorTableRowOdd);
            table_late.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRow, colorOddRow));
            lateAdapter = new LateAdapter(getContext(), al_late);
            table_late.setDataAdapter(lateAdapter);
        }
        loadClaim(body);
    }

    public void loadClaim(String body) {
        JSONArray jArray;
        if (body.equals("[]") || body.equals("") || body.equals("false")) {
            tv_no_late_history.setVisibility(View.VISIBLE);
        } else {
            try {
                tv_no_late_history.setVisibility(View.INVISIBLE);
                al_late.clear();
                jArray = new JSONArray(body);
                int tot_late = 0;
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jLateObj = jArray.getJSONObject(i);
                    Late obj = new Late(jLateObj.getString("d_attend"), jLateObj.getString("i_total_late"));
                    al_late.add(obj);
                    tot_late += jLateObj.getInt("i_total_late");
                }

                tv_tot_late_minute.setVisibility(View.VISIBLE);
                tv_tot_late_minute.setText("Total: " + tot_late + " minutes late");
                Collections.reverse(al_late);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            lateAdapter.notifyDataSetChanged();
        }
    }

    private static class LateDateComparator implements Comparator<Late> {
        @Override
        public int compare(Late late, Late t1) {
            return late.getDate().compareTo(t1.getDate());
        }
    }

    private static class LateMinuteComparator implements Comparator<Late> {
        @Override
        public int compare(Late late, Late t1) {
            return late.getMinute().compareTo(t1.getMinute());
        }
    }
}
