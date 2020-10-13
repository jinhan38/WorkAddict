package com.example.workaddict.Utility;

import android.app.Activity;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class KeyBoardEnter {
    private static final String TAG = "keyBoardEnter";

    public KeyBoardEnter(EditText editText, Activity activity) {
        keyboardEnterChange(editText, activity);
    }

    public void keyboardEnterChange(EditText editText, Activity activity){
        editText.setOnEditorActionListener((v, actionId, event) -> {
            Log.e(TAG, "setupListener: ");
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH:
                    Util.hideKeyboard(activity);
                    break;
                default:
                    return false;
            }
            return true;
        });
    }
}
