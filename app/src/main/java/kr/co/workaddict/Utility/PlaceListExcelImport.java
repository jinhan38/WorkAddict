//package kr.co.workaddict.Utility;
//
//import android.app.Activity;
//
//import org.apache.poi.xssf.usermodel.XSSFCell;
//import org.apache.poi.xssf.usermodel.XSSFRow;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//
//public class PlaceListExcelImport {
//    private Activity activity;
//
//
//    // https://huskdoll.tistory.com/890
//    FileInputStream file = null;
//        try
//
//    {
//        file = new FileInputStream("D:/tmp/upload/right_excel/test.xlsx");
//    } catch(
//    FileNotFoundException ex)
//
//    {
//        ex.printStackTrace();
//    }
//
//    XSSFWorkbook workbook = new XSSFWorkbook(file);
//
//    int rowindex = 0;
//    int columnindex = 0;
//    //시트 수 (첫번째에만 존재하므로 0을 준다)
//    //만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
//    XSSFSheet sheet = workbook.getSheetAt(0);
//    //행의 수
//    int rows = sheet.getPhysicalNumberOfRows();
//        for(rowindex=0;rowindex<rows;rowindex++)
//
//    {
//        //행을읽는다
//        XSSFRow row = sheet.getRow(rowindex);
//        if (row != null) {
//            //셀의 수
//            int cells = row.getPhysicalNumberOfCells();
//            for (columnindex = 0; columnindex <= cells; columnindex++) {
//                //셀값을 읽는다
//                XSSFCell cell = row.getCell(columnindex);
//                String value = "";
//                //셀이 빈값일경우를 위한 널체크
//                if (cell == null) {
//                    continue;
//                } else {
//                    //타입별로 내용 읽기
//                    switch (cell.getCellType()) {
//                        case XSSFCell.CELL_TYPE_FORMULA:
//                            value = cell.getCellFormula();
//                            break;
//                        case XSSFCell.CELL_TYPE_NUMERIC:
//                            value = cell.getNumericCellValue() + "";
//                            break;
//                        case XSSFCell.CELL_TYPE_STRING:
//                            value = cell.getStringCellValue() + "";
//                            break;
//                        case XSSFCell.CELL_TYPE_BLANK:
//                            value = cell.getBooleanCellValue() + "";
//                            break;
//                        case XSSFCell.CELL_TYPE_ERROR:
//                            value = cell.getErrorCellValue() + "";
//                            break;
//                    }
//                }
//                System.out.println(rowindex + "번 행 : " + columnindex + "번 열 값은: " + value);
//            }
//
//        }
//    }
//
//
//    public PlaceListExcelImport() throws IOException {
//    }
//}
