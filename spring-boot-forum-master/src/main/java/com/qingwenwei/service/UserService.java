package com.qingwenwei.service;

import com.qingwenwei.persistence.model.User;
import com.qingwenwei.web.dto.UserRegistrationDto;
import com.qingwenwei.web.dto.UserSettingsDto;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface UserService {
	
	int save(User user);
	
	User findById(Long id);
	
	User findByUsername(String username);
	
	User findByEmail(String email);
	
	User findByConfirmationToken(String confirmationToken);
	
	User findAuthenticatedUser();
	
	Map<String, Object> getUserProfileAndPostsByUserIdByTabType(Long userId, String tabType);
	
	Map<String, Object> updateUserProfile(UserSettingsDto newUserSettingsForm);
	
	Map<String, Object> getUserSettingPage();
	
	Map<String, Object> registerUserAccount(UserRegistrationDto userDto, HttpServletRequest request);

}
