package com.example.consul;

import com.example.consul.api.WB_Api;
import com.example.consul.dto.WB.WB_DetailReport;
import com.example.consul.mapping.WB_dataProcessing;
import com.example.consul.services.WB_Service;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class WBTestsConsul {

    @Test
    void getDetailReport(){
        WB_Service wbService = new WB_Service(new WB_Api());
        wbService.setApiKey("eyJhbGciOiJFUzI1NiIsImtpZCI6IjIwMjQwMjI2djEiLCJ0eXAiOiJKV1QifQ.eyJlbnQiOjEsImV4cCI6MTczMDQxNjIyNCwiaWQiOiIzNDRlMzA1Ni1jMDU4LTQxMmEtODk3Zi1kZjJkYTdiNDdiYjQiLCJpaWQiOjQ1ODkwNDkwLCJvaWQiOjg5NzE2NiwicyI6MTAyMiwic2lkIjoiMTZhMGZiZWEtYWVmZi00YjgxLThmNzEtZjYyZDlhYjJmMGM1IiwidCI6ZmFsc2UsInVpZCI6NDU4OTA0OTB9.QL4J2FabaLOHCdPovbyaUWKw28VdRbruv-PY1m5tLhWea_0DEcExywqEvwcRAiHfQyNOydOJe2biakFg68iH9Q");
        List<WB_DetailReport> list= wbService.getDetailReport("2024-01-01", "2024-01-31");
        System.out.println(WB_dataProcessing.sumAcquiring(WB_dataProcessing.groupBySaName(list)));
        List<WB_DetailReport> list2= wbService.getDetailReport("2024-01-01", "2024-01-31");
        System.out.println(WB_dataProcessing.sumCountSale(WB_dataProcessing.groupBySaName(list2)));
    }
}
