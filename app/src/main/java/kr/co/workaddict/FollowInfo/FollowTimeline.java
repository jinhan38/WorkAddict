package kr.co.workaddict.FollowInfo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import kr.co.workaddict.DataClass.CategoryData;
import kr.co.workaddict.DataClass.PlaceData;
import kr.co.workaddict.DataClass.TimeLine;

import kr.co.workaddict.R;
import kr.co.workaddict.TimeLineClass.TimelineExcelExport;
import kr.co.workaddict.TimeLineClass.TimeLineAdapter;
import kr.co.workaddict.Utility.Util;
import kr.co.workaddict.databinding.ActivityFollowTimelineBinding;
import kr.co.workaddict.popup.TimeLineCalendarPopup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class FollowTimeline extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "FollowTimeline";
    public ActivityFollowTimelineBinding b;
    private ArrayList<TimeLine> timeLines = new ArrayList<TimeLine>();
    public ArrayList<CategoryData> categoryData = new ArrayList<CategoryData>(); // 카테고리명만 있는 경우
    public ArrayList<PlaceData> placeData = new ArrayList<PlaceData>();
    public ArrayList<TimeLine> timeLinesFilter = new ArrayList<TimeLine>();
    public ArrayList<TimeLine> timeLinesSearch = new ArrayList<TimeLine>();
    public String ID;
    private TimeLineAdapter adapter;
    public static FollowTimeline followTimeline;
    private int categorySpinnerPosition = 0;
    public String filterSelectedDate = "";
    private TimeLineCalendarPopup timeLineCalendarPopup;
    private boolean isOpenDateFilter = false;
    private RequestManager glide;
    private final int TIMELINE_FILTER_ALL = 1;
    private final int TIMELINE_FILTER_ACTION_N = 2;
    private final int TIMELINE_FILTER_ACTION_Y = 3;
    private int current_timeline_filter_num = 0;
    private boolean isOpenSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_follow_timeline);
        followTimeline = this;
        b.followTimelineProgressBar.setVisibility(View.VISIBLE);
        b.followTimelineRecyclerView.setVisibility(View.GONE);
        initView();
        getFirebaseData();
        setupListener();

    }


    private void initView() {

        Intent intent = getIntent();
        ID = intent.getExtras().getString("ID");

        b.followTimelineToolbar.setTitle(ID);
        b.followTimelineToolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(b.followTimelineToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        glide = Glide.with(followTimeline);
        current_timeline_filter_num = TIMELINE_FILTER_ALL;

        if (timeLines.size() > 0) {
            b.timelineShare.setVisibility(View.VISIBLE);
            b.timelineActionFilter.setVisibility(View.VISIBLE);
        }

    }


    private void setupListener() {
        spinnerSelectListener();

        b.timelineActionFilter.setOnClickListener(this);
        b.timelineShare.setOnClickListener(this);
        b.followTimelineRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (!adapter.createAdapter) {

                    if (dy > 0) { // 아래로 스크롤하면 +
                        b.timelineActionFilter.hide();
                        b.timelineShare.hide();
                    } else { //위로 스크롤하면 -
                        b.timelineActionFilter.show();
                        b.timelineShare.show();
                    }


                } else {

                    b.timelineActionFilter.show();
                    b.timelineShare.show();
                    adapter.createAdapter = false;
                }


                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }


    private void spinnerSelectListener() {


        b.searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0) {
                    timeLinesFilter.clear();
                    timeLinesSearch.clear();
                    b.closeButton.setVisibility(View.VISIBLE);
                    for (int i = 0; i < timeLines.size(); i++) {
                        if (timeLines.get(i).getPlaceName().contains(s) || timeLines.get(i).getSomeThing().contains(s)) {
                            timeLinesSearch.add(timeLines.get(i));
                        }
                    }

                    Log.e(TAG, "onTextChanged: timelinesearch : " + timeLinesSearch.size());
                    Log.e(TAG, "onTextChanged: keyword : " + s);
                    setAdapter(timeLinesSearch, String.valueOf(s));

                } else {

                    b.closeButton.setVisibility(View.GONE);
                    setRecyclerView();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        b.closeButton.setOnClickListener(v -> b.searchText.setText(""));


        b.searchText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Util.hideKeyboard(followTimeline);
            } else {
                return false;
            }
            return true;
        });


        b.followSpinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ArrayList<PlaceData> pd = new ArrayList<PlaceData>();
                pd.clear();

                if (position == 0) {
                    setRecyclerView();
                    categorySpinnerPosition = 0;
                    Util.setPlaceTimeLineSpinner(followTimeline, b.followSpinnerPlace, placeData);
                } else {
                    categorySpinnerPosition = 1;
                    String categoryName = "";
                    categoryName = b.followSpinnerCategory.getSelectedItem().toString();

                    for (int i = 0; i < placeData.size(); i++) {
                        if (placeData.get(i).getCategoryName().equals(categoryName)) {
                            pd.add(placeData.get(i));
                        }
                    }

                    for (int i = 0; i < timeLines.size(); i++) {
                        if (timeLines.get(i).getCategoryName().equals(categoryName)) {
                            timeLinesFilter.add(timeLines.get(i));
                        }
                    }

                    setFilteredRecyclerView(timeLinesFilter);
                }

                Util.setPlaceTimeLineSpinner(followTimeline, b.followSpinnerPlace, pd);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        b.followSpinnerPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeLinesFilter.clear();

                if (timeLines.size() > 0) {
                    if (position == 0 && categorySpinnerPosition == 1) {

                        for (int i = 0; i < timeLines.size(); i++) {
                            if (timeLines.get(i).getCategoryName().equals(b.followSpinnerCategory.getSelectedItem().toString())) {
                                timeLinesFilter.add(timeLines.get(i));
                            }
                        }

                        setFilteredRecyclerView(timeLinesFilter);
                    } else if (position != 0) {
                        for (int i = 0; i < timeLines.size(); i++) {
                            if (timeLines.get(i).getPlaceName().equals(b.followSpinnerPlace.getSelectedItem().toString().trim())) {
                                timeLinesFilter.add(timeLines.get(i));
                            }
                        }
                        setFilteredRecyclerView(timeLinesFilter);
                    }


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void getFirebaseData() {
        DatabaseReference myRef = Util.getFirebaseDatabase().getReference("users").child(ID.replaceAll("\\.", ""));


        myRef.child("CategoryData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categoryData.clear();
                CategoryData cat = null;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        cat = dataSnapshot1.getValue(CategoryData.class);
                        if (cat != null) {
                            categoryData.add(cat);
                        }
                    }

                    if (categoryData.size() > 0) {
                        Util.setCategoryTimeLineSpinner(followTimeline, b.followSpinnerCategory, categoryData);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });


        myRef.child("PlaceData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                placeData.clear();
                PlaceData pd = null;

                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        pd = dataSnapshot1.getValue(PlaceData.class);
                        if (pd != null) {
                            placeData.add(pd);
                        }
                    }
                    if (placeData.size() > 0) {
                        Util.setPlaceTimeLineSpinner(followTimeline, b.followSpinnerPlace, placeData);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        myRef.child("TimeLine").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                timeLines.clear();
                TimeLine tl = null;

                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        tl = dataSnapshot1.getValue(TimeLine.class);
                        if (tl != null) {
                            timeLines.add(tl);

                        }
                    }
                }

                if (timeLines.size() > 0) setRecyclerView();

                b.followTimelineProgressBar.setVisibility(View.GONE);
                b.followTimelineRecyclerView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                b.followTimelineProgressBar.setVisibility(View.GONE);
                b.followTimelineRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }


    public void setRecyclerView() {
        Log.e(TAG, "setRecyclerView: 타임라인 사이즈 확인 : " + timeLines.size());
        if (b.followTimelineRecyclerView != null && timeLines.size() > 0) {


            b.followTimelineRecyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setStackFromEnd(true);
            b.followTimelineRecyclerView.setLayoutManager(linearLayoutManager);
            adapter = new TimeLineAdapter(timeLines, categoryData, "y",
                    b.followTimelineRecyclerView, followTimeline);
            b.followTimelineRecyclerView.setAdapter(adapter);
        }
    }


    public void setFilteredRecyclerView(ArrayList<TimeLine> filteredTimeLines) {

        if (filteredTimeLines.size() > 0) {

            b.followTimelineRecyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setStackFromEnd(true);
            b.followTimelineRecyclerView.setLayoutManager(linearLayoutManager);
            adapter = new TimeLineAdapter(filteredTimeLines, categoryData, "y",
                    b.followTimelineRecyclerView, followTimeline);
            b.followTimelineRecyclerView.setAdapter(adapter);
            b.followTimelineProgressBar.setVisibility(View.GONE);
            b.followTimelineRecyclerView.setVisibility(View.VISIBLE);

        } else {
            b.followTimelineRecyclerView.setAdapter(null);
        }
    }


    /**
     * 키워드 검색 setAdapter
     *
     * @param timeLines
     * @param keyword
     */
    public void setAdapter(ArrayList<TimeLine> timeLines, String keyword) {
        if (timeLines != null) {

            Log.e(TAG, "setAdapter: 키워드 timelines : " + timeLines.size() );
            if (timeLines.size() > 0) {
                b.followTimelineRecyclerView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(followTimeline);
                linearLayoutManager.setStackFromEnd(true);
                b.followTimelineRecyclerView.setLayoutManager(linearLayoutManager);

                adapter = new TimeLineAdapter(b.followTimelineRecyclerView, timeLines, categoryData, false, keyword);
                b.followTimelineRecyclerView.setAdapter(adapter);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.follow_timeline_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        b.searchWrap.setVisibility(View.GONE);
        b.spinnerWrap.setVisibility(View.VISIBLE);

        switch (item.getItemId()) {
            case android.R.id.home:  // 왼쪽 상단 버튼 눌렀을 때
                onBackPressed();
                break;
            case R.id.follow_timeline_search:
                isOpenSearch = true;
                b.searchWrap.setVisibility(View.VISIBLE);
                b.spinnerWrap.setVisibility(View.GONE);
                b.searchText.requestFocus();
                Util.showKeyboard(this);
                break;
            case R.id.follow_timeline_date:
                showPopup();
                break;
            default:
                break;
        }


        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.timelineActionFilter:
                Log.e(TAG, "onClick: 클릭");
                switch (current_timeline_filter_num) {
                    case 1:
                        current_timeline_filter_num = TIMELINE_FILTER_ACTION_N;
                        b.timelineActionFilter.setImageResource(R.drawable.timeline_filter_purple);
                        timelineActionFilter("n");

                        break;
                    case 2:
                        current_timeline_filter_num = TIMELINE_FILTER_ACTION_Y;
                        b.timelineActionFilter.setImageResource(R.drawable.timeline_filter_gray);
                        timelineActionFilter("y");
                        break;
                    case 3:
                        current_timeline_filter_num = TIMELINE_FILTER_ALL;
                        b.timelineActionFilter.setImageResource(R.drawable.timeline_filter_black);
                        setRecyclerView();
                        break;
                    case 0:
                        break;
                }
                break;
            case R.id.timelineShare:
                new TimelineExcelExport(this, timeLines, placeData, ID);
                break;

        }
    }

    private void timelineActionFilter(String action) {
        ArrayList<TimeLine> timeline_action_filter = new ArrayList<>();

        for (TimeLine timeline : timeLines) {
            if (timeline.getAction().equals(action)) timeline_action_filter.add(timeline);
        }

        setFilteredRecyclerView(timeline_action_filter);
    }

    public void showPopup() {
        isOpenDateFilter = true;
        filterSelectedDate = "";
        timeLineCalendarPopup = new TimeLineCalendarPopup(Objects.requireNonNull(followTimeline), timeLines, positiveListener, negativeListener);
        timeLineCalendarPopup.setCancelable(false);
        Objects.requireNonNull(timeLineCalendarPopup.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timeLineCalendarPopup.show();

        //디스플레이 해상도 가져오기
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Window window = timeLineCalendarPopup.getWindow();
        int x = (int) (size.x * 0.8f);
        int y = (int) (size.y * 0.8f);
        window.setLayout(x, y);

    }

    private View.OnClickListener positiveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            filterSelectedDate = timeLineCalendarPopup.getSelectedDate();
            Log.e(TAG, "onSingleClick: filterSelectedDate : " + filterSelectedDate);
            if (filterSelectedDate.length() > 0) {
                ArrayList<TimeLine> dateTimeLines = new ArrayList<TimeLine>();
                if (timeLines.size() > 0) {
                    for (int i = 0; i < timeLines.size(); i++) {

                        String str = timeLines.get(i).getDate().substring(0, 10);
                        if (filterSelectedDate.equals(str)) {
                            dateTimeLines.add(timeLines.get(i));
                        }
                    }
                }

                if (dateTimeLines.size() > 0) {

                    setFilteredRecyclerView(dateTimeLines);
                } else {
                    Toast.makeText(followTimeline, "선택하신 날짜에 기록된 TimeLine이 없습니다.", Toast.LENGTH_SHORT).show();
                }

                timeLineCalendarPopup.cancel();

            } else {
                Toast.makeText(followTimeline, "날짜를 선택해주세요", Toast.LENGTH_SHORT).show();
            }

        }
    };


    private View.OnClickListener negativeListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            timeLineCalendarPopup.cancel();
            isOpenDateFilter = false;
        }
    };


    @Override
    public void onBackPressed() {
        if (isOpenDateFilter) {
            isOpenDateFilter = false;
            setRecyclerView();
        } else if (b.searchText.length() > 0) {
            b.searchText.setText("");
            Util.hideKeyboard(followTimeline);
        } else if (isOpenSearch) {
            isOpenSearch = false;
            b.searchWrap.setVisibility(View.GONE);
            b.spinnerWrap.setVisibility(View.VISIBLE);

        } else {
            super.onBackPressed();
        }

    }
}