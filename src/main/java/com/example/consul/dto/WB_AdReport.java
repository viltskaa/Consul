package com.example.consul.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
