package com.example.consul.api.utils.YANDEX;

public enum YANDEX_ApiResponseStatusType {
    OK,
    ERROR;

    public YANDEX_ApiResponseStatusType fromString(String statusStr){
        if(statusStr.equalsIgnoreCase("OK"))
            return OK;

        return ERROR;
    }
}
