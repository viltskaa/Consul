package com.example.consul.dto.WB;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// https://openapi.wildberries.ru/promotion/api/ru/#tag/Finansy/paths/~1adv~1v1~1upd/get
@Setter
@Getter
@Data
public class WB_AdReport {
    String updTime;
    String campName;
    String paymentType;
    Integer updNum;
    Integer updSum;
    Integer advertId;
    Integer advertStatus;
    Integer advertType;
}
