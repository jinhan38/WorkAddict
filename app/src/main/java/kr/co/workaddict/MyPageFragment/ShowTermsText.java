package kr.co.workaddict.MyPageFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.widget.Toast;

import kr.co.workaddict.R;
import kr.co.workaddict.databinding.ActivityShowTermsTextBinding;

public class ShowTermsText extends AppCompatActivity {
    private ActivityShowTermsTextBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_show_terms_text);

        b.myPageToolbarWrap.setOnClickListener(v -> {
            onBackPressed();
        });

        switch (TermsFragment.CURRENT_TERM_NUMBER) {
            case TermsFragment.TERM_FIRST_NUM:
                Toast.makeText(this, "첫번째", Toast.LENGTH_SHORT).show();
                break;
            case TermsFragment.TERM_SECOND_NUM:
                Toast.makeText(this, "두번째", Toast.LENGTH_SHORT).show();
                break;
            case TermsFragment.TERM_THIRD_NUM:
                Toast.makeText(this, "세번째", Toast.LENGTH_SHORT).show();
                break;
            case TermsFragment.TERM_FOURTH_NUM:
                Toast.makeText(this, "네번째", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

    }
}