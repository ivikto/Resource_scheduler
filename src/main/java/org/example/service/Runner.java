package org.example.service;

import org.example.service.operationsService.LaserCutterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Runner {
    private final Request request;
    private final LaserCutterService laserCutterService;

    @Autowired
    public Runner(Request request, LaserCutterService laserCutterService) {
        this.request = request;
        this.laserCutterService = laserCutterService;
    }


    public void run() {
        //request.doRequest();
        laserCutterService.getAllLaserCutter().forEach(laserCutter -> System.out.println(laserCutter));
    }
}
