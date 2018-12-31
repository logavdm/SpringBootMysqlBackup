package com.kambaa.controller.user;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionDemo {

	
	@RequestMapping("/set/session")
	public String setSessionValues(HttpSession session) {
		session.setAttribute("NAME","loganathan");		
		return "hii";
	}
	
	
	@RequestMapping("/get/session")
	public String getSessionValues(HttpSession session) {
		return session.getAttribute("NAME").toString();
	}
	
}
