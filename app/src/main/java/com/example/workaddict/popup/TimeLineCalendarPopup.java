package com.example.workaddict.popup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.workaddict.BottomNavi;
import com.example.workaddict.DataClass.TimeLine;
import com.example.workaddict.TimeLineClass.EventDecorator;
import com.example.workaddict.TimeLineClass.OneDayDecorator;
import com.example.workaddict.R;
import com.example.workaddict.TimeLineClass.SaturdayDecorator;
import com.example.workaddict.TimeLineClass.SundayDecorator;
import com.example.workaddict.BottomFragment.TimeLinePage;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class TimeLineCalendarPopup extends Dialog {
    private static final String TAG = "OptionMenuMove";
    private View.OnClickListener positiveListener;
    private View.OnClickListener negativeListener;
    private Button btn_confirm;
    private Button btn_cancel;
    public static TimeLineCalendarPopup timeLineCalendarPopup;
    public MaterialCalendarView materialCalendarView;
    private ArrayList<TimeLine> timeLines = new ArrayList<TimeLine>();
    private String selectedDate = "";

    @Override
    public void onBackPressed() {
        if (timeLineCalendarPopup.isShowing()) {
            timeLineCalendarPopup.cancel();
        }
    }

    public TimeLineCalendarPopup(@NonNull Context context, ArrayList<TimeLine> timeLines, View.OnClickListener positiveListener, View.OnClickListener negativeListener) {
        super(context);
        this.timeLines = timeLines;
        this.positiveListener = positiveListener;
        this.negativeListener = negativeListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line_calendar_popup);
        timeLineCalendarPopup = this;

        initView();
        setUpListener();

    }


    @SuppressLint("SetTextI18n")
    private void initView() {
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_confirm = findViewById(R.id.btn_confirm);

        materialCalendarView = findViewById(R.id.calendarView);
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorator(new SundayDecorator());
        materialCalendarView.addDecorator(new SaturdayDecorator());
        materialCalendarView.addDecorator(new OneDayDecorator());

        ArrayList<String> strDateList = new ArrayList<String>();
        if (timeLines.size() > 0) {

            for (int i = 0; i < timeLines.size(); i++) {
                String str = timeLines.get(i).getDate().substring(0, 10);
//                Log.e(TAG, "initView: str = " + str );
                if (!strDateList.contains(str)) {
                    strDateList.add(str);
                }
            }
        }
        new ApiSimulator(strDateList).executeOnExecutor(Executors.newSingleThreadExecutor());

    }


    private void setUpListener() {


        btn_cancel.setOnClickListener(negativeListener);
        btn_confirm.setOnClickListener(positiveListener);


        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {

            int size = (date.toString().length() - 1);
            String str = date.toString().substring(12, size);
            String[] filtering = str.split("-");
            String year = filtering[0];
            String month = filtering[1];
            int numberMonth = Integer.parseInt(month);
            numberMonth = numberMonth + 1;
            month = String.valueOf(numberMonth);
            String day = filtering[2];

            if (month.length() == 1) month = "0" + month;
            if (day.length() == 1) day = "0" + day;
            selectedDate = year + "-" + month + "-" + day;

        });
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (BottomNavi.bottomNavi.b.viewPager.getCurrentItem() == 2) {
            TimeLinePage.singlton.toolbar_search.setVisibility(View.VISIBLE);
        }

    }
}

class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

    private static final String TAG = "ApiSimulator";
    ArrayList<String> strDateList;

    ApiSimulator(ArrayList<String> strDateList) {
        this.strDateList = strDateList;
    }

    @Override
    protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        ArrayList<CalendarDay> dates = new ArrayList<>();


        // 오늘날짜에 점 빼기

        /*특정날짜 달력에 점표시해주는곳*/
        /*월은 0이 1월 년,일은 그대로*/
        //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
        for (int i = 0; i < strDateList.size(); i++) {
            String str = strDateList.get(i);
//            Log.e(TAG, "doInBackground: " + str );
            String[] time = str.split("-");
            int year = Integer.parseInt(time[0]);
            int month = Integer.parseInt(time[1]);
            int dayy = Integer.parseInt(time[2]);

//            dates.add(day);
            calendar.set(year, month - 1, dayy);
            CalendarDay day = CalendarDay.from(calendar);
            dates.add(day);
        }

        return dates;
    }

    @Override
    protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
        super.onPostExecute(calendarDays);

//        if (isFinishing()) {
//            return;
//        }

        TimeLineCalendarPopup.timeLineCalendarPopup.materialCalendarView
                .addDecorator(new EventDecorator(Color.MAGENTA, calendarDays));
    }
}