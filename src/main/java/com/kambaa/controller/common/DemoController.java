package com.kambaa.controller.common;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kambaa.controller.user.HomeController;
import com.kambaa.model.Response;

@RestController
public class DemoController {

	@Autowired
	Response response;
	
	@Value("${password.hash.key}")
	String passwordSalt;
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value="/demo",method= {RequestMethod.GET})
	public Response getBcriptPasswordHash(@RequestParam("password")String password) {
		try {
			logger.info("password hash generate success");
			response.setResponsecode("200");
			response.setMessage("password hash generate success");
			response.setResponse(BCrypt.hashpw(password,passwordSalt));
			
		}catch (Exception e) {
			logger.error("error occured when getting the password hash :"+e);
			response.setResponsecode("500");
			response.setMessage("something went wrong please try again later");
			response.setResponse(null);
		}
		return response;
	}
	
}
