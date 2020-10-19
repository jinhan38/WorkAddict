package kr.co.workaddict.TimeLineClass;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;

import kr.co.workaddict.BottomFragment.TimeLinePage;
import kr.co.workaddict.Utility.Util;

public class AddTimeLineInputSomeThing extends ActivityCompat {

    private static final String TAG = "AddTimeLineInputSomThin";

    private EditText editText;
    private ImageView imageView;
    private ImageButton camera;
    private ImageButton gallery;
    private ImageButton delete;
    private Button button;
    private String category = "";
    private String placeName = "";
    private String date;
    private AddTimeLineContent activity;
    public static ProgressDialog dialog;


    public AddTimeLineInputSomeThing(EditText editText, ImageView imageView, ImageButton camera, ImageButton gallery, ImageButton delete, Button button, String category,
                                     String placeName, String date, AddTimeLineContent activity) {

        this.editText = editText;
        this.imageView = imageView;
        this.camera = camera;
        this.gallery = gallery;
        this.delete = delete;
        this.button = button;
        this.category = category;
        this.placeName = placeName;
        this.date = date;
        this.activity = activity;

        addTimeLineTask();

    }


    private void addTimeLineTask() {
        button.setOnClickListener(v -> {
            Util.addTimeLine(category, placeName, date, editText.getText().toString(), "타임라인 추가", activity, TimeLinePage.singlton.adapter);
        });

        delete.setOnClickListener(v -> {
            AddTimeLineContent.addTimeLineContent.imageFileInit();
            imageView.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            activity.imageExist = false;
        });

        gallery.setOnClickListener(v -> {
            AddTimeLineContent.PICK_NUM = AddTimeLineContent.PICK_FROM_ALBUM;
            AddTimeLineContent.addTimeLineContent.goToAlbum();
        });

        camera.setOnClickListener(v -> {
            AddTimeLineContent.PICK_NUM = AddTimeLineContent.PICK_FROM_CAMERA;
            AddTimeLineContent.addTimeLineContent.takePhoto();

        });
    }


}
