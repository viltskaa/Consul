package com.example.consul.controllers;

import com.example.consul.dto.WB_AdReport;
import com.example.consul.services.WB_Service;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(path="/adreport")
public class WB_AdReportController {
    private final WB_Service wbService;

    public WB_AdReportController(WB_Service wbService) {
        this.wbService = wbService;
        wbService.setApi("eyJhbGciOiJFUzI1NiIsImtpZCI6IjIwMjQwMjI2djEiLCJ0eXAiOiJKV1QifQ.eyJlbnQiOjEsImV4cCI6MTcyNzgxMzkyMSwiaWQiOiIwYTY5NDVkZS0wODQyLTQ1ZmItOGEyMC0zNTMzNzliYjk1NjUiLCJpaWQiOjQ1ODkwNDkwLCJvaWQiOjg5NzE2NiwicyI6MTA3Mzc0MjMzNCwic2lkIjoiMTZhMGZiZWEtYWVmZi00YjgxLThmNzEtZjYyZDlhYjJmMGM1IiwidCI6ZmFsc2UsInVpZCI6NDU4OTA0OTB9.DtgYFO1TioCOeKiKI4VKw0_QbD8S4908JSxj2196k_pUDH1vgNBiUPImwWMGhaDgpE8GVEdzOQnPj23aiRT4JQ");
    }

    @GetMapping(path = "/get")
    public List<WB_AdReport> getAdReport(){
        return wbService.getAdReport();
    }
}
