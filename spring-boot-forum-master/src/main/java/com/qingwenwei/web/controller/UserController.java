package com.qingwenwei.web.controller;

import com.qingwenwei.exception.BadRequestException;
import com.qingwenwei.exception.ResourceNotFoundException;
import com.qingwenwei.persistence.model.User;
import com.qingwenwei.service.UserService;
import com.qingwenwei.util.NewUserFormValidator;
import com.qingwenwei.web.dto.UserRegistrationDto;
import com.qingwenwei.web.dto.UserSettingsDto;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Controller
public class UserController {

	Logger logger = LogManager.getLogger(UserController.class);
//	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
    private NewUserFormValidator userValidator;
	
	@RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
	public String showUserProfilePage(@RequestParam(value = "tab", required = false) String tabType, 
			@PathVariable Long userId, Model model) {
		if (null == userId) {
			throw new BadRequestException("Path variable userId cound not be null.");
		}
		Map<String, Object> attributes = this.userService.getUserProfileAndPostsByUserIdByTabType(userId, tabType);
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		model.addAllAttributes(attributes);
		return "forum/user-profile";
	}
	
	@RequestMapping(value = "/user/registration", method = RequestMethod.GET)
	public String showRegistrationPage(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
		return "forum/user-registration";  //注册页面
	}
	
	@RequestMapping(value = "/user/registration", method = RequestMethod.POST)  //提交注册
	public String registerNewUser(@Valid @ModelAttribute("userDto") UserRegistrationDto userDto,
			BindingResult bindingResult, Model model, HttpServletRequest request) {
		/*
		 * form validation, check username and email uniqueness
		 */
		this.userValidator.validate(userDto, bindingResult);
        if (bindingResult.hasErrors()) {
        		logger.info("BindingResult has errors >> " + bindingResult.getFieldError());
        		return "forum/user-registration";
        }
        logger.debug("注册"+userDto.getMatchingPassword());
        Map<String, Object> attributes = this.userService.registerUserAccount(userDto, request);
        if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
        model.addAllAttributes(attributes);
		return "forum/user-registration-result";
	}
	
	@RequestMapping(value = "/user/login", method = RequestMethod.GET)
	public String displayLoginPage(Model model) {
		logger.debug("user/login登录");
		model.addAttribute("title", "用户登陆");
		return "forum/user-login"; //登录界面，验证没通过。
	}
	
	@RequestMapping(value = "/user/login-success", method = RequestMethod.GET)
	public String showAdminPage() {
		logger.debug("登录成功");
		return "forum/user-login";
//		return "/";
	}
	
	@RequestMapping(value = "/confirm", method = RequestMethod.GET)
	public String confirmRegistration(@RequestParam("token") String token) {
		return "forum/confirmation";
	}
	
	@RequestMapping(value = "/confirm", method = RequestMethod.POST)
	public String processConfirmation() {
		return "forum/confirmation";
	}
	
	@RequestMapping(value = "/user/settings", method = RequestMethod.GET)
	public String showUserSettingsPage(Model model) {
		Map<String, Object> attributes = this.userService.getUserSettingPage();
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		model.addAllAttributes(attributes);
		return "forum/user-settings";
	}

	@RequestMapping(value = "/user/settings", method = RequestMethod.POST)
	public String handleFileUpload(@ModelAttribute("userSettingsDto") UserSettingsDto userSettingsDto, Model model) {
//		User byConfirmationToken = userService.findByConfirmationToken(userSettingsDto.getPasswordConfirmation());
//		logger.debug(byConfirmationToken.getPassword());
//		logger.debug(userSettingsDto.getPassword());

		if (null == userSettingsDto) {
			throw new BadRequestException("UserSettingsDto cound not be null.");
		}
		Map<String, Object> attributes = this.userService.updateUserProfile(userSettingsDto);
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		model.addAllAttributes(attributes);
		return "forum/user-settings";
	}

}
