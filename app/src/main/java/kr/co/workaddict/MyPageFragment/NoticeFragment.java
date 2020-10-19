package kr.co.workaddict.MyPageFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.co.workaddict.BottomNavi;

import kr.co.workaddict.R;

import kr.co.workaddict.Utility.Util;
import kr.co.workaddict.databinding.ActivityNoticeFragmentBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NoticeFragment extends Fragment implements View.OnClickListener {
    private ActivityNoticeFragmentBinding b;
    private ArrayList<NoticeData> noticeData = new ArrayList<>();
    private static final String TAG = "NoticeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = DataBindingUtil.inflate(inflater, R.layout.activity_notice_fragment, container, false);
        initView();
        setupListener();
        return b.getRoot();
    }

    private void initView() {

        b.noticeProgressbar.setVisibility(View.VISIBLE);
        noticeData.clear();

        DatabaseReference myRef = Util.getFirebaseDatabase().getReference("Notice");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        noticeData.add(dataSnapshot1.getValue(NoticeData.class));
                        Log.e(TAG, "onDataChange: 공지사항 포문" );
                    }
                    Collections.sort(noticeData, new SortByNoticeDate());
                    Collections.reverse(noticeData);
                    Log.e(TAG, "onDataChange: 데이터받기 끝" );
                    b.noticeRecyclerView.setHasFixedSize(true);
                    b.noticeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    b.noticeRecyclerView.setAdapter(new NoticeRecyclerViewAdapter(noticeData));
                    b.noticeProgressbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setupListener() {
        BottomNavi.bottomNavi.b.ibMapPageBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibMapPageBack:
                BottomNavi.bottomNavi.onBackPressed();
                break;
        }
    }

}


class NoticeRecyclerViewAdapter extends RecyclerView.Adapter<NoticeRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<NoticeData> noticeData = new ArrayList<>();


    public NoticeRecyclerViewAdapter(ArrayList<NoticeData> noticeData) {
        this.noticeData = noticeData;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView content;
        private TextView date;


        public MyViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.noticeTextViewTitle);
            content = view.findViewById(R.id.noticeTextViewContent);
            date = view.findViewById(R.id.noticeTextViewDate);
        }
    }

    @NonNull
    @Override
    public NoticeRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_recyclerview_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeRecyclerViewAdapter.MyViewHolder holder, int position) {

        holder.title.setText(noticeData.get(position).getTitle());
        holder.content.setText(noticeData.get(position).getContent());
        holder.date.setText(noticeData.get(position).getDate());

    }

    @Override
    public int getItemCount() {
        return noticeData.size();
    }

}


class NoticeData {

    private String title;
    private String content;
    private String date;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
class SortByNoticeDate implements Comparator<NoticeData> {


    @Override
    public int compare(NoticeData o1, NoticeData o2) {
        return o1.getDate().compareTo(o2.getDate());
    }
}