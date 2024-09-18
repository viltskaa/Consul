package com.example.consul.mapping.excelProcessing;

import com.example.consul.mapping.annotations.ColumnName;
import com.example.consul.mapping.sheets.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.consul.mapping.excelProcessing.FormatExcel.findAutoFilterRow;
import static com.example.consul.mapping.excelProcessing.FormatExcel.getColumnByHeader;

public class DataFromExcel {
    private static Map<Field, Integer> getFieldColumnMapping(Class<?> clazz, Sheet sheet) {
        Map<Field, Integer> fieldColumnMapping = new HashMap<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ColumnName.class)) {
                ColumnName columnNameAnnotation = field.getAnnotation(ColumnName.class);
                String columnName = columnNameAnnotation.name().name;

                int columnIndex = getColumnByHeader(columnName, sheet);
                fieldColumnMapping.put(field, columnIndex);
            }
        }

        return fieldColumnMapping;
    }

    public static List<YANDEX_DeliveredGoods> getDeliveredGoods(Sheet sheet) {
        List<YANDEX_DeliveredGoods> list = new ArrayList<>();
        int headerRow = findAutoFilterRow(sheet);

        Map<Field, Integer> fieldColumnMapping = getFieldColumnMapping(YANDEX_DeliveredGoods.class, sheet);

        for (int j = headerRow + 1; j <= sheet.getLastRowNum() - 1; j++) {
            Row row = sheet.getRow(j);
            if (row == null) continue;

            YANDEX_DeliveredGoods data = new YANDEX_DeliveredGoods();

            for (Map.Entry<Field, Integer> entry : fieldColumnMapping.entrySet()) {
                Field field = entry.getKey();
                int columnIndex = entry.getValue();

                field.setAccessible(true);
                Cell cell = row.getCell(columnIndex);

                if (cell == null) continue;

                switch (field.getType().getSimpleName()) {
                    case "int":
                        try {
                            field.set(data, (int) cell.getNumericCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "double":
                        try {
                            field.set(data, cell.getNumericCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "String":
                        try {
                            field.set(data, cell.getStringCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                }
            }

            list.add(data);
        }

        return list;
    }

    public static List<YANDEX_GoodsInDelivery> getGoodsInDelivery(Sheet sheet) {
        List<YANDEX_GoodsInDelivery> list = new ArrayList<>();
        int headerRow = findAutoFilterRow(sheet);

        Map<Field, Integer> fieldColumnMapping = getFieldColumnMapping(YANDEX_GoodsInDelivery.class, sheet);

        for (int j = headerRow + 1; j <= sheet.getLastRowNum() - 1; j++) {
            Row row = sheet.getRow(j);
            if (row == null) continue;

            YANDEX_GoodsInDelivery data = new YANDEX_GoodsInDelivery();

            for (Map.Entry<Field, Integer> entry : fieldColumnMapping.entrySet()) {
                Field field = entry.getKey();
                int columnIndex = entry.getValue();

                field.setAccessible(true);
                Cell cell = row.getCell(columnIndex);

                if (cell == null) continue;

                switch (field.getType().getSimpleName()) {
                    case "long":
                        try {
                            field.set(data, (long) row.getCell(columnIndex).getNumericCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "String":
                        try {
                            field.set(data, row.getCell(columnIndex).getStringCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                }
            }

            list.add(data);
        }

        return list;
    }

    public static List<YANDEX_ReturnedGoods> getReturnedGoods(Sheet sheet) {
        List<YANDEX_ReturnedGoods> list = new ArrayList<>();
        int headerRow = findAutoFilterRow(sheet);

        Map<Field, Integer> fieldColumnMapping = getFieldColumnMapping(YANDEX_ReturnedGoods.class, sheet);

        for (int j = headerRow + 1; j <= sheet.getLastRowNum() - 1; j++) {
            Row row = sheet.getRow(j);
            if (row == null) continue;

            YANDEX_ReturnedGoods data = new YANDEX_ReturnedGoods();

            for (Map.Entry<Field, Integer> entry : fieldColumnMapping.entrySet()) {
                Field field = entry.getKey();
                int columnIndex = entry.getValue();

                field.setAccessible(true);
                Cell cell = row.getCell(columnIndex);

                if (cell == null) continue;

                switch (field.getType().getSimpleName()) {
                    case "int":
                        try {
                            field.set(data, (int) cell.getNumericCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "double":
                        try {
                            field.set(data, cell.getNumericCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "String":
                        try {
                            field.set(data, cell.getStringCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                }
            }

            list.add(data);
        }

        return list;
    }

    public static List<YANDEX_Shelves> getShelves(Sheet sheet) {
        List<YANDEX_Shelves> list = new ArrayList<>();
        int headerRow = findAutoFilterRow(sheet);

        Map<Field, Integer> fieldColumnMapping = getFieldColumnMapping(YANDEX_Shelves.class, sheet);

        for (int j = headerRow + 1; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            if (row == null) continue;

            YANDEX_Shelves data = new YANDEX_Shelves();

            for (Map.Entry<Field, Integer> entry : fieldColumnMapping.entrySet()) {
                Field field = entry.getKey();
                int columnIndex = entry.getValue();

                field.setAccessible(true);
                Cell cell = row.getCell(columnIndex);

                if (cell == null) continue;

                if (field.getType().getSimpleName().equals("double")) {
                    try {
                        field.set(data, cell.getNumericCellValue());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            list.add(data);
        }

        return list;
    }

    public static List<YANDEX_BoostSales> getBoostSales(Sheet sheet) {
        List<YANDEX_BoostSales> list = new ArrayList<>();
        int headerRow = findAutoFilterRow(sheet);

        Map<Field, Integer> fieldColumnMapping = getFieldColumnMapping(YANDEX_BoostSales.class, sheet);

        for (int j = headerRow + 1; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            if (row == null) continue;

            YANDEX_BoostSales data = new YANDEX_BoostSales();

            for (Map.Entry<Field, Integer> entry : fieldColumnMapping.entrySet()) {
                Field field = entry.getKey();
                int columnIndex = entry.getValue();

                field.setAccessible(true);
                Cell cell = row.getCell(columnIndex);

                if (cell == null) continue;

                switch (field.getType().getSimpleName()) {
                    case "double":
                        try {
                            field.set(data, cell.getNumericCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "String":
                        try {
                            field.set(data, cell.getStringCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                }
            }

            list.add(data);
        }

        return list;
    }

    public static List<YANDEX_LoyaltyProgram> getLoyaltyProgram(Sheet sheet) {
        List<YANDEX_LoyaltyProgram> list = new ArrayList<>();
        int headerRow = findAutoFilterRow(sheet);

        Map<Field, Integer> fieldColumnMapping = getFieldColumnMapping(YANDEX_LoyaltyProgram.class, sheet);

        for (int j = headerRow + 1; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            if (row == null) continue;

            YANDEX_LoyaltyProgram data = new YANDEX_LoyaltyProgram();

            for (Map.Entry<Field, Integer> entry : fieldColumnMapping.entrySet()) {
                Field field = entry.getKey();
                int columnIndex = entry.getValue();

                field.setAccessible(true);
                Cell cell = row.getCell(columnIndex);

                if (cell == null) continue;

                switch (field.getType().getSimpleName()) {
                    case "double":
                        try {
                            field.set(data, cell.getNumericCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "String":
                        try {
                            field.set(data, cell.getStringCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                }
            }

            list.add(data);
        }

        return list;
    }

    public static List<YANDEX_ShowPlacement> getShowPlacement(Sheet sheet) {
        List<YANDEX_ShowPlacement> list = new ArrayList<>();
        int headerRow = findAutoFilterRow(sheet);

        Map<Field, Integer> fieldColumnMapping = getFieldColumnMapping(YANDEX_ShowPlacement.class, sheet);

        for (int j = headerRow + 2; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            if (row == null) continue;

            YANDEX_ShowPlacement data = new YANDEX_ShowPlacement();

            for (Map.Entry<Field, Integer> entry : fieldColumnMapping.entrySet()) {
                Field field = entry.getKey();
                int columnIndex = entry.getValue();

                field.setAccessible(true);
                Cell cell = row.getCell(columnIndex);

                if (cell == null) continue;

                switch (field.getType().getSimpleName()) {
                    case "double":
                        try {
                            field.set(data, cell.getNumericCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "String":
                        try {
                            field.set(data, cell.getStringCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                }
            }

            list.add(data);
        }

        return list;
    }

    public static List<YANDEX_DeliveryCustomer> getDeliveryCustomer(Sheet sheet) {
        List<YANDEX_DeliveryCustomer> list = new ArrayList<>();
        int headerRow = findAutoFilterRow(sheet);

        Map<Field, Integer> fieldColumnMapping = getFieldColumnMapping(YANDEX_DeliveryCustomer.class, sheet);

        for (int j = headerRow + 1; j <= sheet.getPhysicalNumberOfRows() - 1; j++) {
            Row row = sheet.getRow(j);
            if (row == null) continue;

            YANDEX_DeliveryCustomer data = new YANDEX_DeliveryCustomer();

            for (Map.Entry<Field, Integer> entry : fieldColumnMapping.entrySet()) {
                Field field = entry.getKey();
                int columnIndex = entry.getValue();

                field.setAccessible(true);
                Cell cell = row.getCell(columnIndex);

                if (cell == null) continue;

                switch (field.getType().getSimpleName()) {
                    case "long":
                        try {
                            field.set(data, (long) cell.getNumericCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "double":
                        try {
                            field.set(data, cell.getNumericCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "String":
                        try {
                            field.set(data, cell.getStringCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                }
            }

            list.add(data);
        }

        return list;
    }

    public static List<YANDEX_AcceptingPayment> getAcceptingPayment(Sheet sheet) {
        List<YANDEX_AcceptingPayment> list = new ArrayList<>();
        int headerRow = findAutoFilterRow(sheet);

        Map<Field, Integer> fieldColumnMapping = getFieldColumnMapping(YANDEX_AcceptingPayment.class, sheet);

        for (int j = headerRow + 1; j <= sheet.getPhysicalNumberOfRows() - 1; j++) {
            Row row = sheet.getRow(j);
            if (row == null) continue;

            YANDEX_AcceptingPayment data = new YANDEX_AcceptingPayment();

            for (Map.Entry<Field, Integer> entry : fieldColumnMapping.entrySet()) {
                Field field = entry.getKey();
                int columnIndex = entry.getValue();

                field.setAccessible(true);
                Cell cell = row.getCell(columnIndex);

                if (cell == null) continue;

                switch (field.getType().getSimpleName()) {
                    case "long":
                        try {
                            field.set(data, (long) cell.getNumericCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "double":
                        try {
                            field.set(data, cell.getNumericCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "String":
                        try {
                            field.set(data, cell.getStringCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                }
            }

            list.add(data);
        }

        return list;
    }

    public static List<YANDEX_TransferPayment> getTransferPayment(Sheet sheet) {
        List<YANDEX_TransferPayment> list = new ArrayList<>();
        int headerRow = findAutoFilterRow(sheet);

        Map<Field, Integer> fieldColumnMapping = getFieldColumnMapping(YANDEX_TransferPayment.class, sheet);

        for (int j = headerRow + 1; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            if (row == null) continue;

            YANDEX_TransferPayment data = new YANDEX_TransferPayment();

            for (Map.Entry<Field, Integer> entry : fieldColumnMapping.entrySet()) {
                Field field = entry.getKey();
                int columnIndex = entry.getValue();

                field.setAccessible(true);
                Cell cell = row.getCell(columnIndex);

                if (cell == null) continue;

                switch (field.getType().getSimpleName()) {
                    case "long":
                        try {
                            field.set(data, (long) cell.getNumericCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "double":
                        try {
                            field.set(data, cell.getNumericCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "String":
                        try {
                            field.set(data, cell.getStringCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                }
            }

            list.add(data);
        }

        return list;
    }

    public static List<YANDEX_ProcessingOrders> getProcessingOrders(Sheet sheet) {
        List<YANDEX_ProcessingOrders> list = new ArrayList<>();
        int headerRow = findAutoFilterRow(sheet);

        Map<Field, Integer> fieldColumnMapping = getFieldColumnMapping(YANDEX_ProcessingOrders.class, sheet);

        for (int j = headerRow + 1; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            if (row == null) continue;

            YANDEX_ProcessingOrders data = new YANDEX_ProcessingOrders();

            for (Map.Entry<Field, Integer> entry : fieldColumnMapping.entrySet()) {
                Field field = entry.getKey();
                int columnIndex = entry.getValue();

                field.setAccessible(true);
                Cell cell = row.getCell(columnIndex);

                if (cell == null) continue;

                switch (field.getType().getSimpleName()) {
                    case "long":
                        try {
                            field.set(data, (long) cell.getNumericCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "double":
                        try {
                            field.set(data, cell.getNumericCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "String":
                        try {
                            field.set(data, cell.getStringCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                }
            }

            list.add(data);
        }

        return list;
    }

    public static List<YANDEX_TransactionsOrdersAndProducts> getTransactionsOnOrdersAndProducts(Sheet sheet) {
        List<YANDEX_TransactionsOrdersAndProducts> list = new ArrayList<>();
        int headerRow = findAutoFilterRow(sheet);

        Map<Field, Integer> fieldColumnMapping = getFieldColumnMapping(YANDEX_TransactionsOrdersAndProducts.class, sheet);

        for (int j = headerRow + 1; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            if (row == null) continue;

            YANDEX_TransactionsOrdersAndProducts data = new YANDEX_TransactionsOrdersAndProducts();

            for (Map.Entry<Field, Integer> entry : fieldColumnMapping.entrySet()) {
                Field field = entry.getKey();
                int columnIndex = entry.getValue();

                field.setAccessible(true);
                Cell cell = row.getCell(columnIndex);

                if (cell == null) continue;

                switch (field.getType().getSimpleName()) {
                    case "long":
                        try {
                            field.set(data, (long) cell.getNumericCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "String":
                        try {
                            field.set(data, cell.getStringCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                }
            }

            list.add(data);
        }

        return list;
    }

    public static List<YANDEX_StorageReturns> getStorageReturns(Sheet sheet) {
        List<YANDEX_StorageReturns> list = new ArrayList<>();
        int headerRow = findAutoFilterRow(sheet);

        Map<Field, Integer> fieldColumnMapping = getFieldColumnMapping(YANDEX_StorageReturns.class, sheet);

        for (int j = headerRow + 1; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            if (row == null) continue;

            YANDEX_StorageReturns data = new YANDEX_StorageReturns();

            for (Map.Entry<Field, Integer> entry : fieldColumnMapping.entrySet()) {
                Field field = entry.getKey();
                int columnIndex = entry.getValue();

                field.setAccessible(true);
                Cell cell = row.getCell(columnIndex);

                if (cell == null) continue;

                switch (field.getType().getSimpleName()) {
                    case "long":
                        try {
                            field.set(data, (long) cell.getNumericCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "double":
                        try {
                            field.set(data, cell.getNumericCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "String":
                        try {
                            field.set(data, cell.getStringCellValue());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                }
            }

            list.add(data);
        }

        return list;
    }

}
