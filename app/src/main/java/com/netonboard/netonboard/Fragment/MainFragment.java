package com.netonboard.netonboard.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netonboard.netonboard.Object.GlobalFileIO;
import com.netonboard.netonboard.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MainFragment extends Fragment {
    GlobalFileIO fileIO;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);
        fileIO = new GlobalFileIO(getContext());

        loadSavedData(view);
        
        return view;
    }
    
    public void loadSavedData(View view){
        TextView tv_homepage_claim_data = view.findViewById(R.id.tv_homepage_claim_data);
        TextView tv_homepage_leave_data = view.findViewById(R.id.tv_homepage_leave_data);
        try{
            String body = fileIO.readFile(GlobalFileIO.FILENAMELEAVECALENDAR);
            JSONObject jsonObject = new JSONObject(body);
            String leave_balance = jsonObject.getString("leave_balance");
            String available_claim = jsonObject.getString("available_claim");
            tv_homepage_leave_data.setText(leave_balance);
            tv_homepage_claim_data.setText(available_claim);
        } catch (JSONException e) {
            e.printStackTrace();
            tv_homepage_leave_data.setText("Failed to load data");
            tv_homepage_claim_data.setText("Failed to load data");
        }
//            tv_homepage_claim_data.setText("RM300 claim");
//            tv_homepage_leave_data.setText("365 Days");

        TextView tv_support_primary = view.findViewById(R.id.tv_homepage_support_primary_data);
        TextView tv_support_secondary = view.findViewById(R.id.tv_homepage_support_secondary_data);


        try{
            String body = fileIO.readFile(GlobalFileIO.FILENAMESUPPORT);
            JSONObject jsonObject = new JSONObject(body);
            String primarySupport = jsonObject.getString("s_user_id_standby");
            String secondarySupport = jsonObject.getString("s_user_id_standby_backup");
            tv_support_primary.setText(primarySupport);
            tv_support_secondary.setText(secondarySupport);
        } catch (JSONException e) {
            e.printStackTrace();
            tv_support_primary.setText("Failed to load data");
            tv_support_secondary.setText("Failed to load data");
        }

        TextView tv_garbage_collector_date = view.findViewById(R.id.tv_garbage_collector_date);
        TextView tv_garbage_collector = view.findViewById(R.id.tv_garbage_collector_name);

        try{
            String body = fileIO.readFile(GlobalFileIO.FILENAMEGARBAGECOLLECTOR);
            JSONObject jsonObject = new JSONObject(body);
            String d_duty = jsonObject.getString("d_duty");
            String s_user_duty = jsonObject.getString("s_user_duty");
            tv_garbage_collector_date.setText(d_duty);
            tv_garbage_collector.setText(s_user_duty);
        } catch (JSONException e) {
            e.printStackTrace();
            tv_garbage_collector_date.setText("Failed to load data");
            tv_garbage_collector.setText("Failed to load data");
        }
    }
}
