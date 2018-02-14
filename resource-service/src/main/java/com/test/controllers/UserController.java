package com.test.controllers;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('USER')")
public class UserController {

    @RequestMapping("/")
    public Principal resource(Principal principal) {
        return principal;
    }

}