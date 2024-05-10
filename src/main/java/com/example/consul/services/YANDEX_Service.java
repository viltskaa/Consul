package com.example.consul.services;

import com.example.consul.api.YANDEX_Api;
import org.springframework.stereotype.Service;

@Service
public class YANDEX_Service {
    private final YANDEX_Api api;

    public YANDEX_Service(YANDEX_Api api){
        this.api = api;
    }
}
