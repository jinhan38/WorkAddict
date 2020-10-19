package com.example.workaddict.BottomFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.workaddict.BottomNavi;
import com.example.workaddict.BottomSheet.MyBottomSheetTimeLine;
import com.example.workaddict.DataClass.PlaceData;
import com.example.workaddict.DataClass.TimeLine;
import com.example.workaddict.Interface.BackButton;
import com.example.workaddict.OnSingleClickListener;
import com.example.workaddict.R;
import com.example.workaddict.TimeLineClass.AddTimeLineContent;
import com.example.workaddict.TimeLineClass.TimeLineAdapter;
import com.example.workaddict.TimeLineClass.TimelineExcelExport;
import com.example.workaddict.TimeLineDateClass.DateItem;
import com.example.workaddict.TimeLineDateClass.MyDateTime;
import com.example.workaddict.Utility.UserInfo;
import com.example.workaddict.Utility.Util;
import com.example.workaddict.popup.TimeLineCalendarPopup;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class TimeLinePage extends Fragment implements View.OnClickListener, BackButton {
    private static final String TAG = "TimeLinePage";
    private Toolbar toolbar;
    private Spinner spinnerCategoryInTimeLine;
    private Spinner spinnerPlaceInTimeLine;
    public RecyclerView recyclerView_timeline;
    public boolean isOpenCategoryNamePlusPlaceName = false;
    public TimeLineAdapter adapter;
    private int categorySpinnerPosition = 0;
    public ArrayList<TimeLine> timeLinesFilter = new ArrayList<TimeLine>();
    public ArrayList<TimeLine> timeLinesSearch = new ArrayList<TimeLine>();
    private EditText etSearch;
    private ImageView btnSearchClose;
    private boolean isOpenSearch = false;
    private LinearLayout timelineSpinnerWrap;
    private int year = 0;
    private int month = 0;
    private int day = 0;
    private DatePickerDialog datePickerDialog;
    //    private TextView dateFilterText;
    private ImageView dateFilterClose;
    private boolean isOpenDateFilter = false;
    public ImageButton addTimeLineButton;
    public ImageButton timelineActionFilter;
    private TimeLineCalendarPopup timeLineCalendarPopup;
    public String filterSelectedDate = "";
    public CheckBox timelineCheckBox;
    public boolean isOpenCheckBox = false;
    public Button btnDelete;
    private Animation slideUp;
    public ArrayList<Integer> selectedTimeLinesPosition = new ArrayList<Integer>();
    public ArrayList<Integer> selectedTimeLinesAdapterPosition = new ArrayList<Integer>();
    private View v;
    private long backBtnTime = 0;
    public static TimeLinePage singlton;
    public boolean isSetTimeLineSpinner = false;
    public ImageButton toolbar_search;
    public MyBottomSheetTimeLine myBottomSheetTimeLine;
    public FirebaseDatabase database;
    public int selectedMorePosition;
    public View timeLineSomeThingChangeWrap;
    public FrameLayout timeLinePageWrap;
    private Button btnChangeTimeLineSomeThing;
    private RequestManager glide;
    private ImageView timelineItemImageView;
    private ProgressBar timelineItemProgressBar;
    private String imageUrl;
    private String fileName;
    private final int TIMELINE_FILTER_ALL = 1;
    private final int TIMELINE_FILTER_ACTION_N = 2;
    private final int TIMELINE_FILTER_ACTION_Y = 3;
    private int current_timeline_filter_num = 0;
    private LinearLayout zeroPage;
    private ImageButton addTimelineButtonFirst;

    public static TimeLinePage newInstance() {
        if (singlton == null) {
            singlton = new TimeLinePage();
        }
        return singlton;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_time_line_page, container, false);

        Log.e(TAG, "onCreateView: 사이즈 0 이상");
        initView();
        setupListener();
        timelineSizeCheck();

        return v;
    }


    public void timelineSizeCheck() {

        if (BottomNavi.timeLines.size() == 0) {
            zeroPage.setVisibility(View.VISIBLE);
            timeLinePageWrap.setVisibility(View.GONE);
            v.findViewById(R.id.addTimelineButtonFirst).setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), AddTimeLineContent.class);
                intent.putExtra("pageType", "1");
                startActivity(intent);
            });
        } else {
            zeroPage.setVisibility(View.GONE);
            timeLinePageWrap.setVisibility(View.VISIBLE);
        }
    }


    private void initView() {

        glide = Glide.with(getActivity());
        recyclerView_timeline = v.findViewById(R.id.recyclerView_timeline);

        spinnerCategoryInTimeLine = v.findViewById(R.id.spinnerCategoryInTimeLine);
        spinnerPlaceInTimeLine = v.findViewById(R.id.spinnerPlaceInTimeLine);

        Log.e(TAG, "initView: 카테고리 사이즈 : " + BottomNavi.categoryData.size());

        setTimeLineSpinner();
        etSearch = v.findViewById(R.id.etSearch);
        btnSearchClose = v.findViewById(R.id.btnSearchClose);
        timelineSpinnerWrap = v.findViewById(R.id.timelineSpinnerWrap);
        dateFilterClose = v.findViewById(R.id.dateFilterClose);
        timelineCheckBox = v.findViewById(R.id.timelineCheckBox);
        btnDelete = v.findViewById(R.id.btnDelete);
        timeLineSomeThingChangeWrap = v.findViewById(R.id.timeLineSomeThingChangeWrap);
        timeLinePageWrap = v.findViewById(R.id.timeLinePageWrap);
        btnChangeTimeLineSomeThing = v.findViewById(R.id.btnChangeTimeLineSomeThing);

        addTimeLineButton = v.findViewById(R.id.addTimeLineButton);
        timelineActionFilter = v.findViewById(R.id.timelineActionFilter);

        zeroPage = v.findViewById(R.id.addTimelineFirstPage);
        addTimelineButtonFirst = v.findViewById(R.id.addTimelineButtonFirst);

        if (BottomNavi.timeLines.size() > 0) {
            addTimeLineButton.setVisibility(View.VISIBLE);
            timelineActionFilter.setVisibility(View.VISIBLE);
        }

        toolbar_search = BottomNavi.bottomNavi.findViewById(R.id.toolbar_search);

        slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.scroll_slide_up);
        current_timeline_filter_num = TIMELINE_FILTER_ALL;

    }

    public void setTimeLineSpinner() {

        Util.setCategoryTimeLineSpinner(getActivity(), spinnerCategoryInTimeLine, BottomNavi.categoryData);

        Util.setPlaceTimeLineSpinner(getActivity(), spinnerPlaceInTimeLine, BottomNavi.placeData);
    }

    public void timeLineAdapterNotify() {
        adapter.notifyDataSetChanged();
    }


    public void setToolbarVisible(boolean show) {

        if (show) {
            toolbar_search.setVisibility(View.VISIBLE);

            BottomNavi.bottomNavi.b.tvMapPageAppName.setVisibility(View.VISIBLE);
            BottomNavi.bottomNavi.b.ibMapPageBack.setVisibility(View.GONE);

        } else {
            toolbar_search.setVisibility(View.GONE);
            BottomNavi.bottomNavi.b.tvMapPageAppName.setVisibility(View.GONE);
            BottomNavi.bottomNavi.b.ibMapPageBack.setVisibility(View.VISIBLE);
        }
    }


    private void setupListener() {

        btnSearchClose.setOnClickListener(this);
        dateFilterClose.setOnClickListener(this);
        addTimeLineButton.setOnClickListener(this);
        toolbar_search.setOnClickListener(this);
        timelineActionFilter.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        BottomNavi.bottomNavi.b.timelinePageOptionMenu.setOnClickListener(this);


        spinnerCategoryInTimeLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                ArrayList<PlaceData> pd = new ArrayList<PlaceData>();
                pd.clear();


                if (position == 0) {
                    setAdapter(BottomNavi.timeLines);
                    categorySpinnerPosition = 0;
                    Util.setPlaceTimeLineSpinner(getActivity(), spinnerPlaceInTimeLine, BottomNavi.placeData);
                } else {
                    categorySpinnerPosition = 1;

                    String categoryName = "";
                    categoryName = spinnerCategoryInTimeLine.getSelectedItem().toString();

                    for (int i = 0; i < BottomNavi.placeData.size(); i++) {
                        if (BottomNavi.placeData.get(i).getCategoryName().equals(categoryName)) {
                            pd.add(BottomNavi.placeData.get(i));
                        }
                    }

                    for (int i = 0; i < BottomNavi.timeLines.size(); i++) {
                        if (BottomNavi.timeLines.get(i).getCategoryName().equals(categoryName)) {
                            timeLinesFilter.add(BottomNavi.timeLines.get(i));
                        }
                    }
                    setAdapter(timeLinesFilter);
                }
                Util.setPlaceTimeLineSpinner(getActivity(), spinnerPlaceInTimeLine, pd);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPlaceInTimeLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeLinesFilter.clear();

                if (BottomNavi.timeLines.size() > 0) {
                    if (position == 0 && categorySpinnerPosition == 1) {

                        for (int i = 0; i < BottomNavi.timeLines.size(); i++) {
                            if (BottomNavi.timeLines.get(i).getCategoryName().equals(spinnerCategoryInTimeLine.getSelectedItem().toString())) {
                                timeLinesFilter.add(BottomNavi.timeLines.get(i));
                            }
                        }

                        setAdapter(timeLinesFilter);
                    } else if (position != 0) {
                        for (int i = 0; i < BottomNavi.timeLines.size(); i++) {
                            if (BottomNavi.timeLines.get(i).getPlaceName().equals(spinnerPlaceInTimeLine.getSelectedItem().toString().trim())) {
                                timeLinesFilter.add(BottomNavi.timeLines.get(i));
                            }
                        }
                        setAdapter(timeLinesFilter);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0) {
                    timeLinesFilter.clear();
                    timeLinesSearch.clear();
                    btnSearchClose.setVisibility(View.VISIBLE);
//                    adapter.notifyDataSetChanged();
                    for (int i = 0; i < BottomNavi.timeLines.size(); i++) {
                        if (BottomNavi.timeLines.get(i).getPlaceName().contains(s) || BottomNavi.timeLines.get(i).getSomeThing().contains(s)) {
                            timeLinesSearch.add(BottomNavi.timeLines.get(i));
                        }
                    }

                    setAdapter(timeLinesSearch, String.valueOf(s));

                } else {

                    btnSearchClose.setVisibility(View.GONE);
                    setAdapter(BottomNavi.timeLines, String.valueOf(s));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            Log.e(TAG, "setupListener: ");
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH:
                    Util.hideKeyboard(Objects.requireNonNull(getActivity()));
                    break;
                default:
                    return false;
            }
            return true;
        });

