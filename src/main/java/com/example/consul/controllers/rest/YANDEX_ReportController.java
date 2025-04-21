package com.example.consul.controllers.rest;

import com.example.consul.controllers.rest.requestBodies.YANDEX_RequestBody;
import com.example.consul.document.models.ReportFile;
import com.example.consul.document.models.YANDEX_TableRow;
import com.example.consul.services.YANDEX_Service;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(path = "/yandexReport")
public class YANDEX_ReportController {
    private final YANDEX_Service yandexService;

    public YANDEX_ReportController(YANDEX_Service service) {
        this.yandexService = service;
    }

    @PostMapping(path = "/get")
    public List<YANDEX_TableRow> getReport(@RequestBody YANDEX_RequestBody body){
        return yandexService.getData(body.getAuth(),
                body.getCampaignId(),
                body.getBusinessId(),
                body.getYear(),
                body.getMonth());
    }

    @PostMapping(path = "/getExcel")
    public @ResponseBody ResponseEntity<Resource> getReportExcel(@RequestBody YANDEX_RequestBody body){
        ReportFile report = yandexService.createReport(
                body.getAuth(),
                body.getCampaignId(),
                body.getBusinessId(),
                body.getYear(),
                body.getMonth()
        );
        return report.toOkResource();
    }
}
