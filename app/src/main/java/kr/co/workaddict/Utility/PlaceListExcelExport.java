package kr.co.workaddict.Utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import kr.co.workaddict.DataClass.PlaceData;
import kr.co.workaddict.DataSort.SortByDatePlaceData;

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

public class PlaceListExcelExport {
    private Activity activity;
    private ArrayList<PlaceData> placeData = new ArrayList<>();
    private String ID;


    private final int CELL_NUM = 0;
    private final int CELL_PLACE_NAME = 1;
    private final int CELL_CATEGORY_NAME = 2;
    private final int CELL_FAVORITES = 3;
    private final int CELL_PHONE = 4;
    private final int CELL_DATE = 5;
    private final int CELL_ADDRESS = 6;
    private final int CELL_ADDRESS_ROAD = 7;


    public PlaceListExcelExport(Activity activity, ArrayList<PlaceData> placeData, String ID) {
        this.activity = activity;
        this.placeData = placeData;
        this.ID = ID;
        Collections.sort(placeData, new SortByDatePlaceData());
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
        cell = row.createCell(CELL_FAVORITES);
        cell.setCellValue("즐겨찾기");

        //5번 셀
        cell = row.createCell(CELL_PHONE);
        cell.setCellValue("연락처");

        //6번 셀
        cell = row.createCell(CELL_DATE);
        cell.setCellValue("날짜");

        //7번 셀
        cell = row.createCell(CELL_ADDRESS);
        cell.setCellValue("주소");

        //7번 셀
        cell = row.createCell(CELL_ADDRESS_ROAD);
        cell.setCellValue("주소(도로명)");


        for (int i = 0; i < placeData.size(); i++) {
            row = sheet.createRow(i + 1);


            //1번 셀
            cell = row.createCell(CELL_NUM);
            cell.setCellValue(i + 1);

            //2번 셀
            cell = row.createCell(CELL_PLACE_NAME);
            cell.setCellValue(placeData.get(i).getPlaceName());

            //3번 셀
            cell = row.createCell(CELL_CATEGORY_NAME);
            cell.setCellValue(placeData.get(i).getCategoryName());

            //4번 셀
            cell = row.createCell(CELL_FAVORITES);
            cell.setCellValue(placeData.get(i).getFavorites());

            //5번 셀
            cell = row.createCell(CELL_PHONE);
            String phone = placeData.get(i).getPhone();
            cell.setCellValue(phone.length() > 0 ? phone : "-");


            //6번 셀
            cell = row.createCell(CELL_DATE);
            cell.setCellValue(placeData.get(i).getDateTime());

            //7번 셀
            cell = row.createCell(CELL_ADDRESS);
            String address = placeData.get(i).getAddress();
            cell.setCellValue(address.length() > 0 ? address : "-");

            //7번 셀
            cell = row.createCell(CELL_ADDRESS_ROAD);
            String address_road = placeData.get(i).getRoadAddress();
            cell.setCellValue(address_road.length() > 0 ? address_road : "-");

        }


        File excelFile = new File(activity.getExternalFilesDir(null), "placeData_" + ID + ".xls");

        try {
            FileOutputStream fos = new FileOutputStream(excelFile);
            workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri path = FileProvider.getUriForFile(activity, "kr.co.workaddict.provider", excelFile);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/excel");
        shareIntent.putExtra(Intent.EXTRA_STREAM, path);
        activity.startActivity(Intent.createChooser(shareIntent, "엑셀 내보내기"));
//        Toast.makeText(this, excelFile.getAbsolutePath() + "에 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

}
