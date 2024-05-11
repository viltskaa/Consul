package com.example.consul.mapping;

import joinery.DataFrame;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YANDEX_dataProcessing {

    // Разгруппировка строк и запись названия заголовка ниже (если строка ниже пустая)
    private static void ungroupCells(Sheet sheet) {
        for (int i = sheet.getNumMergedRegions() - 1; i >= 0; i--) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);

            int mergedRow = mergedRegion.getFirstRow();
            int mergedColumn = mergedRegion.getFirstColumn();

            Row row = sheet.getRow(mergedRow);
            Cell mergedCell = row.getCell(mergedColumn);

            if (mergedCell != null) {
                String cellValue = mergedCell.getStringCellValue();
                Row nextRow = sheet.getRow(mergedRow + 1);
                if (nextRow != null) {
                    Cell nextCell = nextRow.getCell(mergedColumn);
                    if (nextCell != null && nextCell.getCellType() == CellType.BLANK) {
                        nextCell.setCellValue(cellValue);
                        mergedCell.setCellValue("");
                    }
                }

                sheet.removeMergedRegion(i);
            }
        }
    }

    // Если на листе есть автофильтр, вернуть строку, где содержится автофильтр (нахождение расположения заголовков)
    private static Integer findAutofilterRow(Sheet sheet) {
        if (sheet instanceof XSSFSheet) {
            XSSFSheet xssfSheet = (XSSFSheet) sheet;
            if (xssfSheet.getCTWorksheet().getAutoFilter() != null) {
                AreaReference ref = new AreaReference(xssfSheet.getCTWorksheet().getAutoFilter().getRef(), SpreadsheetVersion.EXCEL2007);
                CellReference firstCell = ref.getFirstCell();
                return firstCell.getRow();
            }
        }
        return null;
    }

    // Получить заголовки для датафрейма
    private static List<String> getTitleForDataFrame(Sheet sheet){
        List<String> listHeader = new ArrayList<>();
        int numSuffix = 1;

        for (int i = 0; i < sheet.getRow(findAutofilterRow(sheet)).getPhysicalNumberOfCells(); i++) {
            String cellValue = sheet.getRow(findAutofilterRow(sheet)).getCell(i).getStringCellValue();

            if (listHeader.contains(cellValue)) {
                String newCellValue = cellValue + numSuffix;
                numSuffix++;
                listHeader.add(newCellValue);
            } else {
                listHeader.add(cellValue);
            }
        }

        return listHeader;
    }

    private static DataFrame<Object> setDataToDataFrame(Sheet sheet, DataFrame<Object> df){
        List<Object> listData = new ArrayList<>();

        for(int j = findAutofilterRow(sheet) + 1; j < sheet.getLastRowNum() + 1; j++){
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

    static public DataFrame<Object> getDataFromSheet(String fileName, Integer sheetNum) throws IOException {

        File file = new File(fileName);
        Workbook wb = WorkbookFactory.create(file);
        Sheet sheet = wb.getSheetAt(sheetNum);

        ungroupCells(sheet);

        DataFrame<Object> df = new DataFrame<>(getTitleForDataFrame(sheet));

        return setDataToDataFrame(sheet, df);
    }

    public static DataFrame<Object> getDeliveredData(DataFrame<Object> df) throws IOException {
        return df.head(df.length()-1)
                .groupBy(3)
                .sum()
                .retain(2,8)
                .rename("Цена с НДС с учётом всех скидок, руб. за шт.","Начислено");
    }

    public static DataFrame<Object> getReturnData(DataFrame<Object> df) throws IOException {
        return df.head(df.length()-1)
                .groupBy(3)
                .sum()
                .retain(3,8)
                .rename("Цена с НДС с учётом всех скидок, руб. за шт.","Стоимость возврата");
    }

    public static DataFrame<Object> getPlacingOnShowcase(DataFrame<Object> df) throws IOException {
        df = df.groupBy(9)
                .sum()
                .retain(33);

        return df.tail(df.length()-1)
                .rename("Стоимость услуги без скидок и наценок (гр.34=гр.12*гр.27), ₽","Размещение товаров на витрине");
    }

    public static DataFrame<Object> getDeliveryToConsumer(DataFrame<Object> df) throws IOException {
        return df.groupBy(8)
                .sum()
                .retain(21)
                .rename("Стоимость услуги, ₽","Доставка покупателю");
    }

    public static DataFrame<Object> getAcceptAndTransferPayment(DataFrame<Object> dfAccept,DataFrame<Object> dfTransfer) throws IOException {
        DataFrame<Object> dfJoin = dfAccept.groupBy(8)
                                            .sum()
                                            .retain(6)
                                            .join(dfTransfer.groupBy(8)
                                                            .sum()
                                                            .retain(6),
                                                    DataFrame.JoinType.OUTER);

        return dfJoin.transpose()
                .add(0)
                .groupBy(dfJoin.length())
                .sum()
                .transpose()
                .tail(dfJoin.length())
                .rename(null,"Приём и перевод платежа покупателя");
    }

    public static DataFrame<Object> getLoyaltyProgram(DataFrame<Object> df) throws IOException {
        return df.groupBy(8)
                .sum()
                .retain(9)
                .rename("Стоимость услуги, ₽","Программа лояльности");
    }

    public static DataFrame<Object> getBoostSales(DataFrame<Object> df) throws IOException {
        return df.groupBy(8)
                .sum()
                .retain(8)
                .rename("Постоплата, ₽","Буст продаж");
    }

    public static DataFrame<Object> getAllData(Map<String, List<Integer>> files) throws IOException {
        DataFrame<Object> dataFrame = new DataFrame<>();

        for (Map.Entry<String, List<Integer>> file : files.entrySet()) {
            String fileName = file.getKey();
            List<Integer> fileLists = file.getValue();

            DataFrame<Object> tempDataFrame = new DataFrame<>();

            for (Integer list : fileLists) {
                switch (fileName) {
                    case "Отчет по стоимости услуг.xlsx":
                        switch (list) {
                            case 1 ->
                                    tempDataFrame = tempDataFrame.join(getPlacingOnShowcase(getDataFromSheet(fileName, list)), DataFrame.JoinType.OUTER);
                            case 3 ->
                                    tempDataFrame = tempDataFrame.join(getLoyaltyProgram(getDataFromSheet(fileName, list)), DataFrame.JoinType.OUTER);
                            case 4 ->
                                    tempDataFrame = tempDataFrame.join(getBoostSales(getDataFromSheet(fileName, list)), DataFrame.JoinType.OUTER);
                            case 8 ->
                                    tempDataFrame = tempDataFrame.join(getDeliveryToConsumer(getDataFromSheet(fileName, list)), DataFrame.JoinType.OUTER);
                            case 11 ->
                                    tempDataFrame = tempDataFrame.join(getAcceptAndTransferPayment(getDataFromSheet(fileName, list), getDataFromSheet(fileName, list + 1)), DataFrame.JoinType.OUTER);
                        }
                        break;
                    case "Отчет по реализации.xlsx":
                        switch (list) {
                            case 2 ->
                                    tempDataFrame = tempDataFrame.join(getDeliveredData(getDataFromSheet(fileName, list)), DataFrame.JoinType.OUTER);
                            case 4 ->
                                    tempDataFrame = tempDataFrame.join(getReturnData(getDataFromSheet(fileName, list)), DataFrame.JoinType.OUTER);
                        }
                        break;
                }
            }
            dataFrame = dataFrame.join(tempDataFrame, DataFrame.JoinType.OUTER);
        }

        return dataFrame;
    }
}
