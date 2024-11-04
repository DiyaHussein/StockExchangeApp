package com.stockexchange.stockexchange;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MyController {

    @PostMapping("/resource")
    public String handlePostRequest(@RequestBody String payload) {
        // Your logic for handling the POST request
        System.out.println("Received payload: " + payload);
        return "Request processed";
    }
}