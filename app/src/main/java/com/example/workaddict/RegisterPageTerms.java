package com.example.workaddict;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.workaddict.Utility.Util;
import com.example.workaddict.databinding.ActivityRegisterPageTermsBinding;

public class RegisterPageTerms extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterPageTerms";
    private ActivityRegisterPageTermsBinding b;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_register_page_terms);


        Util.setTextColor(this, R.string.tv_term_1, b.checkBox1, getResources().getColor(R.color.my_red_light), 0, 4);
        Util.setTextColor(this, R.string.tv_term_2, b.checkBox2, getResources().getColor(R.color.my_red_light), 0, 4);
        Util.setTextColor(this, R.string.tv_term_3, b.checkBox3, getResources().getColor(R.color.my_red_light), 0, 4);
        Util.setTextColor(this, R.string.tv_term_4, b.checkBox4, getResources().getColor(R.color.pion_basic_soft_blue), 0, 4);

        b.ibtnBack.setOnClickListener(this);
        b.checkBox1.setOnClickListener(this);
        b.checkBox2.setOnClickListener(this);
        b.checkBox3.setOnClickListener(this);
        b.checkBox4.setOnClickListener(this);
        b.checkBoxAll.setOnClickListener(this);
        b.btnNextInTerms.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtnBack:
                onBackPressed();
                break;
            case R.id.checkBox1:
            case R.id.checkBox2:
            case R.id.checkBox3:
            case R.id.checkBox4:
                checkBoxControl();
                break;
            case R.id.checkBoxAll:
                if (b.checkBoxAll.isChecked()) {
                    setCheckBoxAll(true);
                    b.btnNextInTerms.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_purple_round_3dp));
                } else {
                    b.btnNextInTerms.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_gray_round_3dp));
                    setCheckBoxAll(false);
                }
                break;
            case R.id.btnNextInTerms:
                if (b.checkBox1.isChecked() && b.checkBox2.isChecked() && b.checkBox3.isChecked()) {
                    startActivity(new Intent(this, Registration.class));
                } else {
                    Toast.makeText(this, "약관에 동의해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }

    private void setCheckBoxAll(boolean show) {
        b.checkBox1.setChecked(show);
        b.checkBox2.setChecked(show);
        b.checkBox3.setChecked(show);
        b.checkBox4.setChecked(show);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void checkBoxControl() {
        if (b.checkBox1.isChecked() && b.checkBox2.isChecked() && b.checkBox3.isChecked() && b.checkBox4.isChecked()) {
            b.checkBoxAll.setChecked(true);
            b.btnNextInTerms.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_purple_round_3dp));
        } else if (b.checkBox1.isChecked() && b.checkBox2.isChecked() && b.checkBox3.isChecked()) {
            b.btnNextInTerms.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_purple_round_3dp));
        } else {
            b.checkBoxAll.setChecked(false);
            b.btnNextInTerms.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_gray_round_3dp));
        }
    }

}
