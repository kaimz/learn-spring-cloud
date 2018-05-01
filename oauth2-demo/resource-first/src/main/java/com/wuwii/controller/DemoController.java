package com.wuwii.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by KronChan on 2018/5/1 11:58.
 */
@RestController
@RequestMapping("/demo")
public class DemoController {
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public String getDemo(){
        return "good";
    }
}
