package com.kambaa.controller.user;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kambaa.model.Response;
import com.kambaa.model.UserModel;
import com.kambaa.services.UserService;

@RestController
@RequestMapping(value = "/user")
public class LoginController {

	@Autowired
	UserService userServices;

	@Autowired
	Response response;

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@RequestMapping(value = "/login", method = { RequestMethod.POST })
	public Response loginUserWithMobileAndPassword(@RequestParam(value = "mobile") String mobile,@RequestParam(value = "password") String password, HttpSession session) {
		logger.info("----------------- USER LOGIN CONTROLLER START HERE --------------------------------------");
		try {
			if (!userServices.existsMobile(mobile)) {
				logger.info("Mobile number not found");
				response.setResponsecode("404");
				response.setMessage("Invalid mobile number or password");
				response.setResponse(null);
			} else {
				if (userServices.validateMobileAndPassword(mobile, password, session)) {
					UserModel user = (UserModel) session.getAttribute("USERDETAILS");
					logger.info("login success");
					response.setResponsecode("200");
					response.setMessage("Login success");
					response.setResponse(user);
				} else {
					response.setResponsecode("403");
					response.setMessage("Invalid Mobile number or password");
					response.setResponse(null);
				}
			}

		} catch (Exception e) {
			logger.error("Error occured when user login controller :" + e);
			response.setResponsecode("500");
			response.setMessage("Something went wrong please try to contact the administrator");
			response.setResponse(null);
		}
		logger.info("----------------- USER LOGIN CONTROLLER END HERE --------------------------------------");
		return response;
	}

}
