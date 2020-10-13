package com.example.workaddict.FollowInfo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.workaddict.BottomNavi;
import com.example.workaddict.DataClass.Privacy;
import com.example.workaddict.FirebaseDataCall.UserCall;
import com.example.workaddict.R;
import com.example.workaddict.Utility.Util;
import com.example.workaddict.databinding.ActivityFollowsInviteBinding;

import java.util.ArrayList;

public class FollowInvite extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Follows";
    public ActivityFollowsInviteBinding b;
    public static FollowInvite followInvite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_follows_invite);

        followInvite = this;
        Log.e(TAG, "onCreateView: ");
        initView();
    }


    private void initView() {
        setSupportActionBar(b.followsInviteToolbar);
        b.followsInviteToolbar.setTitle("친구초대");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        b.searchInviteEditText.requestFocus();
        setupListener();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //menu 클릭 이벤트를 설정합니다.

        switch (item.getItemId()) {
            case android.R.id.home:  // 왼쪽 상단 버튼 눌렀을 때
                onBackPressed();
                break;
            default:
                break;
        }

        return true;
    }

    private void setupListener() {
        b.closeInviteText.setOnClickListener(this);
        b.followSearchButton.setOnClickListener(this);

        b.searchInviteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count > 0) b.closeInviteText.setVisibility(View.VISIBLE);
                else b.closeInviteText.setVisibility(View.GONE);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) b.closeInviteText.setVisibility(View.VISIBLE);
                else b.closeInviteText.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                int textCount = b.searchInviteEditText.length();

                if (textCount > 0) {
                    b.closeInviteText.setVisibility(View.VISIBLE);
                } else {
                    b.closeInviteText.setVisibility(View.GONE);
                }


            }
        });


        b.searchInviteEditText.setOnEditorActionListener((v, actionId, event) -> {
            Log.e(TAG, "setupListener: ");
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH:
                    if (b.searchInviteEditText.getText().length() > 0) {
                        Util.hideKeyboard(this);
                        b.inviteRecyclerView.setAdapter(null);
                        new UserCall(b.searchInviteEditText.getText().toString(), b.inviteRecyclerView, this);
                        showProgressBar();

                    } else {
                        Toast.makeText(followInvite, "이름이나 아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    return false;
            }
            return true;
        });
    }

    public void removeItem(int position, ArrayList<Privacy> privacies) {
        FollowInvite.followInvite.b.inviteRecyclerView.getAdapter().notifyItemRemoved(position);
        FollowInvite.followInvite.b.inviteRecyclerView.getAdapter().notifyItemRangeChanged(position, privacies.size());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeInviteText:
                b.searchInviteEditText.setText("");
                b.inviteRecyclerView.setAdapter(null);
                break;
            case R.id.followSearchButton:
                if (b.searchInviteEditText.getText().length() > 0) {
                    b.inviteRecyclerView.setAdapter(null);
                    new UserCall(b.searchInviteEditText.getText().toString(), b.inviteRecyclerView, this);
                    Util.hideKeyboard(this);
                    showProgressBar();
                } else {
                    Toast.makeText(followInvite, "이름이나 아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        BottomNavi.bottomNavi.b.followInviteButton.setVisibility(View.GONE);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void showProgressBar() {
        b.progressBar.setVisibility(View.VISIBLE);
        b.inviteRecyclerView.setVisibility(View.INVISIBLE);
    }

    public void hideProgressBar() {
        b.progressBar.setVisibility(View.GONE);
        b.inviteRecyclerView.setVisibility(View.VISIBLE);

    }
}
