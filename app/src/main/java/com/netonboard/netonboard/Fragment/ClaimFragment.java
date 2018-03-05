package com.netonboard.netonboard.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.netonboard.netonboard.Adapter.ClaimAdapter;
import com.netonboard.netonboard.Object.Claim;
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
 * Created by Netonboard on 23/2/2018.
 */

public class ClaimFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "ClaimFragment";
    SortableTableView table_claim;
    ArrayList<Claim> al_claim;
    ClaimAdapter claimAdapter;

    TextView tv_no_claim_history;

    int userId, year;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        Calendar currentDate = Calendar.getInstance();
        al_claim = new ArrayList<>();
        year = currentDate.get(Calendar.YEAR);
        if (bundle != null)
            userId = bundle.getInt("userId");
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Claim");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_claim, container, false);
        tv_no_claim_history = (TextView) view.findViewById(R.id.tv_no_claim_history);
        table_claim = (SortableTableView) view.findViewById(R.id.claim_history);
        getActivity().setTitle("Claim");
        loadClaimData();
        return view;
    }

    public void loadClaimData() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://cloudsub04.trio-mobile.com/curl/mobile/hr_info/claim_history.php?id=" + userId + "&y=" + year, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String body = new String(responseBody);
                updateClaim(body);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateClaim(String body) {
        //tv_no_server_down_history = (TextView) findViewById(R.id.tv_error_history_no_server_down);
        if (table_claim != null) {
            table_claim.setColumnCount(3);
            TableColumnWeightModel weightModel = new TableColumnWeightModel(3);
            weightModel.setColumnWeight(0, 3);
            weightModel.setColumnWeight(1, 5);
            weightModel.setColumnWeight(2, 3);

            table_claim.setColumnModel(weightModel);
            String[] table_header = {"Date", "Remark", "Total"};
            table_claim.setHeaderAdapter(new SimpleTableHeaderAdapter(getContext(), table_header));
            table_claim.setColumnComparator(0, new ClaimDateComparator());
            table_claim.setHeaderBackgroundColor(getResources().getColor(R.color.colorTextAreaBG));
            int colorEvenRow = getResources().getColor(R.color.colorTableRowEven);
            int colorOddRow = getResources().getColor(R.color.colorTableRowOdd);
            table_claim.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRow, colorOddRow));

            claimAdapter = new ClaimAdapter(getContext(), al_claim);
            table_claim.setDataAdapter(claimAdapter);
        }
        loadClaim(body);
    }

    public void loadClaim(String body) {
        if (body.equals("false")) {
            tv_no_claim_history.setVisibility(View.VISIBLE);
        } else {
            try {
                JSONArray jsonArray = new JSONArray(body);
                tv_no_claim_history.setVisibility(View.INVISIBLE);
                al_claim.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    al_claim.add(new Claim(jsonObject.getString("d_submit"), jsonObject.getString("s_particulars"), jsonObject.getString("f_total")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Collections.reverse(al_claim);
            claimAdapter.notifyDataSetChanged();
        }
    }


    private static class ClaimDateComparator implements Comparator<Claim> {
        @Override
        public int compare(Claim claim, Claim t1) {
            return claim.getD_submit().compareTo(t1.getD_submit());
        }
    }
}