//        recyclerView_timeline.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//
//                if (!adapter.createAdapter) {
//
//                    if (!isOpenCheckBox) {
//
//                        if (dy > 0) { // 아래로 스크롤하면 +
//                            addTimeLineButton.hide();
//                        } else { //위로 스크롤하면 -
//                            addTimeLineButton.show();
//                        }
//
//                    }
//
//                } else {
//
//                    addTimeLineButton.show();
//                    adapter.createAdapter = false;
//                }
//
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });

    }


    private void checkBoxInit() {

        Log.e(TAG, "checkBoxInit: 진입");
        if (isOpenCheckBox) {
            selectedTimeLinesAdapterPosition.clear();
            selectedTimeLinesPosition.clear();
            btnDelete.setBackground(TimeLinePage.singlton.getResources().getDrawable(R.drawable.back_gray_round_4dp));
            btnDelete.setEnabled(false);
        }
    }

    public void showPopup() {
        filterSelectedDate = "";
        timeLineCalendarPopup = new TimeLineCalendarPopup(Objects.requireNonNull(getActivity()), BottomNavi.timeLines, positiveListener, negativeListener);
        timeLineCalendarPopup.setCancelable(false);
        Objects.requireNonNull(timeLineCalendarPopup.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timeLineCalendarPopup.show();

        //디스플레이 해상도 가져오기
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Window window = timeLineCalendarPopup.getWindow();
        int x = (int) (size.x * 0.8f);
        int y = (int) (size.y * 0.8f);
        window.setLayout(x, y);

    }

    private OnSingleClickListener positiveListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            filterSelectedDate = timeLineCalendarPopup.getSelectedDate();
            if (filterSelectedDate.length() > 0) {
                ArrayList<TimeLine> timeLines = new ArrayList<TimeLine>();
                if (BottomNavi.timeLines.size() > 0) {
                    for (int i = 0; i < BottomNavi.timeLines.size(); i++) {

                        String str = BottomNavi.timeLines.get(i).getDate().substring(0, 10);
                        if (filterSelectedDate.equals(str)) {
                            timeLines.add(BottomNavi.timeLines.get(i));
                        }
                    }
                }

                if (timeLines.size() > 0) {

                    isOpenDateFilter = true;
                    timelineSpinnerWrap.setVisibility(View.GONE);
                    etSearch.setVisibility(View.GONE);

                    BottomNavi.bottomNavi.b.tvMapPageAppName.setVisibility(View.VISIBLE);
//                    dateFilterText.setVisibility(View.VISIBLE);
                    dateFilterClose.setVisibility(View.VISIBLE);
//                    dateFilterText.setText(filterSelectedDate);
                    setAdapter(timeLines);
                } else {
                    Toast.makeText(getActivity(), "선택하신 날짜에 기록된 TimeLine이 없습니다.", Toast.LENGTH_SHORT).show();
                }

                timeLineCalendarPopup.cancel();

            } else {
                Toast.makeText(getActivity(), "날짜를 선택해주세요", Toast.LENGTH_SHORT).show();
            }


        }
    };

    private OnSingleClickListener negativeListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            timeLineCalendarPopup.cancel();
            setToolbarVisible(true);
        }
    };


    private void optionMenuClick() {
        PopupMenu popup = new PopupMenu(getActivity(), BottomNavi.bottomNavi.b.timelinePageOptionMenu);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.timeline_menu, popup.getMenu());


        popup.setOnMenuItemClickListener(menuItem -> {

            switch (menuItem.getItemId()) {
                case R.id.menu_calendar_timeline:
                    showPopup();
                    toolbar_search.setVisibility(View.GONE);
                    break;
                case R.id.menu_share_timeline:
                    if (BottomNavi.timeLines.size() > 0) {
                        new TimelineExcelExport(getActivity(), BottomNavi.timeLines, BottomNavi.placeData, UserInfo.getID());
                    }
                    break;
                case R.id.menu_delete_timeline:
                    if (BottomNavi.timeLines.size() > 0) {
                        isOpenCheckBox = true;
                        BottomNavi.bottomNavi.b.ibMapPageBack.setVisibility(View.VISIBLE);
                        BottomNavi.bottomNavi.b.tvMapPageAppName.setText("삭제");
                        toolbar_search.setVisibility(View.GONE);
                        btnDelete.setVisibility(View.VISIBLE);
                        btnDelete.startAnimation(slideUp);
                        btnDelete.setBackground(TimeLinePage.singlton.getResources().getDrawable(R.drawable.back_gray_round_4dp));
                        btnDelete.setEnabled(false);
                        setAdapter(BottomNavi.timeLines);
                        BottomNavi.bottomNavi.b.tvMapPageAppName.setText("삭제(0/" + BottomNavi.timeLines.size() + ")");
                    }
                    break;
            }

            return false;
        });

        popup.show();

    }

    /**
     * 기본 setAdapter
     *
     * @param timeLines
     */
    public void setAdapter(ArrayList<TimeLine> timeLines) {
        Log.e(TAG, "setAdapter: 진입");
        if (timeLines != null) {

            Log.e(TAG, "setAdapter: 타임라인 not null");
            if (timeLines.size() > 0) {
                Log.e(TAG, "setAdapter: 사이즈 있음");
                if (recyclerView_timeline == null) {
                    Log.e(TAG, "setAdapter: recyclerview null");
                    initView();
                    setupListener();
                }
                recyclerView_timeline.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setStackFromEnd(true);
                recyclerView_timeline.setLayoutManager(linearLayoutManager);
                adapter = new TimeLineAdapter(timeLines, isOpenCheckBox);
                recyclerView_timeline.setAdapter(adapter);
            } else {
                Log.e(TAG, "setAdapter: 사이즈 0");
                recyclerView_timeline.setAdapter(null);
            }
        }
        checkBoxInit();
    }


    public void setAdapter(ArrayList<TimeLine> timeLines, ArrayList<Integer> selectedTimeLinesPosition) {
        if (timeLines != null) {
            if (timeLines.size() > 0) {
                recyclerView_timeline.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setStackFromEnd(true);
                recyclerView_timeline.setLayoutManager(linearLayoutManager);
                adapter = new TimeLineAdapter(timeLines, isOpenCheckBox);
                recyclerView_timeline.setAdapter(adapter);
            }
        }
        checkBoxInit();
    }


    /**
     * 키워드 검색 setAdapter
     *
     * @param timeLines
     * @param keyword
     */
    public void setAdapter(ArrayList<TimeLine> timeLines, String keyword) {
        if (timeLines != null) {

            if (timeLines.size() > 0) {
                recyclerView_timeline.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setStackFromEnd(true);
                recyclerView_timeline.setLayoutManager(linearLayoutManager);
                adapter = new TimeLineAdapter(timeLines, isOpenCheckBox, keyword);
                recyclerView_timeline.setAdapter(adapter);
            }
        }
        checkBoxInit();
    }

    public ImageView timelineImageView() {
        return timelineItemImageView;
    }

    public ProgressBar timeLineImageViewItemProgressbar() {
        return timelineItemProgressBar;
    }

    public void timelineImageDialog(Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(activity));
        View view = LayoutInflater.from(getActivity()).
                inflate(R.layout.timeline_imageview_item, null, false);
        builder.setView(view);
        timelineItemImageView = view.findViewById(R.id.timeLineImageViewItem);
        timelineItemProgressBar = view.findViewById(R.id.timeLineImageViewItemProgressbar);


        builder.setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss())
                .setNeutralButton("다운로드", (dialog, which) -> Util.fileDownload(getImageUrl(), getFileName(), getActivity()));

        final AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void someThingChangeDialog(int position, TextView textView) {

        ArrayList<DateItem> dateItems = Util.initItemList(Util.orderByTimeDesc(Util.changeTimeLineData(BottomNavi.timeLines)));

        for (int i = 0; i < BottomNavi.timeLines.size(); i++) {
            if (BottomNavi.timeLines.get(i).getPlaceName().equals(((MyDateTime) dateItems.get(position)).getPlaceName())
                    && BottomNavi.timeLines.get(i).getSomeThing().equals(((MyDateTime) dateItems.get(position)).getSomeThing())) {
                TimeLinePage.singlton.selectedMorePosition = i;
            }
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        View view = LayoutInflater.from(getActivity()).
                inflate(R.layout.timeline_something_change, null, false);
        builder.setMessage("수정할 내용을 입력해주세요");

        builder.setView(view);
        EditText timeLineSomeThingEdit = view.findViewById(R.id.timeLineSomeThingEdit);
        ImageButton closeTimeLineSomeThing = view.findViewById(R.id.closeTimeLineSomeThing);

        builder.setPositiveButton("수정", (dialogInterface, i) -> {
            Util.updateTimeLineSomeThing(selectedMorePosition, timeLineSomeThingEdit.getText().toString(),
                    BottomNavi.timeLineDataKeyList, textView);
            Util.hideKeyboard(Objects.requireNonNull(getActivity()));
            dialogInterface.dismiss();

        }).setNegativeButton("취소", (dialog, which) -> {
            Util.hideKeyboard(Objects.requireNonNull(getActivity()));
            dialog.dismiss();
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        Util.showKeyboard(getActivity());


        timeLineSomeThingEdit.setText(BottomNavi.timeLines.get(selectedMorePosition).getSomeThing());
        Log.e(TAG, "someThingChangeDialog: 섬띵 텍스트 : " + BottomNavi.timeLines.get(selectedMorePosition).getSomeThing());
        closeTimeLineSomeThing.setOnClickListener(this);

        timeLineSomeThingEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count > 0) closeTimeLineSomeThing.setVisibility(View.VISIBLE);
                else closeTimeLineSomeThing.setVisibility(View.GONE);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) closeTimeLineSomeThing.setVisibility(View.VISIBLE);
                else closeTimeLineSomeThing.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                int textCount = s.length();
                if (textCount > 0) closeTimeLineSomeThing.setVisibility(View.VISIBLE);
                else closeTimeLineSomeThing.setVisibility(View.GONE);
                Log.e(TAG, "afterTextChanged: textCount : " + textCount);

            }
        });

        closeTimeLineSomeThing.setOnClickListener(v -> {
            timeLineSomeThingEdit.setText("");
            closeTimeLineSomeThing.setVisibility(View.INVISIBLE);
        });


    }


    @Override
    public void onBackPressed() {
        if (BottomNavi.bottomNavi.b.viewPager.getCurrentItem() == 2) {
            if (etSearch.getText().length() > 0) {
                etSearch.setText("");
            } else if (isOpenDateFilter) {
                isOpenDateFilter = false;
                timelineSpinnerWrap.setVisibility(View.VISIBLE);
                etSearch.setVisibility(View.GONE);
//                dateFilterText.setVisibility(View.GONE);
                dateFilterClose.setVisibility(View.GONE);
                setAdapter(BottomNavi.timeLines);
                setToolbarVisible(true);
            } else if (isOpenSearch) {
                isOpenSearch = false;
                etSearch.setVisibility(View.GONE);
                btnSearchClose.setVisibility(View.GONE);
                timelineSpinnerWrap.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.GONE);
                setToolbarVisible(true);
                BottomNavi.bottomNavi.b.ibMapPageBack.setVisibility(View.GONE);
                BottomNavi.bottomNavi.b.tvMapPageAppName.setText(getString(R.string.timeline));
                Util.hideKeyboard(Objects.requireNonNull(getActivity()));
            } else if (isOpenCheckBox) {
                Log.e(TAG, "onBackPressed: isOpenCheckBox 트루");
                checkBoxTaskFinish();
            } else {
                long curTime = System.currentTimeMillis();
                long gapTime = curTime - backBtnTime;
                if (0 <= gapTime && 2000 >= gapTime) {
                    Objects.requireNonNull(getActivity()).finish();
                } else {
                    backBtnTime = curTime;
                    Toast.makeText(getActivity(), "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void checkBoxTaskFinish() {
        deleteTask();
        Util.setCategoryTimeLineSpinner(getActivity(), spinnerCategoryInTimeLine, BottomNavi.categoryData);
        Util.setPlaceTimeLineSpinner(getActivity(), spinnerPlaceInTimeLine, BottomNavi.placeData);
        setAdapter(BottomNavi.timeLines);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearchClose:
                etSearch.setText("");
                btnSearchClose.setVisibility(View.GONE);
                Util.hideKeyboard(getActivity());
                break;
            case R.id.dateFilterClose:
                isOpenDateFilter = false;
                timelineSpinnerWrap.setVisibility(View.VISIBLE);
                dateFilterClose.setVisibility(View.GONE);
                setAdapter(BottomNavi.timeLines);
                break;
            case R.id.addTimeLineButton:
                Intent intent = new Intent(getActivity(), AddTimeLineContent.class);
                intent.putExtra("pageType", "1");
                startActivity(intent);
                break;
            case R.id.toolbar_search:
                isOpenSearch = true;
                BottomNavi.bottomNavi.b.ibMapPageBack.setVisibility(View.VISIBLE);
                BottomNavi.bottomNavi.b.tvMapPageAppName.setText("검색");
                etSearch.setVisibility(View.VISIBLE);
                etSearch.requestFocus();
                Util.showKeyboard(getActivity());
                timelineSpinnerWrap.setVisibility(View.GONE);
                toolbar_search.setVisibility(View.VISIBLE);
                break;
            case R.id.btnDelete:
                AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(TimeLinePage.singlton.getActivity()));
                builder.setMessage("선택하신 Timeline을 삭제하시겠습니까?");

                builder.setPositiveButton("삭제", (dialogInterface, k) -> {
                    Util.removeTimeLine(selectedTimeLinesPosition, BottomNavi.timeLineDataKeyList, getActivity());
                    dialogInterface.dismiss();
                }).setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

                final AlertDialog dialog = builder.create();
                dialog.show();

                break;
            case R.id.timelineActionFilter:
                switch (current_timeline_filter_num) {
                    case 1:
                        current_timeline_filter_num = TIMELINE_FILTER_ACTION_N;
                        timelineActionFilter.setImageResource(R.drawable.timeline_filter_purple);
                        timelineActionFilter("n");

                        break;
                    case 2:
                        current_timeline_filter_num = TIMELINE_FILTER_ACTION_Y;
                        timelineActionFilter.setImageResource(R.drawable.timeline_filter_gray);
                        timelineActionFilter("y");
                        break;
                    case 3:
                        current_timeline_filter_num = TIMELINE_FILTER_ALL;
                        timelineActionFilter.setImageResource(R.drawable.timeline_filter_black);
                        setAdapter(BottomNavi.timeLines);
                        break;
                    case 0:
                        break;
                }
                break;
            case R.id.timelinePageOptionMenu:
                optionMenuClick();
                break;

        }
    }


    @Override
    public void onPause() {
        if (BottomNavi.timeLines != null) {
            if (BottomNavi.timeLines.size() > 0) {

                isOpenCheckBox = false;
//        BottomNavi.bottomNavi.b.ibMapPageBack.setVisibility(View.GONE);
//        BottomNavi.bottomNavi.b.tvMapPageAppName.setText(getString(R.string.timeline));
                toolbar_search.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.GONE);
                setAdapter(BottomNavi.timeLines);
            }
        }
        super.onPause();
    }


    private void timelineActionFilter(String action) {
        ArrayList<TimeLine> timeline_action_filter = new ArrayList<>();

        for (TimeLine timeline : BottomNavi.timeLines) {
            if (timeline.getAction().equals(action)) timeline_action_filter.add(timeline);
        }

        setAdapter(timeline_action_filter);
    }

    public void deleteTask() {
        isOpenCheckBox = false;
        selectedTimeLinesPosition.clear();
        selectedTimeLinesAdapterPosition.clear();
        addTimeLineButton.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.GONE);
        BottomNavi.bottomNavi.b.ibMapPageBack.setVisibility(View.GONE);
        BottomNavi.bottomNavi.b.tvMapPageAppName.setText(getString(R.string.timeline));

        setToolbarVisible(true);

    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}



