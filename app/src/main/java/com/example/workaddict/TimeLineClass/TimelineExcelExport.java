package com.example.workaddict.TimeLineClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.workaddict.DataClass.PlaceData;
import com.example.workaddict.DataClass.TimeLine;
import com.example.workaddict.DataSort.SortByDateTimeLine;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class TimelineExcelExport {
    private Activity activity;
    private ArrayList<TimeLine> timeLines = new ArrayList<>();
    private ArrayList<PlaceData> placeData = new ArrayList<>();
    private String ID;


    private final int CELL_NUM = 0;
    private final int CELL_PLACE_NAME = 1;
    private final int CELL_CATEGORY_NAME = 2;
    private final int CELL_SOMETHING = 3;
    private final int CELL_PHONE = 4;
    private final int CELL_ACTION = 5;
    private final int CELL_DATE = 6;
    private final int CELL_ADDRESS = 7;
    private final int CELL_ADDRESS_ROAD = 8;


    public TimelineExcelExport(Activity activity, ArrayList<TimeLine> timeLines, ArrayList<PlaceData> placeData, String ID) {
        this.activity = activity;
        this.timeLines = timeLines;
        this.placeData = placeData;
        this.ID = ID;
        Collections.sort(timeLines, new SortByDateTimeLine());
        checkPermissions();
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            saveExcel();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(activity, "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    };


    @SuppressLint("ObsoleteSdkInt")
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) { // 마시멜로(안드로이드 6.0) 이상 권한 체크
            TedPermission.with(activity)
                    .setPermissionListener(permissionlistener)
                    .setRationaleMessage("공유 기능을 이용하기 위해서는 접근 권한이 필요합니다")
                    .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다...\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
                    .setPermissions(new String[]{
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE // 기기, 사진, 미디어, 파일 엑세스 권한
                    })
                    .check();

        } else {
            saveExcel();
        }
    }


    private void saveExcel() {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Row row = sheet.createRow(0);
        Cell cell;

        //1번 셀
        cell = row.createCell(CELL_NUM);
        cell.setCellValue("No");

        //2번 셀
        cell = row.createCell(CELL_PLACE_NAME);
        cell.setCellValue("장소명");

        //3번 셀
        cell = row.createCell(CELL_CATEGORY_NAME);
        cell.setCellValue("카테고리");

        //4번 셀
        cell = row.createCell(CELL_SOMETHING);
        cell.setCellValue("내용");

        //5번 셀
        cell = row.createCell(CELL_PHONE);
        cell.setCellValue("연락처");

        //6번 셀
        cell = row.createCell(CELL_ACTION);
        cell.setCellValue("실행여부");

        //7번 셀
        cell = row.createCell(CELL_DATE);
        cell.setCellValue("날짜");

        //7번 셀
        cell = row.createCell(CELL_ADDRESS);
        cell.setCellValue("주소");

        //8번 셀
        cell = row.createCell(CELL_ADDRESS_ROAD);
        cell.setCellValue("주소(도로명)");


        for (int i = 0; i < timeLines.size(); i++) {
            row = sheet.createRow(i + 1);


            String phone = "";
            String address = "";
            String address_road = "";
            for (int k = 0; k < placeData.size(); k++) {
                if (placeData.get(k).getPlaceName().equals(timeLines.get(i).getPlaceName())) {

                    if (placeData.get(k).getPhone().length() > 0)
                        phone = placeData.get(k).getPhone();
                    else phone = "-";

                    if (placeData.get(k).getAddress().length() > 0)
                        address = placeData.get(k).getAddress();
                    else address = "-";

                    if (placeData.get(k).getRoadAddress().length() > 0)
                        address_road = placeData.get(k).getRoadAddress();
                    else address_road = "-";

                    break;

                }

            }


            //1번 셀
            cell = row.createCell(CELL_NUM);
            cell.setCellValue(i + 1);

            //2번 셀
            cell = row.createCell(CELL_PLACE_NAME);
            cell.setCellValue(timeLines.get(i).getPlaceName());

            //3번 셀
            cell = row.createCell(CELL_CATEGORY_NAME);
            cell.setCellValue(timeLines.get(i).getCategoryName());

            //4번 셀
            cell = row.createCell(CELL_SOMETHING);
            cell.setCellValue(timeLines.get(i).getSomeThing());

            //5번 셀

            cell = row.createCell(CELL_PHONE);
            cell.setCellValue(phone);


            //6번 셀
            cell = row.createCell(CELL_ACTION);
            cell.setCellValue(timeLines.get(i).getAction());

            //7번 셀
            cell = row.createCell(CELL_DATE);
            cell.setCellValue(timeLines.get(i).getDate());

            //7번 셀
            cell = row.createCell(CELL_ADDRESS);
            cell.setCellValue(address);

            //8번 셀
            cell = row.createCell(CELL_ADDRESS_ROAD);
            cell.setCellValue(address_road);
        }


        File excelFile = new File(activity.getExternalFilesDir(null), "timeline_"+ ID + ".xls");

        try {
            FileOutputStream fos = new FileOutputStream(excelFile);
            workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri path = FileProvider.getUriForFile(activity, "com.example.workaddict.provider", excelFile);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/excel");
        shareIntent.putExtra(Intent.EXTRA_STREAM, path);
        activity.startActivity(Intent.createChooser(shareIntent, "엑셀 내보내기"));
//        Toast.makeText(this, excelFile.getAbsolutePath() + "에 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

}
