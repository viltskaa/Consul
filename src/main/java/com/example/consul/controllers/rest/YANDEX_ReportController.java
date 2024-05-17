package com.example.consul.controllers.rest;

import com.example.consul.controllers.rest.requestBodies.OZON_RequestBody;
import com.example.consul.controllers.rest.requestBodies.YANDEX_RequestBody;
import com.example.consul.document.models.YANDEX_TableRow;
import com.example.consul.services.YANDEX_Service;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(path = "/yandexReport")
public class YANDEX_ReportController {
    private final YANDEX_Service yandexService;

    public YANDEX_ReportController(YANDEX_Service service) {
        this.yandexService = service;
    }

    @GetMapping(path = "/get")
    public List<YANDEX_TableRow> getReport(@RequestBody YANDEX_RequestBody body){
        return yandexService.getData(body.getAuth(),
                body.getCampaignId(),
                body.getYear(),
                body.getMonth(),
                body.getBusinessId(),
                body.getPlacementPrograms());
    }
}
