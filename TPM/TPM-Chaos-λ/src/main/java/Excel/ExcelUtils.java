package Excel;


import java.io.File;
import java.io.IOException;

/**
 * @author : wangxuan
 * @date : 14:51 2020/6/26 0026
 */
public class ExcelUtils {
//    private File file;
//    public ExcelUtils(){
//        file = new File("C:\\Users\\Administrator\\Desktop\\TPM\\src\\Excel\\Out.xls");
//    }
//    public ExcelUtils(String path){
//        file = new File(path);
//    }
//    public void Write(Object[] data, int row) throws IOException, WriteException {
//        // 创建一个工作簿
//        WritableWorkbook workbook = Workbook.createWorkbook(file);
//        // 创建一个工作表
//        WritableSheet sheet = workbook.createSheet("sheet1", 0);
//        for (int i = 0 ; i<data.length;i++)
//        {
//            sheet.addCell(new Label(row, i, String.valueOf(data[i])));
//        }
//        workbook.write();
//        workbook.close();
//    }
//
//    public void read(String path) throws BiffException, IOException
//    {
//        File xlsFile = new File(path);
//        // 获得工作簿对象
//        Workbook workbook = Workbook.getWorkbook(xlsFile);
//        // 获得所有工作表
//        Sheet[] sheets = workbook.getSheets();
//        // 遍历工作表
//        if (sheets != null)
//        {
//            for (Sheet sheet : sheets)
//            {
//                // 获得行数
//                int rows = sheet.getRows();
//                // 获得列数
//                int cols = sheet.getColumns();
//                // 读取数据
//                for (int row = 0; row < rows; row++)
//                {
//                    for (int col = 0; col < cols; col++)
//                    {
//                        System.out.printf("%10s", sheet.getCell(col, row)
//                                .getContents());
//                    }
//                    System.out.println();
//                }
//            }
//        }
//        workbook.close();
//    }
//    public static void main(String[] args) throws IOException, WriteException {
//        double[] data = {0.9,0.1,0.2,0.3};
//        ExcelUtils excelUtils = new ExcelUtils();
//        excelUtils.Write(data,3);
//        excelUtils.Write(data,4);
//    }
}
