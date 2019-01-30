package com.kambaa.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.kambaa.services.UserService;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthorizationFilter implements Filter {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);
	
	@Autowired
	UserService userServices;
	
	private static final String[] pathArrays={
			"/",
			"/demo",
			"/user/login",
			"/user/login/new",
			"/user/register",
			"/user/activate",
			"/user/resendotp",
			"/user/forgotpassword",
			"/user/forgotpassword/validateotp",
			"/user/changepassword",
			};
	private final List<String> PublicURLS= new ArrayList<>(Arrays.asList(pathArrays));
	
	public AuthorizationFilter() {
		logger.info("Authorisation Filter Initialized");
	}
	
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		try {
			//ALLOW HEADER CONFIGURATIONS
			httpResponse.setHeader("Access-Control-Allow-Origin", "*");
			httpResponse.setHeader("Access-Control-Allow-Methods", "GET,POST, PUT,OPTIONS, DELETE");
			httpResponse.setHeader("Access-Control-Max-Age", "3600");
			httpResponse.setHeader("Access-Control-Allow-Headers","content-type,authorization,api-call-type,RemoteIP");
			httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
			
			if ("OPTIONS".equals(httpRequest.getMethod())) {
				httpResponse.setStatus(HttpServletResponse.SC_OK);
			} else {
				if(CheckPathParameters(httpRequest.getRequestURI())) {
					chain.doFilter(request, response);
				}else {
					if(httpRequest.getHeader("authorization")!=null) {
						if(validateAuthorisation(httpRequest.getHeader("authorization"),httpRequest)) {
							chain.doFilter(request, response);
						}else {
							logger.error("Auth header username password validation failed");
							httpResponse.sendError(403);
						}
					}else {
						logger.error("Auth header received as empty");
						httpResponse.sendError(403);
					}
				}			
			}
		}catch (Exception e) {
			logger.error("Error occured on filter :"+e);
			httpResponse.sendError(403);
		}
	}
	
	
	private boolean CheckPathParameters(String path) {
		if(PublicURLS.contains(path)) {
			return true;
		}else {
			return false;
		}
	}

	boolean validateAuthorisation(String token,HttpServletRequest request) {
		try {
			token = token.substring("Basic ".length());
			String[] credentials = new String(Base64.getDecoder().decode(token), "UTF-8").split(":");
			MDC.put("username",credentials[0]);
			if(userServices.validateMobileAndPassword(credentials[0],credentials[1],request.getSession())) {
				logger.info("database validation success");
				return true;
			}else {
				logger.info("database validation failed");
				return false;
			}
		}catch (Exception e) {
			logger.error("Error occured when validate and parse the auth token :"+e);
			return false;
		}
	}
	
	
	
}
