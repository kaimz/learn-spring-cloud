package com.wuwii.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Created by KronChan on 2018/5/1 11:31.
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping
    public Principal user(Principal user) {
        return user;
    }
}
