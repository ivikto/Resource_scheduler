package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Runner {
    private final Request request;

    @Autowired
    public Runner(Request request) {
        this.request = request;
    }


    public void run() {
        request.doRequest();
    }
}
