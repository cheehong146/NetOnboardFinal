package com.netonboard.netonboard.Fragment;


import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.netonboard.netonboard.Object.GlobalFileIO;
import com.netonboard.netonboard.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import cz.msebera.android.httpclient.Header;

public class CalendarFragment extends Fragment {
    private static final String TAG = "CalendarFragment";
    TextView tv_holiday, tv_festival, tv_onleave, tv_onleavedata;
    Calendar currentDate;

    MaterialCalendarView calendarView;
    GlobalFileIO fileIO;

    HashMap<String, ArrayList<String>> hm_leaveData;

    HashMap<CalendarDay, String> hm_calendarDate, hm_calendar_leave;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        tv_holiday = view.findViewById(R.id.tv_holiday);
        tv_festival = view.findViewById(R.id.tv_festival);
        tv_onleave = view.findViewById(R.id.tv_onleave);
        tv_onleavedata = view.findViewById(R.id.tv_onleave_data);
        calendarView = view.findViewById(R.id.calendar_view);

        hm_leaveData = new HashMap<>();
        fileIO = new GlobalFileIO(getContext());
        hm_calendarDate = new HashMap<>();
        hm_calendar_leave = new HashMap<>();

        loadData();

        return view;
    }

    public void loadData() {
        currentDate = Calendar.getInstance();
        calendarView.state().edit()
                .setMinimumDate(CalendarDay.from(currentDate.get(Calendar.YEAR), 0, 1))
                .setMaximumDate(CalendarDay.from(currentDate.get(Calendar.YEAR), 11, 31))
                .commit();


        if (isOnline(getContext())) {
            loadHoliday(currentDate.get(Calendar.YEAR));
            loadLeave(currentDate.get(Calendar.YEAR));
        } else {
            Toast.makeText(getContext(), "No connection, loading previous data", Toast.LENGTH_SHORT);
        }

        final SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        calendarView.setWeekDayTextAppearance(Color.BLUE);
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                CalendarDay selectedDate = calendarView.getSelectedDate();
                for (CalendarDay obj :
                        hm_calendarDate.keySet()) {
                    if (selectedDate.equals(obj)) {
                        tv_festival.setVisibility(View.VISIBLE);
                        tv_holiday.append(hm_calendarDate.get(obj));
                        break;
                    } else {
                        tv_festival.setVisibility(View.INVISIBLE);
                        tv_holiday.setText("");
                    }
                }
                for (CalendarDay obj :
                        hm_calendar_leave.keySet()) {
                    if (selectedDate.equals(obj)) {
                        tv_onleave.setVisibility(View.VISIBLE);
                        ArrayList<String> arrayList = hm_leaveData.get(serverDateFormat.format(obj.getDate()));
                        for (int i = 0; i < arrayList.size(); i++) {
                            tv_onleavedata.append(i + 1 + " - " + arrayList.get(i) + "");
                        }
                        break;
                    } else {
                        tv_onleave.setVisibility(View.INVISIBLE);
                        tv_onleavedata.setText("");
                    }
                }
            }
        });
    }

    public void loadHoliday(int year) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://cloudsub04.trio-mobile.com/curl/mobile/calendar/company_holiday.php?y=" + year, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String body = new String(responseBody);
                fileIO.writeToFile(GlobalFileIO.FILENAMECALENDAR, body);
                updateHolidayCalendar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void loadLeave(int year) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://cloudsub04.trio-mobile.com/curl/mobile/calendar/leave_applied.php?y=" + year, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String body = new String(responseBody);
                fileIO.writeToFile(GlobalFileIO.FILENAMELEAVECALENDAR, body);
                updateLeaveCalendar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void updateHolidayCalendar() {
        File file = new File(getContext().getFilesDir(), GlobalFileIO.FILENAMECALENDAR);
        if (file.exists()) {
            try {
                String body = fileIO.readFile(GlobalFileIO.FILENAMECALENDAR);
                JSONObject jsonBody = new JSONObject(body);
                JSONArray response = new JSONArray(jsonBody.getString("response"));
                hm_calendarDate.clear();
                SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject = response.getJSONObject(i);
                    Date date = serverDateFormat.parse(jsonObject.getString("d_holiday"));
                    hm_calendarDate.put(new CalendarDay(date), jsonObject.getString("s_remark"));

                }
                calendarView.addDecorator(new EventDecorator(Color.GREEN, hm_calendarDate.keySet()));

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "Calendar file doesn't exist", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateLeaveCalendar() {
        File file = new File(getContext().getFilesDir(), GlobalFileIO.FILENAMELEAVECALENDAR);
        if (file.exists()) {
            try {
                String body = fileIO.readFile(GlobalFileIO.FILENAMELEAVECALENDAR);
                if (!body.equals("")) {
                    JSONArray jsonArray = new JSONArray(body);
                    SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject leaveObj = jsonArray.getJSONObject(i);
                        String date = leaveObj.getString("d_apply");
                        String remark = leaveObj.getString("s_remark");

                        if (hm_leaveData.containsKey(date)) {
                            hm_leaveData.get(date).add(remark);
                        } else {
                            hm_leaveData.put(date, new ArrayList<String>());
                            hm_leaveData.get(date).add(remark);
                        }
                    }

                    for (String date
                            : hm_leaveData.keySet()) {
                        Date dateFormat = (serverDateFormat.parse(date));
                        hm_calendar_leave.put(new CalendarDay(dateFormat), date);
                    }
                    calendarView.addDecorator(new EventDecorator(Color.RED, hm_calendar_leave.keySet()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "Calendar file doesn't exist", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    public class EventDecorator implements DayViewDecorator {

        private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(15, color));
        }
    }

}
