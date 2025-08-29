package com.skrrrrr.harudam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class controller {
	
	@GetMapping("/test")
	public String test() {
		return "스프링 부트 열려라";
	}
}
