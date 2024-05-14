package com.example.consul.mapping;

import com.example.consul.document.models.YANDEX_TableRow;
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
import java.util.concurrent.CompletableFuture;

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

    private static DataFrame<Object> getDataFromServiceInputStream(InputStream inputStream) throws IOException {
        DataFrame<Object> mainDataFrame;
        Workbook wb = WorkbookFactory.create(inputStream);

        final Sheet[] sheet = {wb.getSheetAt(1), wb.getSheetAt(3), wb.getSheetAt(4), wb.getSheetAt(8),
                wb.getSheetAt(11), wb.getSheetAt(18), wb.getSheetAt(12)};

        CompletableFuture<DataFrame<Object>> placingOnShowcaseCompletableFuture = CompletableFuture
                .supplyAsync(() -> {
                    ungroupCells(sheet[0]);
                    return getPlacingOnShowcase(setDataToDataFrame(sheet[0], new DataFrame<>(getTitleForDataFrame(sheet[0]))));
                });

        CompletableFuture<DataFrame<Object>> loyaltyProgramCompletableFuture = CompletableFuture
                .supplyAsync(() -> getLoyaltyProgram(setDataToDataFrame(sheet[1], new DataFrame<>(getTitleForDataFrame(sheet[1])))));

        CompletableFuture<DataFrame<Object>> boostSalesCompletableFuture = CompletableFuture
                .supplyAsync(() -> getBoostSales(setDataToDataFrame(sheet[2], new DataFrame<>(getTitleForDataFrame(sheet[2])))));

        CompletableFuture<DataFrame<Object>> deliveryToConsumerCompletableFuture = CompletableFuture
                .supplyAsync(() -> getDeliveryToConsumer(setDataToDataFrame(sheet[3], new DataFrame<>(getTitleForDataFrame(sheet[3])))));

        CompletableFuture<DataFrame<Object>> acceptAndTransferPaymentCompletableFuture = CompletableFuture
                .supplyAsync(() -> getAcceptAndTransferPayment(
                        setDataToDataFrame(sheet[4], new DataFrame<>(getTitleForDataFrame(sheet[4]))),
                        setDataToDataFrame(sheet[6], new DataFrame<>(getTitleForDataFrame(sheet[6])))
                ));

        CompletableFuture<DataFrame<Object>> favorSortingCenterPaymentCompletableFuture = CompletableFuture
                .supplyAsync(() -> getFavorSortingCenter(
                        setDataToDataFrame(sheet[5], new DataFrame<>(getTitleForDataFrame(sheet[5]))),
                        setDataToDataFrame(sheet[3], new DataFrame<>(getTitleForDataFrame(sheet[3]))),
                        setDataToDataFrame(sheet[4], new DataFrame<>(getTitleForDataFrame(sheet[4])))));

        mainDataFrame = placingOnShowcaseCompletableFuture.join();
        mainDataFrame = mainDataFrame.join(loyaltyProgramCompletableFuture.join(), DataFrame.JoinType.OUTER);
        mainDataFrame = mainDataFrame.join(boostSalesCompletableFuture.join(), DataFrame.JoinType.OUTER);
        mainDataFrame = mainDataFrame.join(deliveryToConsumerCompletableFuture.join(), DataFrame.JoinType.OUTER);
        mainDataFrame = mainDataFrame.join(acceptAndTransferPaymentCompletableFuture.join(), DataFrame.JoinType.OUTER);
        mainDataFrame = mainDataFrame.join(favorSortingCenterPaymentCompletableFuture.join(), DataFrame.JoinType.OUTER);

        return mainDataFrame;
    }

    private static DataFrame<Object> getDataFromRealizationInputStream(InputStream inputStream) throws IOException {
        DataFrame<Object> mainDataFrame;
        Workbook wb = WorkbookFactory.create(inputStream);

        final Sheet[] sheet = {wb.getSheetAt(2), wb.getSheetAt(4)};

        CompletableFuture<DataFrame<Object>> deliveredDataCompletableFuture = CompletableFuture
                .supplyAsync(() -> getDeliveredData(setDataToDataFrame(sheet[0], new DataFrame<>(getTitleForDataFrame(sheet[0])))));

        CompletableFuture<DataFrame<Object>> returnDataCompletableFuture = CompletableFuture
                .supplyAsync(() -> getReturnData(setDataToDataFrame(sheet[1], new DataFrame<>(getTitleForDataFrame(sheet[1])))));

        mainDataFrame = deliveredDataCompletableFuture.join();
        mainDataFrame = mainDataFrame.join(returnDataCompletableFuture.join(), DataFrame.JoinType.OUTER);

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
                .retain(2,11)
                .rename("Стоимость всех доставленных штук с НДС с учётом всех скидок, руб.","Начислено");
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

    //кринж (почему у списка нет метода, который бы заменил все null)
    public static List<YANDEX_TableRow> getTableRowList(InputStream inputStreamRealization, InputStream inputStreamServices) throws IOException {
        DataFrame<Object> tempDataFrame = new DataFrame<>();
        List<YANDEX_TableRow> listRows = new ArrayList<>();

        tempDataFrame = tempDataFrame.join(getDataFromServiceInputStream(inputStreamServices), DataFrame.JoinType.OUTER)
                                        .join(getDataFromRealizationInputStream(inputStreamRealization), DataFrame.JoinType.OUTER);

        Object[] skus = tempDataFrame.index().toArray();

        for(int i = 0; i < tempDataFrame.length(); i++) {
            List<Object> listDF = tempDataFrame.row(i);
            listRows.add(YANDEX_TableRow.builder()
                            .offerId(skus[i].toString())
                            .deliveryCount(listDF.get(6) != null ? (Double) listDF.get(6) : 0.0)
                            .accrued(listDF.get(7) != null ? (Double) listDF.get(7) : 0.0)
                            .returnCount(listDF.get(8) != null ? (Double) listDF.get(8) : 0.0)
                            .returnCost(listDF.get(9) != null ? (Double) listDF.get(9) : 0.0)
                            .showcasePlacing(listDF.get(0) != null ? (Double) listDF.get(0) : 0.0)
                            .deliveryToConsumer(listDF.get(3) != null ? (Double) listDF.get(3) : 0.0)
                            .acceptAndTransferPayment(listDF.get(4) != null ? (Double) listDF.get(4) : 0.0)
                            .favorSorting(listDF.get(5) != null ? (Double) listDF.get(5) : 0.0)
                            .unredeemedStorage(0.0)
                            .adCampaignCost(0.0)
                            .loyaltyProgram(listDF.get(1) != null ? (Double) listDF.get(1) : 0.0)
                            .boostSales(listDF.get(2) != null ? (Double) listDF.get(2) : 0.0)
                            .promotionFavor(0.0)
                            //крииинж...
                            .count(((listDF.get(6) != null ? (Double) listDF.get(6) : 0.0) -
                                    (listDF.get(8) != null ? (Double) listDF.get(8) : 0.0)) == 0.0 ? -1 :
                                    ((listDF.get(6) != null ? (Double) listDF.get(6) : 0.0) -
                                            (listDF.get(8) != null ? (Double) listDF.get(8) : 0.0)))
                    .build());
        }

        return listRows;
    }
}
