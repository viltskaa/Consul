package com.example.consul.mapping;

import joinery.DataFrame;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
        if (sheet instanceof XSSFSheet xssfSheet) {
            if (xssfSheet.getCTWorksheet().getAutoFilter() != null) {
                AreaReference ref = new AreaReference(xssfSheet.getCTWorksheet().getAutoFilter().getRef(), SpreadsheetVersion.EXCEL2007);
                CellReference firstCell = ref.getFirstCell();
                return firstCell.getRow();
            }
        }
        return null;
    }

    // Получить заголовки для датафрейма
    private static List<String> getTitleForDataFrame(Sheet sheet) {
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

    private static DataFrame<Object> setDataToDataFrame(Sheet sheet, DataFrame<Object> df) {
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

    public static DataFrame<Object> getDataFromInputStream(InputStream inputStream) throws IOException {
        Workbook wb = WorkbookFactory.create(inputStream);
        DataFrame<Object> mainDataFrame = new DataFrame<>();

        if (wb.getNumberOfSheets()>5) {
            for (int list = 0; list < wb.getNumberOfSheets(); list++) {
                switch (list) {
                    case 1:
                        Sheet sheet = wb.getSheetAt(list);
                        ungroupCells(sheet);
                        mainDataFrame = getPlacingOnShowcase(setDataToDataFrame(sheet, new DataFrame<>(getTitleForDataFrame(sheet))));
                        break;
                    case 3:
                        sheet = wb.getSheetAt(list);
                        mainDataFrame = mainDataFrame.join(getLoyaltyProgram(setDataToDataFrame(sheet, new DataFrame<>(getTitleForDataFrame(sheet)))),
                                                            DataFrame.JoinType.OUTER);
                        break;
                    case 4:
                        sheet = wb.getSheetAt(list);
                        mainDataFrame = mainDataFrame.join(getBoostSales(setDataToDataFrame(sheet, new DataFrame<>(getTitleForDataFrame(sheet)))),
                                                            DataFrame.JoinType.OUTER);
                        break;
                    case 8:
                        sheet = wb.getSheetAt(list);
                        mainDataFrame = mainDataFrame.join(getDeliveryToConsumer(setDataToDataFrame(sheet, new DataFrame<>(getTitleForDataFrame(sheet)))),
                                                            DataFrame.JoinType.OUTER);
                        break;
                    case 11:
                        sheet = wb.getSheetAt(list);
                        Sheet sheet2 = wb.getSheetAt(list + 1);
                        mainDataFrame = mainDataFrame.join(getAcceptAndTransferPayment(setDataToDataFrame(sheet, new DataFrame<>(getTitleForDataFrame(sheet))),
                                                                                        setDataToDataFrame(sheet2, new DataFrame<>(getTitleForDataFrame(sheet2)))),
                                                            DataFrame.JoinType.OUTER);
                        break;
                    case 18:
                        sheet = wb.getSheetAt(list);
                        sheet2 = wb.getSheetAt(list - 10);
                        Sheet sheet3 = wb.getSheetAt(list - 7);
                        mainDataFrame = mainDataFrame.join(getFavorSortingCenter(setDataToDataFrame(sheet, new DataFrame<>(getTitleForDataFrame(sheet))),
                                                                                    setDataToDataFrame(sheet2, new DataFrame<>(getTitleForDataFrame(sheet2))),
                                                                                    setDataToDataFrame(sheet3, new DataFrame<>(getTitleForDataFrame(sheet3)))),
                                                            DataFrame.JoinType.OUTER);
                        break;
                }
            }
        }
        else {
            for (int list = 0; list < wb.getNumberOfSheets(); list++) {
                switch (list) {
                    case 2:
                        Sheet sheet = wb.getSheetAt(list);
                        mainDataFrame = getDeliveredData(setDataToDataFrame(sheet, new DataFrame<>(getTitleForDataFrame(sheet))));
                        break;
                    case 4:
                        sheet = wb.getSheetAt(list);
                        mainDataFrame = mainDataFrame.join(getReturnData(setDataToDataFrame(sheet, new DataFrame<>(getTitleForDataFrame(sheet)))),
                                                            DataFrame.JoinType.OUTER);
                        break;
                }
            }
        }

        return mainDataFrame;
    }

    private static DataFrame<Object> getFavorSortingCenter(DataFrame<Object> dfSortingCenter,
                                                          DataFrame<Object> dfDelivery,
                                                          DataFrame<Object> dfPayment) {
        return dfDelivery.concat(dfPayment)
                .groupBy(7,8)
                .sum()
                .reindex(0)
                .join(dfSortingCenter.groupBy(7).sum(), DataFrame.JoinType.OUTER)
                .groupBy(0)
                .sum()
                .retain(31)
                .rename("Тариф за заказ или отправление, ₽", "Услуги по обработке в сортировочном центре");
    }

    private static DataFrame<Object> getDeliveredData(DataFrame<Object> df) {
        return df.head(df.length()-1)
                .groupBy(3)
                .sum()
                .retain(2,8)
                .rename("Цена с НДС с учётом всех скидок, руб. за шт.","Начислено");
    }

    private static DataFrame<Object> getReturnData(DataFrame<Object> df) {
        return df.head(df.length()-1)
                .groupBy(3)
                .sum()
                .retain(3,8)
                .rename("Цена с НДС с учётом всех скидок, руб. за шт.","Стоимость возврата");
    }

    private static DataFrame<Object> getPlacingOnShowcase(DataFrame<Object> df) {
        df = df.groupBy(9)
                .sum()
                .retain(33);

        return df.tail(df.length()-1)
                .rename("Стоимость услуги без скидок и наценок (гр.34=гр.12*гр.27), ₽","Размещение товаров на витрине");
    }

    private static DataFrame<Object> getDeliveryToConsumer(DataFrame<Object> df) {
        return df.groupBy(8)
                .sum()
                .retain(21)
                .rename("Стоимость услуги, ₽","Доставка покупателю");
    }

    private static DataFrame<Object> getAcceptAndTransferPayment(DataFrame<Object> dfAccept,DataFrame<Object> dfTransfer) {
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

    private static DataFrame<Object> getLoyaltyProgram(DataFrame<Object> df) {
        return df.groupBy(8)
                .sum()
                .retain(9)
                .rename("Стоимость услуги, ₽","Программа лояльности");
    }

    private static DataFrame<Object> getBoostSales(DataFrame<Object> df) {
        return df.groupBy(8)
                .sum()
                .retain(8)
                .rename("Постоплата, ₽","Буст продаж");
    }

}
