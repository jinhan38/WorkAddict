package com.example.workaddict.TimeLineClass;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.workaddict.BottomFragment.ListFragment;
import com.example.workaddict.BottomFragment.TimeLinePage;
import com.example.workaddict.BottomNavi;
import com.example.workaddict.DataClass.CategoryData;
import com.example.workaddict.DataClass.TimeLine;
import com.example.workaddict.DataSort.SortByDateDateItem;
import com.example.workaddict.FollowInfo.FollowTimeline;
import com.example.workaddict.R;
import com.example.workaddict.TimeLineDateClass.DateItem;
import com.example.workaddict.TimeLineDateClass.MyDateTime;
import com.example.workaddict.TimeLineDateClass.TimeClassification;
import com.example.workaddict.Utility.TextSearchAndHighLight;
import com.example.workaddict.Utility.UserInfo;
import com.example.workaddict.Utility.Util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class TimeLineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "TimeLineAdapter";
    public static ArrayList<DateItem> dateItems;
    public ArrayList<TimeLine> followListTimeline = new ArrayList<>();
    private ArrayList<Integer> selectedTimeLinesPosition;
    private OnItemClickListener listener;
    private boolean isOpenCheckBox;
    private String keyword;
    public int tagPosition = 0;
    public Context parentContext;
    public RequestManager glide;
    public Uri imageUri = null;
    public boolean createAdapter = true;
    private String isFollowList = "n";
    private RecyclerView recyclerView;
    private ArrayList<CategoryData> followCategoryData;
    private Activity followActivity;


    /**
     * 기본적인 adapter 생성자
     *
     * @param timeline
     * @param isOpenCheckBox
     */
    public TimeLineAdapter(ArrayList<TimeLine> timeline, boolean isOpenCheckBox) {

        this.dateItems = initItemList(orderByTimeDesc(Util.changeTimeLineData(timeline)));
        this.isOpenCheckBox = isOpenCheckBox;

    }


    /**
     * 딜리트 버튼 클릭했을 때 생성자
     *
     * @param timeline
     * @param isOpenCheckBox
     * @param selectedTimeLinesPosition
     */
    public TimeLineAdapter(ArrayList<TimeLine> timeline, boolean isOpenCheckBox,
                           ArrayList<Integer> selectedTimeLinesPosition,
                           RequestManager glide) {

        this.dateItems = initItemList(orderByTimeDesc(Util.changeTimeLineData(timeline)));
        this.selectedTimeLinesPosition = selectedTimeLinesPosition;
        this.isOpenCheckBox = isOpenCheckBox;
        this.glide = glide;

    }


    /**
     * 텍스트 검색할 때 생성자
     *
     * @param timeline
     * @param isOpenCheckBox
     * @param keyword
     */
    public TimeLineAdapter(ArrayList<TimeLine> timeline, boolean isOpenCheckBox, String keyword) {

        this.dateItems = initItemList(orderByTimeDesc(Util.changeTimeLineData(timeline)));
        this.isOpenCheckBox = isOpenCheckBox;
        this.keyword = keyword;

    }


    /**
     * 팔로우 정보에서 타임라인에서의 생성자
     *
     * @param timeline
     */
    public TimeLineAdapter(ArrayList<TimeLine> timeline, ArrayList<CategoryData> categoryData,
                           String isFollowList, RecyclerView recyclerView, Activity activity) {

        this.dateItems = initItemList(orderByTimeDesc(Util.changeTimeLineData(timeline)));
        this.followListTimeline = timeline;
        this.isFollowList = isFollowList;
        this.recyclerView = recyclerView;
        this.followCategoryData = categoryData;
        this.followActivity = activity;
    }


    public static class TimeViewHolder extends RecyclerView.ViewHolder {
        public TextView date_year_month;

        public TimeViewHolder(@NonNull View itemView) {
            super(itemView);
            this.date_year_month = (TextView) itemView.findViewById(R.id.date_year_month);
        }
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView date;
        public TextView placeNameTimeLine;
        public TextView someThingTimeLine;
        public LinearLayout timeLineCheckBoxWrap;
        public CheckBox timelineCheckBox;
        public OnViewHolderClickListener listener;
        public ImageView timelineLeftViewBar;
        public ImageView timeLineImageView;
        public boolean isSelected;
        public View view;
        public LinearLayout timeLineBubble;
        public ImageView timelineEditButton;
        public ImageView timelineGalleryButton;
        public CardView timelineCardView;
        public LinearLayout timelineItemWrap;
        public DataViewHolder dataViewHolder;
        public LinearLayout timelineEditWrap;


        public DataViewHolder(View v, OnViewHolderClickListener listener) {
            super(v);
            dataViewHolder = this;
            date = v.findViewById(R.id.date);
            placeNameTimeLine = v.findViewById(R.id.placeNameTimeLine);
            someThingTimeLine = v.findViewById(R.id.someThingTimeLine);
            timeLineCheckBoxWrap = v.findViewById(R.id.timeLineCheckBoxWrap);
            timelineCheckBox = v.findViewById(R.id.timelineCheckBox);
            timelineLeftViewBar = v.findViewById(R.id.timelineLeftViewBar);
            timeLineImageView = v.findViewById(R.id.timeLineImageView);
            timeLineBubble = v.findViewById(R.id.timeLineBubble);
            timelineEditButton = v.findViewById(R.id.timelineEditButton);
            timelineGalleryButton = v.findViewById(R.id.timelineGalleryButton);
            timelineCardView = v.findViewById(R.id.timelineCardView);
            timelineItemWrap = v.findViewById(R.id.timelineItemWrap);
            timelineEditWrap = v.findViewById(R.id.timelineEditWrap);
            this.listener = listener;

            if (Util.isFollowTimeline) {

                if (FollowTimeline.followTimeline.b.followTimelineRecyclerView.getVisibility() == View.VISIBLE) {
                    timelineEditButton.setVisibility(View.GONE);
                    Log.e(TAG, "DataViewHolder: 팔로우 타임라인 ");
                }

                timelineGalleryButton.setOnClickListener(v1 -> {

                    TimeLinePage timeLinePage = TimeLinePage.singlton;
                    timeLinePage.timelineImageDialog(FollowTimeline.followTimeline);

                    ProgressBar progressBar = timeLinePage.timeLineImageViewItemProgressbar();
                    ImageView imageView = timeLinePage.timelineImageView();

                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference riversRef = mStorageRef
                            .child("users/" + FollowTimeline.followTimeline.ID.replaceAll("\\.", "")
                                    + "/timeline/" + ((MyDateTime) dateItems.get(getAdapterPosition())).getImageKey() + ".jpg");

                    riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        if (uri != null) {
                            Log.e(TAG, "DataViewHolder: uri : " + uri);

                            timeLinePage.setImageUrl(String.valueOf(uri));
                            timeLinePage.setFileName(
                                    ((MyDateTime) dateItems.get(getAdapterPosition())).getPlaceName() + "_" +
                                            ((MyDateTime) dateItems.get(getAdapterPosition())).getDate()
                            );


                            Glide.with(FollowTimeline.followTimeline)
                                    .load(uri)
                                    .centerInside()
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            progressBar.setVisibility(View.GONE);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            progressBar.setVisibility(View.GONE);
                                            return false;
                                        }


                                    })
                                    .into(imageView);

                        }

                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "onDataChange: 이미지 uri 다운로드 실패 : " + e);
                        Toast.makeText(timeLinePage.getActivity(), "이미지 다운로드에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    });


                });

            } else {


                if (TimeLinePage.singlton.isOpenCheckBox) {

                    timelineEditWrap.setVisibility(View.GONE);

                    timelineItemWrap.setOnClickListener(view -> {
                        Log.e(TAG, "DataViewHolder: 체크박스 클릭 ");

                        int position = setClickedPositionToTimelinePositionChange(getAdapterPosition());

                        Log.e(TAG, "DataViewHolder: position : " + position);
                        if (TimeLinePage.singlton.selectedTimeLinesPosition.size() > 0) {

                            if (TimeLinePage.singlton.selectedTimeLinesPosition.contains(position)) {

                                Object objectAdapterPosition = getAdapterPosition();
                                TimeLinePage.singlton.selectedTimeLinesAdapterPosition.remove(objectAdapterPosition);
                                Object objectPosition = position;
                                TimeLinePage.singlton.selectedTimeLinesPosition.remove(objectPosition);
                                timelineCheckBox.setChecked(false);
//                                timelineCheckBox.setImageDrawable(TimeLinePage.singlton.getResources().getDrawable(R.drawable.checkbox_gray));

                            } else {
                                TimeLinePage.singlton.selectedTimeLinesAdapterPosition.add(getAdapterPosition());
                                TimeLinePage.singlton.selectedTimeLinesPosition.add(position);
                                timelineCheckBox.setChecked(true);
                            }

                        } else {
                            TimeLinePage.singlton.selectedTimeLinesAdapterPosition.add(getAdapterPosition());
                            TimeLinePage.singlton.selectedTimeLinesPosition.add(position);
                            timelineCheckBox.setChecked(true);

                        }

                        Log.e(TAG, "DataViewHolder: TimeLinePage.singlton.selectedTimeLinesPosition : " + TimeLinePage.singlton.selectedTimeLinesPosition);
                        Log.e(TAG, "DataViewHolder: TimeLinePage.singlton.selectedTimeLinesAdapterPosition : " + TimeLinePage.singlton.selectedTimeLinesAdapterPosition);


                        int size = TimeLinePage.singlton.selectedTimeLinesPosition.size();
                        int all = BottomNavi.timeLines.size();

                        BottomNavi.bottomNavi.b.tvMapPageAppName.setText("삭제(" + size + "/" + all + ")");


                        if (TimeLinePage.singlton.selectedTimeLinesPosition.size() > 0) {
                            TimeLinePage.singlton.btnDelete.setBackground(TimeLinePage.singlton.getResources().getDrawable(R.drawable.back_purple_round_3dp));
                            TimeLinePage.singlton.btnDelete.setEnabled(true);
                        } else {
                            TimeLinePage.singlton.btnDelete.setBackground(TimeLinePage.singlton.getResources().getDrawable(R.drawable.back_gray_round_4dp));
                            TimeLinePage.singlton.btnDelete.setEnabled(false);
                        }


                    });


                } else {

                    timelineEditWrap.setVisibility(View.VISIBLE);

                    //타임라인 그냥 클릭
                    timelineItemWrap.setOnClickListener(view -> {


                        ArrayList<TimeLine> timeLines = BottomNavi.timeLines;
                        int position = getAdapterPosition();
                        String clickedPositionDate = ((MyDateTime) dateItems.get(position)).getDate().substring(0, 16);
                        for (int i = 0; i < BottomNavi.timeLines.size(); i++) {
                            if (timeLines.get(i).getPlaceName().equals(((MyDateTime) dateItems.get(position)).getPlaceName())
                                    && timeLines.get(i).getSomeThing().equals(((MyDateTime) dateItems.get(position)).getSomeThing())
                                    && timeLines.get(i).getDate().contains(clickedPositionDate)) {
                                TimeLinePage.singlton.selectedMorePosition = i;
                                break;
                            }
                        }

                        final String[] action = {""};

                        if (BottomNavi.timeLines.get(TimeLinePage.singlton.selectedMorePosition).getAction().equals("y")) {

                            action[0] = "n";
                            dataViewHolder.timelineLeftViewBar.setImageDrawable(null);

                            int drawable = Util.getTimelineCategoryColor(BottomNavi.categoryData, BottomNavi.timeLines.get(TimeLinePage.singlton.selectedMorePosition));

                            dataViewHolder.timelineLeftViewBar.setImageDrawable(TimeLinePage.singlton.getResources().getDrawable(drawable));
                            dataViewHolder.placeNameTimeLine.setTextColor(TimeLinePage.singlton.getActivity().getColor(R.color.black));
                            dataViewHolder.date.setTextColor(TimeLinePage.singlton.getActivity().getColor(R.color.strong_gray));
                            dataViewHolder.someThingTimeLine.setTextColor(TimeLinePage.singlton.getActivity().getColor(R.color.black));
                            dataViewHolder.timelineCardView.setCardElevation(10);

                        } else {

                            action[0] = "y";

                            dataViewHolder.timelineLeftViewBar.setImageDrawable(null);
                            dataViewHolder.timelineLeftViewBar.setImageDrawable(TimeLinePage.singlton.getResources().getDrawable(R.drawable.back_gray_round_3dp));
                            dataViewHolder.placeNameTimeLine.setTextColor(TimeLinePage.singlton.getActivity().getColor(R.color.middleSoft_gray));
                            dataViewHolder.date.setTextColor(TimeLinePage.singlton.getActivity().getColor(R.color.middleSoft_gray));
                            dataViewHolder.someThingTimeLine.setTextColor(TimeLinePage.singlton.getActivity().getColor(R.color.middleSoft_gray));
                            dataViewHolder.timelineCardView.setCardElevation(0);

                        }

                        Util.updateTimeLineAction(TimeLinePage.singlton.selectedMorePosition, BottomNavi.timeLineDataKeyList, action[0]);

                    });

                    //타임라인 롱클릭
                    timelineItemWrap.setOnLongClickListener(v1 -> {
                        Log.e(TAG, "DataViewHolder: 롱클릭");

                        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(TimeLinePage.singlton.getActivity()));
                        builder.setMessage("선택하신 Timeline을 삭제하시겠습니까?");

                        builder.setPositiveButton("삭제", (dialogInterface, k) -> {
                            ArrayList<TimeLine> timeLines = BottomNavi.timeLines;

                            int position = getAdapterPosition();
                            Log.e(TAG, "DataViewHolder: position 타임라인 : " + position);

                            String clickedPositionDate = ((MyDateTime) dateItems.get(position)).getDate().substring(0, 16);

                            for (int i = 0; i < BottomNavi.timeLines.size(); i++) {
                                if (timeLines.get(i).getPlaceName().equals(((MyDateTime) dateItems.get(position)).getPlaceName())
                                        && timeLines.get(i).getSomeThing().equals(((MyDateTime) dateItems.get(position)).getSomeThing())
                                        && timeLines.get(i).getDate().contains(clickedPositionDate)) {
                                    TimeLinePage.singlton.selectedMorePosition = i;

                                    String key = BottomNavi.timeLineDataKeyList.get(i);

                                    DatabaseReference myRef = Util.getFirebaseDatabase().getReference("users")
                                            .child(UserInfo.getID().replaceAll("\\.", "")).child("TimeLine").child(key);
                                    myRef.removeValue()
                                            .addOnSuccessListener(aVoid -> {
                                                Log.e(TAG, "delete 타임라인 : 데이터 삭제 성공");
                                                dateItems.remove(position);
                                                TimeLinePage.singlton.adapter.notifyDataSetChanged();

                                            }).addOnFailureListener(e -> {
                                        Log.e(TAG, "delete 타임라인 : 데이터 삭제 실패");
                                    });

                                    break;
                                }
                            }

//                            Util.removeOneTimeLine(TimeLinePage.singlton.selectedMorePosition, TimeLinePage.singlton.adapter);

                            dialogInterface.dismiss();
                        }).setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

                        final AlertDialog dialog = builder.create();
                        dialog.show();

                        return false;
                    });

                    //내용 수정 클릭
                    timelineEditButton.setOnClickListener(v1 -> {
                        TimeLinePage timeLinePage = TimeLinePage.singlton;
                        timeLinePage.someThingChangeDialog(getAdapterPosition(), someThingTimeLine);
                    });

                    //이미지 보기
                    timelineGalleryButton.setOnClickListener(v1 -> {

                        TimeLinePage timeLinePage = TimeLinePage.singlton;
                        timeLinePage.timelineImageDialog(TimeLinePage.singlton.getActivity());

                        ProgressBar progressBar = timeLinePage.timeLineImageViewItemProgressbar();
                        ImageView imageView = timeLinePage.timelineImageView();

                        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                        StorageReference riversRef = mStorageRef
                                .child("users/" + UserInfo.getID().replaceAll("\\.", "")
                                        + "/timeline/" + ((MyDateTime) dateItems.get(getAdapterPosition())).getImageKey() + ".jpg");

                        riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            if (uri != null) {
                                Log.e(TAG, "DataViewHolder: uri : " + uri);

                                timeLinePage.setImageUrl(String.valueOf(uri));
                                timeLinePage.setFileName(
                                        ((MyDateTime) dateItems.get(getAdapterPosition())).getPlaceName() + "_" +
                                                ((MyDateTime) dateItems.get(getAdapterPosition())).getDate()
                                );


                                Glide.with(TimeLinePage.singlton)
                                        .load(uri)
                                        .centerInside()
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                progressBar.setVisibility(View.GONE);
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                progressBar.setVisibility(View.GONE);
                                                return false;
                                            }


                                        })
                                        .into(imageView);

                            }

                        }).addOnFailureListener(e -> {
                            Log.e(TAG, "onDataChange: 이미지 uri 다운로드 실패 : " + e);
                            Toast.makeText(timeLinePage.getActivity(), "이미지 다운로드에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                        });


                    });
                }
            }

        }


        @Override
        public void onClick(View v) {
//            if (listener != null) {
//                listener.onViewHolderClick(getPosition());
//            }
        }

        private interface OnViewHolderClickListener {
            void onViewHolderClick(int position);
        }
    }


    public ArrayList<DateItem> initItemList(ArrayList<MyDateTime> dateItems) {
        ArrayList<DateItem> result = new ArrayList<>();

        int year = 0, month = 0, dayOfMonth = 0;
        for (MyDateTime data : dateItems) {

            if (year != data.getYear() || month != data.getMonth() || dayOfMonth != data.getDayOfMonth()) {
                result.add(new TimeClassification(data.getYear(), data.getMonth(), data.getDayOfMonth(),
                        data.getHour(), data.getMinute()));
                year = data.getYear();
                month = data.getMonth();
                dayOfMonth = data.getDayOfMonth();
            }
            result.add(data);
        }
        return result;
    }

    public ArrayList<MyDateTime> orderByTimeDesc(ArrayList<MyDateTime> dataset) {
        ArrayList<MyDateTime> result = dataset;
        for (int i = 0; i < result.size() - 1; i++) {
            for (int j = 0; j < result.size() - i - 1; j++) {
                if (result.get(j).getTime() < result.get(j + 1).getTime()) {
                    MyDateTime temp2 = result.remove(j + 1);
                    MyDateTime temp1 = result.remove(j);
                    result.add(j, temp2);
                    result.add(j + 1, temp1);
                }
            }
        }
        Collections.sort(result, new SortByDateDateItem());
        return result;
    }


    @Override
    public int getItemViewType(int position) {
        return dateItems.get(position).getType();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == DateItem.TYPE_TIME) {
            return new TimeViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_date_classification, parent, false));

        } else {
            parentContext = parent.getContext();

            return new DataViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.timeline_item, parent, false)
                    , new DataViewHolder.OnViewHolderClickListener() {

                @Override
                public void onViewHolderClick(int position) {
                    if (listener != null) {
                        Log.e(TAG, "onViewHolderClick: 포지션 확인 : " + position);
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof TimeViewHolder) {
            TimeViewHolder timeViewHolder = (TimeViewHolder) holder;
            timeViewHolder.date_year_month.setText(dateItems.get(timeViewHolder.getAdapterPosition()).getTimeToString());

        } else {

            if (holder instanceof DataViewHolder) {

                DataViewHolder dataViewHolder = (DataViewHolder) holder;
                dataViewHolder.timelineItemWrap.setTag(dataViewHolder.getAdapterPosition());
                dataViewHolder.date.setText(dateItems.get(position).getTimeHour());
                dataViewHolder.placeNameTimeLine.setText(((MyDateTime) dateItems.get(position)).getPlaceName());
                dataViewHolder.someThingTimeLine.setText(((MyDateTime) dateItems.get(position)).getSomeThing());


                if (TimeLinePage.singlton.isOpenCheckBox) {
                    dataViewHolder.timeLineCheckBoxWrap.setVisibility(View.VISIBLE);
                } else {
                    dataViewHolder.timeLineCheckBoxWrap.setVisibility(View.GONE);
                }


                if (((MyDateTime) dateItems.get(position)).getImageKey().length() > 0) {
                    dataViewHolder.timelineGalleryButton.setVisibility(View.VISIBLE);
                } else {
                    dataViewHolder.timelineGalleryButton.setVisibility(View.GONE);
                }

                if (TimeLinePage.singlton.isOpenCheckBox) {

                    dataViewHolder.timelineCheckBox.setChecked(false);

                    if (TimeLinePage.singlton.selectedTimeLinesAdapterPosition.contains(dataViewHolder.getAdapterPosition())) {
                        dataViewHolder.timelineCheckBox.setChecked(true);
                    }


                }

                if (isFollowList.equals("n")) {
                    circleChange(dateItems, dataViewHolder, position);
                } else {
                    followCircleChange(dateItems, followCategoryData, followListTimeline, dataViewHolder, position);
                }


                new TextSearchAndHighLight(keyword, dateItems, dataViewHolder.placeNameTimeLine, dataViewHolder.someThingTimeLine);


                if (createAdapter && position == dateItems.size() - 1) {
                    if (isFollowList.equals("n")) {
                        TimeLinePage.singlton.recyclerView_timeline.scrollToPosition(position);
                    } else {
                        recyclerView.scrollToPosition(position);
                    }
                }
//                if (createAdapter && position == dateItems.size() - 1) {
//                    TimeLinePage.singlton.recyclerView_timeline.scrollToPosition(position);
//                }

            }

        }
    }


    public void circleChange(ArrayList<DateItem> dateItems, DataViewHolder dataViewHolder, int position) {

        ArrayList<TimeLine> timeLines = BottomNavi.timeLines;

        int clickedTimelinePosition = 0;

        String clickedPositionDate = ((MyDateTime) dateItems.get(position)).getDate().substring(0, 16);
        for (int i = 0; i < BottomNavi.timeLines.size(); i++) {
            if (timeLines.get(i).getPlaceName().equals(((MyDateTime) dateItems.get(position)).getPlaceName())
                    && timeLines.get(i).getSomeThing().equals(((MyDateTime) dateItems.get(position)).getSomeThing())
                    && timeLines.get(i).getDate().contains(clickedPositionDate)) {
                clickedTimelinePosition = i;
                break;
            }
        }

        int drawable = Util.getTimelineCategoryColor(BottomNavi.categoryData, BottomNavi.timeLines.get(clickedTimelinePosition));

        dataViewHolder.timelineLeftViewBar.setImageDrawable(TimeLinePage.singlton.getResources().getDrawable(drawable));
        dataViewHolder.placeNameTimeLine.setTextColor(TimeLinePage.singlton.getActivity().getColor(R.color.black));
        dataViewHolder.date.setTextColor(TimeLinePage.singlton.getActivity().getColor(R.color.strong_gray));
        dataViewHolder.someThingTimeLine.setTextColor(TimeLinePage.singlton.getActivity().getColor(R.color.black));
        dataViewHolder.timelineCardView.setCardElevation(10);

        if (BottomNavi.timeLines.get(clickedTimelinePosition).getAction().equals("y")) {
            dataViewHolder.timelineLeftViewBar.setImageDrawable(TimeLinePage.singlton.getResources().getDrawable(R.drawable.back_gray_round_3dp));
            dataViewHolder.placeNameTimeLine.setTextColor(TimeLinePage.singlton.getActivity().getColor(R.color.middleSoft_gray));
            dataViewHolder.date.setTextColor(TimeLinePage.singlton.getActivity().getColor(R.color.middleSoft_gray));
            dataViewHolder.someThingTimeLine.setTextColor(TimeLinePage.singlton.getActivity().getColor(R.color.middleSoft_gray));
            dataViewHolder.timelineCardView.setCardElevation(0);

        }

    }


    /**
     * 타임라인 공유받기에서 imageview 컬러 설정하기
     * @param dateItems
     * @param followTimeLines
     * @param dataViewHolder
     * @param position
     */
    public void followCircleChange(ArrayList<DateItem> dateItems, ArrayList<CategoryData> categoryData, ArrayList<TimeLine> followTimeLines, DataViewHolder dataViewHolder, int position) {

        ArrayList<TimeLine> timeLines = followTimeLines;

        int clickedTimelinePosition = 0;

        String clickedPositionDate = ((MyDateTime) dateItems.get(position)).getDate().substring(0, 16);
        for (int i = 0; i < timeLines.size(); i++) {
            if (timeLines.get(i).getPlaceName().equals(((MyDateTime) dateItems.get(position)).getPlaceName())
                    && timeLines.get(i).getSomeThing().equals(((MyDateTime) dateItems.get(position)).getSomeThing())
                    && timeLines.get(i).getDate().contains(clickedPositionDate)) {
                clickedTimelinePosition = i;
                break;
            }
        }

        int drawable = Util.getTimelineCategoryColor(categoryData, followTimeLines.get(clickedTimelinePosition));

        dataViewHolder.timelineLeftViewBar.setImageDrawable(parentContext.getResources().getDrawable(drawable));
        dataViewHolder.placeNameTimeLine.setTextColor(parentContext.getColor(R.color.black));
        dataViewHolder.date.setTextColor(parentContext.getColor(R.color.strong_gray));
        dataViewHolder.someThingTimeLine.setTextColor(parentContext.getColor(R.color.black));
        dataViewHolder.timelineCardView.setCardElevation(10);

        if (timeLines.get(clickedTimelinePosition).getAction().equals("y")) {
            dataViewHolder.timelineLeftViewBar.setImageDrawable(parentContext.getResources().getDrawable(R.drawable.back_gray_round_3dp));
            dataViewHolder.placeNameTimeLine.setTextColor(parentContext.getColor(R.color.middleSoft_gray));
            dataViewHolder.date.setTextColor(parentContext.getColor(R.color.middleSoft_gray));
            dataViewHolder.someThingTimeLine.setTextColor(parentContext.getColor(R.color.middleSoft_gray));
            dataViewHolder.timelineCardView.setCardElevation(0);

        }

    }


    /**
     * 뷰홀더의 getAdapterPostion의 값을 timeline 데이터 포지션값으로 변경
     *
     * @param position
     */
    public static int setClickedPositionToTimelinePositionChange(int position) {
        ArrayList<TimeLine> timeLines = BottomNavi.timeLines;

        int result = 0;
        String clickedPositionDate = ((MyDateTime) dateItems.get(position)).getDate().substring(0, 16);
        for (int i = 0; i < BottomNavi.timeLines.size(); i++) {
            if (timeLines.get(i).getPlaceName().equals(((MyDateTime) dateItems.get(position)).getPlaceName())
                    && timeLines.get(i).getSomeThing().equals(((MyDateTime) dateItems.get(position)).getSomeThing())
                    && timeLines.get(i).getDate().contains(clickedPositionDate)) {
                result = i;
                break;
            }
        }

        Log.e(TAG, "setClickedPositionToTimelinePositionChange: result : " + result);
        return result;
    }


    @Override
    public int getItemCount() {
        return dateItems.size();
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }


}
