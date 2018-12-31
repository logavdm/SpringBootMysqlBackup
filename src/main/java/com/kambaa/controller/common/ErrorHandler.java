package com.kambaa.controller.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kambaa.model.Response;

@RestController
public class ErrorHandler implements ErrorController {

	@Autowired
	Response res;
	
	private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);
	
	@RequestMapping("/error")
	public ResponseEntity<Response> GetErrorFormattedResponse(HttpServletRequest request, HttpServletResponse response) {
		ResponseEntity<Response> responseEntity;		
		switch(response.getStatus()) {
		case 400:
			logger.info("MISSING SOME PARAMETER");
			res.setResponsecode("400");
			res.setMessage("Missing some Parameter Please check");
			res.setResponse(null);
			responseEntity=new ResponseEntity<Response>(res,HttpStatus.UNAUTHORIZED);
		break;
		case 401:
			logger.info("UN AUTHORISED ERROR");
			res.setResponsecode("401");
			res.setMessage("Un authorised error");
			res.setResponse(null);
			responseEntity=new ResponseEntity<Response>(res,HttpStatus.UNAUTHORIZED);
		break;
		case 403:
			logger.info("FORBIDDEN ACCESS REQUEST");
			res.setResponsecode("403");
			res.setMessage("Forbidden to access request");
			res.setResponse(null);
			responseEntity=new ResponseEntity<Response>(res,HttpStatus.UNAUTHORIZED);
		break;
		case 404:
			logger.info("REQUEST RESOURCE NOT FOUND");
			res.setResponsecode("404");
			res.setMessage("Requesting Resource Not Found");
			res.setResponse(null);
			responseEntity=new ResponseEntity<Response>(res,HttpStatus.UNAUTHORIZED);
		break;
		
		case 500:
			logger.info("REQUEST METHOD NOT FOUND");
			res.setResponsecode("500");
			res.setMessage("Requesting Method Not Found");
			res.setResponse(null);
			responseEntity=new ResponseEntity<Response>(res,HttpStatus.UNAUTHORIZED);
		break;
		
		case 1000:
			logger.info("REQUEST PARAMETERS INVALID");
			res.setResponsecode("1000");
			res.setMessage("Invalid Request Parameters Please check the request parameters are correct or not");
			res.setResponse(null);
			responseEntity=new ResponseEntity<Response>(res,HttpStatus.UNAUTHORIZED);
		break;
		
		case 1001:
			logger.info("EXCEEDED THE MAXIMUM NUMBER OF ATTEMPTS");
			res.setResponsecode("1001");
			res.setMessage("You Exceed the maximum number of attempt please try after Some Time");
			res.setResponse(null);
			responseEntity=new ResponseEntity<Response>(res,HttpStatus.UNAUTHORIZED);
		break;
		
		case 1002:
			logger.info("APPLICATION UNDER MAINTENANCE MODE");
			res.setResponsecode("1002");
			res.setMessage("Application under Maintenance Mode");
			res.setResponse(null);
			responseEntity=new ResponseEntity<Response>(res,HttpStatus.UNAUTHORIZED);
		break;
		
		default:
			logger.info("SOMETHING WENT WRONG DEFAULT ERROR MESSAGE");
			res.setResponsecode("500");
			res.setMessage("Something went wrong please try again");
			res.setResponse(null);
			responseEntity=new ResponseEntity<Response>(res,HttpStatus.UNAUTHORIZED);
		break;
		}
		
		responseEntity=new ResponseEntity<Response>(res,HttpStatus.OK);
		return responseEntity;
	}
	

	@Override
	public String getErrorPath() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
