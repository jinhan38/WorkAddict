package kr.co.workaddict.TimeLineClass;

import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

public class AddTimeLineDatePicker {

    private static final String TAG = "AddTimeLineDatePicker";
    private DatePicker datePicker;
    public String choiceDate;


    public AddTimeLineDatePicker(DatePicker datePicker) {
        this.datePicker = datePicker;
        setDatePicker();
    }


    private void setDatePicker() {
        Calendar calendar = Calendar.getInstance();

        datePicker.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                (view, pickedYear, monthOfYear, dayOfMonth) -> {

                    int plusMonth = monthOfYear + 1;

                    String strMonth = "";
                    String strDay = "";

                    if (plusMonth < 10) strMonth = "0" + plusMonth;
                    else strMonth = plusMonth + "";

                    if (dayOfMonth < 10) strDay = "0" + dayOfMonth;
                    else strDay = String.valueOf(dayOfMonth);

                    choiceDate = pickedYear + "-" + strMonth + "-" + strDay;

                    Log.e(TAG, "setDatePicker: choiceDAta : " + choiceDate);

                    AddTimeLineContent.addTimeLineContent.pickedDate = choiceDate;
                    AddTimeLineContent.addTimeLineContent.pageChange(AddTimeLineContent.SET_TIME);

                });
    }

}
