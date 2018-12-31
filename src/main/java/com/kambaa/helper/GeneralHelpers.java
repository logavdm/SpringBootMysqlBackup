package com.kambaa.helper;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kambaa.entity.User;
import com.kambaa.model.UserModel;

@Service
public class GeneralHelpers {
	
	private static final Logger logger = LoggerFactory.getLogger(GeneralHelpers.class);

	boolean convertToUserModel(User user, HttpSession session) {
		try {
			UserModel userModel = new UserModel();
			userModel.setEmail(user.getEmail());
			userModel.setMobile(user.getMobile());
			userModel.setFullname(user.getFullname());
			userModel.setEnabled(user.isEnable());
			userModel.setId(user.getId());
			session.setAttribute("USERDETAILS", userModel);
			return true;
		} catch (Exception e) {
			logger.error("Error occcured when convert to user model:" + e);
			return false;
		}
	}
	
	
}
