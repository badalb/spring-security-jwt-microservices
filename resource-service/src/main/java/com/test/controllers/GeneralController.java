package com.test.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeneralController {

	@RequestMapping("/hello")
	public String home() {
		return "Hello World";
	}
}
