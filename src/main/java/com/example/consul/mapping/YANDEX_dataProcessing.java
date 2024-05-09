package com.example.consul.mapping;

import joinery.DataFrame;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YANDEX_dataProcessing {

    private static Integer findRowNumberByString(Sheet sheet, String name) {
        int rowIndex = -1;

        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell.getCellType() == CellType.STRING) {
                    if (cell.getStringCellValue().equals(name)) {
                        rowIndex = row.getRowNum();
                        break;
                    }
                }
            }
        }

        return rowIndex;
    }

    static public DataFrame<Object> getDataFromSheet(String fileName, Integer sheetNum) throws IOException {

        File file = new File(fileName);
        Workbook wb = WorkbookFactory.create(file);
        Sheet sheet = wb.getSheetAt(sheetNum);
        List<String> listHeader = new ArrayList<>();
        List<Object> listData = new ArrayList<>();
        String columnName = "Номер заказа";

        for (int i = 0; i < sheet.getRow(findRowNumberByString(sheet, columnName)).getPhysicalNumberOfCells(); i++) {
            listHeader.add(sheet.getRow(findRowNumberByString(sheet, columnName)).getCell(i).getStringCellValue());
        }

        DataFrame<Object> df = new DataFrame<>(listHeader);

        for(int j = findRowNumberByString(sheet,columnName)+1; j < (findRowNumberByString(sheet, columnName)>10 ? sheet.getLastRowNum() : sheet.getLastRowNum()+1); j++){
            for(int i = 0; i < sheet.getRow(j).getPhysicalNumberOfCells(); i++){
                switch (sheet.getRow(j).getCell(i).getCellType()) {
                    case BLANK, NUMERIC -> listData.add(sheet.getRow(j).getCell(i).getNumericCellValue());
                    case BOOLEAN -> listData.add(sheet.getRow(j).getCell(i).getBooleanCellValue());
                    case STRING -> listData.add(sheet.getRow(j).getCell(i).getStringCellValue());
                }
            }
            df.append(listData);
            listData = new ArrayList<>();
        }

        return df;
    }

    public static DataFrame<Object> getDeliveredData(DataFrame<Object> df) throws IOException {
        return df.groupBy(3)
                .sum()
                .retain(2,8);
    }

    public static DataFrame<Object> getReturnData(DataFrame<Object> df) throws IOException {
        return df.groupBy(3)
                .sum()
                .retain(3,8);
    }

    public static DataFrame<Object> getFavorData(DataFrame<Object> dfFavor, DataFrame<Object> dfDelivery) throws IOException {
        return dfFavor.reindex(7)
                .join(dfDelivery.reindex(0)
                        .retain(2), DataFrame.JoinType.RIGHT)
                .groupBy(0)
                .sum()
                .retain(7,9,10,12,14,15,17);
    }
}
